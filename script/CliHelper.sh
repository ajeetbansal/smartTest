#!/bin/bash

source $(dirname "$0")/SetSmartTestCliEnv.sh

function analyze_test_dependency() {
    paramVersion=$1
    granularity=$2
    module=$3
    if [ -d $module/src/integTest ]
    then
      echo Starting test dependency analysis for $module ...
      #dump the existing coverage data to eliminate noise
      ./gradlew integTestCoverageNonCumulative     

      ./gradlew :$module:integTest -x:core-common:gT -x:gR;
      if [ $? -eq 0 ]; then
        ./gradlew integTestCoverageNonCumulative jacocoReport -x:core-common:gT -x:gR;
        ./smart-test/smart-test-cli/build/install/smart-test-cli/bin/smart-test-cli --command generate --granularity $granularity --dependentModule $module --input $IDCS_CODE/idaas --version $paramVersion
      else
        echo "Rolling back because integ test execution failed"
        ./smart-test/smart-test-cli/build/install/smart-test-cli/bin/smart-test-cli --command rollback --granularity $granularity  --input $IDCS_CODE/idaas --version $paramVersion
        exit 1
      fi
    else
        echo Skipping $module because it does not have any integration tests...
    fi
}

function getDependentIntegTestAsGradleTasks() {
	paramVersion=$1
	paramParentBranch=$2
	git diff --name-only $paramParentBranch > /tmp/diff.txt
	./smart-test/smart-test-cli/build/install/smart-test-cli/bin/smart-test-cli -c getDepModules -g module -i /tmp/diff.txt -o /tmp/GradleTaskList.txt -v $paramVersion
}
