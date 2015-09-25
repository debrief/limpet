package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public abstract class SimpleDescriptiveQuantity extends CoreAnalysis
{

	public SimpleDescriptiveQuantity()
	{
		super("Quantity Analysis");
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
					IQuantityCollection<?> o = (IQuantityCollection<?>) thisC;
					
					// output some high level data
					titles.add("Dimension");
					values.add(o.getDimension().toString());
					titles.add("Units");
					values.add(o.getUnits().toString());

					// collate the values into an array
					double[] data = new double[o.size()];

					// Add the data from the array
					int ctr = 0;
					Iterator<?> iterV = o.getValues().iterator();
					while (iterV.hasNext())
					{
						Quantity<?> object = (Quantity<?>) iterV.next();
						data[ctr++] = object.getValue().doubleValue();
					}
					
					// Get a DescriptiveStatistics instance
					DescriptiveStatistics stats = new DescriptiveStatistics(data);
					
					// output some basic overview stats
					titles.add("Min");
					values.add("" + stats.getMin());
					titles.add("Max");
					values.add("" + stats.getMax());
					titles.add("Mean");
					values.add("" + stats.getMean());
					titles.add("Std");
					values.add("" + stats.getStandardDeviation());
					titles.add("Median");
					values.add("" + stats.getPercentile(50));
					
					// also do some frequency binning
					double range = stats.getMax() - stats.getMin();
					
					final int BIN_COUNT;
					if(range > 10)
						BIN_COUNT = 10;
					else
						BIN_COUNT = (int) range;
					
					long[] histogram = new long[BIN_COUNT];
					EmpiricalDistribution distribution = new EmpiricalDistribution(BIN_COUNT);
					distribution.load(data);
					int k = 0;
					for(SummaryStatistics sStats: distribution.getBinStats())
					{
					    histogram[k++] = sStats.getN();
					}
					
					// now output the bins
					StringBuffer freqBins = new StringBuffer();					
					double rangeSoFar = stats.getMin();
					double rangeStep = range / BIN_COUNT;
					for (int i = 0; i < histogram.length; i++)
					{
						long l = histogram[i];
						freqBins.append((int)rangeSoFar);
						freqBins.append("-");
						freqBins.append((int)(rangeSoFar + rangeStep));
						freqBins.append(": ");
						freqBins.append(l);
						freqBins.append(", ");
						rangeSoFar += rangeStep;						
					}
					titles.add("Frequency bins");
					values.add(freqBins.toString());
					
				}
			}
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	private boolean appliesTo(List<ICollection> selection)
	{
		return aTests.allQuantity(selection);
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
