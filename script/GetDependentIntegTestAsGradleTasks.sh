#!/bin/bash

source $(dirname "$0")/CliHelper.sh

cd $IDCS_CODE/idaas

paramVersion=$1
if [ -z $paramVersion ]
then
	echo Missing parameter: version
	exit 1
fi

paramParentBranch=$2
if [ -z $paramParentBranch ]
then
        echo Missing parameter: parentBranch
        exit 1
fi

getDependentIntegTestAsGradleTasks $paramVersion $paramParentBranch
echo The gradle command to run selective integration tests is:
echo ---------------------------------------------------------------------------------------------------------------------------------------
echo $IDCS_CODE/idaas/gradlew `cat /tmp/GradleTaskList.txt`
echo ---------------------------------------------------------------------------------------------------------------------------------------
echo "Gradle task lisk is available in /tmp/GradleTaskList.txt"
