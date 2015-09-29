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

import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

public class MultiplyQuantityOperation implements
		IOperation<IQuantityCollection<?>>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public Collection<ICommand<IQuantityCollection<?>>> actionsFor(
			List<IQuantityCollection<?>> selection, IStore destination)
	{
		Collection<ICommand<IQuantityCollection<?>>> res = new ArrayList<ICommand<IQuantityCollection<?>>>();
		if (appliesTo(selection))
		{
			ICommand<IQuantityCollection<?>> newC = new MultiplyQuantityValues(
					selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IQuantityCollection<?>> selection)
	{
		// first check we have quantity data
		if (!aTests.allQuantity(selection))
		{
			return false;
		}
		else
		{
			// ok, we have quantity data. See if we have series of the same length, or
			// singletons
			return aTests.allEqualLengthOrSingleton(selection);
		}
	}

	public static class MultiplyQuantityValues extends
			AbstractCommand<IQuantityCollection<?>>
	{

		public MultiplyQuantityValues(List<IQuantityCollection<?>> selection,
				IStore store)
		{
			super("Multiply Series", "Multiply series", store, false, false,
					selection);
		}

		private int getNonSingletonArrayLength(List<IQuantityCollection<?>> inputs)
		{
			int size = 0;

			Iterator<IQuantityCollection<?>> iter = inputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> thisC = (IQuantityCollection<?>) iter.next();
				if (thisC.size() > 1)
				{
					size = thisC.size();
					break;
				}
			}

			return size;
		}

		
		@Override
		public void execute()
		{			
			Unit<?> unit = calculateOutputUnit();
			List<IQuantityCollection<?>> outputs = new ArrayList<IQuantityCollection<?>>();
			
			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>("Multiplication product",
					this, unit);
			
			outputs.add(target);
			
			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs);

			// tell each series that we're a dependent
			Iterator<IQuantityCollection<?>> iter = _inputs.iterator();
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

		private Unit<?> calculateOutputUnit()
		{
			Iterator<IQuantityCollection<?>> inputsIterator = _inputs.iterator();
			Unit<?> unit = inputsIterator.next().getUnits();
			while (inputsIterator.hasNext()) {
				unit = unit.multiply(inputsIterator.next().getUnits());
			}
			return unit;
		}
		
		@Override
		protected void recalculate()
		{
			Unit<?> unit = calculateOutputUnit();

			// update the results
			performCalc(unit, _outputs);
		}
		
		/** wrap the actual operation.  We're doing this since we need to separate it from the core "execute" 
		 * operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(Unit<?> unit, List<IQuantityCollection<?>> outputs)
		{
			// TODO: Dinko - we don't use a single set of units here, they results
			// type has to change
			// as the multiplications occur. Or, we may have to calculate the units
			// first (in order
			// to declare the "target" collection, then populate that collection

			
			IQuantityCollection<?> target = outputs.iterator().next();

			// clear out the lists, first
			Iterator<IQuantityCollection<?>> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter
						.next();
				qC.getValues().clear();	
				
				// hey, if it's a time series we need to clear the times, too
			}
			

			// find the (non-singleton) array length
			int length = getNonSingletonArrayLength(_inputs);

			// start adding values.
			for (int j = 0; j < length; j++)
			{
				Quantity<?> runningTotal = Quantities.getQuantity(0, unit);

				for (int i = 0; i < _inputs.size(); i++)
				{
					IQuantityCollection<?> thisC = _inputs.get(i);

					final Quantity<?> thisValue;

					// just check that this isn't a singleton
					if (thisC.size() == 1)
					{
						thisValue = thisC.getValues().get(0);
					}
					else
					{
						thisValue = (Quantity<?>) thisC.getValues().get(j);
					}

					// TODO: Dinko - here's the dodgy bit where we switch from addition
					// to multiplication
					runningTotal = runningTotal.multiply(thisValue);
				}

				target.add(runningTotal);
			}
		}
		
			}

}
