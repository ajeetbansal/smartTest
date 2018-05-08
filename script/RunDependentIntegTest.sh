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

startTime=`date`
git diff --name-only $paramParentBranch > /tmp/diff.txt
getDependentIntegTestAsGradleTasks $paramVersion $paramParentBranch
gradleTaskList=`cat /tmp/GradleTaskList.txt`
echo ./gradlew $gradleTaskList
./gradlew $gradleTaskList
endTime=`date`
echo Start time: $startTime, End time: $endTime
