package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
					QuantityCollection<?> o = (QuantityCollection<?>) thisC;

					// Get a DescriptiveStatistics instance
					DescriptiveStatistics stats = new DescriptiveStatistics();

					// Add the data from the array
					List<?> theseV = o.getValues();
					Iterator<?> iterV = theseV.iterator();
					while (iterV.hasNext())
					{
						Quantity<?> object = (Quantity<?>) iterV.next();
						stats.addValue(object.getValue().doubleValue());
					}
					
					// Compute some statistics
					double mean = stats.getMean();
					double std = stats.getStandardDeviation();
					double median = stats.getPercentile(50);

					titles.add("Mean");
					values.add("" + mean);
					titles.add("Std");
					values.add("" + std);
					titles.add("Median");
					values.add("" + median);
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
