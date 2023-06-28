#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

# Variables
BASE_DIR=".."
WORKDIR=$BASE_DIR
TARGET_DIR="$BASE_DIR/target"
# Path to the dogu json of the dogu
DOGU_JSON_FILE=${WORKDIR}/dogu.json
DOGU_JSON_DEV_FILE=${TARGET_DIR}/dogu.json
# Name of the dogu is extracted from the dogu.json
ARTIFACT_ID=$(jq -er ".Name" "${DOGU_JSON_FILE}" | sed "s|.*/||g")
# Namespace of the dogu is extracted from the dogu.json
ARTIFACT_NAMESPACE=$(jq -er ".Name" "${DOGU_JSON_FILE}" | sed "s|/.*||g")
# Namespace of the dogu is extracted from the dogu.json
VERSION=$(jq -er ".Version" ${DOGU_JSON_FILE})
# Image of the dogu is extracted from the dogu.json
IMAGE=$(jq -er ".Image" ${DOGU_JSON_FILE}):${VERSION}
K3S_CLUSTER_FQDN=k3ces.local
K3S_LOCAL_REGISTRY_PORT=30099
NAMESPACE="ecosystem"
K3CES_REGISTRY_URL_PREFIX="${K3S_CLUSTER_FQDN}:${K3S_LOCAL_REGISTRY_PORT}"
IMAGE_DEV_WITHOUT_TAG=$(jq -er ".Image" "${DOGU_JSON_FILE}" | sed "s|registry\.cloudogu\.com\(.\+\)|${K3CES_REGISTRY_URL_PREFIX}\1|g")
IMAGE_DEV="${IMAGE_DEV_WITHOUT_TAG}:${VERSION}"
K8S_RESOURCE_TEMP_YAML=${TARGET_DIR}/${ARTIFACT_ID}_${VERSION}.yaml
echo "$IMAGE_DEV_WITHOUT_TAG"
echo "$IMAGE_DEV"

echo "k8s image-import"
docker build "$BASE_DIR" -t "$IMAGE_DEV"
docker push "$IMAGE_DEV"

echo "generate modified dogu.json"
mkdir -p "$TARGET_DIR"
touch "$DOGU_JSON_DEV_FILE"
jq ".Image=\"$IMAGE_DEV_WITHOUT_TAG\"" "$DOGU_JSON_FILE" > "$DOGU_JSON_DEV_FILE"

echo "install dogu descriptor (dogu.json as configmap)"
kubectl create configmap "${ARTIFACT_ID}-descriptor" --from-file="${DOGU_JSON_DEV_FILE}" --dry-run=client -o yaml | kubectl apply -f - --namespace="${NAMESPACE}"

echo "create dogu resource"
cat <<EOF > "$K8S_RESOURCE_TEMP_YAML"
apiVersion: k8s.cloudogu.com/v1
kind: Dogu
metadata:
  name: $ARTIFACT_ID
  labels:
    dogu: $ARTIFACT_ID
spec:
  name: $ARTIFACT_NAMESPACE/$ARTIFACT_ID
  version: $VERSION
EOF

echo "apply dogu resource"
kubectl apply -f "${K8S_RESOURCE_TEMP_YAML}" --namespace="${NAMESPACE}"