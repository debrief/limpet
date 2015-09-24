package info.limpet.data.impl.samples;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.TemporalObjectCollection;

import javax.measure.quantity.Acceleration;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;

import tec.units.ri.unit.Units;

public class StockTypes
{

	/**
	 * time series (temporal) collections
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

		public static class ElapsedTime_Sec extends
				TemporalQuantityCollection<Time>
		{
			public ElapsedTime_Sec(String name)
			{
				super(name, Units.SECOND.asType(Time.class));
			}
		}

		public static class Frequency_Hz extends
				TemporalQuantityCollection<Frequency>
		{
			public Frequency_Hz(String name)
			{
				super(name, Units.HERTZ.asType(Frequency.class));
			}
		}

		public static class AcousticStrength extends
				TemporalQuantityCollection<Dimensionless>
		{
			public AcousticStrength(String name)
			{
				super(name, Units.ONE.asType(Dimensionless.class));
			}
		}

		public static class Angle_Radians extends TemporalQuantityCollection<Angle>
		{
			public Angle_Radians(String name)
			{
				super(name, Units.RADIAN.asType(Angle.class));
			}
		}

		public static class Angle_Degrees extends TemporalQuantityCollection<Angle>
		{
			public Angle_Degrees(String name)
			{
				// TODO: use units.mulitply - something like this:
				// Units.RADIAN.multiply(2 * Math.PI);
				super(name, Units.RADIAN.asType(Angle.class));
			}
		}

		public static class Locations extends TemporalObjectCollection<TmpLocationItem>
		{
			public Locations(String name)
			{
				super(name);
			}
		}
		
	}

	/**
	 * non-time series types
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

		public static class Acceleration_MSecSec extends
				QuantityCollection<Acceleration>
		{
			public Acceleration_MSecSec(String name)
			{
				super(name, Units.METRE.divide(Units.SECOND).divide(Units.SECOND)
						.asType(Acceleration.class));
			}
		}
		

		public static class Locations extends ObjectCollection<TmpLocationItem>
		{
			public Locations(String name)
			{
				super(name);
			}
		}

	}

	public static class TmpLocationItem
	{
		public double latVal, longVal, depthVal;
		public TmpLocationItem(double latV, double longV, double depthV)
		{
			latVal = latV;
			longVal = longV;
			depthVal = depthV;
		}
	}

}
