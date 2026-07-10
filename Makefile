MAKEFILES_VERSION=10.10.0
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
