# scm - Dogu v3

## Introduction
Dogu v3 support for the SCM Manager has been added in the following areas:

### resources/dogu_v3
- Adds Dogu v3-specific startup and service account hook scripts.
- Prepares the `dogu.json` layout expected by `doguctl` in Kubernetes.
- Ensures that persistent SCM directories belong to the SCM runtime user.
- Customizes the creation, deletion, existence checking, and rotation management of service accounts for the Dogu v3 service account sidecar.

### resources/var/tmp/scm/init.script.d
- Customizes the SCM initialization scripts for the Dogu v3 runtime environment.
- Ensures that the SCM configuration is available to the ecosystem at startup.
- Contains configuration management for integrations, which are now deployed within the SCM deployment.

### scm/k8s/helm
- Implements the Helm-based Dogu v3 deployment for SCM.
- Defines Kubernetes resources such as StatefulSet, Services, Exposure, configuration, network policies and service account integration.
- Adds chart values and metadata for SCM image configuration, persistence, security contexts, resources and Dogu configuration.
- Bundles Gotenberg as part of the SCM Helm release, eliminating the need for a separate Gotenberg Dogu.

In addition, the `Makefile` and `Dockerfile` files have been updated to support the building, publishing, installing, and uninstalling of the Dogu v3 variant.