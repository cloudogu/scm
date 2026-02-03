FROM registry.cloudogu.com/official/java:17.0.18-1
LABEL maintainer="sebastian.sdorra@cloudogu.com"

ARG SCM_PKG_URL=https://packages.scm-manager.org/repository/releases/sonia/scm/packaging/unix/3.11.2/unix-3.11.2.tar.gz
ARG SCM_PKG_SHA256=15161781b9fb98a74c98f1bd804280a8aa282969da7182f028be5dfad0251167
ARG SCM_CODE_EDITOR_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-code-editor-plugin/3.0.0/scm-code-editor-plugin-3.0.0.smp
ARG SCM_CODE_EDITOR_PLUGIN_SHA256=2d4821f1930bd19407a0bfc386edebc26f2cd03ef6a855e4bb7abaf8bb0a4220
ARG SCM_SCRIPT_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-script-plugin/3.2.0/scm-script-plugin-3.2.0.smp
ARG SCM_SCRIPT_PLUGIN_SHA256=8416ddac56175e7f4ad47e340c62650e125c8175fc4e0b8dc34cf69851e1dac3
ARG SCM_CAS_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-cas-plugin/3.4.1/scm-cas-plugin-3.4.1.smp
ARG SCM_CAS_PLUGIN_SHA256=a2cd9727039e5f690b5a53c94365be1aa1c76c256dc91280cf7008fdb57dbcf8
ARG SCM_CES_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-ces-plugin/3.0.0/scm-ces-plugin-3.0.0.smp
ARG SCM_CES_PLUGIN_SHA256=860e71fc1c2120ba1c515d15dcbbc696c2101d044641f852d38657638fea85f2

# scm-server environment
ENV SCM_HOME=/var/lib/scm \
    SCM_REQUIRED_PLUGINS=/opt/scm-server/required-plugins \
    SSL_CERT_FILE=/opt/scm-server/conf/ca-certificates.crt \
    CES_TOKEN_HEADER=X-CES-Token \
    CES_TOKEN_CONFIGURATION_KEY=serviceaccount_token \
    # mark as webapp for nginx
    SERVICE_8080_TAGS="webapp" \
    SERVICE_8080_NAME="scm"

## install scm-server
RUN set -x -o errexit -o nounset -o pipefail \
    && apk update \
    && apk upgrade \
    && apk add --no-cache graphviz ttf-dejavu mercurial jq unzip curl \
    && curl --fail  -Lks ${SCM_PKG_URL} -o /tmp/scm-server.tar.gz \
    && echo "${SCM_PKG_SHA256} */tmp/scm-server.tar.gz" | sha256sum -c - \
    && addgroup -S -g 1000 scm \
    && adduser -S -h /opt/scm-server -s /bin/bash -G scm -u 1000 scm \
    && gunzip /tmp/scm-server.tar.gz \
    && tar -C /opt -xf /tmp/scm-server.tar \
    && cd /tmp \
    # download scm-script-plugin & scm-cas-plugin
    && mkdir ${SCM_REQUIRED_PLUGINS} \
    && curl --fail -Lks ${SCM_CODE_EDITOR_PLUGIN_URL} -o ${SCM_REQUIRED_PLUGINS}/scm-code-editor-plugin.smp \
    && echo "${SCM_CODE_EDITOR_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-code-editor-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks ${SCM_SCRIPT_PLUGIN_URL} -o ${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp \
    && echo "${SCM_SCRIPT_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-script-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks ${SCM_CAS_PLUGIN_URL} -o ${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp \
    && echo "${SCM_CAS_PLUGIN_SHA256} *${SCM_REQUIRED_PLUGINS}/scm-cas-plugin.smp" | sha256sum -c - \
    && curl --fail -Lks ${SCM_CES_PLUGIN_URL} -o ${SCM_REQUIRED_PLUGINS}/scm-ces-plugin.smp \
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

HEALTHCHECK --interval=5s CMD doguctl healthy scm || exit 1

# start scm
CMD ["/startup.sh"]
