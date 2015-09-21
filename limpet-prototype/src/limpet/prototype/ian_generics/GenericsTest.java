package limpet.prototype.ian_generics;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Speed;

import junit.framework.TestCase;
import limpet.prototype.ian_generics.Temporal.QuantityType;
import limpet.prototype.ian_generics.Temporal.TemporalObservation;
import tec.units.ri.quantity.DefaultQuantityFactory;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

public class GenericsTest extends TestCase{

	
	
	public void testSimpleAddition()
	{
		// the units for this measurement 
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR).asType(Speed.class);

		// the target collection
		QuantityType<Speed> speedCollection = new Temporal.QuantityType<Speed>("Speed");

		
		// create a measurement
		Quantity<Speed> speedVal = DefaultQuantityFactory.getInstance(Speed.class).create(5, kmh);

		// store the measurement
		speedCollection.add(12,  speedVal);

		// check it got stored
		assertEquals("some test", 1, speedCollection.size());
		
		TemporalObservation<Quantity<?>> thisMeasure = speedCollection.getMeasurements().iterator().next();
		assertEquals("correct time", 12, thisMeasure.getTime());
		Quantity<?> theS = thisMeasure.getObservation();
		assertEquals("correct speed value", 5, theS.getValue());
		assertEquals("correct speed units", kmh, theS.getUnit());
		
		// ok, now add another
		// create a measurement
		speedVal = DefaultQuantityFactory.getInstance(Speed.class).create(12, kmh);

		// store the measurement
		speedCollection.add(14,  speedVal);

		// check it got stored
		assertEquals("some test", 2, speedCollection.size());
	}
	
	
}
