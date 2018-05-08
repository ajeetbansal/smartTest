#!/bin/bash

source $(dirname "$0")/CliHelper.sh

cd $IDCS_CODE/idaas
paramVersion=$1
if [ -z $paramVersion ]
then
	echo Missing parameter: version
	exit 1
fi
paramGranularity=$2
if [ -z paramGranularity ]
then
	echo Missing parameter: granularity
	exit 1
fi

startTime=`date`
for module in `./gradlew -q projects | tail -n +7 | head -n -3 | cut -d " " -f3 | tr -d "':"`; do
        analyze_test_dependency $paramVersion $paramGranularity $module
done
endTime=`date`
echo Start time: $startTime, End time: $endTime
