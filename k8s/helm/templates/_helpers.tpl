{{- define "scm.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "scm.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name (include "scm.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/* All-in-one labels */}}
{{- define "scm.labels" -}}
app: ces
{{ include "scm.selectorLabels" . }}
helm.sh/chart: {{- printf " %s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- if .Values.extraLabels }}
{{ toYaml .Values.extraLabels }}
{{- end }}
{{- end }}

{{- define "scm.selectorLabels" -}}
app.kubernetes.io/name: {{ include "scm.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: {{ include "scm.name" . }}
{{- end }}

{{- define "scm.scmSelectorLabels" -}}
{{ include "scm.selectorLabels" . }}
app.kubernetes.io/component: scm
{{- end }}

{{- define "scm.backupLabels" -}}
k8s.cloudogu.com/backup-scope: scm
{{- end }}

{{- define "scm.gotenbergName" -}}
{{- printf "%s-gotenberg" (include "scm.fullname" .) -}}
{{- end -}}

{{- define "scm.gotenbergSelectorLabels" -}}
app.kubernetes.io/name: {{ include "scm.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: gotenberg
{{- end }}


{{/*
Common environment variables shared across all needed containers
*/}}
{{- define "scm.configEnv" -}}
- name: DOGU_NAME
  value: {{ .Values.scm.configuration.configEnv.doguName | quote }}
- name: DOGU_REGISTRY_DIR
  value: {{ .Values.scm.configuration.configEnv.doguRegistryDir  | quote }}
- name: GLOBAL_CONFIG_DIR
  value: {{ .Values.scm.configuration.configEnv.globalConfigDir  | quote }}
- name: DOGU_CONFIG_DIR
  value: {{ .Values.scm.configuration.configEnv.doguConfigDir | quote }}
- name: SENSITIVE_CONFIG_DIR
  value: {{ .Values.scm.configuration.configEnv.sensitiveConfigDir | quote }}
- name: LOCAL_CONFIG_DIR
  value: {{ .Values.scm.configuration.configEnv.localConfigDir | quote }}
{{- end }}


{{/*

Common volumes shared across all needed containers
- dogu registry folder (dogu.json) for doguctl config-key validation.
- Read-only platform config: global + normal + sensitive.Read-only platform config: global + normal + sensitive.
- Writable local doguctl config store (successfulInitialConfiguration, admin_user, ...).
*/}}
{{- define "scm.configVolumeMounts" -}}
{{- $root := .root | default . -}}
- name: scm-dogu-json
  mountPath: {{ $root.Values.scm.configuration.configEnv.doguRegistryDir | quote }}
  {{- if hasKey . "doguRegistryReadOnly" }}
  readOnly: true
  {{- end }}
- name: global-config
  mountPath: {{ $root.Values.scm.configuration.configEnv.globalConfigDir  | quote }}
  readOnly: true
- name: normal-config
  mountPath: {{ $root.Values.scm.configuration.configEnv.doguConfigDir  | quote }}
  readOnly: true
- name: secret-config
  mountPath: {{ $root.Values.scm.configuration.configEnv.sensitiveConfigDir | quote }}
  readOnly: true
- name: scm-data
  mountPath: {{ $root.Values.scm.configuration.configEnv.localConfigDir | quote }}
  subPath: localConfig
{{- end }}