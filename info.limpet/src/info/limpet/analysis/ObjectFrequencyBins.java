package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.Frequency;

public abstract class ObjectFrequencyBins extends CoreAnalysis
{
	private static final int MAX_SIZE = 2000;
	final CollectionComplianceTests aTests;

	public ObjectFrequencyBins()
	{
		super("Quantity Frequency Bins");
		aTests = new CollectionComplianceTests();
	}

	public static class BinnedData extends ArrayList<Bin>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final ICollection collection;

		public BinnedData(ICollection collection, int number)
		{
			this.collection = collection;
		}
	}

	public static class Bin
	{
		final public Object indexVal;
		final public long freqVal;

		public Bin(Object index, long freq)
		{
			indexVal = index;
			freqVal = freq;
		}
	}

	public static BinnedData doBins(IObjectCollection<?> collection)
	{

		// build up the histogram
		Frequency freq = new Frequency();
		Iterator<?> iter2 = collection.getValues().iterator();
		while (iter2.hasNext())
		{
			Object object = (Object) iter2.next();
			freq.addValue(object.toString());
		}

		// ok, now output this one
		int numItems = freq.getUniqueCount();

		BinnedData res = new BinnedData(collection, numItems);

		Iterator<Comparable<?>> vIter = freq.valuesIterator();
		while (vIter.hasNext())
		{
			Comparable<?> value = vIter.next();
			res.add(new Bin(value, freq.getCount(value)));
		}

		return res;
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

					if (thisC.size() <= MAX_SIZE)
					{
						IObjectCollection<?> o = (IObjectCollection<?>) thisC;

						BinnedData res = doBins(o);

						titles.add("Unique values");
						values.add(res.size() + "");

						StringBuffer freqBins = new StringBuffer();

						Iterator<Bin> bIter = res.iterator();
						while (bIter.hasNext())
						{
							ObjectFrequencyBins.Bin bin = (ObjectFrequencyBins.Bin) bIter
									.next();
							freqBins.append(bin.indexVal);
							freqBins.append(':');
							freqBins.append(bin.freqVal);
							freqBins.append(", ");
						}
						titles.add("Frequency");
						values.add(freqBins.toString());
					}
				}
			}
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return aTests.allCollections(selection) && aTests.allNonQuantity(selection)  && aTests.allNonLocation(selection);
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
