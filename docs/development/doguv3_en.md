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

## Integrations with other dogus

In classic and multinode Dogu v2 setups, SCM detects on its own whether other dogus are installed
and installs and configures the matching plugins. With Dogu v3 this is no longer possible.
Therefore every integration is enabled explicitly via `scm.integrations` in the `values.yaml`:

```yaml
scm:
  integrations:
    cockpit:
      enabled: true          # scm-cockpit-legacy-plugin (SCM widget in cockpit)
    jenkins:
      enabled: true          # scm-jenkins-plugin, scm-ci-plugin
    redmine:
      enabled: true          # scm-redmine-plugin
      type: EASY_REDMINE     # REDMINE | EASY_REDMINE
    jira:
      enabled: false         # scm-jira-plugin
```

The chart renders one environment variable `SCM_INTEGRATION_<NAME>_ENABLED` per integration plus
`SCM_INTEGRATION_REDMINE_TYPE`. The init scripts query the integrations via
`EcoSystem.isIntegrationEnabled(<dogu>)` and `EcoSystem.getRedmineIntegration()`:

- If the environment variable is set, its value is used.
- If it is missing (classic, multinode v2), the scripts check whether the dogu is installed, as before.

Changes take effect on the next SCM start. Disabling an integration does not uninstall plugins.

Gotenberg is not part of `scm.integrations`; it is a subchart controlled by `gotenberg.enabled`.
