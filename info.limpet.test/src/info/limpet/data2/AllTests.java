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
import info.limpet.rcp.TestStackedCharts;
import junit.framework.TestSuite;

@Suite.SuiteClasses(
{ 
  TestAdmin.class,
  TestAnalysis.class,
  TestArithmeticCollections.class,
  TestBistaticAngleCalculations.class,
  TestCollectionCompliance.class,
  TestCollections.class,
  TestCsvParser.class,
  TestDynamic.class,
  TestExport.class,
  TestExport.class,
  TestGeotoolsGeometry.class,
  TestGrids.class,
  TestLocations.class,
  TestOperations.class,
  TestPersistence.class,
  TestReflectivePropertySource.class,
  TestRepParser.class
})

@RunWith(Suite.class)
public class AllTests extends TestSuite
{

}
