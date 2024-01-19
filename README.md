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
### What is the Cloudogu EcoSystem?
The Cloudogu EcoSystem is an open platform, which lets you choose how and where your team creates great software. Each service or tool is delivered as a Dogu, a Docker container. Each Dogu can easily be integrated in your environment just by pulling it from our registry. We have a growing number of ready-to-use Dogus, e.g. SCM-Manager, Jenkins, Nexus, SonarQube, Redmine and many more. Every Dogu can be tailored to your specific needs. Take advantage of a central authentication service, a dynamic navigation, that lets you easily switch between the web UIs and a smart configuration magic, which automatically detects and responds to dependencies between Dogus. The Cloudogu EcoSystem is open source and it runs either on-premises or in the cloud. The Cloudogu EcoSystem is developed by Cloudogu GmbH under [MIT License](https://cloudogu.com/license.html).

### How to get in touch?
Want to talk to the Cloudogu team? Need help or support? There are several ways to get in touch with us:

* [Website](https://cloudogu.com)
* [myCloudogu-Forum](https://forum.cloudogu.com/topic/34?ctx=1)
* [Email hello@cloudogu.com](mailto:hello@cloudogu.com)

---
&copy; 2020 Cloudogu GmbH - MADE WITH :heart:&nbsp;FOR DEV ADDICTS. [Legal notice / Impressum](https://cloudogu.com/imprint.html)


## Release
To release a new version of the SCM-Manager Dogu you need to:
- Adjust the changelog file and commit the changes
- Create a new branch with the prefix `release/{{new_version_number}}-{{counter}}` and push it

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
