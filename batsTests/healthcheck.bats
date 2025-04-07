#!/usr/bin/env bats
# Bind an unbound BATS variables that fail all tests when combined with 'set -o nounset'
export BATS_TEST_START_TIME="0"

load '/workspace/target/bats_libs/bats-support/load.bash'
load '/workspace/target/bats_libs/bats-assert/load.bash'
load '/workspace/target/bats_libs/bats-mock/load.bash'

setup() {
  export CES_TOKEN_HEADER=TestHeader

  doguctl="$(mock_create)"
  export doguctl
  curl="$(mock_create)"
  export curl

  export PATH="${BATS_TMPDIR}:${PATH}"
  ln -s "${doguctl}" "${BATS_TMPDIR}/doguctl"
  ln -s "${curl}" "${BATS_TMPDIR}/curl"
}

teardown() {
  unset CES_TOKEN_HEADER
  unset API_TOKEN

  rm "${BATS_TMPDIR}/doguctl"
  rm "${BATS_TMPDIR}/curl"
}

@test "health_check passes when everything is healthy" {
  source /workspace/resources/healthcheck.sh

  # curl
  mock_set_status "${curl}" 0
  mock_set_output "${curl}" "200"

  run runHealthCheck myApiToken

  assert_success
  assert_equal "$(mock_get_call_num "${curl}")" "1"
  assert_equal "$(mock_get_call_num "${doguctl}")" "0"
}

@test "health_check fails when curl returns != 200" {
  source /workspace/resources/healthcheck.sh

  # curl
  mock_set_status "${curl}" 0
  mock_set_output "${curl}" "401"

  run runHealthCheck myApiToken

  assert_failure
  assert_equal "$(mock_get_call_num "${curl}")" "1"
  assert_equal "$(mock_get_call_num "${doguctl}")" "0"
}


