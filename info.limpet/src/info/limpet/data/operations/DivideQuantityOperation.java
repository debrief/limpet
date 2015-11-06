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

public class DivideQuantityOperation implements IOperation<IStoreItem>
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

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();

		if (appliesTo(selection))
		{
			ICollection item1 = (ICollection) selection.get(0);
			ICollection item2 = (ICollection) selection.get(1);

			String oName = item1.getName() + " / " + item2.getName();
			ICommand<IStoreItem> newC = new DivideQuantityValues("Divide "
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

	private boolean appliesTo(List<IStoreItem> selection)
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

	public class DivideQuantityValues extends AbstractCommand<IStoreItem>
	{
		final IQuantityCollection<Quantity> _item1;
		final IQuantityCollection<Quantity> _item2;

		@SuppressWarnings("unchecked")
		public DivideQuantityValues(String title, String outputName,
				List<IStoreItem> selection, ICollection item1, ICollection item2,
				IStore store)
		{
			super(title, "Divide provided series", outputName, store, false, false,
					selection);
			_item1 = (IQuantityCollection<Quantity>) item1;
			_item2 = (IQuantityCollection<Quantity>) item2;
		}

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		@Override
		public void execute()
		{
			// get the unit
			Unit<Quantity> unit = calculateOutputUnit();

			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// ok, generate the new series
			IQuantityCollection<Quantity> target = new QuantityCollection(getOutputName(),
					this, unit);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs, _item1, _item2);

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

		@SuppressWarnings("unchecked")
		private Unit<Quantity> calculateOutputUnit()
		{			
			return (Unit<Quantity>) _item1.getUnits().divide(_item2.getUnits());
		}

		@Override
		protected void recalculate()
		{
			Unit<Quantity> unit = calculateOutputUnit();

			// update the results
			performCalc(unit, outputs, _item1, _item2);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		@SuppressWarnings("unchecked")
		private void performCalc(Unit<Quantity> unit, List<IStoreItem> outputs,
				ICollection item1, ICollection item2)
		{
			IQuantityCollection<Quantity> target = (IQuantityCollection<Quantity>) outputs
					.iterator().next();
			
			// clear out the lists, first
			Iterator<IStoreItem> iter = outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<Quantity> qC = (IQuantityCollection<Quantity>) iter.next();
				qC.clearQuiet();
			}

			// find the (non-singleton) array length
			final int length = getNonSingletonArrayLength(inputs);

			for (int j = 0; j < length; j++)
			{
				final double thisValue;
				if(_item1.size() == 1)
				{
					thisValue = _item1.getValues().get(0).doubleValue((Unit<Quantity>) _item1.getUnits());
				}
				else
				{
					thisValue =_item1.getValues().get(j).doubleValue((Unit<Quantity>) _item1.getUnits());
				}
				
				final double otherValue;
				if(_item2.size() == 1)
				{
					otherValue = _item2.getValues().get(0).doubleValue((Unit<Quantity>) _item2.getUnits());
				}
				else
				{
					otherValue = _item2.getValues().get(j).doubleValue((Unit<Quantity>) _item2.getUnits()); 
				}
				
				double res = thisValue / otherValue;
				
				target.add(res);
			}
		}
	}

}
