FROM registry.cloudogu.com/official/java:11.0.11-2
LABEL maintainer="sebastian.sdorra@cloudogu.com"

# scm-server environment
ENV SCM_HOME=/var/lib/scm \
    SCM_REQUIRED_PLUGINS=/opt/scm-server/required-plugins \
    SSL_CERT_FILE=/opt/scm-server/conf/ca-certificates.crt \
    CES_TOKEN_HEADER=X-CES-Token \
    CES_TOKEN_CONFIGURATION_KEY=serviceaccount_token \
    # mark as webapp for nginx
    SERVICE_8080_TAGS="webapp" \
    SERVICE_8080_NAME="scm" \
    SCM_PKG_URL=https://packages.scm-manager.org/repository/releases/sonia/scm/packaging/unix/2.23.0/unix-2.23.0.tar.gz \
    SCM_PKG_SHA256=78fc3617c828dc465524ab27faacda492c397afa82df828897a3850cffa5d354 \
    SCM_CODE_EDITOR_PLUGIN_VERSION=1.0.0 \
    SCM_CODE_EDITOR_PLUGIN_SHA256=c5d80fa7ab9723fd3d41b8422ec83433bc3376f59850d97a589fe093f5ca8989 \
    SCM_SCRIPT_PLUGIN_VERSION=2.3.0 \
    SCM_SCRIPT_PLUGIN_SHA256=07de0736ce324d2154b199b306c156ff74ecf7816638f2c5307bd3cdbd3da7f6 \
    SCM_CAS_PLUGIN_VERSION=2.3.1 \
    SCM_CAS_PLUGIN_SHA256=c73674301e4f1f41a901e90b3f1ffd2895426e4b926fd88ac0d26285ef7368c4 \
    SCM_CES_PLUGIN_VERSION=1.0.1 \
    SCM_CES_PLUGIN_SHA256=da3b73f8eca4dced6471edcbad53545a8d0e13441e729bac641d337b2fcf1f06

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
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-code-editor-plugin/${SCM_CODE_EDITOR_PLUGIN_VERSION}/scm-code-editor-plugin-${SCM_CODE_EDITOR_PLUGIN_VERSION}.smp -o ${SCM_REQUIRED_PLUGINS}/scm-code-editor-plugin.smp \
    && echo "${SCM_CODE_EDITOR_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-code-editor-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-script-plugin/${SCM_SCRIPT_PLUGIN_VERSION}/scm-script-plugin-${SCM_SCRIPT_PLUGIN_VERSION}.smp -o ${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp \
    && echo "${SCM_SCRIPT_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-cas-plugin/${SCM_CAS_PLUGIN_VERSION}/scm-cas-plugin-${SCM_CAS_PLUGIN_VERSION}.smp -o ${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp \
    && echo "${SCM_CAS_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-ces-plugin/${SCM_CES_PLUGIN_VERSION}/scm-ces-plugin-${SCM_CES_PLUGIN_VERSION}.smp -o ${SCM_REQUIRED_PLUGINS}/scm-ces-plugin.smp \
    && echo "${SCM_CES_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-ces-plugin.smp" | sha256sum -c - \
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
