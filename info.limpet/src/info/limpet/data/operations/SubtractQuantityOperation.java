package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.ITemporalQuantityCollection.InterpMethod;

import java.util.Collection;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

public class SubtractQuantityOperation<Q extends Quantity> extends
		CoreQuantityOperation<Q> implements IOperation<IQuantityCollection<Q>>
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

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Override
	protected void addInterpolatedCommands(
			List<IQuantityCollection<Q>> selection, IStore destination,
			Collection<ICommand<IQuantityCollection<Q>>> res)
	{
		ITemporalQuantityCollection<Q> longest = getLongestTemporalCollections(selection);

		if (longest != null)
		{
			ICollection item1 = (ICollection) selection.get(0);
			ICollection item2 = (ICollection) selection.get(1);

			String oName = item2.getName() + " from " + item1.getName();
			ICommand<? extends IStoreItem> newC = new SubtractQuantityValues(
					"Subtract " + item2.getName() + " from " + item1.getName(), oName,
					selection, item1, item2, destination, longest);
			res.add((ICommand<IQuantityCollection<Q>>) newC);
			oName = item1.getName() + " from " + item2.getName();
			newC = new SubtractQuantityValues("Subtract " + item1.getName()
					+ " from " + item2.getName(), oName, selection, item2, item1,
					destination, longest);
			res.add((ICommand<IQuantityCollection<Q>>) newC);
		}
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected void addIndexedCommands(List<IQuantityCollection<Q>> selection,
			IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res)
	{
		ICollection item1 = (ICollection) selection.get(0);
		ICollection item2 = (ICollection) selection.get(1);

		String oName = item2.getName() + " from " + item1.getName();
		ICommand<? extends IStoreItem> newC = new SubtractQuantityValues(
				"Subtract " + item2.getName() + " from " + item1.getName(), oName,
				selection, item1, item2, destination);
		res.add((ICommand<IQuantityCollection<Q>>) newC);
		oName = item1.getName() + " from " + item2.getName();
		newC = new SubtractQuantityValues("Subtract " + item1.getName() + " from "
				+ item2.getName(), oName, selection, item2, item1, destination);
		res.add((ICommand<IQuantityCollection<Q>>) newC);
	}

	public class SubtractQuantityValues<T extends Quantity> extends
			CoreQuantityOperation<Q>.CoreQuantityCommand
	{
		final IQuantityCollection<T> _item1;
		final IQuantityCollection<T> _item2;

		public SubtractQuantityValues(String title, String outputName,
				List<IQuantityCollection<Q>> selection, ICollection item1,
				ICollection item2, IStore store)
		{
			this(title, outputName, selection, item1, item2, store, null);
		}

		@SuppressWarnings("unchecked")
		public SubtractQuantityValues(String title, String outputName,
				List<IQuantityCollection<Q>> selection, ICollection item1,
				ICollection item2, IStore store,
				ITemporalQuantityCollection<Q> timeProvider)
		{
			super(title, "Subtract provided series", outputName, store, false, false,
					selection, timeProvider);
			_item1 = (IQuantityCollection<T>) item1;
			_item2 = (IQuantityCollection<T>) item2;
		}

		@Override
		protected Double calcThisElement(int elementCount)
		{
			final Measurable<T> thisValue = _item1.getValues().get(elementCount);
			final Measurable<T> otherValue = _item2.getValues().get(elementCount);
			double runningTotal = thisValue.doubleValue(_item1.getUnits())
					- otherValue.doubleValue(_item2.getUnits());
			return runningTotal;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Double calcThisInterpolatedElement(long time)
		{
			ITemporalQuantityCollection<Q> tqc1 = (ITemporalQuantityCollection<Q>) _item1;
			ITemporalQuantityCollection<Q> tqc2 = (ITemporalQuantityCollection<Q>) _item2;

			final Measurable<T> thisValue = (Measurable<T>) tqc1.interpolateValue(
					time, InterpMethod.Linear);
			double thisD = 0;
			if (thisValue != null)
				thisD = thisValue.doubleValue(_item1.getUnits());

			final Measurable<T> otherValue = (Measurable<T>) tqc2.interpolateValue(
					time, InterpMethod.Linear);
			double otherD = 0;
			if (otherValue != null)
				otherD = otherValue.doubleValue(_item1.getUnits());

			return thisD - otherD;
		}

	}

}
