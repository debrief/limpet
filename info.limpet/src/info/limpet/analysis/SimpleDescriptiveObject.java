package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opengis.geometry.Geometry;

public abstract class SimpleDescriptiveObject extends CoreAnalysis
{

	final CollectionComplianceTests aTests;

	public SimpleDescriptiveObject()
	{
		super("Object Analysis");
		aTests = new CollectionComplianceTests();
	}

	@Override
	public void analyse(List<IStoreItem> selection)
	{
		List<String> titles = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		// check compatibility
		if (appliesTo(selection))
		{
			if (selection.size() == 1)
			{
				// ok, let's go for it.
				for (Iterator<IStoreItem> iter = selection.iterator(); iter.hasNext();)
				{
					ICollection thisC = (ICollection) iter.next();
					ObjectCollection<?> o = (ObjectCollection<?>) thisC;

					// check it has some data
					if (o.size() > 0)
					{
						titles.add("Content Type");
						Object nextObject = o.getValues().iterator().next();
						values.add(typeFor(nextObject, nextObject.getClass()));
					}
				}
			}
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	public String typeFor(Object subject, Object oClass)
	{
		String res = "un-recognised";

		if (oClass.equals(String.class))
		{
			res = "String";
		}
		else if (subject instanceof Geometry)
		{
			res = "Location";
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return aTests.allCollections(selection) && aTests.allNonQuantity(selection);
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
