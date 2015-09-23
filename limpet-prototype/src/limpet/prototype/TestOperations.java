package limpet.prototype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import limpet.prototype.commands.ICommand;
import limpet.prototype.generics.dinko.impl.QuantityCollection;
import limpet.prototype.generics.dinko.interfaces.ICollection;
import limpet.prototype.operations.AddQuantityOperation;
import limpet.prototype.store.InMemoryStore;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class TestOperations extends TestCase
{
	public void testValidOperation()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		QuantityCollection<Speed> speed1 = new QuantityCollection<Speed>(
				"Speed 1", kmh);
		QuantityCollection<Speed> speed2 = new QuantityCollection<Speed>(
				"Speed 2", kmh);
		QuantityCollection<Speed> speed3 = new QuantityCollection<Speed>(
				"Speed 3", kmh);

		List<ICollection> selection = new ArrayList<ICollection>(3);
		selection.add(speed1);
		selection.add(speed2);
		selection.add(speed3);
				

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal1 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed, kmh);
			Quantity<Speed> speedVal2 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed*2, kmh);
			Quantity<Speed> speedVal3 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed/2, kmh);

			// store the measurement
			speed1.add( speedVal1);
			speed2.add( speedVal2);
			speed3.add( speedVal3);
		}
		
		InMemoryStore store = new InMemoryStore();
		assertEquals("store empty", 0, store.rootSize());
		
		Collection<ICommand> actions = new AddQuantityOperation().actionsFor(selection, store );
		
		assertEquals("correct number of actions returned", 1, actions.size());
		
		ICommand addAction = actions.iterator().next();
		addAction.execute();
		
		assertEquals("new collection added to store", 1, store.rootSize());
		
	}
	
	public void testInvalidOperation()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		QuantityCollection<Speed> speed1 = new QuantityCollection<Speed>(
				"Speed 1", kmh);
		QuantityCollection<Speed> speed2 = new QuantityCollection<Speed>(
				"Speed 2", kmh);
		QuantityCollection<Speed> speed3 = new QuantityCollection<Speed>(
				"Speed 3", kmh);

		List<ICollection> selection = new ArrayList<ICollection>(3);
		selection.add(speed1);
		selection.add(speed2);
		selection.add(speed3);
				

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal1 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed, kmh);
			Quantity<Speed> speedVal2 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed*2, kmh);
			Quantity<Speed> speedVal3 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed/2, kmh);

			// store the measurement
			speed1.add( speedVal1);
			speed2.add( speedVal2);
			speed3.add( speedVal3);
		}
		
		Quantity<Speed> speedVal3a = DefaultQuantityFactory
				.getInstance(Speed.class).create(12, kmh);

		// store the measurement
		speed3.add(speedVal3a);

		
		
		InMemoryStore store = new InMemoryStore();
		assertEquals("store empty", 0, store.rootSize());
		
		Collection<ICommand> actions = new AddQuantityOperation().actionsFor(selection, store );
		
		assertEquals("correct number of actions returned", 0, actions.size());
			
	}
	
	
}
