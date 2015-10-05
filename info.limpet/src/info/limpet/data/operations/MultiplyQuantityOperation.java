package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class MultiplyQuantityOperation implements IOperation<ICollection>
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

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new MultiplyQuantityValues(outputName,
					selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		// first check we have quantity data
		if (aTests.nonEmpty(selection) && aTests.allQuantity(selection))
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
			AbstractCommand<ICollection>
	{

		public MultiplyQuantityValues(String outputName,
				List<ICollection> selection, IStore store)
		{
			super("Multiply series", "Multiply series", outputName, store, false,
					false, selection);
		}

		@Override
		public void execute()
		{
			Unit<?> unit = calculateOutputUnit();
			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>(getOutputName(),
					this, unit);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs);

			// tell each series that we're a dependent
			Iterator<ICollection> iter = _inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<ICollection> res = new ArrayList<ICollection>();
			res.add(target);
			getStore().addAll(res);
		}

		private Unit<?> calculateOutputUnit()
		{
			Iterator<ICollection> inputsIterator = _inputs.iterator();
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
			performCalc(unit, _outputs);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(Unit<?> unit, List<ICollection> outputs)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) outputs
					.iterator().next();

			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();

				// hey, if it's a time series we need to clear the times, too
			}

			// find the (non-singleton) array length
			int length = getNonSingletonArrayLength(_inputs);

			// start adding values.
			for (int j = 0; j < length; j++)
			{
				Double runningTotal = null;

				for (int i = 0; i < _inputs.size(); i++)
				{
					@SuppressWarnings("unchecked")
					IQuantityCollection<Quantity> thisC = (IQuantityCollection<Quantity>) _inputs
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
