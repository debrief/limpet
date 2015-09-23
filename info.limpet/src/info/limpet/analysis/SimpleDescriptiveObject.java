package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.Frequency;

public abstract class SimpleDescriptiveObject extends CoreAnalysis
{

	public SimpleDescriptiveObject()
	{
		super("Object Analysis");
	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	@Override
	public void analyse(List<ICollection> selection)
	{
		List<String> titles = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		// check compatibility
		if (appliesTo(selection))
		{
			if (selection.size() == 1)
			{
				// ok, let's go for it.
				for (Iterator<ICollection> iter = selection.iterator(); iter.hasNext();)
				{					
					ICollection thisC = (ICollection) iter.next();
					ObjectCollection<?> o = (ObjectCollection<?>) thisC;

					// build up the histogram
					Frequency freq = new Frequency();
					Iterator<?> iter2 = o.getValues().iterator();
					while (iter2.hasNext())
					{
						Object object = (Object) iter2.next();
						freq.addValue(object.toString());
					}

					// ok, now output this one
					int numItems = freq.getUniqueCount();

					titles.add("Unique values");
					values.add(numItems + "");

					Iterator<Comparable<?>> vIter = freq.valuesIterator();
					StringBuilder outBuffer = new StringBuilder();
					while (vIter.hasNext())
					{
						Comparable<?> value = vIter.next();
						outBuffer.append(value);
						outBuffer.append(':');
						outBuffer.append(freq.getCount(value));
						outBuffer.append(", ");
					}
					titles.add("Frequency");
					values.add(outBuffer.toString());
				}
			}
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	private boolean appliesTo(List<ICollection> selection)
	{
		return aTests.allNonQuantity(selection);
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
