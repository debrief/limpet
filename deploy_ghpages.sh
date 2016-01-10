#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ] 
then
	rm -rf out || exit 0;
	mkdir out;
	cd out
	git init
	git config user.name "Travis-CI"
	git config user.email "travis@w3ctag.org"
	touch .nojekyll
	mkdir 1.0
	cd 1.0
	cp ../../info.limpet.product/target/repository/content.jar .
	cp ../../info.limpet.product/target/repository/artifacts.jar .
	mkdir binary
	cp ../../info.limpet.product/target/repository/binary/* binary
	mkdir features
	cp ../../info.limpet.product/target/repository/features/info.limpet* features
	cp ../../info.limpet.product/target/repository/features/org.geotools* features
	mkdir plugins
	cp ../../info.limpet.product/target/repository/plugins/info.limpet* plugins
	cp ../../info.limpet.product/target/repository/plugins/org.geotools* plugins
	cp ../../info.limpet.product/www/* .
	cd ..
	rm -rf coverage
	mkdir coverage
	cp -r ../target/jacoco/report/ coverage
	rm -rf static
	mkdir static
        mkdir static/report
	cp ../info.limpet.site/index.html static/report
	cp ../info.limpet.site/*.css static/report
	cp -r ../info.limpet/target/site static/report/info.limpet
	cp -r ../info.limpet.rcp/target/site static/report/info.limpet.rcp
	cp -r ../info.limpet.test/target/site static/report/info.limpet.rcp.test
	git add .
	git commit -m "Deploy Limpet Artifacts"
	git push --force --quiet "https://${GH_TOKEN}@${GH_REF}" master:gh-pages > /dev/null 2>&1
fi
