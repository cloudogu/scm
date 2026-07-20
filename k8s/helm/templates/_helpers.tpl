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
