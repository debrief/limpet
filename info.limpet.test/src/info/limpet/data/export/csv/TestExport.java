package info.limpet.data.export.csv;

import info.limpet.ICollection;
import info.limpet.data.csv.CsvGenerator;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.store.InMemoryStore;
import junit.framework.TestCase;

public class TestExport extends TestCase
{

	private static InMemoryStore data = new SampleData().getData(20);
	
	public void testExportAngleDegrees()
	{
		generate(SampleData.ANGLE_ONE);
		generate(SampleData.STRING_ONE);
		generate(SampleData.SPEED_ONE);
	}

	private void generate(String name)
	{
		ICollection collection = (ICollection) data.get(name);
		String csv = new CsvGenerator().generate(collection);
		assertTrue(name + " isn't created.", csv != null);
	}

}
