package limpet.prototype;

import javax.measure.Unit;
import javax.measure.quantity.Speed;

import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

/**
 * 
 *
 */
public class App {
	public static void main(String[] args) {
		Unit<Speed> kmh = MetricPrefix.KILO(Units.METRE).divide(Units.HOUR).asType(Speed.class);
		System.out.println(kmh.toString());
	}
}
