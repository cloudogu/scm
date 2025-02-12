# SCM-Manager Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.7.1-2] 2025-02-12
### Removed
- uplicate configuration keys from dogu.json [#104] 

## [3.7.1-1] 2025-01-28
### Changed
- Upgrade SCM-Manager to version 3.7.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.7.1/CHANGELOG.md))

## [3.7.0-1] 2025-01-24
### Changed
- Update Java base image to 17.0.13-1 [#102]
- Upgrade SCM-Manager to version 3.7.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.7.0/CHANGELOG.md))

## [3.6.1-1] 2025-01-17
### Changed
- Upgrade SCM-Manager to version 3.6.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.6.1/CHANGELOG.md))

## [3.6.0-1] 2024-12-06
### Changed
- Upgrade SCM-Manager to version 3.6.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.6.0/CHANGELOG.md))
 
## [3.5.0-2] 2024-11-11
### Fixed
- Read local configs from DoguConfig instead of GlobalConfig [#100]

## [3.5.0-1] 2024-10-10
### Changed
- Upgrade SCM-Manager to version 3.5.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.5.0/CHANGELOG.md))
 
## [3.4.1-3] 2024-09-24
### Changed
- Use doguctl for accessing dogu and global EcoSystem config [#95]
- Check if dogus are installed from mounted dogu registry in multinode EcoSystems [#95]
- Extract config and dogu registry calls into shared lib package for init scripts
### Added
- Explicit service account for CAS, which is necessary for CAS to work in multinode [#95]
- Version constraint on CAS, as a specific version is required for explicit service accounts to work [#95]
- Optional dependencies for dogus SCM provides automatic integration for [#95]
    - This is necessary for multinode EcoSystems to mount the required dogu registries in SCM
- Add configuration `install_smeagol_plugin` to determine the plugin installation at startup and omit smeagol as optional dependency because this would be a dependency cycle.

## [3.4.1-2] 2024-09-19
### Changed
- Relicense to AGPL-3.0-only

## [3.4.1-1] 2024-08-29
### Changed
- Upgrade SCM-Manager to version 3.4.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.4.1/CHANGELOG.md)) 

## [3.4.0-1] 2024-08-20
### Changed
- Upgrade SCM-Manager to version 3.4.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.4.0/CHANGELOG.md))

## [3.3.0-3] 2024-08-06
### Fixed
- Upgrade base image to version 17.0.12-1

## [3.3.0-2] 2024-07-15
### Fixed 
- Environment variable names for caching configuration

## [3.3.0-1] 2024-07-05
### Changed
- Upgrade SCM-Manager to version 3.3.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.3.0/CHANGELOG.md))

## [3.2.2-1] 2024-06-24
### Changed
- Upgrade SCM-Manager to version 3.2.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.2.2/CHANGELOG.md))

## [3.2.1-1] 2024-06-03
### Changed
- Upgrade SCM-Manager to version 3.2.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.2.1/CHANGELOG.md))

## [3.2.0-1] 2024-05-24
### Changed
- Upgrade SCM-Manager to version 3.2.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.2.0/CHANGELOG.md))
 
## [3.1.0-1] 2024-04-09
### Changed
- Upgrade SCM-Manager to version 3.1.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.1.0/CHANGELOG.md))

## [3.0.4-1] 2024-03-12
### Changed
- Upgrade SCM-Manager to version 3.0.4 ([Changelog](https://github.com/scm-manager/scm-manager/blob/3.0.4/CHANGELOG.md))
- Adapt to new mail plugin API ([#87](https://github.com/cloudogu/scm/pull/87))

## [3.0.0-2] 2024-01-30
### Fixed
- Container restart strategy

## [3.0.0-1] 2024-01-30
### Changed
- Upgrade SCM-Manager to version 3.0.0 and adjust config (Changelog for [3.0.0](https://github.com/scm-manager/scm-manager/blob/2.48.0/CHANGELOG.md) and [3.0.0](https://github.com/scm-manager/scm-manager/blob/3.0.0/CHANGELOG.md))

## [2.48.3-2] 2024-01-19
### Changed
- Re-added restart strategy with exit code 42

## [2.48.3-1] 2023-12-08
### Changed
- Upgrade SCM-Manager to version 2.48.3 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.48.3/CHANGELOG.md))

## [2.48.2-1] 2023-12-08
### Changed
- Upgrade SCM-Manager to version 2.48.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.48.2/CHANGELOG.md))

## [2.48.1-1] 2023-11-17
### Changed
- Upgrade SCM-Manager to version 2.48.1 (Changelog for [2.48.0](https://github.com/scm-manager/scm-manager/blob/2.48.0/CHANGELOG.md) and [2.48.1](https://github.com/scm-manager/scm-manager/blob/2.48.1/CHANGELOG.md))
- Remove restart strategy exit code 42

## [2.47.0-1] 2023-10-12
### Added
- Configuration option to configure either Redmine or EasyRedmine if both dogus are installed ([#83](https://github.com/cloudogu/scm/pull/83))
### Changed
- Upgrade SCM-Manager to version 2.47.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.47.0/CHANGELOG.md))

## [2.46.5-1] 2025-01-17
### Changed
- Upgrade SCM-Manager to version 2.46.5 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.46.5/CHANGELOG.md))

## [2.46.4-1] 2024-06-24
### Changed
- Upgrade SCM-Manager to version 2.46.4 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.46.4/CHANGELOG.md))

## [2.46.3-1] 2024-05-29
### Changed
- Upgrade SCM-Manager to version 2.46.3 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.46.3/CHANGELOG.md))

## [2.46.2-1] 2024-04-03
### Changed
- Upgrade SCM-Manager to version 2.46.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.46.2/CHANGELOG.md))

## [2.46.0-1] 2023-08-25
### Changed
- Upgrade SCM-Manager to version 2.46.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.46.0/CHANGELOG.md))

## [2.45.1-1] 2023-07-24
### Changed
- Use Java 17 base image ([#77](https://github.com/cloudogu/scm/pull/77))

## [2.44.3-1] 2023-08-31
### Changed
- Upgrade SCM-Manager to version 2.44.3 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.44.3/CHANGELOG.md))

## [2.44.2-3] 2023-07-21
### Fixed
- Integration tests after upgrading CAS ([#78](htts://github.com/cloudogu/scm/pull/78))
- Volume for temporary folder ([#82](https://github.com/cloudogu/scm/pull/82))

## [2.44.2-2] 2023-06-28
### Added
- [#72] Configuration options for resource requirements
- [#72] Defaults for CPU and memory requests
- Configuration options for store caches ([#75](https://github.com/cloudogu/scm/pull/75))
### Removed
- Default Plugins ([#70](https://github.com/cloudogu/scm/pull/70))


## [2.44.2-1] 2023-06-23
### Changed
- Upgrade SCM-Manager to version 2.44.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.44.2/CHANGELOG.md))

## [2.44.1-1] 2023-06-13
### Changed
- Upgrade SCM-Manager to version 2.44.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.44.1/CHANGELOG.md))

## [2.44.0-1] 2023-06-09
### Changed
- Upgrade SCM-Manager to version 2.44.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.44.0/CHANGELOG.md))

## [2.43.1-1] 2023-05-12
### Changed
- Upgrade SCM-Manager to version 2.43.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.43.1/CHANGELOG.md))

## [2.43.0-1] 2023-04-13
### Changed
- Upgrade SCM-Manager to version 2.43.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.43.0/CHANGELOG.md))

## [2.42.3-1] 2023-03-13
### Changed
- Upgrade SCM-Manager to version 2.42.3 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.42.3/CHANGELOG.md))

## [2.42.2-1] 2023-03-03
### Changed
- Upgrade SCM-Manager to version 2.42.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.42.2/CHANGELOG.md))

## [2.42.1-1] 2023-02-16
### Changed
- Upgrade SCM-Manager to version 2.42.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.42.1/CHANGELOG.md))

## [2.41.1-1] 2023-02-16
### Changed
- Upgrade SCM-Manager to version 2.41.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.41.1/CHANGELOG.md))

## [2.41.0-1] 2023-01-18
### Changed
- Upgrade SCM-Manager to version 2.41.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.41.0/CHANGELOG.md))

## [2.40.0-1] 2022-11-23
### Changed
- Upgrade SCM-Manager to version 2.40.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.40.0/CHANGELOG.md))

## [2.39.1-1] 2022-10-12
### Changed
- Upgrade SCM-Manager to version 2.39.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.39.1/CHANGELOG.md))

## [2.39.0-1] 2022-09-16
### Changed
- Upgrade SCM-Manager to version 2.39.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.39.0/CHANGELOG.md))

## [2.38.1-1] 2022-08-09
### Changed
- Upgrade SCM-Manager to version 2.38.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.38.1/CHANGELOG.md))

## [2.37.0-1] 2022-06-29
### Changed
- Upgrade SCM-Manager to version 2.37.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.37.0/CHANGELOG.md))

## [2.36.0-1] 2022-06-16
### Changed
- Upgrade SCM-Manager to version 2.36.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.36.0/CHANGELOG.md))

## [2.35.0-1] 2022-06-03
### Changed
- Upgrade SCM-Manager to version 2.35.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.35.0/CHANGELOG.md))

## [2.34.0-1] 2022-05-13
### Added
- Configuration of login info and alerts url via cesapp ([#57](https://github.com/cloudogu/scm/pull/57))
### Changed
- Upgrade SCM-Manager to version 2.34.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.34.0/CHANGELOG.md))

## [2.33.0-1] 2022-05-10
### Added
- Configuration of feedback url via cesapp ([#54](https://github.com/cloudogu/scm/pull/54))
### Changed
- Upgrade SCM-Manager to version 2.33.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.33.0/CHANGELOG.md))

## [2.32.2-1] 2022-03-23
### Changed
- Set explicit configuration for EasyRedmine
- Upgrade SCM-Manager to version 2.32.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.32.2/CHANGELOG.md))

## [2.32.1-1] 2022-03-10
### Changed
- Upgrade SCM-Manager to version 2.32.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.32.1/CHANGELOG.md))

## [2.32.0-1] 2022-03-10
### Changed
- Upgrade SCM-Manager to version 2.32.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.32.0/CHANGELOG.md))

## [2.31.0-5] 2022-05-10
### Added
- Configuration of login info and alerts url via cesapp ([#57](https://github.com/cloudogu/scm/pull/57)

## [2.31.0-4] 2022-04-05
### Changed
- Upgrade java base image to 11.0.14-3; #56

### Fixed
- Upgrade zlib to fix CVE-2018-25032; #56

## [2.31.0-3] 2022-03-30
### Added
- Configuration of plugin center authentication url via cesapp ([#53](https://github.com/cloudogu/scm/pull/53))

## [2.31.0-2] 2022-03-14
### Changed
- Removed gravatar plugin from default plugins list

## [2.31.0-1] 2022-02-18
### Changed
- Upgrade SCM-Manager to version 2.31.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.31.0/CHANGELOG.md))

## [2.30.1-1] 2022-02-08
### Changed
- Updated to latest base image ([#50](https://github.com/cloudogu/scm/pull/50))
- Upgrade SCM-Manager to version 2.30.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.30.1/CHANGELOG.md))

## [2.30.0-1] 2022-01-24
### Changed
- Upgrade SCM-Manager to version 2.30.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.30.0/CHANGELOG.md))

## [2.29.1-2] 2022-01-18
### Re-Release of SCM-Manager version 2.29.1 without further changes

## [2.29.1-1] 2022-01-17
### Changed
- Upgrade SCM-Manager to version 2.29.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.29.1/CHANGELOG.md))

## [2.29.0-1] 2022-01-12
### Changed
- Upgrade SCM-Manager to version 2.29.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.29.0/CHANGELOG.md))

## [2.28.0-1] 2021-12-22
### Changed
- Upgrade SCM-Manager to version 2.28.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.28.0/CHANGELOG.md))

## [2.27.4-1] 2021-12-20
### Changed
- Upgrade SCM-Manager to version 2.27.4 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.27.4/CHANGELOG.md))

## [2.27.3-1] 2021-12-15
### Changed
- Upgrade SCM-Manager to version 2.27.3 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.27.3/CHANGELOG.md))

## [2.27.2-1] 2021-11-19
### Changed
- Upgrade SCM-Manager to version 2.27.2 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.27.2/CHANGELOG.md))

## [2.27.1-1] 2021-11-18
### Changed
- Upgrade SCM-Manager to version 2.27.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.27.1/CHANGELOG.md))

## [2.27.0-1] 2021-11-16
### Added
- Configuration of Gotenberg plugin ([#48](https://github.com/cloudogu/scm/pull/48))
### Changed
- Upgrade SCM-Manager to version 2.27.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.27.0/CHANGELOG.md))

## [2.26.0-1] 2021-11-04
### Changed
- Upgrade SCM-Manager to version 2.26.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.26.0/CHANGELOG.md))

## [2.25.0-1] 2021-10-21
### Changed
- Upgrade SCM-Manager to version 2.25.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.25.0/CHANGELOG.md))

## [2.24.0-1] 2021-10-08
### Added
- Scripts to create and remove service accounts ([#43](https://github.com/cloudogu/scm/pull/43))
### Changed
- Upgrade SCM-Manager to version 2.24.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.24.0/CHANGELOG.md))
- Update CAS plugin to version 2.4.0 ([Changelog](https://github.com/scm-manager/scm-cas-plugin/blob/2.4.0/CHANGELOG.md))

## [2.23.0-1] 2021-09-10
### Changed
- Upgrade SCM-Manager to version 2.23.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.23.0/CHANGELOG.md))

## [2.22.0-1] 2021-07-30
### Changed
- Upgrade SCM-Manager to version 2.22.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.22.0/CHANGELOG.md))

### Added
- Configuration option for workdir cache ([#41](https://github.com/cloudogu/scm/pull/41))

## [2.21.0-1] 2021-07-21
### Changed
- Configure CAS proxy chains to allow all dogus within fqdn range ([#39](https://github.com/cloudogu/scm/pull/39))
- Expose temporary directory for server ([#40](https://github.com/cloudogu/scm/pull/40))
- Update script plugin to version 2.2.1 ([Changelog](https://github.com/scm-manager/scm-script-plugin/blob/2.2.1/CHANGELOG.md))
- Upgrade SCM-Manager to version 2.21.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.21.0/CHANGELOG.md))

### Fixed
- Integration-Tests for SCM-Manager UI for new header

## [2.20.1-3] 2021-06-16
### Changed
- Removed gravatar plugin from default plugins list

## [2.20.1-2]
### Changed
- Update to new Alpine base image to update Expat library

## [2.20.1-1]
### Changed
- Upgrade SCM-Manager to version 2.20.0 ([Changelog](https://scm-manager.org/download/2.20.1/#changelog))

## [2.20.0-1] 2021-06-16
### Changed
- Upgrade SCM-Manager to version 2.20.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.20.0/CHANGELOG.md))
- Upgrade CAS-Plugin to version 2.3.0 ([Changelog](https://github.com/scm-manager/scm-cas-plugin/blob/2.3.0/CHANGELOG.md))

## [2.19.1-1] 2021-06-10
### Changed
- Upgrade SCM-Manager to version 2.19.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.19.1/CHANGELOG.md))

## [2.19.0-1] 2021-06-04
### Added
- Add scm-ssl-context-plugin to default plugins
- Add scm-repository-mirror-plugin to default plugins

### Changed
- Upgrade SCM-Manager to version 2.19.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.19.0/CHANGELOG.md))


## [2.18.0-1] 2021-05-05
### Changed
- Upgrade SCM-Manager to version 2.18.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.18.0/CHANGELOG.md))

## [2.17.1-1] 2021-04-26
### Changed
- Upgrade SCM-Manager to version 2.17.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.17.1/CHANGELOG.md))

## [2.17.0-1] 2021-04-22
### Added
- Old admin group will be deleted if value is changed in etcd ([#35](https://github.com/cloudogu/scm/pull/35))

### Changed
- Upgrade SCM-Manager to version 2.17.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.17.0/CHANGELOG.md))

### Fixed
- Missing graphiz for PlantUML plugin ([#36](https://github.com/cloudogu/scm/pull/36))

## [2.16.0-1] 2021-03-26
### Changed
- Upgrade SCM-Manager to version 2.16.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.16.0/CHANGELOG.md))

## [2.15.1-1] 2021-03-17
### Added
- Install and configure Smeagol plugin ([#34](https://github.com/cloudogu/scm/pull/34))

### Changed
- Upgrade SCM-Manager to version 2.15.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.15.1/CHANGELOG.md))

## [2.15.0-1] 2021-03-15
### Changed
- Upgrade SCM-Manager to version 2.15.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.15.0/CHANGELOG.md))

## [2.14.1-1] 2021-03-03
### Changed
- Upgrade SCM-Manager to version 2.14.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.14.1/CHANGELOG.md))

## [2.14.0-1] 2021-02-11
### Added
- Configuration for loglevel of SCM-Manager core classes ([#33](https://github.com/cloudogu/scm/pull/33))

### Changed
- Upgrade SCM-Manager version to 2.14.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.14.0/CHANGELOG.md))

## [2.13.0-1] 2021-01-29
### Added
- Add font ttf-dejavu ([#32](https://github.com/cloudogu/scm/pull/32))
- Add SCM-MarkDown-PlantUML-Plugin as default plugin

### Changed
- Upgrade SCM-Manager version to 2.13.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.13.0/CHANGELOG.md))

## [2.12.0-1] 2020-12-18
### Changed

- Added the ability to configure the memory limits with `cesapp edit-config`
- Ability to configure the `MaxRamPercentage` and `MinRamPercentage` for the SCM process inside the container via `cesapp edit-conf` (#29)
- SCM-Manager Version update to 2.12.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.12.0/CHANGELOG.md))
