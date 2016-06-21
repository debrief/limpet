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
package info.limpet.data;

import java.io.File;
import java.io.IOException;

import info.limpet.IStore;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.StoreGroup;
import junit.framework.TestCase;

public class TestPersistence extends TestCase
{

  public void testSaveThenLoadSampleData()
  {
    StoreGroup data = new SampleData().getData(20);
    final long storeSize = data.size();
    final String fileName = "testtemp.lap";

    // clear the test file
    File testF = new File(fileName);
    if (testF.exists() && testF.delete())
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    assertTrue("file created", testF.exists());

    IStore data2 = new XStreamHandler().load(fileName);
    assertNotNull("found store", data2);
    if (data2 instanceof StoreGroup)
    {
      StoreGroup ims = (StoreGroup) data2;
      assertEquals("correct num of objects", storeSize, ims.size());
    }

  }

}
