package info.limpet.data.operations;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.ITemporalQuantityCollection.InterpMethod;

import java.util.Collection;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

public class SubtractQuantityOperation<Q extends Quantity> extends CoreQuantityOperation<Q> implements IOperation<IQuantityCollection<Q>>
{
	public static final String DIFFERENCE_OF_INPUT_SERIES = "Difference of input series";

	
	public SubtractQuantityOperation(String name)
	{
		super(name);
	}

	public SubtractQuantityOperation()
	{
		this(DIFFERENCE_OF_INPUT_SERIES);
	}

	@Override
	protected boolean appliesTo(List<IQuantityCollection<Q>> selection)
	{
		if (aTests.exactNumber(selection, 2) && aTests.allCollections(selection))
		{
			boolean allQuantity = aTests.allQuantity(selection);
			boolean suitableLength = aTests.allTemporal(selection)
					|| aTests.allNonTemporal(selection)
					&& aTests.allEqualLength(selection);
			boolean equalDimensions = aTests.allEqualDimensions(selection);
			return (allQuantity && suitableLength && equalDimensions);
		}
		else
		{
			return false;
		}
	}

	@Override
	protected void addInterpolatedCommands(List<IQuantityCollection<Q>> selection, IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res)
	{
		ITemporalQuantityCollection<Q> longest = getLongestTemporalCollections(selection);

		if (longest != null)
		{
			IQuantityCollection<Q> item1 =  selection.get(0);
			IQuantityCollection<Q> item2 =  selection.get(1);

			String oName = item2.getName() + " from " + item1.getName();
			ICommand<IQuantityCollection<Q>> newC = new SubtractQuantityValues("Subtract " + item2.getName() + " from " + item1.getName() + " (interpolated)", oName, selection, item1, item2, destination, longest);

			res.add(newC);
			oName = item1.getName() + " from " + item2.getName();
			newC = new SubtractQuantityValues("Subtract " + item1.getName() + " from " + item2.getName() + " (interpolated)", oName, selection, item2, item1,
					destination, longest);
			res.add(newC);
		}
	}

	protected void addIndexedCommands(List<IQuantityCollection<Q>> selection, IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res)
	{
		IQuantityCollection<Q> item1 =  selection.get(0);
		IQuantityCollection<Q> item2 =  selection.get(1);

		String oName = item2.getName() + " from " + item1.getName();
		ICommand<IQuantityCollection<Q>> newC = new SubtractQuantityValues(
				"Subtract " + item2.getName() + " from " + item1.getName() + " (indexed)", oName,
				selection, item1, item2, destination);

		res.add(newC);
		oName = item1.getName() + " from " + item2.getName();
		newC = new SubtractQuantityValues("Subtract " + item1.getName()
				+ " from " + item2.getName() + " (indexed)", oName, selection, item2, item1,
				destination);
		res.add(newC);
	}


	public class SubtractQuantityValues extends
			CoreQuantityCommand
	{
		final IQuantityCollection<Q> _item1;
		final IQuantityCollection<Q> _item2;

		public SubtractQuantityValues(String title, String outputName,
				List<IQuantityCollection<Q>> selection, IQuantityCollection<Q> item1,
				IQuantityCollection<Q> item2, IStore store)
		{
			this(title, outputName, selection, item1, item2, store, null);
		}

		public SubtractQuantityValues(String title, String outputName,
				List<IQuantityCollection<Q>> selection, IQuantityCollection<Q> item1,
				IQuantityCollection<Q> item2, IStore store,
				ITemporalQuantityCollection<Q> timeProvider)
		{
			super(title, "Subtract provided series", outputName, store, false, false,
					selection, timeProvider);
			_item1 =  item1;
			_item2 =  item2;
		}

		@Override
		protected Double calcThisElement(int elementCount)
		{
			final Measurable<Q> thisValue = _item1.getValues().get(elementCount);
			final Measurable<Q> otherValue = _item2.getValues().get(elementCount);
			double runningTotal = thisValue.doubleValue(_item1.getUnits()) - otherValue.doubleValue(_item2.getUnits());
			return runningTotal;
		}

		@Override
		protected Double calcThisInterpolatedElement(long time)
		{
			ITemporalQuantityCollection<Q> tqc1 = (ITemporalQuantityCollection<Q>) _item1;
			ITemporalQuantityCollection<Q> tqc2 =  (ITemporalQuantityCollection<Q>) _item2;

			final Measurable<Q> thisValue = (Measurable<Q>) tqc1.interpolateValue(
					time, InterpMethod.Linear);
			double thisD = 0;
			if (thisValue != null)
				thisD = thisValue.doubleValue(_item1.getUnits());

			final Measurable<Q> otherValue = (Measurable<Q>) tqc2.interpolateValue(
					time, InterpMethod.Linear);
			double otherD = 0;
			if (otherValue != null)
				otherD = otherValue.doubleValue(_item1.getUnits());
			return thisD - otherD;
		}
	}
}
