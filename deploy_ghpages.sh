#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ] 
then
	cd reports
	git init
        git config user.name "Travis-CI"
        git config user.email "travis@w3ctag.org"
	git add .
	git commit -m "Deploy Limpet Artifacts"
	git push --force --quiet "https://${GH_TOKEN}@${GH_REF}" master:gh-pages > /dev/null 2>&1
fi
