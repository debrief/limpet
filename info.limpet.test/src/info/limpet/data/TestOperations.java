package info.limpet.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SimpleMovingAverageOperation;
import info.limpet.data.operations.UnitConversionOperation;
import info.limpet.data.store.InMemoryStore;
import junit.framework.TestCase;

import static javax.measure.unit.SI.*;
import static javax.measure.unit.NonSI.*;


public class TestOperations extends TestCase
{

	public void testAppliesTo()
	{
		// the units for this measurement
		Unit<Velocity> kmh = KILO(METRE).divide(HOUR)
				.asType(Velocity.class);
		Unit<Velocity> kmm = KILO(METRE).divide(MINUTE)
				.asType(Velocity.class);
		Unit<Length> m = (METRE).asType(Length.class);

		// the target collection
		QuantityCollection<Velocity> speed_good_1 = new QuantityCollection<Velocity>(
				"Speed 1", kmh);
		QuantityCollection<Velocity> speed_good_2 = new QuantityCollection<Velocity>(
				"Speed 2", kmh);
		QuantityCollection<Velocity> speed_longer = new QuantityCollection<Velocity>(
				"Speed 3", kmh);
		QuantityCollection<Velocity> speed_diff_units = new QuantityCollection<Velocity>(
				"Speed 4", kmm);
		QuantityCollection<Length> len1 = new QuantityCollection<Length>(
				"Length 1", m);
		TemporalQuantityCollection<Velocity> temporal_speed_1 = new TemporalQuantityCollection<Velocity>(
				"Speed 5", kmh);
		TemporalQuantityCollection<Velocity> temporal_speed_2 = new TemporalQuantityCollection<Velocity>(
				"Speed 6", kmh);
		ObjectCollection<String> string_1 = new ObjectCollection<>("strings 1");
		ObjectCollection<String> string_2 = new ObjectCollection<>("strings 2");

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Measurable<Velocity> speedVal1 = Measure.valueOf(thisSpeed, kmh);
			Measurable<Velocity> speedVal2 = Measure.valueOf(thisSpeed * 2, kmh);
			Measurable<Velocity> speedVal3 = Measure.valueOf(thisSpeed / 2, kmh);
			Measurable<Velocity> speedVal4 = Measure.valueOf(thisSpeed / 2, kmm);
			Measurable<Length> lenVal1 = Measure.valueOf(thisSpeed / 2, m);

			// store the measurements
			speed_good_1.add(speedVal1);
			speed_good_2.add(speedVal2);
			speed_longer.add(speedVal3);
			speed_diff_units.add(speedVal4);
			temporal_speed_1.add(i, speedVal2);
			temporal_speed_2.add(i, speedVal3);
			len1.add(lenVal1);

			string_1.add(i + " ");
			string_2.add(i + "a ");
		}

		Measurable<Velocity> speedVal3a = Measure.valueOf(2, kmh);
		speed_longer.add(speedVal3a);

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
		assertEquals("store empty", 0, store.size());

		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		Collection<ICommand<ICollection>> actions = new AddQuantityOperation()
				.actionsFor(selection, store);

		assertEquals("correct number of actions returned", 1, actions.size());

		ICommand<?> addAction = actions.iterator().next();
		addAction.execute();

		assertEquals("new collection added to store", 1, store.size());

		ICollection firstItem = store.iterator().next();
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

	public void testDimensionlessMultiply()
	{
		// place to store results data
		InMemoryStore store = new SampleData().getData(10);

		// ok, let's try one that works
		List<ICollection> selection = new ArrayList<ICollection>(3);

		// ///////////////
		// TEST INVALID PERMUTATIONS
		// ///////////////
		ICollection speed_good_1 = store.get(SampleData.SPEED_ONE);
		ICollection speed_good_2 = store.get(SampleData.SPEED_TWO);
		ICollection string_1 = store.get(SampleData.STRING_ONE);
		ICollection len1 = store.get(SampleData.LENGTH_ONE);
		ICollection factor = store.get(SampleData.FLOATING_POINT_FACTOR);

		selection.clear();
		selection.add(speed_good_1);
		selection.add(string_1);
		Collection<ICommand<ICollection>> commands = new MultiplyQuantityOperation()
				.actionsFor(selection, store);
		assertEquals("invalid collections - not both quantities", 0,
				commands.size());

		selection.clear();
		selection.add(speed_good_1);
		selection.add(len1);

		commands = new MultiplyQuantityOperation().actionsFor(selection, store);
		assertEquals("valid collections - both quantities", 1, commands.size());

		selection.clear();
		selection.add(speed_good_1);
		selection.add(speed_good_2);
		store.clear();
		assertEquals("store empty", 0, store.size());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store);
		assertEquals("valid collections - both speeds", 1, commands.size());

		// //////////////////////////
		// now test valid collections
		// /////////////////////////

		selection.clear();
		selection.add(speed_good_1);
		selection.add(factor);

		assertEquals("store empty", 0, store.size());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store);
		assertEquals("valid collections - one is singleton", 1, commands.size());

		ICommand<ICollection> command = commands.iterator().next();

		// test actions has single item: "Multiply series by constant"
		assertEquals("correct name", "Multiply series", command.getTitle());

		// apply action
		command.execute();

		// test store has a new item in it
		assertEquals("store not empty", 1, store.size());

		ICollection newS = store.get(MultiplyQuantityOperation.SERIES_NAME);

		// test results is same length as thisSpeed
		assertEquals("correct size", 10, newS.size());

		selection.clear();
		selection.add(speed_good_1);
		selection.add(factor);
		store.clear();
		assertEquals("store empty", 0, store.size());
		commands = new MultiplyQuantityOperation().actionsFor(selection, store);
		assertEquals("valid collections - one is singleton", 1, commands.size());
	}

	public void testUnitConversion()
	{
		// place to store results data
		InMemoryStore store = new SampleData().getData(10);

		List<ICollection> selection = new ArrayList<ICollection>(3);
		// speed one defined in m/s
		ICollection speed_good_1 = store.get(SampleData.SPEED_ONE);
		selection.add(speed_good_1);

		// test incompatible target unit
		Collection<ICommand<ICollection>> commands = new UnitConversionOperation(
				METRE).actionsFor(selection, store);
		assertEquals("target unit not same dimension as input", 0, commands.size());

		// test valid target unit
		commands = new UnitConversionOperation(KILOMETRES_PER_HOUR)
				.actionsFor(selection, store);
		assertEquals("valid unit dimensions", 1, commands.size());

		ICommand<ICollection> command = commands.iterator().next();

		// apply action
		command.execute();

		ICollection newS = store.get(speed_good_1.getName()
				+ UnitConversionOperation.CONVERTED_TO + KILOMETRES_PER_HOUR);
		assertNotNull(newS);

		// test results is same length as thisSpeed
		assertEquals("correct size", 10, newS.size());

		// TODO: check that operation isn't offered if the dataset is already in
		// that type
		commands = new UnitConversionOperation(METRES_PER_SECOND).actionsFor(
				selection, store);
		assertEquals("already in destination units", 0, commands.size());

		IQuantityCollection<?> inputSpeed = (IQuantityCollection<?>) speed_good_1;

		// TODO: avoid suppressing these warnings
		@SuppressWarnings("unchecked")
		Measurable<Velocity> firstInputSpeed = (Measurable<Velocity>) inputSpeed.getValues()
				.get(0);

		IQuantityCollection<?> outputSpeed = (IQuantityCollection<?>) newS;

		// TODO: avoid suppressing these warnings
		@SuppressWarnings("unchecked")
		Measurable<Velocity> firstOutputSpeed = (Measurable<Velocity>) outputSpeed
				.getValues().get(0);
		
		UnitConverter oc = inputSpeed.getUnits().getConverterTo(KILOMETERS_PER_HOUR);

		assertEquals(oc.convert(firstInputSpeed.doubleValue((Unit<Velocity>) inputSpeed.getUnits())),
				firstOutputSpeed);

	}

	public void testSimpleMovingAverage()
	{
		// place to store results data
		InMemoryStore store = new SampleData().getData(10);

		List<ICollection> selection = new ArrayList<>();

		@SuppressWarnings("unchecked")
		IQuantityCollection<Velocity> speed_good_1 = (IQuantityCollection<Velocity>) store
				.get(SampleData.SPEED_ONE);
		selection.add(speed_good_1);

		int windowSize = 3;

		Collection<ICommand<ICollection>> commands = new SimpleMovingAverageOperation(
				windowSize).actionsFor(selection, store);
		assertEquals(1, commands.size());

		ICommand<ICollection> command = commands.iterator().next();

		// apply action
		command.execute();

		@SuppressWarnings("unchecked")
		IQuantityCollection<Velocity> newS = (IQuantityCollection<Velocity>) store
				.get(speed_good_1.getName()+ " " + SimpleMovingAverageOperation.SERIES_NAME_TEMPLATE);
		assertNotNull(newS);

		// test results is same length as thisSpeed
		assertEquals("correct size", 10, newS.size());

		// calculate sum of input values [0..windowSize-1]
		double sum = 0;
		for (int i = 0; i < windowSize; i++)
		{
			Measurable<Velocity> inputQuantity = speed_good_1.getValues().get(i);
			sum += inputQuantity.doubleValue(speed_good_1.getUnits());
		}
		double average = sum / windowSize;

		// compare to output value [windowSize-1]
		Measurable<Velocity> simpleMovingAverage = newS.getValues().get(windowSize - 1);

		assertEquals(average, simpleMovingAverage.doubleValue(newS.getUnits()), 0);
		
	}
}
