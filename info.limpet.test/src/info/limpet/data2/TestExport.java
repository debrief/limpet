package info.limpet.data2;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.persistence.CsvGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestExport extends TestCase
{

	private static StoreGroup data = new SampleData().getData(20);

	public void testCsvGenerate()
	{
		List<IStoreItem> collections = getCollections();
		for (IStoreItem collection:collections)
		{
			System.out.println("Generate csv for " + collection.getName());
			generate(collection);
		}
	}

	private void generate(IStoreItem collection)
	{
		String csv = CsvGenerator.generate(collection);
		assertTrue(collection.getName() + " isn't created.", csv != null);
	}

	private List<IStoreItem> getCollections()
	{
		List<IStoreItem> collections = new ArrayList<IStoreItem>();
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
					if (iStoreItem instanceof IDocument)
					{
						collections.add((IDocument) iStoreItem);
					}
				}
			}
			if (item instanceof IDocument)
			{
				collections.add(item);
			}
		}
		return collections;
	}

}
