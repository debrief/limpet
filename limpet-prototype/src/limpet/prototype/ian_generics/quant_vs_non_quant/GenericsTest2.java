package limpet.prototype.ian_generics.quant_vs_non_quant;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import limpet.prototype.ian_generics.ITemporalCollection;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class GenericsTest2 extends TestCase
{

	public void testSimpleAddition()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);
		Unit<Speed> m_sec = Units.METRE.divide(Units.SECOND).asType(Speed.class);

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
	}

	public void testSimpleObjectAddition()
	{

		// the target collection
		TemporalObjectCollection<String> objCollection = new TemporalObjectCollection<String>(
				"Statements");

		// store the measurement
		objCollection.add(12, "some text");

		// check it got stored
		assertEquals("correct number of samples", 1, objCollection.size());

		long thisTime = objCollection.getTimes().iterator().next();
		String thisObs = objCollection.getValues().iterator().next();

		assertEquals("correct time", 12L, thisTime);
		assertEquals("correct text", "some text", thisObs);

		// store the measurement
		objCollection.add(14, "other speed");

		// check it didn't get stored
		assertEquals("correct number of samples", 2, objCollection.size());

	}

	public void testTimeCollectionAPI()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		TemporalQuantityCollection<Speed> speedCollection = new TemporalQuantityCollection<Speed>(
				"Speed", kmh);

		for (int i = 1; i <= 100; i++)
		{
			// create a measurement
			double thisSpeed = Math.sin(Math.toRadians(i));
			Quantity<Speed> speedVal = DefaultQuantityFactory
					.getInstance(Speed.class).create(thisSpeed, kmh);

			// store the measurement
			speedCollection.add(i, speedVal);
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 100, speedCollection.size());

		ITemporalCollection it = speedCollection;
		assertEquals("correct start", 1, it.start());
		assertEquals("correct finish", 100, it.finish());
		assertEquals("correct duration", 99, it.duration());
		assertEquals("correct start", 1d, it.rate());
	}

	public void testTimeSingletonAPI()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		TemporalQuantityCollection<Speed> speedCollection = new TemporalQuantityCollection<Speed>(
				"Speed", kmh);

		// create a measurement
		double thisSpeed = Math.sin(Math.toRadians(10));
		Quantity<Speed> speedVal = DefaultQuantityFactory.getInstance(Speed.class)
				.create(thisSpeed, kmh);

		// store the measurement
		speedCollection.add(55, speedVal);

		// check it didn't get stored
		assertEquals("correct number of samples", 1, speedCollection.size());

		ITemporalCollection it = speedCollection;
		assertEquals("correct start", 55, it.start());
		assertEquals("correct finish", 55, it.finish());
		assertEquals("correct duration", 0, it.duration());
		assertEquals("correct start", -1d, it.rate());
	}
}
