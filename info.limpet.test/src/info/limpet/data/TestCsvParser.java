package info.limpet.data;

import java.io.File;
import java.util.List;

import info.limpet.IStore.IStoreItem;
import info.limpet.data.csv.CsvParser;
import junit.framework.TestCase;

public class TestCsvParser extends TestCase
{

	public void testIntervals() throws Exception
	{
		File file = getDataFile("intervals.csv");
		assertTrue(file.isFile());
		List<IStoreItem> items = new CsvParser().parse(file.getAbsolutePath());
		assertTrue(items.size() == 1);
	}
	
	public void testFrequencs() throws Exception
	{
		File file = getDataFile("frequences.csv");
		assertTrue(file.isFile());
		List<IStoreItem> items = new CsvParser().parse(file.getAbsolutePath());
		assertTrue(items.size() == 1);
	}

	private File getDataFile(String name)
	{
		File file = new File(getFileName(name));
		return file;
	}

	private String getFileName(String name)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("..");
		builder.append(File.separator);
		builder.append("info.limpet.sample_data");
		builder.append(File.separator);
		builder.append("data");
		builder.append(File.separator);
		builder.append(name);
		return builder.toString();
	}

}
