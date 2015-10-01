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

public class UnitConversionOperation implements
		IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static final String UNIT_CONVERSION_OF_INPUT_SERIES = "Unit conversion of input series";

	final protected String outputName;
	
	final protected Unit<?> targetUnit;

	public UnitConversionOperation(String name, Unit<?> targetUnit)
	{
		this.outputName = name;
		this.targetUnit = targetUnit;
	}

	public UnitConversionOperation(Unit<?> targetUnit)
	{
		this(UNIT_CONVERSION_OF_INPUT_SERIES, targetUnit);
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new ConvertQuanityValues(
					outputName, selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean singleSeries = selection.size() == 1;
		boolean allQuantity = aTests.allQuantity(selection);
		boolean sameDimension = false;
		if (allQuantity) {
			Unit<?> units = ((IQuantityCollection<?>)selection.get(0)).getUnits();		
			sameDimension = units.getDimension().equals(targetUnit.getDimension());
		}
		return (singleSeries && allQuantity && sameDimension);
	}

	public class ConvertQuanityValues extends
			AbstractCommand<ICollection>
	{

		public ConvertQuanityValues(String outputName,
				List<ICollection> selection, IStore store)
		{
			super("Convert series units", "Convert units of the provided series", outputName,
					store, false, false, selection);
		}

		@Override
		public void execute()
		{
			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>(
					getOutputName(), this, targetUnit);

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(outputs);

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

		@Override
		protected void recalculate()
		{
			// update the results
			performCalc(_outputs);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(List<ICollection> outputs)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) outputs.iterator().next();

			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			IQuantityCollection<?> singleInputSeries = (IQuantityCollection<?>) _inputs
					.get(0);
			
			for (int j = 0; j < singleInputSeries.getValues().size(); j++)
			{
				
				// TODO: figure out how to avoid the compiler warnings
				@SuppressWarnings("rawtypes")
				Quantity thisValue = singleInputSeries.getValues().get(j);				
				@SuppressWarnings("unchecked")
				Quantity<?> converted = thisValue.to(targetUnit);
				
				target.add(converted.getValue());
			}
		}
	}

}
