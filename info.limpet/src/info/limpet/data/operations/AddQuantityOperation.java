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

public class AddQuantityOperation implements IOperation
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	@Override
	public Collection<ICommand> actionsFor(List<ICollection> selection,
			IStore destination)
	{
		Collection<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection))
		{
			ICommand newC = new AddQuantityValues<>(selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		return (aTests.allQuantity(selection) && aTests.allEqualLength(selection)
				&& aTests.allEqualDimensions(selection) && aTests
					.allEqualUnits(selection));
	}

	public static class AddQuantityValues<T extends Quantity<T>> extends
			AbstractCommand
	{

		public AddQuantityValues(List<ICollection> selection, IStore store)
		{
			super("Add series", "Add numeric values in provided series", store,
					false, false, selection);
		}

		@Override
		public void execute()
		{
			// get the unit
			@SuppressWarnings("unchecked")
			IQuantityCollection<T> first = (IQuantityCollection<T>) _inputs.get(0);
			Unit<T> unit = (Unit<T>) first.getUnits();

			// ok, generate the new series
			IQuantityCollection<T> target = new QuantityCollection<T>("Speed Total",
					this, unit);

			// store the output
			addOutput(target);

			// start adding values.
			for (int j = 0; j < _inputs.get(0).size(); j++)
			{
				Quantity<T> runningTotal = Quantities.getQuantity(0, unit);

				for (int i = 0; i < _inputs.size(); i++)
				{
					@SuppressWarnings("unchecked")
					IQuantityCollection<T> thisC = (IQuantityCollection<T>) _inputs
							.get(i);
					runningTotal = runningTotal.add((Quantity<T>) thisC.getValues().get(j));
				}

				target.add(runningTotal);
			}

			// tell each series that we're a dependent
			Iterator<ICollection> iter = _inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<ICollection> res = new ArrayList<ICollection>();
			res.add(target);
			getStore().add(res);

		}
	}

}
