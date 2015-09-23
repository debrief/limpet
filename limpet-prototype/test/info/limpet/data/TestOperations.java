package info.limpet.data;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IStore;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.BaseOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class TestOperations extends TestCase
{

	public void testAppliesTo()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);
		Unit<Speed> kmm = MetricPrefix.KILO(Units.METRE).divide(Units.MINUTE)
				.asType(Speed.class);
		Unit<Length> m = (Units.METRE).asType(Length.class);

	
		// the target collection
		QuantityCollection<Speed> speed_good_1 = new QuantityCollection<Speed>(
				"Speed 1", kmh);
		QuantityCollection<Speed> speed_good_2 = new QuantityCollection<Speed>(
				"Speed 2", kmh);
		QuantityCollection<Speed> speed_longer = new QuantityCollection<Speed>(
				"Speed 3", kmh);
		QuantityCollection<Speed> speed_diff_units = new QuantityCollection<Speed>(
				"Speed 4", kmm);
		QuantityCollection<Length> len1 = new QuantityCollection<Length>(
				"Length 1", m);
		TemporalQuantityCollection<Speed> temporal_speed_1 = new TemporalQuantityCollection<Speed>(
				"Speed 5", kmh);
		TemporalQuantityCollection<Speed> temporal_speed_2 = new TemporalQuantityCollection<Speed>(
				"Speed 6", kmh);
				
	
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
			Quantity<Speed> speedVal4 = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed/2, kmm);
			Quantity<Length> lenVal1 = DefaultQuantityFactory
					.getInstance(Length.class).create(thisSpeed/2, m);
	
			// store the measurements
			speed_good_1.add( speedVal1);
			speed_good_2.add( speedVal2);
			speed_longer.add( speedVal3);
			speed_diff_units.add( speedVal4);
			temporal_speed_1.add(i, speedVal2);
			temporal_speed_2.add(i, speedVal3);
			len1.add(lenVal1);
		}

		Quantity<Speed> speedVal3a = DefaultQuantityFactory
				.getInstance(Speed.class).create(2, kmh);
		speed_longer.add( speedVal3a);


		List<ICollection> selection = new ArrayList<ICollection>(3);
		DummyOperation testOp = new DummyOperation();
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);

		assertTrue("all same dim", testOp.allEqualDimensions(selection));
		assertTrue("all same units", testOp.allEqualUnits(selection));
		assertTrue("all same length", testOp.allEqualLength(selection));
		assertTrue("all quantities", testOp.allQuantity(selection));
		assertFalse("all temporal", testOp.allTemporal(selection));
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);
		selection.add(speed_diff_units);

		assertTrue("all same dim", testOp.allEqualDimensions(selection));
		assertFalse("all same units", testOp.allEqualUnits(selection));
		assertTrue("all same length", testOp.allEqualLength(selection));
		assertTrue("all quantities", testOp.allQuantity(selection));
		assertFalse("all temporal", testOp.allTemporal(selection));

		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);
		selection.add(len1);

		assertFalse("all same dim", testOp.allEqualDimensions(selection));
		assertFalse("all same units", testOp.allEqualUnits(selection));
		assertTrue("all same length", testOp.allEqualLength(selection));
		assertTrue("all quantities", testOp.allQuantity(selection));
		assertFalse("all temporal", testOp.allTemporal(selection));
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);
		selection.add(speed_longer);

		assertTrue("all same dim", testOp.allEqualDimensions(selection));
		assertTrue("all same units", testOp.allEqualUnits(selection));
		assertFalse("all same length", testOp.allEqualLength(selection));
		assertTrue("all quantities", testOp.allQuantity(selection));
		assertFalse("all temporal", testOp.allTemporal(selection));

		selection.clear();
		selection.add(temporal_speed_1);
		selection.add(temporal_speed_2);

		assertTrue("all same dim", testOp.allEqualDimensions(selection));
		assertTrue("all same units", testOp.allEqualUnits(selection));
		assertTrue("all same length", testOp.allEqualLength(selection));
		assertTrue("all quantities", testOp.allQuantity(selection));
		assertTrue("all temporal", testOp.allTemporal(selection));


		// ok, let's try one that works
		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);

		InMemoryStore store = new InMemoryStore();
		assertEquals("store empty", 0, store.rootSize());
		
		Collection<ICommand> actions = new AddQuantityOperation().actionsFor(selection, store );
		
		assertEquals("correct number of actions returned", 1, actions.size());
		
		ICommand addAction = actions.iterator().next();
		addAction.execute();
		
		assertEquals("new collection added to store", 1, store.rootSize());
		
		
	}

	public static class DummyOperation extends BaseOperation
	{
	
		@Override
		public Collection<ICommand> actionsFor(List<ICollection> selection,
				IStore destination)
		{
			throw new UnsupportedOperationException("not implemented, just for testing");
		}
		
	}
	
}
