FROM registry.cloudogu.com/official/java:11.0.5-4
LABEL maintainer="sebastian.sdorra@cloudogu.com"

# scm-server environment
ENV SCM_HOME=/var/lib/scm \
    SCM_REQUIRED_PLUGINS=/opt/scm-server/required-plugins \
    SSL_CERT_FILE=/opt/scm-server/conf/ca-certificates.crt \
    # mark as webapp for nginx
    SERVICE_8080_TAGS="webapp" \
    SERVICE_8080_NAME="scm" \
    SCM_PKG_URL=https://packages.scm-manager.org/repository/releases/sonia/scm/packaging/unix/2.20.0/unix-2.20.0.tar.gz \
    SCM_PKG_SHA256=20c917d628db7bb4401efa0c4e64b26313a2eda3fae9eb15d265d469088c690d \
    SCM_CODE_EDITOR_PLUGIN_SHA256=c5d80fa7ab9723fd3d41b8422ec83433bc3376f59850d97a589fe093f5ca8989 \
    SCM_SCRIPT_PLUGIN_SHA256=4765df9331136df8adc2fb9a4f3a302914ca0a31981b854cac1cc9d2af03e355 \
    SCM_CAS_PLUGIN_SHA256=b6e8f960cdc7f81f73da4acae1b53c87e0855e418f7145c2cd37b98e0e94f008

## install scm-server
RUN set -x \
    && apk add --no-cache graphviz ttf-dejavu mercurial jq unzip \
    && curl --fail  -Lks ${SCM_PKG_URL} -o /tmp/scm-server.tar.gz \
    && echo "${SCM_PKG_SHA256} */tmp/scm-server.tar.gz" | sha256sum -c - \
    && addgroup -S -g 1000 scm \
    && adduser -S -h /opt/scm-server -s /bin/bash -G scm -u 1000 scm \
    && gunzip /tmp/scm-server.tar.gz \
    && tar -C /opt -xf /tmp/scm-server.tar \
    && cd /tmp \
    # download scm-script-plugin & scm-cas-plugin
    && mkdir ${SCM_REQUIRED_PLUGINS} \
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-code-editor-plugin/1.0.0/scm-code-editor-plugin-1.0.0.smp -o ${SCM_REQUIRED_PLUGINS}/scm-code-editor-plugin.smp \
    && echo "${SCM_CODE_EDITOR_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-code-editor-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-script-plugin/2.2.0/scm-script-plugin-2.2.0.smp -o ${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp \
    && echo "${SCM_SCRIPT_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-cas-plugin/2.3.0/scm-cas-plugin-2.3.0.smp -o ${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp \
    && echo "${SCM_CAS_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp" | sha256sum -c - \
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

HEALTHCHECK CMD doguctl healthy scm || exit 1

# start scm
CMD ["/startup.sh"]
