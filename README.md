<img src="https://cloudogu.com/images/dogus/scm-manager.png" alt="scm-manager logo" height="100px">


[![GitHub license](https://img.shields.io/github/license/cloudogu/scm.svg)](https://github.com/cloudogu/scm/blob/develop/LICENSE)
[![GitHub release](https://img.shields.io/github/release/cloudogu/scm.svg)](https://github.com/cloudogu/scm/releases)

# SCM-Manager Dogu

## About this Dogu

**Name:** official/scm

**Description:** [SCM-Manager](https://www.scm-manager.org/) is a web application for managing Git, Mercurial and Subversion repositories.

**Website:** https://www.scm-manager.org

**Docs:** https://scm-manager.org/docs/latest/de/

**Dependencies:** cas, nginx, postfix

## Installation Ecosystem
```
cesapp install official/scm

cesapp start scm
```

---

## Release
To release a new version of the SCM-Manager Dogu you need to:
- Create a new branch with the prefix `release/{{new_version_number}}-{{counter}}`
- Adjust the changelog file and commit the changes
- Push the changed release branch to the server

Afterwards the automatic release flow will:
- Update the scm-manager package and related sha256 hash in the `Dockerfile`
- Update the versions in the `dogu.json`
- Deploy the new dogu to the namespaces `official` after the build and test steps are successful
- Add the new release to the [SCM-Manager website](https://scm-manager.org/download/#ces)

## Build Dogu with unreleased SCM-Manager

To build the dogu with an unreleased version of SCM-Manager you need to:
- Build the unix package of SCM-Manager with `./gradlew -PenablePackaging :scm-packaging:unix:distribution`
- Copy the resulting `scm-packaging/unix/build/libs/unix-{{version}}.tar.gz` to the root folder of the dogu
- Adjust the `Dockerfile` to use the new version:
  - Add a `COPY` command to copy the unix package into the image (`COPY unix-{{version}}.tar.gz /tmp/scm-server.tar.gz`)
  - Uncomment the `curl` to download `scm-server.tar.gz` from packages.scm-manager.org
  - Uncomment the following `echo ...` line with the checksum of the unix package

Now you can build the dogu as always with `cesapp build .`

---

## What is the Cloudogu EcoSystem?
The Cloudogu EcoSystem is an open platform, which lets you choose how and where your team creates great software. Each service or tool is delivered as a Dogu, a Docker container. Each Dogu can easily be integrated in your environment just by pulling it from our registry.

We have a growing number of ready-to-use Dogus, e.g. SCM-Manager, Jenkins, Nexus Repository, SonarQube, Redmine and many more. Every Dogu can be tailored to your specific needs. Take advantage of a central authentication service, a dynamic navigation, that lets you easily switch between the web UIs and a smart configuration magic, which automatically detects and responds to dependencies between Dogus.

The Cloudogu EcoSystem is open source and it runs either on-premises or in the cloud. The Cloudogu EcoSystem is developed by Cloudogu GmbH under [AGPL-3.0-only](https://spdx.org/licenses/AGPL-3.0-only.html).

## License
Copyright © 2020 - present Cloudogu GmbH
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, version 3.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program. If not, see https://www.gnu.org/licenses/.
See [LICENSE](LICENSE) for details.


---
MADE WITH :heart:&nbsp;FOR DEV ADDICTS. [Legal notice / Imprint](https://cloudogu.com/en/imprint/?mtm_campaign=ecosystem&mtm_kwd=imprint&mtm_source=github&mtm_medium=link)

