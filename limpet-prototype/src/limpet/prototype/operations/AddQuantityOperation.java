package limpet.prototype.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import limpet.prototype.commands.AbstractCommand;
import limpet.prototype.commands.ICommand;
import limpet.prototype.generics.dinko.impl.QuantityCollection;
import limpet.prototype.generics.dinko.interfaces.ICollection;
import limpet.prototype.generics.dinko.interfaces.IQuantityCollection;
import limpet.prototype.store.IStore;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class AddQuantityOperation extends BaseOperation
{
	@Override
	public Collection<ICommand> actionsFor(List<ICollection> selection, IStore destination)
	{
		Collection<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection))
		{
			AddQuantityValues newC = new AddQuantityValues(selection, destination);
			res.add(newC);
		}

		return res;
	}

	protected boolean appliesTo(List<ICollection> selection)
	{
		// are the all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);
			if (thisC.isQuantity())
			{
				// great, valid, check the size
				if (size == -1)
				{
					size = thisC.size();
				}
				else
				{
					if (size != thisC.size())
					{
						// oops, no
						allValid = false;
						break;
					}
				}
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}

		}

		return allValid;
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
			// ok, generate the new series
			Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
					.asType(Speed.class);
			IQuantityCollection<Speed> target = new QuantityCollection<Speed>(
					"Speed Total", kmh);

			// start adding values.
			for (int j = 0; j < _series.get(0).size(); j++)
			{
				double runningTotal = 0;
				for (int i = 0; i < _series.size(); i++)
				{
					@SuppressWarnings("unchecked")
					IQuantityCollection<Speed> thisC = (IQuantityCollection<Speed>) _series.get(0);
					double thisQ = thisC.getValues().get(j).getValue().doubleValue();
					runningTotal += thisQ;
				}
				
				Quantity<Speed> value = (Quantity<Speed>) DefaultQuantityFactory.getInstance(Speed.class)
						.create(runningTotal, (Unit<Speed>) kmh);

				target.add(value);
			}
			
			// ok, done
			List<ICollection> res = new ArrayList<ICollection>();
			res.add(target);
			getStore().add(res);

		}
	}

}
