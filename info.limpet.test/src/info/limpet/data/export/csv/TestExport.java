package info.limpet.data.export.csv;

import info.limpet.ICollection;
import info.limpet.IStoreItem;
import info.limpet.data.csv.CsvGenerator;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.store.StoreGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestExport extends TestCase
{

	private static StoreGroup data = new SampleData().getData(20);

	public void testCsvGenerate()
	{
		List<ICollection> collections = getCollections();
		for (ICollection collection:collections)
		{
			System.out.println("Generate csv for " + collection.getName());
			generate(collection);
		}
	}

	private void generate(ICollection collection)
	{
		String csv = CsvGenerator.generate(collection);
		assertTrue(collection.getName() + " isn't created.", csv != null);
	}

	private List<ICollection> getCollections()
	{
		List<ICollection> collections = new ArrayList<ICollection>();
		Iterator<IStoreItem> iter = data.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof StoreGroup)
			{
				StoreGroup group = (StoreGroup) item;
				Iterator<IStoreItem> iter2 = group.iterator();
				while (iter2.hasNext())
				{
					IStoreItem iStoreItem = (IStoreItem) iter2.next();
					if (iStoreItem instanceof ICollection)
					{
						collections.add((ICollection) iStoreItem);
					}
				}
			}
			if (item instanceof ICollection)
			{
				collections.add((ICollection) item);
			}
		}
		return collections;
	}

}
