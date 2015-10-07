package info.limpet.data;

import java.io.File;
import java.io.IOException;

import info.limpet.IStore;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.InMemoryStore;
import junit.framework.TestCase;

public class TestPersistence extends TestCase
{

	public void testSaveThenLoadSampleData()
	{
		InMemoryStore data = new SampleData().getData(20);
		final long storeSize = data.size();
		final String fileName = "testtemp.lap";

		// clear the test file
		File testF = new File(fileName);
		if (testF.exists())
		{
			testF.delete();
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
		if (data2 instanceof InMemoryStore)
		{
			InMemoryStore ims = (InMemoryStore) data2;
			assertEquals("correct num of objects", storeSize, ims.size());
		}
		
		// do a bit of tidying up
//		if (testF.exists())
//		{
//			testF.delete();
//		}


	}

}
