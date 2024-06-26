FROM registry.cloudogu.com/official/java:17.0.6-2
LABEL maintainer="sebastian.sdorra@cloudogu.com"

ARG SCM_PKG_URL=https://packages.scm-manager.org/repository/releases/sonia/scm/packaging/unix/3.2.2/unix-3.2.2.tar.gz
ARG SCM_PKG_SHA256=4b9715e5dc82f00ddaf0f40d2c5092b121fc630f84f30d51142bb1181d3f962c
ARG SCM_CODE_EDITOR_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-code-editor-plugin/2.1.0/scm-code-editor-plugin-2.1.0.smp
ARG SCM_CODE_EDITOR_PLUGIN_SHA256=97b2ea672873b644abc60f6b531d9baa868b84604f974adbee3852ec1becba6f
ARG SCM_SCRIPT_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-script-plugin/2.5.1/scm-script-plugin-2.5.1.smp
ARG SCM_SCRIPT_PLUGIN_SHA256=ab1011a57f5455ec3a184eb42c900e58ad6dc829e7ca30c1ab519a0706d36cd0
ARG SCM_CAS_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-cas-plugin/2.4.0/scm-cas-plugin-2.4.0.smp
ARG SCM_CAS_PLUGIN_SHA256=bd9a0e0794fb1be40f357cbbde7396889055bd1af2c42b4f2cdb34763c5fb372
ARG SCM_CES_PLUGIN_URL=https://packages.scm-manager.org/repository/plugin-releases/sonia/scm/plugins/scm-ces-plugin/1.0.1/scm-ces-plugin-1.0.1.smp
ARG SCM_CES_PLUGIN_SHA256=da3b73f8eca4dced6471edcbad53545a8d0e13441e729bac641d337b2fcf1f06

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

HEALTHCHECK CMD doguctl healthy scm || exit 1

# start scm
CMD ["/startup.sh"]
