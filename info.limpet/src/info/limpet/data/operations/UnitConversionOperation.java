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
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class UnitConversionOperation implements IOperation<ICollection>
{
	public static final String CONVERTED_TO = " converted to ";

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	final protected Unit<?> targetUnit;

	public UnitConversionOperation(Unit<?> targetUnit)
	{
		this.targetUnit = targetUnit;
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			String unitsName = targetUnit.toString();
			String name = "Convert to " + unitsName;
			String outputName = CONVERTED_TO + unitsName;
			ICommand<ICollection> newC = new ConvertQuanityValues(name, outputName,
					selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean singleSeries = selection.size() == 1;
		boolean allQuantity = aTests.allQuantity(selection);
		boolean sameDimension = false;
		boolean sameUnits = true;
		if (selection.size() > 0)
		{
			if (allQuantity)
			{
				Unit<?> units = ((IQuantityCollection<?>) selection.get(0)).getUnits();
				sameDimension = units.getDimension().equals(targetUnit.getDimension());

				// check they're different units. It's not worth offering the operation
				// if
				// they're already in the same units
				sameUnits = units.equals(targetUnit);
			}
		}
		return (singleSeries && allQuantity && sameDimension && !sameUnits);
	}

	public class ConvertQuanityValues extends AbstractCommand<ICollection>
	{

		public ConvertQuanityValues(String operationName, String outputName,
				List<ICollection> selection, IStore store)
		{
			super(operationName, "Convert units of the provided series", outputName,
					store, false, false, selection);
		}

		@Override
		public void execute()
		{
			List<ICollection> outputs = new ArrayList<ICollection>();

			String seriesName = _inputs.iterator().next().getName();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>(seriesName
					+ getOutputName(), this, targetUnit);

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
			getStore().addAll(res);
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
		@SuppressWarnings("unchecked")
		private void performCalc(List<ICollection> outputs)
		{
			IQuantityCollection<Quantity> target = (IQuantityCollection<Quantity>) outputs
					.iterator().next();

			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<Quantity> qC = (IQuantityCollection<Quantity>) iter.next();
				qC.getValues().clear();
			}

			IQuantityCollection<Quantity> singleInputSeries = (IQuantityCollection<Quantity>) _inputs
					.get(0);

			UnitConverter converter = singleInputSeries.getUnits().getConverterTo(target.getUnits());
			
			for (int j = 0; j < singleInputSeries.getValues().size(); j++)
			{

				Measurable<Quantity> thisValue = singleInputSeries.getValues().get(j);
				double converted = converter.convert(thisValue.doubleValue(singleInputSeries.getUnits()));
				target.add(converted);
			}
		}
	}

}
