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

public class AddQuantityOperation<Q extends Quantity> extends
		CoreQuantityOperation<Q> implements IOperation<IQuantityCollection<Q>>
{
	public static final String SUM_OF_INPUT_SERIES = "Sum of input series";

	public AddQuantityOperation(String name)
	{
		super(name);
	}

	public AddQuantityOperation()
	{
		this(SUM_OF_INPUT_SERIES);
	}

	@Override
	protected void addInterpolatedCommands(
			List<IQuantityCollection<Q>> selection, IStore destination,
			Collection<ICommand<IQuantityCollection<Q>>> res)
	{
		ITemporalQuantityCollection<Q> longest = getLongestTemporalCollections(selection);

		if (longest != null)
		{
			ICommand<IQuantityCollection<Q>> newC = new AddQuantityValues<Q>(
					outputName + " (interpolated)", selection, destination, longest);
			res.add(newC);
		}
	}

	protected void addIndexedCommands(List<IQuantityCollection<Q>> selection,
			IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res)
	{
		ICommand<IQuantityCollection<Q>> newC = new AddQuantityValues<Q>(
				outputName, selection, destination);
		res.add(newC);
	}

	protected boolean appliesTo(List<IQuantityCollection<Q>> selection)
	{
		boolean nonEmpty = aTests.nonEmpty(selection);
		boolean allQuantity = aTests.allQuantity(selection);
		boolean suitableLength = aTests.allTemporal(selection)
				|| aTests.allNonTemporal(selection) && aTests.allEqualLength(selection);
		boolean equalDimensions = aTests.allEqualDimensions(selection);
		boolean equalUnits = aTests.allEqualUnits(selection);

		return (nonEmpty && allQuantity && suitableLength && equalDimensions && equalUnits);
	}

	public class AddQuantityValues<T extends Quantity> extends
			CoreQuantityOperation<Q>.CoreQuantityCommand
	{

		public AddQuantityValues(String outputName,
				List<IQuantityCollection<Q>> selection, IStore store)
		{
			super(outputName, "Add numeric values in provided series", outputName,
					store, false, false, selection);
		}

		public AddQuantityValues(String outputName,
				List<IQuantityCollection<Q>> selection, IStore destination,
				ITemporalQuantityCollection<Q> timeProvider)
		{
			super(outputName, "Add numeric values in provided series", outputName,
					destination, false, false, selection, timeProvider);
		}

		@Override
		protected Double calcThisElement(int elementCount)
		{
			Double thisResult = null;

			for (int seriesCount = 0; seriesCount < inputs.size(); seriesCount++)
			{
				IQuantityCollection<Q> thisC = inputs.get(seriesCount);
				Measurable<Q> thisV = (Measurable<Q>) thisC.getValues().get(
						elementCount);

				// is this the first field?
				if (thisResult == null)
				{
					thisResult = thisV.doubleValue(thisC.getUnits());
				}
				else
				{
					thisResult += thisV.doubleValue(thisC.getUnits());
				}
			}
			return thisResult;
		}

		@Override
		protected Double calcThisInterpolatedElement(long time)
		{
			Double thisResult = null;

			for (int seriesCount = 0; seriesCount < inputs.size(); seriesCount++)
			{
				ITemporalQuantityCollection<Q> thisC = (ITemporalQuantityCollection<Q>) inputs
						.get(seriesCount);

				// find the value to use
				Measurable<Q> thisV = thisC.interpolateValue(time, InterpMethod.Linear);

				if (thisV != null)
				{
					// is this the first field?
					if (thisResult == null)
					{
						thisResult = thisV.doubleValue(thisC.getUnits());
					}
					else
					{
						thisResult += thisV.doubleValue(thisC.getUnits());
					}
				}
			}
			return thisResult;
		}
	}

}
