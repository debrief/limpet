package info.limpet.analysis;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public abstract class TimeFrequencyBins extends CoreAnalysis
{
	private static final int MAX_SIZE = 10000;
	private static final double THRESHOLD_VALUE = 0.001;
	final CollectionComplianceTests aTests;

	public TimeFrequencyBins()
	{
		super("Time Frequency Bins");
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
		public final long lowerVal;
		public final long upperVal;
		public final long freqVal;

		public Bin(final long lower, final long upper, long freq)
		{
			upperVal = upper;
			lowerVal = lower;
			freqVal = freq;
		}
	}

	public static BinnedData doBins(ICollection c, IBaseTemporalCollection o)
	{
		// collate the values into an array
		double[] data = new double[c.size()];

		// Add the data from the array
		int ctr = 0;
		Iterator<Long> iterV = o.getTimes().iterator();
		while (iterV.hasNext())
		{
			long time = iterV.next();
			data[ctr++] = time;
		}

		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics(data);

		// also do some frequency binning
		double range = stats.getMax() - stats.getMin();

		// aah, double-check we don't have zero range
		final int BIN_COUNT;
		final int maxRange = 30;
		if (range > maxRange)
			BIN_COUNT = maxRange;
		else
			BIN_COUNT = (int) Math.max(2, range);

		BinnedData res = new BinnedData(c, BIN_COUNT);

		if (range > THRESHOLD_VALUE)
		{

			long[] histogram = new long[BIN_COUNT];
			EmpiricalDistribution distribution = new EmpiricalDistribution(BIN_COUNT);
			distribution.load(data);

			int k = 0;
			for (SummaryStatistics sStats : distribution.getBinStats())
			{
				histogram[k++] = sStats.getN();
			}

			long rangeSoFar = (long) stats.getMin();
			long rangeStep = (long) (range / BIN_COUNT);
			for (int i = 0; i < histogram.length; i++)
			{
				long l = histogram[i];
				res.add(new Bin(rangeSoFar, rangeSoFar + rangeStep, l));
				rangeSoFar += rangeStep;
			}
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
					IBaseTemporalCollection o = (IBaseTemporalCollection) thisC;

					if (thisC.size() > 1)
					{
						if (thisC.size() < MAX_SIZE)
						{
							BinnedData res = doBins(thisC, o);

							// now output the bins
							StringBuffer freqBins = new StringBuffer();

							Iterator<Bin> bIter = res.iterator();
							while (bIter.hasNext())
							{
								TimeFrequencyBins.Bin bin = (TimeFrequencyBins.Bin) bIter
										.next();
								freqBins.append((int) bin.lowerVal);
								freqBins.append("-");
								freqBins.append((int) bin.upperVal);
								freqBins.append(": ");
								freqBins.append(bin.freqVal);
								freqBins.append(", ");
							}

							titles.add("Frequency bins");
							values.add(freqBins.toString());
						}
					}
				}
			}
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return aTests.allCollections(selection) && aTests.allTemporal(selection);
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
