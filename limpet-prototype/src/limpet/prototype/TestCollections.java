package limpet.prototype;

import java.util.Iterator;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import limpet.prototype.generics.dinko.impl.ObjectCollection;
import limpet.prototype.generics.dinko.impl.QuantityCollection;
import limpet.prototype.generics.dinko.impl.TemporalObjectCollection;
import limpet.prototype.generics.dinko.impl.TemporalQuantityCollection;
import limpet.prototype.generics.dinko.interfaces.IBaseTemporalCollection;
import limpet.prototype.generics.dinko.interfaces.ITemporalObjectCollection.Doublet;
import si.uom.SI;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

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

	public void testCreateQuantity()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		QuantityCollection<Speed> speedCollection = new QuantityCollection<Speed>(
				"Speed", kmh);

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(speedVal);
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, speedCollection.size());
		assertEquals("correct name", "Speed", speedCollection.getName());

		assertEquals("correct min", 2d, speedCollection.min().getValue()
				.doubleValue());
		assertEquals("correct max", 20d, speedCollection.max().getValue()
				.doubleValue());
		assertEquals("correct mean", 11d, speedCollection.mean().getValue()
				.doubleValue());
		assertEquals("correct variance", 33, speedCollection.variance().getValue()
				.doubleValue(), 0.1);
		assertEquals("correct sd", 5.744, speedCollection.sd().getValue()
				.doubleValue(), 0.001);
	}

	public void testTemporalQuantityAddition()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);
		Unit<Speed> m_sec = SI.METRES_PER_SECOND;

		// the target collection
		TemporalQuantityCollection<Speed> sc = new TemporalQuantityCollection<Speed>(
				"Speed", kmh);

		// create a measurement
		Quantity<Speed> speedVal = DefaultQuantityFactory.getInstance(Speed.class)
				.create(5, kmh);

		// store the measurement
		sc.add(12, speedVal);

		// check it got stored
		assertEquals("correct number of samples", 1, sc.size());

		long time = sc.getTimes().iterator().next();
		Quantity<Speed> theS = sc.getValues().iterator().next();

		assertEquals("correct time", 12, time);
		assertEquals("correct speed value", 5, theS.getValue());
		assertEquals("correct speed units", kmh, theS.getUnit());

		// ok, now add another
		speedVal = DefaultQuantityFactory.getInstance(Speed.class)
				.create(25, m_sec);

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
		speedVal = DefaultQuantityFactory.getInstance(Speed.class).create(12, kmh);

		// store the measurement
		sc.add(14, speedVal);

		// check it got get stored
		assertEquals("correct number of samples", 2, sc.size());
		
		boolean eThrown = false;
		try
		{
			sc.add(DefaultQuantityFactory.getInstance(Speed.class).create(12, kmh));
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
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		TemporalQuantityCollection<Speed> speedCollection = new TemporalQuantityCollection<Speed>(
				"Speed", kmh);

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed, kmh);

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
		Iterator<Doublet<Quantity<Speed>>> iter = speedCollection.iterator();
		while (iter.hasNext())
		{
			Doublet<Quantity<Speed>> doublet = iter.next();
			runningValueSum += doublet.getObservation().getValue().doubleValue();
			runningTimeSum += doublet.getTime();
		}
		assertEquals("values adds up", 110d, runningValueSum);
		assertEquals("times adds up", 55d, runningTimeSum);

		assertEquals("correct mean", 11d, speedCollection.mean().getValue()
				.doubleValue());
		assertEquals("correct variance", 33, speedCollection.variance().getValue()
				.doubleValue(), 0.1);
		assertEquals("correct sd", 5.744, speedCollection.sd().getValue()
				.doubleValue(), 0.001);

	}

	public void testQuantityCollectionIterator()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		QuantityCollection<Speed> speedCollection = new QuantityCollection<Speed>(
				"Speed", kmh);

		for (int i = 1; i <= 10; i++)
		{
			// create a measurement
			double thisSpeed = i * 2;
			Quantity<Speed> speedVal = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(speedVal);
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, speedCollection.size());

		// ok, now check the iterator
		double runningValueSum = 0;
		Iterator<Quantity<Speed>> vIter = speedCollection.getValues().iterator();
		while (vIter.hasNext())
		{
			Quantity<Speed> value = vIter.next();
			runningValueSum += value.getValue().doubleValue();
		}
		assertEquals("values adds up", 110d, runningValueSum);

		assertEquals("correct mean", 11d, speedCollection.mean().getValue()
				.doubleValue());
		assertEquals("correct variance", 33, speedCollection.variance().getValue()
				.doubleValue(), 0.1);
		assertEquals("correct sd", 5.744, speedCollection.sd().getValue()
				.doubleValue(), 0.001);

	}
	

}
