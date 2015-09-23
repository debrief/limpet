package info.limpet.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestCollections.class, TestOperations.class, TestAnalysis.class})
public class AllTests extends TestSuite
{

}
