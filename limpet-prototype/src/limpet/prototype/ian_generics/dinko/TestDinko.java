package limpet.prototype.ian_generics.dinko;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import si.uom.SI;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class TestDinko extends TestCase
{
	public void testCreateObject()
	{
		// the target collection
		ObjectCollection<String> stringCollection = new ObjectCollection<String>("strings");

		for (int i = 1; i <= 10; i++)
		{
			// store the measurement
			stringCollection.add(i + "aaa");
		}

		// check it didn't get stored
		assertEquals("correct number of samples", 10, stringCollection.size());
	}

	
	public void testCreateQuantity()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		QuantityCollection<Speed> speedCollection = new QuantityCollection<Speed>("Speed", kmh);

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
	}
	

	public void testSimpleAddition()
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
	}
}
