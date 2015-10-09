package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class MultiplyQuantityOperation implements IOperation<IStoreItem>
{
	public static final String SERIES_NAME = "Multiplication product";

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	final private String outputName;

	public MultiplyQuantityOperation()
	{
		this(SERIES_NAME);
	}

	public MultiplyQuantityOperation(final String seriesName)
	{
		outputName = seriesName;
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			ICommand<IStoreItem> newC = new MultiplyQuantityValues(outputName,
					selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		// first check we have quantity data
		if (aTests.allCollections(selection) && aTests.nonEmpty(selection) && aTests.allQuantity(selection))
		{
			// ok, we have quantity data. See if we have series of the same length, or
			// singletons
			return aTests.allEqualLengthOrSingleton(selection);
		}
		else
		{
			return false;
		}
	}

	public static class MultiplyQuantityValues extends
			AbstractCommand<IStoreItem>
	{

		public MultiplyQuantityValues(String outputName,
				List<IStoreItem> selection, IStore store)
		{
			super("Multiply series", "Multiply series", outputName, store, false,
					false, selection);
		}

		@Override
		public void execute()
		{
			Unit<?> unit = calculateOutputUnit();
			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>(getOutputName(),
					this, unit);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs);

			// tell each series that we're a dependent
			Iterator<IStoreItem> iter = inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<IStoreItem> res = new ArrayList<IStoreItem>();
			res.add(target);
			getStore().addAll(res);
		}

		private Unit<?> calculateOutputUnit()
		{
			Iterator<IStoreItem> inputsIterator = inputs.iterator();
			IQuantityCollection<?> firstItem = (IQuantityCollection<?>) inputsIterator
					.next();
			Unit<?> unit = firstItem.getUnits();

			while (inputsIterator.hasNext())
			{
				IQuantityCollection<?> nextItem = (IQuantityCollection<?>) inputsIterator
						.next();
				unit = unit.times(nextItem.getUnits());
			}
			return unit;
		}

		@Override
		protected void recalculate()
		{
			Unit<?> unit = calculateOutputUnit();

			// update the results
			performCalc(unit, outputs);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(Unit<?> unit, List<IStoreItem> outputs)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) outputs
					.iterator().next();

			// clear out the lists, first
			Iterator<IStoreItem> iter = outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();

				// hey, if it's a time series we need to clear the times, too
			}

			// find the (non-singleton) array length
			int length = getNonSingletonArrayLength(inputs);

			// start adding values.
			for (int j = 0; j < length; j++)
			{
				Double runningTotal = null;

				for (int i = 0; i < inputs.size(); i++)
				{
					@SuppressWarnings("unchecked")
					IQuantityCollection<Quantity> thisC = (IQuantityCollection<Quantity>) inputs
							.get(i);

					final double thisValue;

					// just check that this isn't a singleton
					if (thisC.size() == 1)
					{
						thisValue = thisC.getValues().get(0).doubleValue(thisC.getUnits());
					}
					else
					{
						thisValue = thisC.getValues().get(j).doubleValue(thisC.getUnits());
					}

					// first value?
					if (runningTotal == null)
					{
						runningTotal = thisValue;
					}
					else
					{
						runningTotal = runningTotal * thisValue;
					}
				}

				target.add(runningTotal);
			}
		}

	}

}
