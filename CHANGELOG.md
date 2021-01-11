# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.12.0-1]
### Changed

- Added the ability to configure the memory limits with `cesapp edit-config`
- Ability to configure the `MaxRamPercentage` and `MinRamPercentage` for the SCM process inside the container via `cesapp edit-conf` (#29)
- SCM-Manager Version update to 2.12.0 ([Changelog](https://github.com/scm-manager/scm-manager/blob/2.12.0/CHANGELOG.md))
