	rm -rf ../reports || exit 0;
	mkdir ../reports
	cd ../reports
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
	cp -r ../info.limpet.ui/target/site static/report/info.limpet.ui
	cp -r ../info.limpet.test/target/site static/report/info.limpet.rcp.test
