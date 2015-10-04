package info.limpet;

import static javax.measure.unit.SI.GRAM;
import static javax.measure.unit.SI.KILOGRAM;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Quantity;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

public class App
{
	public static void main(String[] args)
	{
		Measure<Double, Mass> mass = Measure.valueOf(50.0, KILOGRAM);
		System.out.println(mass.to(NonSI.POUND));

		
		// try to store some data
		IQuantityCollection<Mass> qc = new QuantityCollection<Mass>(
				KILOGRAM);
		qc.add(Measure.valueOf(24, GRAM));
		qc.add(Measure.valueOf(4, KILOGRAM));

	}

	public static interface IQuantityCollection<Q extends Quantity>
	{
		void add(Measurable<Q> item);
		void add(double newVal);
	}

	public static class QuantityCollection<Q extends Quantity>
			implements IQuantityCollection<Q>
	{
		final Unit<Q> _units;

		public QuantityCollection(Unit<Q> units)
		{
			_units = units;
		}

		@Override
		public void add(Measurable<Q> item)
		{
			System.out.println("adding:" + item.toString());
		}

		@Override
		public void add(double newVal)
		{
			add(Measure.valueOf(newVal, _units));
		}
		
		
	};

}
