package info.limpet.data;

import info.limpet.IBaseTemporalCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.ITemporalObjectCollection.Doublet;
import info.limpet.QuantityRange;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Speed_MSec;

import java.util.Iterator;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import junit.framework.TestCase;
import static javax.measure.unit.SI.*;
import static javax.measure.unit.NonSI.*;

public class TestCollections extends TestCase
{
	public void testCreateObject()
	{
		// the target collection
		ObjectCollection<String> stringCollection = new ObjectCollection<String>(
				"strings");

		for (int i = 1; i <= 10; i++)
		{
			// store the measurement
			stringCollection.add(i + "aaa");
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, stringCollection.size());
	}

	public void testSampleData()
	{
		IStore data = new SampleData().getData(10);
		IQuantityCollection<Quantity> ranged = (IQuantityCollection<Quantity>) data.get(SampleData.RANGED_SPEED_SINGLETON);
		assertNotNull("found series", ranged);
		
		QuantityRange<Quantity> range = ranged.getRange();		
		assertNotNull("found range", range);
		
		// check the range has values
		assertEquals("correct values", 940d, range.getMinimum().doubleValue(ranged.getUnits()), 0.1);
		assertEquals("correct values", 1050d, range.getMaximum().doubleValue(ranged.getUnits()), 0.1);
	}
	
	public void testCreateTemporalObject()
	{
		// the target collection
		TemporalObjectCollection<String> stringCollection = new TemporalObjectCollection<String>(
				"strings");

		for (int i = 1; i <= 12; i++)
		{
			// store the measurement
			stringCollection.add(i, i + "aaa");
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 12, stringCollection.size());

		IBaseTemporalCollection it = stringCollection;
		assertEquals("correct start", 1, it.start());
		assertEquals("correct finish", 12, it.finish());
		assertEquals("correct duration", 11, it.duration());
		assertEquals("correct start", 1d, it.rate());

		// ok, now check the iterator
		long runningValueSum = 0;
		long runningTimeSum = 0;
		Iterator<Doublet<String>> iter = stringCollection.iterator();
		while (iter.hasNext())
		{
			Doublet<String> doublet = iter.next();
			runningValueSum += doublet.getObservation().length();
			runningTimeSum += doublet.getTime();
		}
		assertEquals("values adds up", 51, runningValueSum);
		assertEquals("times adds up", 78, runningTimeSum);
		
		
		boolean eThrown = false;
		try
		{
			stringCollection.add("done");
		}
		catch (UnsupportedOperationException er)
		{
			eThrown = true;
		}

		assertTrue("exception thrown for invalid add operation", eThrown);
		
	}

	public void testUnitlessQuantity()
	{
		// the units for this measurement
		Unit<Velocity> kmh = KILO(METRE).divide(HOUR)
				.asType(Velocity.class);

		// the target collection
		Speed_MSec speedCollection = new StockTypes.NonTemporal.Speed_MSec("Speed");
		
		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(speedVal);
		}
		
		assertEquals("correct num of items", 10, speedCollection.size());
		
		speedCollection.add(12);
		
		assertEquals("correct num of items", 11, speedCollection.size());
	}
	
	public void testCreateQuantity()
	{
		// the units for this measurement
		Unit<Velocity> kmh = KILO(METRE).divide(HOUR)
				.asType(Velocity.class);

		// the target collection
		Speed_MSec speedCollection = new StockTypes.NonTemporal.Speed_MSec("Speed");

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(speedVal);
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, speedCollection.size());
		assertEquals("correct name", "Speed", speedCollection.getName());

		assertEquals("correct min", 2d, speedCollection.min());
		assertEquals("correct max", 20d, speedCollection.max());
		assertEquals("correct mean", 11d, speedCollection.mean());
		assertEquals("correct variance", 33, speedCollection.variance().doubleValue(speedCollection.getUnits()), 0.1);
		assertEquals("correct sd", 5.744, speedCollection.sd().doubleValue(speedCollection.getUnits()), 0.001);
	}

	public void testTemporalQuantityAddition()
	{
		// the units for this measurement
		Unit<Velocity> kmh = KILO(METRE).divide(HOUR)
				.asType(Velocity.class);
		Unit<Velocity> m_sec = METRES_PER_SECOND;

		// the target collection
		TemporalQuantityCollection<Velocity> sc = new TemporalQuantityCollection<Velocity>(
				"Speed", kmh);

		// create a measurement
		Measurable<Velocity> speedVal = Measure.valueOf(5, kmh);

		// store the measurement
		sc.add(12, speedVal);

		// check it got stored
		assertEquals("correct number of samples", 1, sc.size());

		long time = sc.getTimes().iterator().next();
		Measurable<Velocity> theS = sc.getValues().iterator().next();

		assertEquals("correct time", 12, time);
		assertEquals("correct speed value", 5, theS.doubleValue(sc.getUnits()));
		assertEquals("correct speed units", kmh, sc.getUnits());

		// ok, now add another
		speedVal = Measure.valueOf(25, m_sec);

		// store the measurement
		boolean errorThrown = false;
		try
		{
			sc.add(14, speedVal);
		}
		catch (Exception e)
		{
			errorThrown = true;
		}

		// check the error got thrown
		assertTrue("runtime got thrown", errorThrown);

		// check it didn't get stored
		assertEquals("correct number of samples", 1, sc.size());

		// ok, now add another
		speedVal = Measure.valueOf(12, kmh);

		// store the measurement
		sc.add(14, speedVal);

		// check it got get stored
		assertEquals("correct number of samples", 2, sc.size());
		
		boolean eThrown = false;
		try
		{
			sc.add(12);
		}
		catch (UnsupportedOperationException er)
		{
			eThrown = true;
		}

		assertTrue("exception thrown for invalid add operation", eThrown);
				
		
		
	}

	public void testTimeQuantityCollectionIterator()
	{
		// the units for this measurement
		Unit<Velocity> kmh = KILO(METRE).divide(HOUR)
				.asType(Velocity.class);

		// the target collection
		TemporalQuantityCollection<Velocity> speedCollection = new TemporalQuantityCollection<Velocity>(
				"Speed", kmh);

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(i, speedVal);
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, speedCollection.size());

		IBaseTemporalCollection it = speedCollection;
		assertEquals("correct start", 1, it.start());
		assertEquals("correct finish", 10, it.finish());
		assertEquals("correct duration", 9, it.duration());
		assertEquals("correct start", 1d, it.rate());

		// ok, now check the iterator
		double runningValueSum = 0;
		double runningTimeSum = 0;
		Iterator<Doublet<Measurable<Velocity>>> iter = speedCollection.iterator();
		while (iter.hasNext())
		{
			Doublet<Measurable<Velocity>> doublet = iter.next();
			runningValueSum += doublet.getObservation().doubleValue(speedCollection.getUnits());
			runningTimeSum += doublet.getTime();
		}
		assertEquals("values adds up", 110d, runningValueSum);
		assertEquals("times adds up", 55d, runningTimeSum);

		assertEquals("correct mean", 11d, speedCollection.mean().doubleValue(speedCollection.getUnits())
				);
		assertEquals("correct variance", 33, speedCollection.variance().doubleValue(speedCollection.getUnits())
				, 0.1);
		assertEquals("correct sd", 5.744, speedCollection.sd().doubleValue(speedCollection.getUnits())
				, 0.001);

	}

	public void testQuantityCollectionIterator()
	{
		// the units for this measurement
		Unit<Velocity> kmh = KILO(METRE).divide(HOUR)
				.asType(Velocity.class);

		// the target collection
		QuantityCollection<Velocity> speedCollection = new QuantityCollection<Velocity>(
				"Speed", kmh);

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(speedVal);
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, speedCollection.size());

		// ok, now check the iterator
		double runningValueSum = 0;
		Iterator<Measurable<Velocity>> vIter = speedCollection.getValues().iterator();
		while (vIter.hasNext())
		{
			Measurable<Velocity> value = vIter.next();
			runningValueSum += value.doubleValue(speedCollection.getUnits());
		}
		assertEquals("values adds up", 110d, runningValueSum);

		assertEquals("correct mean", 11d, speedCollection.mean().doubleValue(speedCollection.getUnits())
				);
		assertEquals("correct variance", 33, speedCollection.variance().doubleValue(speedCollection.getUnits())
				, 0.1);
		assertEquals("correct sd", 5.744, speedCollection.sd().doubleValue(speedCollection.getUnits())
				, 0.001);

	}
	

}
