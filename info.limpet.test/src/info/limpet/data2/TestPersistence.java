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

import info.limpet.IStoreGroup;
import info.limpet.NumberDocument;
import info.limpet.SampleData;
import info.limpet.StoreGroup;
import info.limpet.data.persistence.xml.XStreamHandler;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class TestPersistence extends TestCase
{
  public void testSaveThenLoadSampleData()
  {
    IStoreGroup data = new SampleData().getData(20);
    final long storeSize = data.size();
    final String fileName = "testtemp.lap";
    
    StoreGroup speedFolder1 = (StoreGroup) data.get(SampleData.SPEED_DATA_FOLDER);
    NumberDocument speedDoc1 = (NumberDocument) speedFolder1.get(SampleData.SPEED_IRREGULAR2);
    assertNotNull("have found speed 1 doc", speedDoc1);
    
    // get some data to look at later on

    // clear the test file
    File testF = new File(fileName);
    if (testF.exists() && !testF.delete())
    {
      System.out.println("testing - file delete failed");
    }

    assertTrue("file doesn't exist", !testF.exists());

    try
    {
      new XStreamHandler().save(data, fileName);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    assertTrue("file created", testF.exists());

    IStoreGroup data2 = new XStreamHandler().load(fileName);
    assertNotNull("found store", data2);
    if (data2 instanceof StoreGroup)
    {
      StoreGroup ims = (StoreGroup) data2;
      assertEquals("correct num of objects", storeSize, ims.size());
      
      // have a look at some data
      StoreGroup speedFolder2 = (StoreGroup) ims.get(SampleData.SPEED_DATA_FOLDER);
      NumberDocument speedDoc2 = (NumberDocument) speedFolder2.get(SampleData.SPEED_IRREGULAR2);
      assertNotNull("have found speed 2 doc", speedDoc2);
      
      // check these are now different objects
      assertNotSame("should not be the same object", speedDoc1, speedDoc2);
      
      // check they're equal
      assertEquals("same name", speedDoc1.getName(), speedDoc2.getName());
      assertEquals("same size", speedDoc1.size(), speedDoc2.size());
      assertEquals("same units", speedDoc1.getUnits(), speedDoc2.getUnits());      
      assertEquals("same units", speedDoc1.getUUID(), speedDoc2.getUUID());      
      assertEquals("same range", speedDoc1.getRange(), speedDoc2.getRange());
      assertEquals("same first value", speedDoc1.getIterator().next(), speedDoc2.getIterator().next());
      assertEquals("same first index", speedDoc1.getIndices().next(), speedDoc2.getIndices().next());
      assertEquals("same quantity", speedDoc1.isQuantity(), speedDoc2.isQuantity());
      assertEquals("same indexed", speedDoc1.isIndexed(), speedDoc2.isIndexed());
      assertEquals("same predecessors", speedDoc1.getPrecedent(), speedDoc2.getPrecedent());
      assertEquals("same dependents", speedDoc1.getDependents(), speedDoc2.getDependents());
      assertEquals("same parent", speedDoc1.getParent().size(), speedDoc2.getParent().size());
      assertEquals("same parent name", speedDoc1.getParent().getName(), speedDoc2.getParent().getName());
    }
    else
    {
      fail("imported data is wrong type");
    }

  }

}
