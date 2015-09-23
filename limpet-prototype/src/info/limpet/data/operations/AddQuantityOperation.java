package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.quantity.Quantities;

public class AddQuantityOperation extends BaseOperation
{
	@Override
	public Collection<ICommand> actionsFor(List<ICollection> selection,
			IStore destination)
	{
		Collection<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection))
		{
			AddQuantityValues newC = new AddQuantityValues(selection, destination);
			res.add(newC);
		}

		return res;
	}
	
	

	private boolean appliesTo(List<ICollection> selection)
	{
		return (allQuantity(selection) && allEqualLength(selection) && allEqualDimensions(selection) && allEqualUnits(selection));
	}



	public static class AddQuantityValues extends AbstractCommand
	{

		private List<ICollection> _series;

		public AddQuantityValues(List<ICollection> selection, IStore store)
		{
			super("Add series", "Add numeric values in provided series", store,
					false, false);
			_series = selection;
		}

		@Override
		public void execute()
		{
			// TODO: DINKO - remove hard-coded Speed value. Use quantity from first series
			// TODO: DINKO - sort out dimension / units consistency for this implementation
			
			// get the dimensions & units
			IQuantityCollection<?> first = (IQuantityCollection<?>) _series.get(0);
			@SuppressWarnings("unused")
			Dimension dim = first.getDimension();
			@SuppressWarnings("unchecked")
			Unit<Speed> units = (Unit<Speed>) first.getUnits();
			
			// ok, generate the new series
			IQuantityCollection<Speed> target = new QuantityCollection<Speed>(
					"Speed Total", units);

			// start adding values.
			for (int j = 0; j < _series.get(0).size(); j++)
			{
				double runningTotal = 0;
				for (int i = 0; i < _series.size(); i++)
				{
					@SuppressWarnings("unchecked")
					IQuantityCollection<Speed> thisC = (IQuantityCollection<Speed>) _series
							.get(0);
					double thisQ = thisC.getValues().get(j).getValue().doubleValue();
					runningTotal += thisQ;
				}

				Quantity<Speed> value = Quantities.getQuantity(runningTotal, units);

				target.add(value);
			}

			// ok, done
			List<ICollection> res = new ArrayList<ICollection>();
			res.add(target);
			getStore().add(res);

		}
	}

}
