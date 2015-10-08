package info.limpet.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.QuantityRange;
import info.limpet.data.operations.CollectionComplianceTests;

public abstract class SimpleDescriptiveQuantity extends CoreAnalysis
{

	public SimpleDescriptiveQuantity()
	{
		super("Quantity Analysis");
	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

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
					@SuppressWarnings("unchecked")
					IQuantityCollection<Quantity> o = (IQuantityCollection<Quantity>) thisC;

					// output some high level data
					if (o.getDimension() != null)
					{
						titles.add("Dimension");
						values.add(o.getDimension().toString());
					}
					if (o.getUnits() != null)
					{
						titles.add("Units");
						values.add(o.getUnits().toString());
					}
					
					// if it's a singleton, show the value
					if(o.size() == 1)
					{
						titles.add("Value");
						values.add("" + o.getValues().iterator().next().doubleValue(o.getUnits()));
					}

					QuantityRange<Quantity> range = o.getRange();
					if (range != null)
					{
						titles.add("Range");
						values.add(range.getMinimum().doubleValue(o.getUnits()) + " - "
								+ range.getMaximum().doubleValue(o.getUnits()) + " " + o.getUnits());

					}

					// we only bother with the stats if there are more than 1 item
					if (o.size() > 1)
					{
						// collate the values into an array
						double[] data = new double[o.size()];

						// Add the data from the array
						int ctr = 0;
						Iterator<?> iterV = o.getValues().iterator();
						while (iterV.hasNext())
						{
							@SuppressWarnings("unchecked")
							Measurable<Quantity> object = (Measurable<Quantity>) iterV.next();
							data[ctr++] = object.doubleValue(o.getUnits());
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
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return aTests.allCollections(selection) && aTests.allQuantity(selection);
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
