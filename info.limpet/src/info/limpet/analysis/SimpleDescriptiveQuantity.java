package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import tec.units.ri.quantity.QuantityRange;

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
					
					QuantityRange<?> range = o.getRange();
					if(range != null)
					{
						titles.add("Range");
						values.add(range.getMinimum().getValue() + " - " + range.getMaximum().getValue() + " " + o.getUnits());
						
					}

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
