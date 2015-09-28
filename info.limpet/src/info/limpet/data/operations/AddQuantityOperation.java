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

public class AddQuantityOperation<Q extends Quantity<Q>> implements
		IOperation<IQuantityCollection<Q>>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static final String SUM_OF_INPUT_SERIES = "Sum of input series";

	final protected String outputName;
	
	public AddQuantityOperation(String name)
	{
		outputName = name;
	}

	public AddQuantityOperation()
	{
		this(SUM_OF_INPUT_SERIES);
	}

	public Collection<ICommand<IQuantityCollection<Q>>> actionsFor(List<IQuantityCollection<Q>> selection,
			IStore destination)
	{
		Collection<ICommand<IQuantityCollection<Q>>> res = new ArrayList<ICommand<IQuantityCollection<Q>>>();
		if (appliesTo(selection))
		{
			ICommand<IQuantityCollection<Q>> newC = new AddQuantityValues<Q>(selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IQuantityCollection<Q>> selection)
	{
		boolean allQuantity = aTests.allQuantity(selection);
		boolean equalLength = aTests.allEqualLength(selection);
		boolean equalDimensions = aTests.allEqualDimensions(selection);
		boolean equalUnits = aTests.allEqualUnits(selection);
		return (allQuantity && equalLength
				&& equalDimensions && equalUnits);
	}

	public class AddQuantityValues<T extends Quantity<T>> extends
			AbstractCommand<IQuantityCollection<T>>
	{


		public AddQuantityValues(List<IQuantityCollection<T>> selection,
				IStore store)
		{
			super("Add series", "Add numeric values in provided series", store,
					false, false, selection);
		}

		@Override
		public void execute()
		{
			// get the unit
			IQuantityCollection<T> first = _inputs.get(0);
			Unit<T> unit = first.getUnits();

			List<IQuantityCollection<T>> outputs = new ArrayList<IQuantityCollection<T>>();
			
			// ok, generate the new series
			IQuantityCollection<T> target = new QuantityCollection<T>(outputName,
					this, unit);
			
			outputs.add(target);
			
			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs);

			// tell each series that we're a dependent
			Iterator<IQuantityCollection<T>> iter = _inputs.iterator();
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
			// get the unit
			IQuantityCollection<T> first = _inputs.get(0);
			Unit<T> unit = first.getUnits();

			// update the results
			performCalc(unit, _outputs);
		}
		
		/** wrap the actual operation.  We're doing this since we need to separate it from the core "execute" 
		 * operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(Unit<T> unit, List<IQuantityCollection<T>> outputs)
		{
			IQuantityCollection<T> target = outputs.iterator().next();

			// clear out the lists, first
			Iterator<IQuantityCollection<T>> iter = _inputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<T> qC = (IQuantityCollection<T>) iter
						.next();
				qC.getValues().clear();				
			}
			
			for (int j = 0; j < _inputs.get(0).size(); j++)
			{
				Quantity<T> runningTotal = Quantities.getQuantity(0d, unit);

				for (int i = 0; i < _inputs.size(); i++)
				{
					IQuantityCollection<T> thisC = _inputs.get(i);
					runningTotal = runningTotal.add((Quantity<T>) thisC.getValues()
							.get(j));
				}

				target.add(runningTotal);
			}
		}
	}

}
