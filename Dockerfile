FROM registry.cloudogu.com/official/java:8u212-1
LABEL maintainer="sebastian.sdorra@cloudogu.com"

# scm-server environment
ENV SCM_HOME=/var/lib/scm \
    SSL_CERT_FILE=/opt/scm-server/conf/ca-certificates.crt \
    # mark as webapp for nginx
    SERVICE_8080_TAGS="webapp" \
    SERVICE_8080_NAME="scm" \
    SCM_PKG_URL=https://oss.cloudogu.com/jenkins/job/scm-manager/job/scm-manager-2.x/job/2.0.0-m3/lastSuccessfulBuild/artifact/scm-server/target/scm-server-app.tar.gz

COPY utils /opt/utils

## install scm-server
RUN set -x \
    && apk add --no-cache mercurial jq unzip \
    && curl --fail  -Lks ${SCM_PKG_URL} -o /tmp/scm-server.tar.gz \
    && addgroup -S -g 1000 scm \
    && adduser -S -h /opt/scm-server -s /bin/bash -G scm -u 1000 scm \
    && gunzip /tmp/scm-server.tar.gz \
    && tar -C /opt -xf /tmp/scm-server.tar \
    && cd /tmp \
    # install scm-script-plugin & scm-cas-plugin
    && unzip /opt/scm-server/var/webapp/scm-webapp.war WEB-INF/plugins/plugin-index.xml \
    && curl --fail -Lks https://oss.cloudogu.com/jenkins/job/scm-manager/job/plugins/job/scm-script-plugin/job/develop/lastSuccessfulBuild/artifact/target/scm-script-plugin-2.0.0-SNAPSHOT.smp -o /tmp/WEB-INF/plugins/scm-script-plugin-2.0.0-SNAPSHOT.smp \
    && curl --fail -Lks https://oss.cloudogu.com/jenkins/job/scm-manager/job/plugins/job/scm-cas-plugin/job/develop/lastSuccessfulBuild/artifact/target/scm-cas-plugin-2.0.0-SNAPSHOT.smp -o /tmp/WEB-INF/plugins/scm-cas-plugin-2.0.0-SNAPSHOT.smp \
    && zip -d /opt/scm-server/var/webapp/scm-webapp.war WEB-INF/plugins/plugin-index.xml \
    && java -cp /opt/utils AddPluginToIndex /tmp/WEB-INF/plugins/plugin-index.xml /tmp/WEB-INF/plugins/scm-script-plugin-2.0.0-SNAPSHOT.smp /tmp/WEB-INF/plugins/scm-cas-plugin-2.0.0-SNAPSHOT.smp \
    && zip -u /opt/scm-server/var/webapp/scm-webapp.war WEB-INF/plugins/* \
    # cleanup
    && rm -rf /tmp/* /var/cache/apk/* \
    # set mercurial system ca-certificates
    # see https://github.com/cloudogu/ecosystem/issues/193
    && mkdir /etc/mercurial \
    && printf "[web]\ncacerts = /opt/scm-server/conf/ca-certificates.crt\n" > /etc/mercurial/hgrc

# copy resources after package installation, that we can override package defaults
COPY ./resources /

# set permissions
RUN mkdir -p ${SCM_HOME} \
    && chown scm:scm ${SCM_HOME} \
    && chown -R scm:scm /opt/scm-server

# run scm-manager
WORKDIR ${SCM_HOME}

VOLUME ${SCM_HOME}

EXPOSE 8080 2222

USER scm

# start scm
CMD ["/startup.sh"]
