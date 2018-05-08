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

paramModule=$3
if [ -z paramGranularity ]
then
	echo Missing parameter: module
	exit 1
fi

analyze_test_dependency $paramVersion $paramGranularity $paramModule

