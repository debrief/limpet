package info.limpet.data.operations;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;

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

	protected void addCommands(List<IQuantityCollection<Q>> selection,
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
		boolean equalLength = aTests.allEqualLength(selection);
		boolean equalDimensions = aTests.allEqualDimensions(selection);
		boolean equalUnits = aTests.allEqualUnits(selection);
		return (nonEmpty && allQuantity && equalLength && equalDimensions && equalUnits);
	}

	public class AddQuantityValues<T extends Quantity> extends
			CoreQuantityOperation<Q>.CoreQuantityCommand
	{

		public AddQuantityValues(String outputName,
				List<IQuantityCollection<Q>> selection, IStore store)
		{
			super("Add series", "Add numeric values in provided series", outputName,
					store, false, false, selection);
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
	}

}
