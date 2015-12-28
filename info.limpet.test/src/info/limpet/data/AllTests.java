package info.limpet.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import info.limpet.data.export.csv.TestExport;
import junit.framework.TestSuite;

@Suite.SuiteClasses(
{ 
	TestCollections.class,
	TestOperations.class,
	TestAnalysis.class,
	TestDynamic.class,
	TestGeotoolsGeometry.class,
	TestPersistence.class,
	TestCsvParser.class,
	TestExport.class,
})

@RunWith(Suite.class)
public class AllTests extends TestSuite
{

}
