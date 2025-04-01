#!/usr/bin/env bats
# Bind an unbound BATS variables that fail all tests when combined with 'set -o nounset'
export BATS_TEST_START_TIME="0"

load '/workspace/target/bats_libs/bats-support/load.bash'
load '/workspace/target/bats_libs/bats-assert/load.bash'
load '/workspace/target/bats_libs/bats-mock/load.bash'

setup() {
  export CES_TOKEN_CONFIGURATION_KEY=TestConfig
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
  unset CES_TOKEN_CONFIGURATION_KEY

  rm "${BATS_TMPDIR}/doguctl"
  rm "${BATS_TMPDIR}/curl"
}

@test "health_check passes when everything is healthy" {
  source /workspace/resources/healthcheck.sh

  # config --encrypted
  mock_set_status "${doguctl}" 0 1
  mock_set_output "${doguctl}" "fake_token" 1

  # curl
  mock_set_status "${curl}" 0
  mock_set_output "${curl}" "200"

  mock_set_status "${doguctl}" 0 2            # doguctl state ready
  mock_set_status "${doguctl}" 0 3            # doguctl healthy
  mock_set_status "${doguctl}" 0 4            # doguctl state ready

  run runHealthCheck

  assert_success
  assert_equal "$(mock_get_call_num "${curl}")" "1"
  assert_equal "$(mock_get_call_num "${doguctl}")" "4"
  assert_equal "$(mock_get_call_args "${doguctl}" "4")" "state ready"
}

@test "health_check fails when curl returns != 200" {
  source /workspace/resources/healthcheck.sh

  # config --encrypted
  mock_set_status "${doguctl}" 0 1
  mock_set_output "${doguctl}" "fake_token" 1

  # curl
  mock_set_status "${curl}" 0
  mock_set_output "${curl}" "401"

  run runHealthCheck

  assert_failure
  assert_equal "$(mock_get_call_num "${curl}")" "1"
  assert_equal "$(mock_get_call_num "${doguctl}")" "2"
  assert_equal "$(mock_get_call_args "${doguctl}" "2")" "state unhealthy"
}

@test "health_check fails when doguctl healthy returns failure (exit 1)" {
   source /workspace/resources/healthcheck.sh

    # config --encrypted
    mock_set_status "${doguctl}" 0 1
    mock_set_output "${doguctl}" "fake_token" 1

    # curl
    mock_set_status "${curl}" 0
    mock_set_output "${curl}" "200"

    mock_set_status "${doguctl}" 0 2            # doguctl state ready
    mock_set_status "${doguctl}" 1 3            # doguctl healthy
    mock_set_status "${doguctl}" 0 4            # doguctl state ready

    run runHealthCheck

    assert_failure
    assert_equal "$(mock_get_call_num "${curl}")" "1"
    assert_equal "$(mock_get_call_num "${doguctl}")" "4"
    assert_equal "$(mock_get_call_args "${doguctl}" "4")" "state unhealthy"
}


