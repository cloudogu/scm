#!/usr/bin/env bats
# Bind an unbound BATS variables that fail all tests when combined with 'set -o nounset'
export BATS_TEST_START_TIME="0"

load '/workspace/target/bats_libs/bats-support/load.bash'
load '/workspace/target/bats_libs/bats-assert/load.bash'

setup() {
  source /workspace/resources/sa-common.sh
}

@test "isServiceAccountOfConsumer matches the dogu v3 service account" {
  run isServiceAccountOfConsumer "baseline_SCM_SA" "baseline"

  assert_success
}

@test "isServiceAccountOfConsumer matches a service account migrated from dogu v2" {
  run isServiceAccountOfConsumer "baseline_svmfjv" "baseline"

  assert_success
}

@test "isServiceAccountOfConsumer matches a dogu v2 service account with digits" {
  run isServiceAccountOfConsumer "jenkins_a1b2c3" "jenkins"

  assert_success
}

@test "isServiceAccountOfConsumer does not match the service account of another consumer" {
  run isServiceAccountOfConsumer "jenkins_SCM_SA" "baseline"

  assert_failure
}

@test "isServiceAccountOfConsumer does not match a consumer that only ends with the name" {
  run isServiceAccountOfConsumer "foo-baseline_SCM_SA" "baseline"

  assert_failure
}

@test "isServiceAccountOfConsumer does not match a consumer that only starts with the name" {
  run isServiceAccountOfConsumer "baseline_foo_svmfjv" "baseline"

  assert_failure
}

@test "isServiceAccountOfConsumer does not match a dogu v2 suffix of the wrong length" {
  run isServiceAccountOfConsumer "baseline_svmfj" "baseline"
  assert_failure

  run isServiceAccountOfConsumer "baseline_svmfjvx" "baseline"
  assert_failure
}

@test "isServiceAccountOfConsumer does not match a dogu v2 suffix with uppercase characters" {
  run isServiceAccountOfConsumer "baseline_SVMFJV" "baseline"

  assert_failure
}

@test "isServiceAccountOfConsumer does not match a regular user" {
  run isServiceAccountOfConsumer "baseline" "baseline"

  assert_failure
}
