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

import javax.measure.Quantity;
import javax.measure.Unit;

public class SubtractQuantityOperation<Q extends Quantity<Q>> implements
		IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static final String SUM_OF_INPUT_SERIES = "Sum of input series";

	final protected String outputName;

	public SubtractQuantityOperation(String name)
	{
		outputName = name;
	}

	public SubtractQuantityOperation()
	{
		this(SUM_OF_INPUT_SERIES);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();

		ICollection item1 = selection.get(0);
		ICollection item2 = selection.get(1);

		if (appliesTo(selection))
		{
			String oName = item2.getName() + " from " + item1.getName();
			ICommand<ICollection> newC = new SubtractQuantityValues("Subtract "
					+ item2.getName() + " from " + item1.getName(), oName, selection,
					item1, item2, destination);
			res.add(newC);
			oName = item1.getName() + " from " + item2.getName();
			newC = new SubtractQuantityValues("Subtract " + item1.getName()
					+ " from " + item2.getName(), oName, selection, item2, item1,
					destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean allQuantity = aTests.allQuantity(selection);
		boolean onlyTwoCollections = aTests.exactNumber(selection, 2);
		boolean equalLength = aTests.allEqualLength(selection);
		boolean equalDimensions = aTests.allEqualDimensions(selection);
		return (allQuantity && equalLength && equalDimensions && onlyTwoCollections);
	}

	public class SubtractQuantityValues<T extends Quantity<T>> extends
			AbstractCommand<ICollection>
	{
		IQuantityCollection<T> _item1;
		IQuantityCollection<T> _item2;

		@SuppressWarnings("unchecked")
		public SubtractQuantityValues(String title, String outputName,
				List<ICollection> selection, ICollection item1, ICollection item2,
				IStore store)
		{
			super(title, "Subtract provided series", outputName, store, false, false,
					selection);
			_item1 = (IQuantityCollection<T>) item1;
			_item2 = (IQuantityCollection<T>) item2;
		}

		@Override
		public void execute()
		{
			// get the unit
			Unit<T> unit = _item1.getUnits();

			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			IQuantityCollection<T> target = new QuantityCollection<T>(
					getOutputName(), this, unit);

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
			getStore().add(res);
		}

		@SuppressWarnings(
		{ "rawtypes", "unchecked" })
		@Override
		protected void recalculate()
		{
			// get the unit
			IQuantityCollection first = (IQuantityCollection) _inputs.get(0);
			Unit<T> unit = first.getUnits();

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
		@SuppressWarnings("unchecked")
		private void performCalc(Unit<T> unit, List<ICollection> outputs,
				ICollection item1, ICollection item2)
		{
			IQuantityCollection<T> target = (IQuantityCollection<T>) outputs
					.iterator().next();

			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<T> qC = (IQuantityCollection<T>) iter.next();
				qC.getValues().clear();
			}

			for (int j = 0; j < _inputs.get(0).size(); j++)
			{
				final Quantity<T> thisValue = _item1.getValues().get(j);
				final Quantity<T> otherValue = _item2.getValues().get(j);
				Quantity<T> runningTotal = thisValue.subtract(otherValue);

				target.add(runningTotal);
			}
		}
	}

}
