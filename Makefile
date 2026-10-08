MAKEFILES_VERSION=10.11.1
.DEFAULT_GOAL:=dogu-release
include build/make/variables.mk
include build/make/self-update.mk
include build/make/clean.mk
include build/make/release.mk
include build/make/bats.mk
include build/make/k8s-dogu.mk

SCM_V3_RELEASE      ?= scm
SCM_V3_HELM_SOURCE  ?= k8s/helm

# The chart composes the image as "<registry>/<repository>:<tag>", so the dev pull ref (IMAGE_DEV) must be split
SCM_V3_IMAGE_REPOSITORY = $(patsubst $(CES_REGISTRY_HOST)/%,%,$(IMAGE_DEV))

.PHONY: scm-v3-install
scm-v3-install: IMAGE = $(IMAGE_DEV_VERSION)
scm-v3-install: image-import $(BINARY_HELM) ## DoguV3 dev: build+push the image and helm upgrade/install the chart.
	@echo "Installing DoguV3 release '$(SCM_V3_RELEASE)' into namespace '$(NAMESPACE)' (context '$(KUBE_CONTEXT_NAME)')..."
	@echo "  image: $(CES_REGISTRY_HOST)/$(SCM_V3_IMAGE_REPOSITORY):$(VERSION)"
	@$(BINARY_HELM) upgrade --install $(SCM_V3_RELEASE) $(SCM_V3_HELM_SOURCE) \
		--kube-context="$(KUBE_CONTEXT_NAME)" \
		--namespace $(NAMESPACE) \
		--set-string fullnameOverride=$(SCM_V3_RELEASE) \
		--set-string scm.image.registry="$(CES_REGISTRY_HOST)" \
		--set-string scm.image.repository="$(SCM_V3_IMAGE_REPOSITORY)" \
		--set-string scm.image.tag="$(VERSION)" \
		--set-string scm.imagePullPolicy=Always
	@echo "Done. Watch rollout: kubectl -n $(NAMESPACE) get pods -w"

.PHONY: scm-v3-uninstall
scm-v3-uninstall: $(BINARY_HELM) ## DoguV3 dev: uninstall the chart (keeps PVCs).
	@$(BINARY_HELM) --kube-context="$(KUBE_CONTEXT_NAME)" uninstall $(SCM_V3_RELEASE) --namespace $(NAMESPACE) || true
	@echo "Note: PVCs are retained. Delete them manually to reset data:"
	@echo "  kubectl -n $(NAMESPACE) delete pvc -l app.kubernetes.io/name=scm"

# -----------------------------------------------------------------------------
# TEMPORARY DoguV3 publish helper
#
# Builds the scm image and the Helm chart under k8s/helm and pushes both to an OCI registry.
# The packaged chart references exactly the pushed image (values.yaml and chart-patch-tpl.yaml are patched).
#
#   make scm-v3-publish         # image + chart
#   make scm-v3-publish-image   # image only
#   make scm-v3-publish-chart   # chart only (references the image ref below)
#
# dev version for image tag and chart: $(VERSION)-dev.<unix-timestamp>
#   make scm-v3-publish STAGE=development                                #
#
# Resulting artifacts (with defaults):
#   image: staging-registry.cloudogu.com/testing/dogu/v3/images/scm:$(VERSION)
#   chart: oci://staging-registry.cloudogu.com/testing/dogu/v3/charts/scm --version $(VERSION)
#
# -----------------------------------------------------------------------------

SCM_V3_PUBLISH_REGISTRY         ?= staging-registry.cloudogu.com
SCM_V3_PUBLISH_IMAGE_REPOSITORY ?= testing/dogu/v3/images/scm
# OCI namespace for the chart; helm appends the chart name ("scm") itself
SCM_V3_PUBLISH_CHART_NAMESPACE  ?= testing/dogu/v3/charts

# Image tag and chart version. ":=" evaluates the timestamp once, so both are identical.
ifndef SCM_V3_PUBLISH_VERSION
ifeq ($(STAGE),development)
SCM_V3_PUBLISH_VERSION := $(VERSION)-dev.$(shell date +%s)
else
SCM_V3_PUBLISH_VERSION := $(VERSION)
endif
endif

SCM_V3_PUBLISH_IMAGE = $(SCM_V3_PUBLISH_REGISTRY)/$(SCM_V3_PUBLISH_IMAGE_REPOSITORY):$(SCM_V3_PUBLISH_VERSION)
SCM_V3_PUBLISH_DIR = $(TARGET_DIR)/scm-v3-publish
SCM_V3_PUBLISH_CHART_DIR = $(SCM_V3_PUBLISH_DIR)/scm

.PHONY: scm-v3-publish
scm-v3-publish: scm-v3-publish-image scm-v3-publish-chart ## DoguV3: build+push image and chart to SCM_V3_PUBLISH_REGISTRY.

.PHONY: scm-v3-publish-image
scm-v3-publish-image: ## DoguV3: build+push the scm image to SCM_V3_PUBLISH_REGISTRY.
	@echo "Building and pushing image $(SCM_V3_PUBLISH_IMAGE)..."
	@DOCKER_BUILDKIT=1 docker build . -t $(SCM_V3_PUBLISH_IMAGE)
	@docker push $(SCM_V3_PUBLISH_IMAGE)

.PHONY: scm-v3-publish-chart
scm-v3-publish-chart: $(BINARY_HELM) $(BINARY_YQ) ## DoguV3: package the chart (pinned to the published image) and push it.
	@echo "Packaging chart scm:$(SCM_V3_PUBLISH_VERSION) with image $(SCM_V3_PUBLISH_IMAGE)..."
	@rm -rf $(SCM_V3_PUBLISH_DIR)
	@mkdir -p $(SCM_V3_PUBLISH_DIR)
	@cp -r $(SCM_V3_HELM_SOURCE) $(SCM_V3_PUBLISH_CHART_DIR)
	@REGISTRY="$(SCM_V3_PUBLISH_REGISTRY)" REPOSITORY="$(SCM_V3_PUBLISH_IMAGE_REPOSITORY)" TAG="$(SCM_V3_PUBLISH_VERSION)" \
		$(BINARY_YQ) -i '.scm.image.registry = strenv(REGISTRY) | .scm.image.repository = strenv(REPOSITORY) | .scm.image.tag = strenv(TAG)' \
		$(SCM_V3_PUBLISH_CHART_DIR)/values.yaml
	@IMAGE="$(SCM_V3_PUBLISH_IMAGE)" \
		$(BINARY_YQ) -i '.values.images.scm = strenv(IMAGE)' $(SCM_V3_PUBLISH_CHART_DIR)/chart-patch-tpl.yaml
	@CHART_VERSION="$(SCM_V3_PUBLISH_VERSION)" \
		$(BINARY_YQ) -i '.version = strenv(CHART_VERSION)' $(SCM_V3_PUBLISH_CHART_DIR)/Chart.yaml
	@$(BINARY_HELM) lint $(SCM_V3_PUBLISH_CHART_DIR)
	@$(BINARY_HELM) package $(SCM_V3_PUBLISH_CHART_DIR) -d $(SCM_V3_PUBLISH_DIR)
	@echo "Pushing chart to oci://$(SCM_V3_PUBLISH_REGISTRY)/$(SCM_V3_PUBLISH_CHART_NAMESPACE)..."
	@$(BINARY_HELM) push $(SCM_V3_PUBLISH_DIR)/scm-$(SCM_V3_PUBLISH_VERSION).tgz \
		oci://$(SCM_V3_PUBLISH_REGISTRY)/$(SCM_V3_PUBLISH_CHART_NAMESPACE)
	@echo "Done."
	@echo "  image: $(SCM_V3_PUBLISH_IMAGE)"
	@echo "  chart: oci://$(SCM_V3_PUBLISH_REGISTRY)/$(SCM_V3_PUBLISH_CHART_NAMESPACE)/scm --version $(SCM_V3_PUBLISH_VERSION)"
