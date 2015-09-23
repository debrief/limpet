package info.limpet.data.impl.samples;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;

import javax.measure.quantity.Acceleration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

import tec.units.ri.unit.Units;

public class StockTypes
{

	/** time series (temporal) collections
	 * 
	 * @author ian
	 *
	 */
	public static class Temporal
	{
		public static class Speed_MSec extends TemporalQuantityCollection<Speed>
		{
			public Speed_MSec(String name)
			{
				super(name, Units.METRE.divide(Units.SECOND).asType(Speed.class));
			}

			// Note: here is how to create the speed series with specific units
			// public TemporalSpeed_MSec(String name, Unit<Speed> units)
			// {
			// super(name,units.asType(Speed.class));
			// }
		}

		public static class Length_M extends TemporalQuantityCollection<Length>
		{
			public Length_M(String name)
			{
				super(name, Units.METRE.asType(Length.class));
			}
		}

		public static class Acceleration_MSecSec extends
				TemporalQuantityCollection<Acceleration>
		{
			public Acceleration_MSecSec(String name)
			{
				super(name, Units.METRE.divide(Units.SECOND).divide(Units.SECOND)
						.asType(Acceleration.class));
			}
		}
	}

	/** non-time series types
	 * 
	 * @author ian
	 *
	 */
	public static class NonTemporal
	{
		public static class Speed_MSec extends QuantityCollection<Speed>
		{
			public Speed_MSec(String name)
			{
				super(name, Units.METRE.divide(Units.SECOND).asType(Speed.class));
			}
		}

		public static class Length_M extends QuantityCollection<Length>
		{
			public Length_M(String name)
			{
				super(name, Units.METRE.asType(Length.class));
			}
		}

		public static class Acceleration_MSecSec extends QuantityCollection<Acceleration>
		{
			public Acceleration_MSecSec(String name)
			{
				super(name, Units.METRE.divide(Units.SECOND).divide(Units.SECOND)
						.asType(Acceleration.class));
			}
		}
	}

}
