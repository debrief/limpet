/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import info.limpet.rcp.TestReflectivePropertySource;
import junit.framework.TestSuite;

@Suite.SuiteClasses(
{ 
	TestCollections.class,
	TestArithmeticCollections.class,
	TestDynamic.class,
	TestExport.class,
	TestReflectivePropertySource.class,
	TestOperations.class,
	TestAnalysis.class,
	TestCsvParser.class,
	TestRepParser.class,
	TestLocations.class,
	TestGeotoolsGeometry.class,
	TestExport.class,
	TestBistaticAngleCalculations.class,
	TestPersistence.class,
	TestGrids.class
})

@RunWith(Suite.class)
public class AllTests extends TestSuite
{

}
