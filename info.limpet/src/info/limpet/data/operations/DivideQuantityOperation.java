package info.limpet.data.operations;

import java.util.Collection;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.ITemporalQuantityCollection.InterpMethod;

public class DivideQuantityOperation<Q extends Quantity> extends CoreQuantityOperation<Q> implements IOperation<IQuantityCollection<Q>>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static final String SUM_OF_DIVISION_SERIES = "Product of division of series";

	public DivideQuantityOperation(String name)
	{
		super(name);
	}

	public DivideQuantityOperation()
	{
		this(SUM_OF_DIVISION_SERIES);
	}
	
	@Override
	protected boolean appliesTo(List<IQuantityCollection<Q>> selection) {
		//boolean res = false;
		if (aTests.exactNumber(selection, 2) && aTests.allCollections(selection))
		{
			boolean allQuantity = aTests.allQuantity(selection);
			boolean suitableLength = aTests.allTemporal(selection) || aTests.allNonTemporal(selection) && aTests.allEqualLength(selection);
			boolean equalDimensions = aTests.allEqualDimensions(selection);
			return (allQuantity && suitableLength && equalDimensions);
		}
		else
		{
			return false;
		}
	}
	public class DivideQuantityValues extends CoreQuantityCommand
	{
		final IQuantityCollection<Q> _item1;
		final IQuantityCollection<Q> _item2;
		//private IBaseTemporalCollection _timeProvider;

		public DivideQuantityValues(String title, String outputName,
				List<IQuantityCollection<Q>> selection, IQuantityCollection<Q> item1,
				IQuantityCollection<Q> item2, IStore store)
		{
			this(title, outputName, selection, item1, item2, store, null);
		}
		
		public DivideQuantityValues(String title, String outputName, List<IQuantityCollection<Q>> selection, IQuantityCollection<Q> item1, IQuantityCollection<Q> item2, IStore store, IBaseTemporalCollection timeProvider)
		{
			super(title, "Divide provided series", outputName, store, false, false,selection);
			_item1 = item1;
			_item2 = item2;
			//_timeProvider = timeProvider;
		}

		/**
		 * produce a target of the correct type
		 * 
		 * @param input
		 *          one of the input series
		 * @param unit
		 *          the units to use
		 * @return
		 */
		public DivideQuantityValues(String title, String outputName, List<IQuantityCollection<Q>> selection, IQuantityCollection<Q> item1,
				IQuantityCollection<Q> item2, IStore store, ITemporalQuantityCollection<Q> timeProvider)
		{
			super(title, "Divide provided series", outputName, store, false, false, selection, timeProvider);
			_item1 =  item1;
			_item2 =  item2;
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */

		@Override
		protected Double calcThisElement(int elementCount) {
			final Measurable<Q> thisValue = _item1.getValues().get(elementCount);
			final Measurable<Q> otherValue = _item2.getValues().get(elementCount);
			double runningTotal = thisValue.doubleValue(_item1.getUnits()) / otherValue.doubleValue(_item2.getUnits());
			return runningTotal;
		}

		@Override
		protected Double calcThisInterpolatedElement(long time) {
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
			return thisD / otherD;
		}
	}

	@Override
	protected void addIndexedCommands(List<IQuantityCollection<Q>> selection, IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res) {
		
		IQuantityCollection<Q> item1 =  selection.get(0);
		IQuantityCollection<Q> item2 =  selection.get(1);

		String oName = item2.getName() + " from " + item1.getName();
		
		ICommand<IQuantityCollection<Q>> newC = new DivideQuantityValues("Divide " + item2.getName() + " from " + item1.getName(), oName, selection, item1, item2, destination);

		res.add(newC);
		oName = item1.getName() + " from " + item2.getName();
		newC = new DivideQuantityValues("Divide " + item1.getName()
				+ " from " + item2.getName(), oName, selection, item2, item1,
				destination, null);
		res.add(newC);
		
	}

	@Override
	protected void addInterpolatedCommands(List<IQuantityCollection<Q>> selection, IStore destination,
			Collection<ICommand<IQuantityCollection<Q>>> res) {
		ITemporalQuantityCollection<Q> longest = getLongestTemporalCollections(selection);

		if (longest != null)
		{
			IQuantityCollection<Q> item1 =  selection.get(0);
			IQuantityCollection<Q> item2 =  selection.get(1);

			String oName = item2.getName() + " from " + item1.getName();
			ICommand<IQuantityCollection<Q>> newC = new DivideQuantityValues(
					"Divide " + item2.getName() + " from " + item1.getName(), oName,
					selection, item1, item2, destination, longest);

			res.add(newC);
			oName = item1.getName() + " from " + item2.getName();
			newC = new DivideQuantityValues("Divide " + item1.getName()
					+ " from " + item2.getName(), oName, selection, item2, item1,
					destination, longest);
			res.add(newC);
		}
	}

}