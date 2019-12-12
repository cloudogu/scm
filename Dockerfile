FROM registry.cloudogu.com/official/java:8u222-1
LABEL maintainer="sebastian.sdorra@cloudogu.com"

# scm-server environment
ENV SCM_HOME=/var/lib/scm \
    SCM_REQUIRED_PLUGINS=/opt/scm-server/required-plugins \
    SSL_CERT_FILE=/opt/scm-server/conf/ca-certificates.crt \
    # mark as webapp for nginx
    SERVICE_8080_TAGS="webapp" \
    SERVICE_8080_NAME="scm" \
    SCM_PKG_URL=https://maven.scm-manager.org/nexus/content/repositories/releases/sonia/scm/scm-server/2.0.0-rc1/scm-server-2.0.0-rc1-app.tar.gz

## install scm-server
RUN set -x \
    && apk add --no-cache mercurial jq unzip \
    && curl --fail  -Lks ${SCM_PKG_URL} -o /tmp/scm-server.tar.gz \
    && addgroup -S -g 1000 scm \
    && adduser -S -h /opt/scm-server -s /bin/bash -G scm -u 1000 scm \
    && gunzip /tmp/scm-server.tar.gz \
    && tar -C /opt -xf /tmp/scm-server.tar \
    && cd /tmp \
    # download scm-script-plugin & scm-cas-plugin
    && mkdir ${SCM_REQUIRED_PLUGINS} \
    && curl --fail -Lks https://maven.scm-manager.org/nexus/content/repositories/plugin-releases/sonia/scm/plugins/scm-script-plugin/2.0.0-rc1/scm-script-plugin-2.0.0-rc1.smp -o ${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp \
    && curl --fail -Lks https://maven.scm-manager.org/nexus/content/repositories/plugin-releases/sonia/scm/plugins/scm-cas-plugin/2.0.0-rc1/scm-cas-plugin-2.0.0-rc1.smp -o ${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp \
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

HEALTHCHECK CMD [ $(doguctl healthy scm; echo $?) == 0 ]

# start scm
CMD ["/startup.sh"]
