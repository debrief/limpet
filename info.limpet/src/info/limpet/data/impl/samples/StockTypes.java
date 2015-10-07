package info.limpet.data.impl.samples;

import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.TemporalQuantityCollection;

import javax.measure.quantity.Acceleration;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.opengis.geometry.Geometry;

public class StockTypes
{

	public static Unit<?> DEGREE_ANGLE = RADIAN.times(2 * Math.PI);

	/**
	 * time series (temporal) collections
	 * 
	 * @author ian
	 * 
	 */
	public static class Temporal
	{
		public static class Speed_MSec extends TemporalQuantityCollection<Velocity>
		{
			public Speed_MSec(String name)
			{
				super(name, METRE.divide(SECOND).asType(Velocity.class));
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
				super(name, METRE.asType(Length.class));
			}
		}

		public static class Strings extends TemporalObjectCollection<String>
		{
			public Strings(String name)
			{
				super(name);
			}
		}

		public static class Acceleration_MSecSec extends
				TemporalQuantityCollection<Acceleration>
		{
			public Acceleration_MSecSec(String name)
			{
				super(name, METRE.divide(SECOND).divide(SECOND)
						.asType(Acceleration.class));
			}
		}

		public static class ElapsedTime_Sec extends
				TemporalQuantityCollection<Duration>
		{
			public ElapsedTime_Sec(String name)
			{
				super(name, SECOND.asType(Duration.class));
			}
		}

		public static class Frequency_Hz extends
				TemporalQuantityCollection<Frequency>
		{
			public Frequency_Hz(String name)
			{
				super(name, HERTZ.asType(Frequency.class));
			}
		}

		public static class AcousticStrength extends
				TemporalQuantityCollection<Dimensionless>
		{
			public AcousticStrength(String name)
			{
				super(name, Dimensionless.UNIT);
			}
		}

		public static class Angle_Radians extends TemporalQuantityCollection<Angle>
		{
			public Angle_Radians(String name)
			{
				super(name, RADIAN.asType(Angle.class));
			}
		}

		public static class Angle_Degrees extends TemporalQuantityCollection<Angle>
		{
			public Angle_Degrees(String name)
			{
				super(name, DEGREE_ANGLE.asType(Angle.class));
			}
		}

		public static class Location extends TemporalObjectCollection<Geometry>
		{
			public Location(String name)
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
		public static class Speed_MSec extends QuantityCollection<Velocity>
		{
			public Speed_MSec(String name)
			{
				super(name, METRE.divide(SECOND).asType(Velocity.class));
			}
		}

		public static class Angle_Degrees extends QuantityCollection<Angle>
		{
			public Angle_Degrees(String name)
			{
				super(name, DEGREE_ANGLE.asType(Angle.class));
			}
		}

		public static class Length_M extends QuantityCollection<Length>
		{
			public Length_M(String name)
			{
				super(name, METRE.asType(Length.class));
			}
		}

		public static class Acceleration_MSecSec extends
				QuantityCollection<Acceleration>
		{
			public Acceleration_MSecSec(String name)
			{
				super(name, METRE.divide(SECOND).divide(SECOND)
						.asType(Acceleration.class));
			}
		}

		public static class Location extends ObjectCollection<Geometry>
		{
			public Location(String name)
			{
				super(name);
			}
		}
		
		public static class Strings extends ObjectCollection<String>
		{
			public Strings(String name)
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
