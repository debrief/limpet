package info.limpet.data;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class TestOperations extends TestCase
{

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		ObjectCollection<String> string_1 = new ObjectCollection<>("strings 1");
		ObjectCollection<String> string_2 = new ObjectCollection<>("strings 2");
				
	
		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal1 = Quantities.getQuantity(thisSpeed, kmh);
			Quantity<Speed> speedVal2 = Quantities.getQuantity(thisSpeed*2, kmh);
			Quantity<Speed> speedVal3 = Quantities.getQuantity(thisSpeed/2, kmh);
			Quantity<Speed> speedVal4 = Quantities.getQuantity(thisSpeed/2, kmm);
			Quantity<Length> lenVal1 = Quantities.getQuantity(thisSpeed/2, m);
	
			// store the measurements
			speed_good_1.add( speedVal1);
			speed_good_2.add( speedVal2);
			speed_longer.add( speedVal3);
			speed_diff_units.add( speedVal4);
			temporal_speed_1.add(i, speedVal2);
			temporal_speed_2.add(i, speedVal3);
			len1.add(lenVal1);
			
			string_1.add(i + " ");
			string_2.add(i + "a ");
		}

		Quantity<Speed> speedVal3a = Quantities.getQuantity(2, kmh);
		speed_longer.add( speedVal3a);


		List<ICollection> selection = new ArrayList<ICollection>(3);
		CollectionComplianceTests testOp = new CollectionComplianceTests();
		
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


		selection.clear();
		selection.add(temporal_speed_1);
		selection.add(string_1);

		assertFalse("all same dim", testOp.allEqualDimensions(selection));
		assertFalse("all same units", testOp.allEqualUnits(selection));
		assertTrue("all same length", testOp.allEqualLength(selection));
		assertFalse("all quantities", testOp.allQuantity(selection));
		assertFalse("all temporal", testOp.allTemporal(selection));

		selection.clear();
		selection.add(string_1);
		selection.add(string_1);

		assertFalse("all same dim", testOp.allEqualDimensions(selection));
		assertFalse("all same units", testOp.allEqualUnits(selection));
		assertTrue("all same length", testOp.allEqualLength(selection));
		assertTrue("all non quantities", testOp.allNonQuantity(selection));
		assertFalse("all temporal", testOp.allTemporal(selection));
		

		// ok, let's try one that works
		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);

		InMemoryStore store = new InMemoryStore();
		assertEquals("store empty", 0, store.rootSize());
		
		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(selection, store );
		
		assertEquals("correct number of actions returned", 1, actions.size());
		
		ICommand<?> addAction = actions.iterator().next();
		addAction.execute();
		
		assertEquals("new collection added to store", 1, store.rootSize());
		
		ICollection firstItem = store.getRoot().get(0);
		ICommand<?> precedent = firstItem.getPrecedent();
		assertNotNull("has precedent", precedent);
		assertEquals("Correct name", "Add series", precedent.getTitle());
		
		List<? extends ICollection> inputs = precedent.getInputs();
		assertEquals("Has both precedents", 2, inputs.size());

		Iterator<? extends ICollection> iIter = inputs.iterator();
		while (iIter.hasNext())
		{
			ICollection thisC = (ICollection) iIter.next();
			List<ICommand<?>> deps = thisC.getDependents();
			assertEquals("has a depedent", 1, deps.size());
			Iterator<ICommand<?>> dIter = deps.iterator();
			while (dIter.hasNext())
			{
				ICommand<?> iCommand = dIter.next();
				assertEquals("Correct dependent", precedent, iCommand);
			}
		}
		
		List<? extends ICollection> outputs = precedent.getOutputs();
		assertEquals("Has both dependents", 1, outputs.size());
		
		Iterator<? extends ICollection> oIter = outputs.iterator();
		while (oIter.hasNext())
		{
			ICollection thisC = (ICollection) oIter.next();
			ICommand<?> dep = thisC.getPrecedent();
			assertNotNull("has a depedent", dep);
			assertEquals("Correct dependent", precedent, dep);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testDimensionlessMultiply()
	{	
		QuantityCollection<Dimensionless> factor = new QuantityCollection<>("Factor 4", Units.ONE);

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
		ObjectCollection<String> string_1 = new ObjectCollection<>("strings 1");
				
	
		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal1 = Quantities.getQuantity(thisSpeed, kmh);
			Quantity<Speed> speedVal2 = Quantities.getQuantity(thisSpeed*2, kmh);
			Quantity<Speed> speedVal3 = Quantities.getQuantity(thisSpeed/2, kmh);
			Quantity<Speed> speedVal4 = Quantities.getQuantity(thisSpeed/2, kmm);
			Quantity<Length> lenVal1 = Quantities.getQuantity(thisSpeed/2, m);
	
			// store the measurements
			speed_good_1.add( speedVal1);
			speed_good_2.add( speedVal2);
			speed_longer.add( speedVal3);
			speed_diff_units.add( speedVal4);
			temporal_speed_1.add(i, speedVal2);
			temporal_speed_2.add(i, speedVal3);
			len1.add(lenVal1);			
			
			string_1.add(i + " ");
		}
		
		// give the singleton a value
		factor.add(4);	

		// ok, let's try one that works
		List<ICollection> selection = new ArrayList<ICollection>(3);

		// place to store results data
		InMemoryStore store = new InMemoryStore();
		
		/////////////////
		// TEST INVALID PERMUTATIONS		
		/////////////////
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(string_1);
		Collection<ICommand> commands = new MultiplyQuantityOperation().actionsFor(selection, store );
		assertEquals("invalid collections - not both quantities", 0, commands.size());

		selection.clear();
		selection.add(speed_good_1);
		selection.add(len1);
		assertEquals("store empty", 0, store.rootSize());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store );		
		assertEquals("valid collections - both quantities", 1, commands.size());


		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);
		store.clear();
		assertEquals("store empty", 0, store.rootSize());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store );		
		assertEquals("valid collections - both speeds", 1, commands.size());

		
		////////////////////////////
		// now test valid collections
		///////////////////////////
		
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(factor);

		assertEquals("store empty", 0, store.rootSize());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store );
		assertEquals("valid collections - one is singleton", 1, commands.size());
		
		// TODO: test actions is non-null
		
		// TODO: test actions has single item: "Multiply series by constant"
		
		// TODO: apply action
		
		// TODO: test store has a new item in it		
		
		// TODO: test results is same length as thisSpeed

		
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(factor);
		store.clear();
		assertEquals("store empty", 0, store.rootSize());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store );		
		assertEquals("valid collections - one is singleton", 1, commands.size());

		// TODO: run operation, check the new series is in the store
		
		// TODO: check the new series is of the correct length 
		
		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_diff_units);
		store.clear();
		assertEquals("store empty", 0, store.rootSize());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store );		
		// TODO: we should get a series returned - they're both speeds

		// TODO: run operation, check the new series is in the store
		
		// TODO: check the new series is of the correct length 
		
		// TODO: check the new series is of the correct units, and that the first item has been converted appropriately

	}
	
}
