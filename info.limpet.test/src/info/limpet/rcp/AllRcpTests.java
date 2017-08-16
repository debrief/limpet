package info.limpet.rcp;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses(
{ 
	TestReflectivePropertySource.class
})

@RunWith(Suite.class)
public class AllRcpTests extends TestSuite
{

}
