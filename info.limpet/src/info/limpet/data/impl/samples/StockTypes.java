package info.limpet.data.impl.samples;

import static javax.measure.unit.NonSI.NAUTICAL_MILE;
import static javax.measure.unit.NonSI.YARD;
import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.KELVIN;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICommand;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalObjectCollection;
import info.limpet.data.impl.TemporalQuantityCollection;

import java.util.List;

import javax.measure.quantity.Acceleration;
import javax.measure.quantity.Angle;
import javax.measure.quantity.AngularVelocity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.opengis.geometry.Geometry;

public class StockTypes
{

	public static Unit<?> DEGREE_ANGLE = RADIAN.times(2 * Math.PI);

	public static interface ILocations
	{
		public List<Geometry> getLocations();
	}

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
			public Speed_MSec(String name, ICommand<?> precedent)
			{
				super(name, precedent, METRE.divide(SECOND).asType(Velocity.class));
			}
			public Speed_MSec(String name)
			{
				super(name, null, METRE.divide(SECOND).asType(Velocity.class));
			}

			public Speed_MSec()
			{
				this(null, null);
			}
		}
		
		public static class Speed_Kts extends TemporalQuantityCollection<Velocity>
		{
			public Speed_Kts(String name)
			{
				super(name, NAUTICAL_MILE.divide(SECOND).asType(Velocity.class));
			}

			public Speed_Kts()
			{
				this(null);
			}
		}


		public static class DimensionlessDouble extends
				TemporalQuantityCollection<Dimensionless>
		{
			public DimensionlessDouble(String name)
			{
				super(name, Dimensionless.UNIT);
			}

			public DimensionlessDouble()
			{
				this(null);
			}
		}

		public static class Length_M extends TemporalQuantityCollection<Length>
		{
			public Length_M(String name)
			{
				super(name, METRE.asType(Length.class));
			}

			public Length_M()
			{
				this(null);
			}
		}
		public static class Length_Yd extends TemporalQuantityCollection<Length>
		{
			public Length_Yd(String name)
			{
				super(name, YARD.asType(Length.class));
			}

			public Length_Yd()
			{
				this(null);
			}
		}

		public static class Temp_C extends TemporalQuantityCollection<Temperature>
		{
			public Temp_C(String name)
			{
				super(name, KELVIN.asType(Temperature.class));
			}

			public Temp_C()
			{
				this(null);
			}
		}

		public static class TurnRate extends
				TemporalQuantityCollection<AngularVelocity>
		{
			public TurnRate(String name)
			{
				super(null, DEGREE_ANGLE.divide(SECOND).asType(AngularVelocity.class));
			}

			public TurnRate()
			{
				this(null);
			}
		}

		public static class Strings extends TemporalObjectCollection<String>
		{
			public Strings(String name)
			{
				super(name);
			}

			public Strings()
			{
				this(null);
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

			public Acceleration_MSecSec()
			{
				this(null);
			}
		}

		public static class ElapsedTime_Sec extends
				TemporalQuantityCollection<Duration>
		{
			public ElapsedTime_Sec(String name)
			{
				super(name, SECOND.asType(Duration.class));
			}

			public ElapsedTime_Sec()
			{
				this(null);
			}
		}

		public static class Frequency_Hz extends
				TemporalQuantityCollection<Frequency>
		{
			public Frequency_Hz(String name)
			{
				super(name, HERTZ.asType(Frequency.class));
			}

			public Frequency_Hz()
			{
				this(null);
			}
		}

		public static class AcousticStrength extends
				TemporalQuantityCollection<Dimensionless>
		{
			public AcousticStrength(String name)
			{
				super(name, Dimensionless.UNIT);
			}

			public AcousticStrength()
			{
				this(null);
			}
		}

		public static class Angle_Radians extends TemporalQuantityCollection<Angle>
		{
			public Angle_Radians(String name)
			{
				super(name, RADIAN.asType(Angle.class));
			}

			public Angle_Radians()
			{
				this(null);
			}
		}

		public static class Angle_Degrees extends TemporalQuantityCollection<Angle>
		{
			public Angle_Degrees(String name,  ICommand<?> precedent)
			{
				super(name, precedent, DEGREE_ANGLE.asType(Angle.class));
			}

			public Angle_Degrees()
			{
				this(null, null);
			}
		}

		public static class Location extends TemporalObjectCollection<Geometry>
				implements ILocations
		{
			public Location(String name)
			{
				super(name);
			}

			@Override
			public List<Geometry> getLocations()
			{
				return super.getValues();
			}

			public Location()
			{
				this(null);
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

			public Speed_MSec()
			{
				this(null);
			}
		}

		public static class Angle_Degrees extends QuantityCollection<Angle>
		{
			public Angle_Degrees(String name)
			{
				super(name, DEGREE_ANGLE.asType(Angle.class));
			}

			public Angle_Degrees()
			{
				this(null);
			}
		}

		public static class Length_M extends QuantityCollection<Length>
		{
			public Length_M(String name)
			{
				super(name, METRE.asType(Length.class));
			}

			public Length_M()
			{
				this(null);
			}

		}

		public static class DimensionlessDouble extends
				QuantityCollection<Dimensionless>
		{
			public DimensionlessDouble(String name)
			{
				super(name, Dimensionless.UNIT);
			}

			public DimensionlessDouble()
			{
				this(null);
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

			public Acceleration_MSecSec()
			{
				this(null);
			}

		}

		public static class Location extends ObjectCollection<Geometry> implements
				ILocations
		{
			public Location(String name)
			{
				super(name);
			}

			public Location()
			{
				this(null);
			}

			@Override
			public List<Geometry> getLocations()
			{
				return super.getValues();
			}
		}

		public static class Strings extends ObjectCollection<String>
		{
			public Strings(String name)
			{
				super(name);
			}

			public Strings()
			{
				this(null);
			}

		}
	}
}
