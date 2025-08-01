FROM registry.cloudogu.com/official/java:17.0.13-1
LABEL maintainer="sebastian.sdorra@cloudogu.com"

ARG SCM_PKG_URL=https://packages.scm-manager.org/repository/releases/sonia/scm/packaging/unix/3.9.0/unix-3.9.0.tar.gz
ARG SCM_PKG_SHA256=701cc8979232e26170978ec0c90ac31796b68a4393f3d9baa2f27ea2c657342d
ARG SCM_CODE_EDITOR_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-code-editor-plugin/3.0.0/scm-code-editor-plugin-3.0.0.smp
ARG SCM_CODE_EDITOR_PLUGIN_SHA256=2d4821f1930bd19407a0bfc386edebc26f2cd03ef6a855e4bb7abaf8bb0a4220
ARG SCM_SCRIPT_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-script-plugin/3.1.0/scm-script-plugin-3.1.0.smp
ARG SCM_SCRIPT_PLUGIN_SHA256=6bf4f8a518c3ce7babc202f53785c87a50dd3035361e2066b1dbcf0f69814861
ARG SCM_CAS_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-cas-plugin/3.3.2/scm-cas-plugin-3.3.2.smp
ARG SCM_CAS_PLUGIN_SHA256=05cbb23226b6d9b745a8f95f96773d5e7108e93c523c1c6b27caad95f0b8aae3
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
