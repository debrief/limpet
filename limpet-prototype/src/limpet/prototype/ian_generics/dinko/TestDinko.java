package limpet.prototype.ian_generics.dinko;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class TestDinko extends TestCase
{
	public void testCreate()
	{
		// the units for this measurement
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR)
				.asType(Speed.class);

		// the target collection
		QuantityCollection<Speed> speedCollection = new QuantityCollection<Speed>();

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
}
