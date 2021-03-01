# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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