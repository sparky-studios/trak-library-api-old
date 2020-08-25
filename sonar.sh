#!/bin/bash

branch_name=$CODEBUILD_SOURCE_VERSION

if [[ "$branch_name" == pr/* ]] ;
then
	pull_request_number="${branch_name:3}"
	mvn verify sonar:sonar -Pcoverage -Dsonar.pullrequest.provider=Github -Dsonar.pullrequest.github.repository=sparky-studios/trak-api -Dsonar.pullrequest.key=$pull_request_number -Dsonar.pullrequest.branch=$CODEBUILD_WEBHOOK_HEAD_REF
else
	mvn verify sonar:sonar -Pcoverage -Dsonar.branch.name=develop
fi

