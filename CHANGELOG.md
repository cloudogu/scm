# SCM-Manager Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.21.0-1]
### Changed
- Configure CAS proxy chains to allow all dogus within fqdn range ([#39](https://github.com/cloudogu/scm/pull/39))
- Expose temporary directory for server ([#40](https://github.com/cloudogu/scm/pull/40))
- Update script plugin to version 2.2.1 ([Changelog](https://github.com/scm-manager/scm-script-plugin/blob/2.2.1/CHANGELOG.md))
- Upgrade SCM-Manager to version 2.21.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.21.0/CHANGELOG.md))

## [2.20.0-1]
### Changed
- Upgrade SCM-Manager to version 2.20.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.20.0/CHANGELOG.md))
- Upgrade CAS-Plugin to version 2.3.0 ([Changelog](https://github.com/scm-manager/scm-cas-plugin/blob/2.3.0/CHANGELOG.md))

## [2.19.1-1]
### Changed
- Upgrade SCM-Manager to version 2.19.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.19.1/CHANGELOG.md))

## [2.19.0-1]
### Added
- Add scm-ssl-context-plugin to default plugins
- Add scm-repository-mirror-plugin to default plugins

### Changed
- Upgrade SCM-Manager to version 2.19.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.19.0/CHANGELOG.md))


## [2.18.0-1]
### Changed
- Upgrade SCM-Manager to version 2.18.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.18.0/CHANGELOG.md))

## [2.17.1-1]
### Changed
- Upgrade SCM-Manager to version 2.17.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.17.1/CHANGELOG.md))

## [2.17.0-1]
### Added
- Old admin group will be deleted if value is changed in etcd ([#35](https://github.com/cloudogu/scm/pull/35))

### Changed
- Upgrade SCM-Manager to version 2.17.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.17.0/CHANGELOG.md))

### Fixed
- Missing graphiz for PlantUML plugin ([#36](https://github.com/cloudogu/scm/pull/36))

## [2.16.0-1]
### Changed
- Upgrade SCM-Manager to version 2.16.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.16.0/CHANGELOG.md))

## [2.15.1-1]
### Added
- Install and configure Smeagol plugin ([#34](https://github.com/cloudogu/scm/pull/34))

### Changed
- Upgrade SCM-Manager to version 2.15.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.15.1/CHANGELOG.md))

## [2.15.0-1]
### Changed
- Upgrade SCM-Manager to version 2.15.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.15.0/CHANGELOG.md))

## [2.14.1-1]
### Changed
- Upgrade SCM-Manager to version 2.14.1 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.14.1/CHANGELOG.md))

## [2.14.0-1]
### Added
- Configuration for loglevel of SCM-Manager core classes ([#33](https://github.com/cloudogu/scm/pull/33))

### Changed
- Upgrade SCM-Manager version to 2.14.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.14.0/CHANGELOG.md))

## [2.13.0-1]
### Added
- Add font ttf-dejavu ([#32](https://github.com/cloudogu/scm/pull/32))
- Add SCM-MarkDown-PlantUML-Plugin as default plugin

### Changed
- Upgrade SCM-Manager version to 2.13.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.13.0/CHANGELOG.md))

## [2.12.0-1]
### Changed

- Added the ability to configure the memory limits with `cesapp edit-config`
- Ability to configure the `MaxRamPercentage` and `MinRamPercentage` for the SCM process inside the container via `cesapp edit-conf` (#29)
- SCM-Manager Version update to 2.12.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.12.0/CHANGELOG.md))
