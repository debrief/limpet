package info.limpet.data.impl.samples;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;

import tec.units.ri.unit.Units;

public class SampleData
{
	public IStore getData()
	{
		InMemoryStore res = new InMemoryStore();


		// // collate our data series
		StockTypes.Temporal.Speed_MSec speedSeries1 = new StockTypes.Temporal.Speed_MSec(
				"Speed One");
		StockTypes.Temporal.Speed_MSec speedSeries2 = new StockTypes.Temporal.Speed_MSec(
				"Speed Two");
		StockTypes.Temporal.Speed_MSec speedSeries3 = new StockTypes.Temporal.Speed_MSec(
				"Speed Three (longer)");
		StockTypes.Temporal.Length_M length1 = new StockTypes.Temporal.Length_M(
				"Length One");
		ITemporalQuantityCollection<Length> length2 = new StockTypes.Temporal.Length_M(
				"Length Two");
		IObjectCollection<String> string1 = new ObjectCollection<String>(
				"String one");
		IObjectCollection<String> string2 = new ObjectCollection<String>(
				"String two");
		IQuantityCollection<Dimensionless> singleton1 = new QuantityCollection<Dimensionless>(
				"Floating point factor", Units.ONE.asType(Dimensionless.class));

		long thisTime = 0; 
		
		for (int i = 1; i <= 10; i++)
		{
			thisTime = new Date().getTime() + i * 500 * 60;

			speedSeries1.add(thisTime, i);
			speedSeries2.add(thisTime, Math.sin(i));
			speedSeries3.add(thisTime, 3 * Math.cos(i));
			length1.add(thisTime, i % 3);
			length2.add(thisTime, i % 5);
			string1.add("item " + i);
			string2.add("item " + (i % 3));
		}

		// add an extra item to speedSeries3
		speedSeries3.add(thisTime + 12 * 500 * 60, 25);
		
		// give the singleton a value		
		singleton1.add(4d);
		
		List<ICollection> list = new ArrayList<ICollection>();

		list.add(speedSeries1);
		list.add(speedSeries2);
		list.add(speedSeries3);
		list.add(length1);
		list.add(length2);
		list.add(string1);
		list.add(string2);
		list.add(singleton1);

		res.add(list);

		// perform an operation, so we have some audit trail
		List<ICollection> selection = new ArrayList<ICollection>();
		selection.add(speedSeries1);
		selection.add(speedSeries2);
		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(selection, res);
		ICommand<?> addAction = actions.iterator().next();
		addAction.execute();	
		
		return res;
	}
}
