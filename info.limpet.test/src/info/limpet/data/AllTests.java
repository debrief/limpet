package info.limpet.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
})

@RunWith(Suite.class)
public class AllTests extends TestSuite
{

}
