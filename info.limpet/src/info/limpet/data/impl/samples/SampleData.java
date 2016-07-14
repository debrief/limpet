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

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.QuantityRange;
import info.limpet.data.impl.MockContext;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTimeSec;
import info.limpet.data.operations.arithmetic.AddQuantityOperation;
import info.limpet.data.operations.arithmetic.MultiplyQuantityOperation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.operations.spatial.IGeoCalculator;
import info.limpet.data.store.StoreGroup;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

public class SampleData
{
  public static final String SPEED_THREE_LONGER = "Speed Three (longer)";
  public static final String SPEED_IRREGULAR2 = "Speed two irregular time";
  public static final String TIME_INTERVALS = "Time intervals";
  public static final String TIME_STAMPS_1 = "Time stamps (early)";
  public static final String TIME_STAMPS_2 = "Time stamps (late)";
  public static final String STRING_TWO = "String two";
  public static final String STRING_ONE = "String one";
  public static final String LENGTH_SINGLETON = "Length Singleton";
  public static final String LENGTH_TWO = "Length Two non-Time";
  public static final String LENGTH_ONE = "Length One non-Time";
  public static final String ANGLE_ONE = "Angle One Time";
  public static final String FREQ_ONE = "Freq One";
  public static final String SPEED_ONE = "Speed One Time";
  public static final String SPEED_TWO = "Speed Two Time";
  public static final String SPEED_FOUR = "Speed Four Time";
  public static final String TRACK_ONE = "Track One Time";
  public static final String COMPOSITE_ONE = "Composite Track One";
  public static final String TRACK_TWO = "Track Two Time";
  public static final String SPEED_EARLY = "Speed Two Time (earlier)";
  public static final String RANGED_SPEED_SINGLETON = "Ranged Speed Singleton";
  public static final String FLOATING_POINT_FACTOR = "Floating point factor";
  public static final String SINGLETON_LOC_1 = "Single Track One";
  public static final String SINGLETON_LOC_2 = "Single Track Two";

  public StoreGroup getData(long count)
  {
    StoreGroup res = new StoreGroup();

    // // collate our data series
    StockTypes.NonTemporal.FrequencyHz freq1 =
        new StockTypes.NonTemporal.FrequencyHz(FREQ_ONE, null);
    StockTypes.Temporal.AngleDegrees angle1 =
        new StockTypes.Temporal.AngleDegrees(ANGLE_ONE, null);
    StockTypes.Temporal.SpeedMSec speedSeries1 =
        new StockTypes.Temporal.SpeedMSec(SPEED_ONE, null);
    StockTypes.Temporal.SpeedMSec speedSeries2 =
        new StockTypes.Temporal.SpeedMSec(SPEED_TWO, null);
    StockTypes.Temporal.SpeedMSec speedSeries3 =
        new StockTypes.Temporal.SpeedMSec(SPEED_THREE_LONGER, null);
    StockTypes.Temporal.SpeedMSec speedEarly1 =
        new StockTypes.Temporal.SpeedMSec(SPEED_EARLY, null);
    StockTypes.Temporal.SpeedMSec speedIrregular =
        new StockTypes.Temporal.SpeedMSec(SPEED_IRREGULAR2, null);
    StockTypes.Temporal.SpeedMSec speedSeries4 =
        new StockTypes.Temporal.SpeedMSec(SPEED_FOUR, null);
    StockTypes.NonTemporal.LengthM length1 =
        new StockTypes.NonTemporal.LengthM(LENGTH_ONE, null);
    StockTypes.NonTemporal.LengthM length2 =
        new StockTypes.NonTemporal.LengthM(LENGTH_TWO, null);
    IObjectCollection<String> string1 =
        new ObjectCollection<String>(STRING_ONE);
    IObjectCollection<String> string2 =
        new ObjectCollection<String>(STRING_TWO);
    IQuantityCollection<Dimensionless> singleton1 =
        new QuantityCollection<Dimensionless>(FLOATING_POINT_FACTOR, null,
            Dimensionless.UNIT);
    StockTypes.NonTemporal.SpeedMSec singletonRange1 =
        new StockTypes.NonTemporal.SpeedMSec(RANGED_SPEED_SINGLETON, null);
    StockTypes.NonTemporal.LengthM singletonLength =
        new StockTypes.NonTemporal.LengthM(LENGTH_SINGLETON, null);
    ElapsedTimeSec timeIntervals =
        new StockTypes.Temporal.ElapsedTimeSec(TIME_INTERVALS, null);
    StockTypes.NonTemporal.ElapsedTimeSec timeStamps_1 =
        new StockTypes.NonTemporal.ElapsedTimeSec(TIME_STAMPS_1, null);
    StockTypes.NonTemporal.ElapsedTimeSec timeStamps_2 =
        new StockTypes.NonTemporal.ElapsedTimeSec(TIME_STAMPS_2, null);
    TemporalLocation track1 = new TemporalLocation(TRACK_ONE);
    TemporalLocation track2 = new TemporalLocation(TRACK_TWO);
    Location singleLoc1 = new Location(SINGLETON_LOC_1);
    Location singleLoc2 = new Location(SINGLETON_LOC_2);

    long thisTime = 0;

    // get ready for the track generation
    final IGeoCalculator calc = GeoSupport.getCalculator();
    Point2D pos1 = calc.createPoint(-4, 55.8);
    Point2D pos2 = calc.createPoint(-4.2, 54.9);

    final long interval = 500L * 60;

    for (int i = 1; i <= count; i++)
    {
      thisTime = new Date().getTime() + i * interval;

      final long earlyTime = thisTime - (1000 * 60 * 60 * 24 * 365 * 20);

      angle1.add(thisTime, 90 + 1.1 * Math.toDegrees(Math.sin(Math
          .toRadians(i * 52.5))));
      speedSeries1.add(thisTime, 1 / Math.sin(i));
      speedSeries2.add(thisTime, 7 + 2 * Math.sin(i));

      // we want the irregular series to only have occasional
      if (i % 5 == 0)
      {
        speedIrregular.add(thisTime + 500 * 45, 7 + 2 * Math.sin(i + 1));
      }
      else
      {
        if (Math.random() > 0.6)
        {
          speedIrregular.add(thisTime + 500 * 25 * 2, 7 + 2 * Math.sin(i - 1));
        }
      }

      speedSeries3.add(thisTime, 3d * Math.cos(i));
      speedSeries4.add(thisTime, 3 + 2d * Math.cos(i / 10));
      speedEarly1.add(earlyTime, Math.sin(i));
      length1.add((double) i % 3);
      length2.add((double) i % 5);
      string1.add("item " + i);
      string2.add("item " + (i % 3));
      timeIntervals.add(thisTime, (4 + Math.sin(Math.toRadians(i) + 3.4
          * Math.random())));

      if (i < ((double)count) * 0.4)
      {
        if (Math.random() > 0.3)
        {
          timeStamps_1.add(thisTime - interval
              + (interval * 2d * Math.random()));
        }
      }
      if (i > ((double)count) * 0.7)
      {
        if (Math.random() > 0.3)
        {
          timeStamps_2.add(thisTime - interval
              + (interval * 2d * Math.random()));
        }
      }
      
      // sort out the tracks
      Point2D p1 = calc.calculatePoint(pos1, Math.toRadians(77 - (i * 4)), 554);

      Point2D p2 = calc.calculatePoint(pos2, Math.toRadians(54 + (i * 5)), 133);

      track1.add(thisTime, p1);
      track2.add(thisTime, p2);

    }

    // add an extra item to speedSeries3
    speedSeries3.add(thisTime + 12 * 500 * 60, 12);

    // give the singletons a value
    singleton1.add(4d);
    singletonRange1.add(998);
    Measure<Double, Velocity> minR =
        Measure.valueOf(940d, singletonRange1.getUnits());
    Measure<Double, Velocity> maxR =
        Measure.valueOf(1050d, singletonRange1.getUnits());
    QuantityRange<Velocity> speedRange =
        new QuantityRange<Velocity>(minR, maxR);
    singletonRange1.setRange(speedRange);
    freq1.add(77);
    singleLoc1.add(calc.createPoint(12, 13));
    singleLoc2.add(calc.createPoint(7, 7));

    singletonLength.add(12d);

    IStoreGroup list = new StoreGroup();
    StoreGroup group1 = new StoreGroup("Speed data");
    group1.add(speedSeries1);
    group1.add(speedIrregular);
    group1.add(speedEarly1);
    group1.add(speedSeries2);
    list.add(group1);

    IStoreGroup factors = new StoreGroup("Factors");
    StockTypes.NonTemporal.SpeedMSec singletonRange2 =
        new StockTypes.NonTemporal.SpeedMSec(RANGED_SPEED_SINGLETON, null);
    Measure<Double, Velocity> minR1 =
        Measure.valueOf(940d, singletonRange1.getUnits());
    Measure<Double, Velocity> maxR1 =
        Measure.valueOf(1050d, singletonRange1.getUnits());
    QuantityRange<Velocity> speedRange1 =
        new QuantityRange<Velocity>(minR1, maxR1);
    singletonRange2.setRange(speedRange1);
    singletonRange2.add(998);
    Unit<Velocity> unit = Velocity.UNIT;

    IQuantityCollection<Velocity> singletonSpeed =
        new QuantityCollection<Velocity>("Ranged Speed", null, Velocity.UNIT);
    Measure<Double, Velocity> min = Measure.valueOf(0d, unit);
    Measure<Double, Velocity> max = Measure.valueOf(100d, unit);
    singletonSpeed.add(54);
    singletonSpeed.setRange(new QuantityRange<Velocity>(min, max));

    IQuantityCollection<Length> singletonLength1 =
        new QuantityCollection<Length>("Ranged Speed", null, Length.UNIT);
    Unit<Length> lUnit = Length.UNIT;
    Measure<Double, Length> minL = Measure.valueOf(40d, lUnit);
    Measure<Double, Length> maxL = Measure.valueOf(150d, lUnit);
    singletonLength1.add(134);
    singletonLength1.setRange(new QuantityRange<Length>(minL, maxL));
    
    factors.add(singletonRange2);
    factors.add(singletonSpeed);
    factors.add(singletonLength1);

    list.add(factors);
    
    IStoreGroup compositeTrack = new StoreGroup(COMPOSITE_ONE);
    compositeTrack.add(angle1);
    compositeTrack.add(track2);
    compositeTrack.add(freq1);
    compositeTrack.add(speedSeries4);
    
    list.add(compositeTrack);

    list.add(length1);
    list.add(length2);
    list.add(string1);
    list.add(string2);
    list.add(singleton1);
    list.add(singletonRange1);
    list.add(singletonLength);
    list.add(timeIntervals);
    list.add(timeStamps_1);
    list.add(timeStamps_2);
    list.add(track1);
    list.add(track2);
    list.add(singleLoc1);
    list.add(singleLoc2);
    list.add(speedSeries3);

    res.addAll(list);

    // perform an operation, so we have some audit trail
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(speedSeries1);
    selection.add(speedSeries2);
    IContext context = new MockContext();
    @SuppressWarnings(
    {"unchecked", "rawtypes"})
    Collection<ICommand<?>> actions =
        new AddQuantityOperation().actionsFor(selection, res, context);
    Iterator<ICommand<?>> addIter = actions.iterator();
    addIter.next();
    ICommand<?> addAction = addIter.next();
    addAction.execute();

    // and an operation using our speed factor
    selection.clear();
    selection.add(speedSeries1);
    selection.add(singleton1);
    Collection<ICommand<IStoreItem>> actions2 =
        new MultiplyQuantityOperation().actionsFor(selection, res, context);
    addAction = actions2.iterator().next();
    addAction.execute();

    // calculate the distance travelled
    selection.clear();
    selection.add(timeIntervals);
    selection.add(singletonRange1);
    Collection<ICommand<IStoreItem>> actions3 =
        new MultiplyQuantityOperation().actionsFor(selection, res, context);
    addAction = actions3.iterator().next();
    addAction.execute();

    return res;
  }
}
