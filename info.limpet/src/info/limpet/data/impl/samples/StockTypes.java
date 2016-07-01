/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
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

import java.awt.geom.Point2D;
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
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

public class StockTypes
{

  /** protected static class, to prevent accidental declaration
   * 
   */
  protected StockTypes()
  {
    
  }

  public static final Unit<?> DEGREE_ANGLE = RADIAN.times(Math.PI / 180d);

  public interface ILocations
  {
    List<Point2D> getLocations();
  }

  /**
   * time series (temporal) collections
   * 
   * @author ian
   * 
   */
  public static class Temporal
  {
    public static class SpeedMSec extends TemporalQuantityCollection<Velocity>
    {
      public SpeedMSec(String name, ICommand<?> precedent)
      {
        super(name, precedent, METRE.divide(SECOND).asType(Velocity.class));
      }

      public SpeedMSec()
      {
        this(null, null);
      }
    }

    public static class SpeedKts extends TemporalQuantityCollection<Velocity>
    {
      public SpeedKts(String name, ICommand<?> prededent)
      {
        super(name, prededent, NAUTICAL_MILE.divide(SECOND.times(3600)).asType(
            Velocity.class));
      }

      public SpeedKts()
      {
        this(null, null);
      }
    }

    public static class DimensionlessDouble extends
        TemporalQuantityCollection<Dimensionless>
    {
      public DimensionlessDouble(String name, ICommand<?> precedent)
      {
        super(name, precedent, Dimensionless.UNIT);
      }

      public DimensionlessDouble()
      {
        this(null, null);
      }
    }

    public static class LengthM extends TemporalQuantityCollection<Length>
    {

      public LengthM(String name, ICommand<?> precedent)
      {
        super(name, precedent, METRE.asType(Length.class));
      }

      public LengthM()
      {
        this(null, null);
      }
    }

    public static class LengthYd extends TemporalQuantityCollection<Length>
    {
      public LengthYd(String name, ICommand<?> precedent)
      {
        super(name, precedent, YARD.asType(Length.class));
      }

      public LengthYd()
      {
        this(null, null);
      }
    }

    public static class TemperatureC extends
        TemporalQuantityCollection<Temperature>
    {
      public TemperatureC(String name, ICommand<?> precedent)
      {
        super(name, precedent, KELVIN.asType(Temperature.class));
      }

      public TemperatureC()
      {
        this(null, null);
      }
    }

    public static class TurnRate extends
        TemporalQuantityCollection<AngularVelocity>
    {
      public TurnRate(String name, ICommand<?> precedent)
      {
        super(name, precedent, DEGREE_ANGLE.divide(SECOND).asType(
            AngularVelocity.class));
      }

      public TurnRate()
      {
        this(null, null);
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

    public static class AccelerationMSecSec extends
        TemporalQuantityCollection<Acceleration>
    {
      public AccelerationMSecSec(String name, ICommand<?> precedent)
      {
        super(name, precedent, METRE.divide(SECOND).divide(SECOND)
            .asType(Acceleration.class));
      }

      public AccelerationMSecSec()
      {
        this(null, null);
      }
    }

    public static class ElapsedTimeSec extends
        TemporalQuantityCollection<Duration>
    {
      public ElapsedTimeSec(String name, ICommand<?> precedent)
      {
        super(name, precedent, SECOND.asType(Duration.class));
      }

      public ElapsedTimeSec()
      {
        this(null, null);
      }
    }

    public static class FrequencyHz extends
        TemporalQuantityCollection<Frequency>
    {
      public FrequencyHz(String name, ICommand<?> precedent)
      {
        super(name, precedent, HERTZ.asType(Frequency.class));
      }

      public FrequencyHz()
      {
        this(null, null);
      }

    }

    public static class AcousticStrength extends
        TemporalQuantityCollection<Dimensionless>
    {
      public AcousticStrength(String name, ICommand<?> precedent)
      {
        super(name, precedent, NonSI.DECIBEL);
      }

      public AcousticStrength(String name)
      {
        this(name, null);
      }

      public AcousticStrength()
      {
        this(null);
      }
    }

    public static class AngleRadians extends TemporalQuantityCollection<Angle>
    {
      public AngleRadians(String name, ICommand<?> precedent)
      {
        super(name, precedent, RADIAN.asType(Angle.class));
      }

      public AngleRadians()
      {
        this(null, null);
      }
    }

    public static class AngleDegrees extends TemporalQuantityCollection<Angle>
    {
      public AngleDegrees(String name, ICommand<?> precedent)
      {
        super(name, precedent, DEGREE_ANGLE.asType(Angle.class));
      }

      public AngleDegrees()
      {
        this(null, null);
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
    public static class SpeedMSec extends QuantityCollection<Velocity>
    {
      public SpeedMSec()
      {
        this(null, null);
      }

      public SpeedMSec(String name, ICommand<?> precedent)
      {
        super(name, precedent, METRE.divide(SECOND).asType(Velocity.class));
      }
    }

    public static class SpeedKts extends QuantityCollection<Velocity>
    {
      public SpeedKts(String name, ICommand<?> precedent)
      {
        super(name, precedent, NAUTICAL_MILE.divide(SECOND.times(3600)).asType(
            Velocity.class));
      }

      public SpeedKts()
      {
        this(null, null);
      }
    }

    public static class AngleDegrees extends QuantityCollection<Angle>
    {
      public AngleDegrees(String name, ICommand<?> precedent)
      {
        super(name, precedent, DEGREE_ANGLE.asType(Angle.class));
      }

      public AngleDegrees()
      {
        this(null, null);
      }
    }

    public static class AngleRadians extends QuantityCollection<Angle>
    {
      public AngleRadians(String name, ICommand<?> precedent)
      {
        super(name, precedent, RADIAN.asType(Angle.class));
      }

      public AngleRadians()
      {
        this(null, null);
      }
    }

    public static class AcousticStrength extends
        QuantityCollection<Dimensionless>
    {
      public AcousticStrength(String name, ICommand<?> precedent)
      {
        super(name, precedent, NonSI.DECIBEL);
      }

      public AcousticStrength(String name)
      {
        this(name, null);
      }

      public AcousticStrength()
      {
        this(null);
      }
    }

    public static class FrequencyHz extends QuantityCollection<Frequency>
    {
      public FrequencyHz()
      {
        this(null, null);
      }

      public FrequencyHz(String name, ICommand<?> precedent)
      {
        super(name, precedent, HERTZ.asType(Frequency.class));
      }
    }

    public static class LengthM extends QuantityCollection<Length>
    {

      public LengthM(String name, ICommand<?> precedent)
      {
        super(name, precedent, METRE.asType(Length.class));
      }

      public LengthM()
      {
        this(null, null);
      }
    }
    
    public static class ElapsedTimeSec extends QuantityCollection<Duration>
    {
      public ElapsedTimeSec(String name, ICommand<?> precedent)
      {
        super(name, precedent, SECOND.asType(Duration.class));
      }

      public ElapsedTimeSec()
      {
        this(null, null);
      }
    }

    public static class DimensionlessDouble extends
        QuantityCollection<Dimensionless>
    {
      public DimensionlessDouble(String name, ICommand<?> precedent)
      {
        super(name, precedent, Dimensionless.UNIT);
      }
      
      public DimensionlessDouble()
      {
        this(null, null);
      }

    }

    public static class AccelerationMSecSec extends
        QuantityCollection<Acceleration>
    {
      public AccelerationMSecSec(String name, ICommand<?> precedent)
      {
        super(name, precedent, METRE.divide(SECOND).divide(SECOND)
            .asType(Acceleration.class));
      }

      public AccelerationMSecSec()
      {
        this(null, null);
      }

    }

    public static class Location extends ObjectCollection<Point2D> implements
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
      public List<Point2D> getLocations()
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
