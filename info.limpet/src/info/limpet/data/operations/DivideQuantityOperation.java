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

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.unit.Unit;

public class DivideQuantityOperation implements IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static final String SUM_OF_DIVISION_SERIES = "Product of division of series";

	final protected String outputName;

	public DivideQuantityOperation(String name)
	{
		outputName = name;
	}

	public DivideQuantityOperation()
	{
		this(SUM_OF_DIVISION_SERIES);
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();

		if (appliesTo(selection))
		{
			ICollection item1 = selection.get(0);
			ICollection item2 = selection.get(1);

			String oName = item1.getName() + " / " + item2.getName();
			ICommand<ICollection> newC = new DivideQuantityValues("Divide "
					+ item1.getName() + " by " + item2.getName(), oName, selection,
					item1, item2, destination);
			res.add(newC);
			oName = item2.getName() + " / " + item1.getName();
			newC = new DivideQuantityValues("Divide " + item2.getName() + " by "
					+ item1.getName(), oName, selection, item2, item1, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean res = false;
		// first check we have quantity data
		if (aTests.exactNumber(selection, 2))
		{
			if (aTests.allQuantity(selection))
			{
				// ok, we have quantity data. See if we have series of the same length,
				// or
				// singletons
				res = aTests.allEqualLengthOrSingleton(selection);
			}
		}

		return res;
	}

	public class DivideQuantityValues extends AbstractCommand<ICollection>
	{
		final IQuantityCollection<?> _item1;
		final IQuantityCollection<?> _item2;

		public DivideQuantityValues(String title, String outputName,
				List<ICollection> selection, ICollection item1, ICollection item2,
				IStore store)
		{
			super(title, "Divide provided series", outputName, store, false, false,
					selection);
			_item1 = (IQuantityCollection<?>) item1;
			_item2 = (IQuantityCollection<?>) item2;
		}

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		@Override
		public void execute()
		{
			// get the unit
			Unit<?> unit = calculateOutputUnit();

			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection(getOutputName(),
					this, unit);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs, _item1, _item2);

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
				unit = unit.divide(nextItem.getUnits());
			}
			return unit;
		}

		@Override
		protected void recalculate()
		{
			Unit<?> unit = calculateOutputUnit();

			// update the results
			performCalc(unit, _outputs, _item1, _item2);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(Unit<?> unit, List<ICollection> outputs,
				ICollection item1, ICollection item2)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) outputs
					.iterator().next();
			
			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			// find the (non-singleton) array length
			final int length = getNonSingletonArrayLength(_inputs);

			for (int j = 0; j < length; j++)
			{
				final double thisValue;
				if(_item1.size() == 1)
				{
					thisValue = _item1.getValues().get(0).doubleValue((Unit<?>) _item1.getUnits());
				}
				else
				{
					thisValue =_item1.getValues().get(j).doubleValue((Unit<?>) _item1.getUnits());
				}
				
				final double otherValue;
				if(_item2.size() == 1)
				{
					otherValue = _item2.getValues().get(0).doubleValue((Unit<?>) _item2.getUnits());
				}
				else
				{
					otherValue = _item2.getValues().get(j).doubleValue((Unit<?>) _item2.getUnits()); 
				}
				
				double res = thisValue / otherValue;
				
				target.add(res);
			}
		}
	}

}
