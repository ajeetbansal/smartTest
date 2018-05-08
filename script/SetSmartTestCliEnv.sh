#!/bin/bash
if [ -z $SMART_TEST_CLI_OPTS ]; then
    export SMART_TEST_CLI_OPTS=-Ddata-source.file.location=$IDCS_SHARED/smart-test
fi
echo Using SMART_TEST_CLI_OPTS: $SMART_TEST_CLI_OPTS
