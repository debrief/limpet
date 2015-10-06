package info.limpet.data;

import java.io.IOException;

import info.limpet.IStore;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.persistence.xml.XStreamHandler;
import junit.framework.TestCase;

public class TestPersistence extends TestCase
{
	
	public void testSaveSampleData()
	{
		IStore data = new SampleData().getData(20);
		try
		{
			new XStreamHandler().save(data, "testtemp.lap");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testLoadSampleData()
	{
		IStore data = new XStreamHandler().load("test.lap");
		System.out.println(data);
	}

}
