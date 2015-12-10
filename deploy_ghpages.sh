#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ] 
then
	rm -rf out || exit 0;
	mkdir out;
	cd out
	git init
	git config user.name "Travis-CI"
	git config user.email "travis@w3ctag.org"
	mkdir 1.0
	cd 1.0
	unzip ../../info.limpet.site/target/site-0.0.1-SNAPSHOT.zip
	cd ..
	rm -rf coverage
	mkdir coverage
	cp -r ../target/jacoco/report/ coverage
	git add .
	git commit -m "Deploy Limpet Update Site"
	git push --force --quiet "https://${GH_TOKEN}@${GH_REF}" master:gh-pages > /dev/null 2>&1
fi
