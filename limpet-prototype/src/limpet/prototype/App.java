package limpet.prototype;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import systems.uom.common.US;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

public class App {
	public static void main(String[] args) {
		Quantity<Mass> mass = Quantities.getQuantity(70, Units.KILOGRAM);
		System.out.println(mass.to(US.POUND).getValue());
	}
}
