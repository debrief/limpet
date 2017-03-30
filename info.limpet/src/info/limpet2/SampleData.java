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
package info.limpet2;

import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet2.operations.arithmetic.AddQuantityOperation;
import info.limpet2.operations.spatial.GeoSupport;
import info.limpet2.operations.spatial.IGeoCalculator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

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

  private static class TmpTemporalStore
  {
    private String _name;
    private Unit<?> _units;
    private ArrayList<Long> _times;
    private ArrayList<Double> _values;
    private ICommand _predecessor;

    TmpTemporalStore(String name, Unit<?> units, ICommand predecessor)
    {
      _name = name;
      _units = units;
      _predecessor = predecessor;

      _times = new ArrayList<Long>();
      _values = new ArrayList<Double>();

    }

    void add(long time, double value)
    {
      _times.add(time);
      _values.add(value);
    }
    
    public Document toDocument()
    {
      DoubleDataset dataset = (DoubleDataset) DatasetFactory.createFromObject(_values);
      dataset.setName(_name);
      
      // sort out the time axis
      LongDataset timeData = (LongDataset) DatasetFactory.createFromObject(_times);
      final AxesMetadata timeAxis = new AxesMetadataImpl();
      timeAxis.initialize(1);      
      timeAxis.setAxis(0, timeData);
      
      
      NumberDocument res = new NumberDocument(dataset, _predecessor, _units);
      return res;
    }
  }

  private static class TmpStore
  {
    private String _name;
    private Unit<?> _units;
    private List<Double> _values;
    private ICommand _predecessor;
    private Range _range;

    TmpStore(String name, Unit<?> units, ICommand predecessor)
    {
      _name = name;
      _units = units;
      _predecessor = predecessor;

      _values = new ArrayList<Double>();

    }

    void add(double value)
    {
      _values.add(value);
    }

    public void setRange(Range range)
    {
      _range = range;
    }

    public Document toDocument()
    {
      DoubleDataset dataset = (DoubleDataset) DatasetFactory.createFromObject(_values);
      dataset.setName(_name);
      NumberDocument res = new NumberDocument(dataset, _predecessor, _units);
      return res;
    }
  }

  private static class ObjectColl extends ArrayList<Object>
  {
    private String _name;

    public ObjectColl(String name)
    {
      _name = name;
    }

    public IStoreItem toDocument()
    {
      // TODO Auto-generated method stub
      return null;
    }
  }

  public static final Unit<?> DEGREE_ANGLE = RADIAN.times(Math.PI / 180d);

  public StoreGroup getData(long count)
  {
    final StoreGroup list = new StoreGroup("Sample Data");

    // // collate our data series
    TmpStore freq1 =
        new TmpStore(FREQ_ONE, HERTZ.asType(Frequency.class), null);
    TmpTemporalStore angle1 =
        new TmpTemporalStore(ANGLE_ONE, DEGREE_ANGLE.asType(Angle.class), null);
    TmpTemporalStore speedSeries1 =
        new TmpTemporalStore(SPEED_ONE, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpTemporalStore speedSeries2 =
        new TmpTemporalStore(SPEED_TWO, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpTemporalStore speedSeries3 =
        new TmpTemporalStore(SPEED_THREE_LONGER, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpTemporalStore speedEarly1 =
        new TmpTemporalStore(SPEED_EARLY, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpTemporalStore speedIrregular =
        new TmpTemporalStore(SPEED_IRREGULAR2, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpTemporalStore speedSeries4 =
        new TmpTemporalStore(SPEED_FOUR, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpStore length1 =
        new TmpStore(LENGTH_ONE, METRE.asType(Length.class), null);
    TmpStore length2 =
        new TmpStore(LENGTH_TWO, METRE.asType(Length.class), null);
    ObjectColl string1 = new ObjectColl(STRING_ONE);
    ObjectColl string2 = new ObjectColl(STRING_TWO);
    TmpStore singleton1 =
        new TmpStore(FLOATING_POINT_FACTOR, Dimensionless.UNIT, null);
    TmpStore singletonRange1 =
        new TmpStore(RANGED_SPEED_SINGLETON, METRE.divide(SECOND).asType(
            Velocity.class), null);
    TmpStore singletonLength =
        new TmpStore(LENGTH_SINGLETON, METRE.asType(Length.class), null);
    TmpTemporalStore timeIntervals =
        new TmpTemporalStore(TIME_INTERVALS, SECOND.asType(Duration.class),
            null);
    TmpStore timeStamps_1 =
        new TmpStore(TIME_STAMPS_1, SECOND.asType(Duration.class), null);
    TmpStore timeStamps_2 =
        new TmpStore(TIME_STAMPS_2, SECOND.asType(Duration.class), null);
    // TemporalLocation track1 = new TemporalLocation(TRACK_ONE);
    // TemporalLocation track2 = new TemporalLocation(TRACK_TWO);
//    Location singleLoc1 = new Location(SINGLETON_LOC_1);
//    Location singleLoc2 = new Location(SINGLETON_LOC_2);

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

      if (i < ((double) count) * 0.4)
      {
        if (Math.random() > 0.3)
        {
          timeStamps_1.add(thisTime - interval
              + (interval * 2d * Math.random()));
        }
      }
      if (i > ((double) count) * 0.7)
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

      // track1.add(thisTime, p1);
      // track2.add(thisTime, p2);

    }

    // add an extra item to speedSeries3
    speedSeries3.add(thisTime + 12 * 500 * 60, 12);

    // give the singletons a value
    singleton1.add(4d);
    singletonRange1.add(998);
    double minR = 940d;
    double maxR = 1050d;
    Range speedRange = new Range(minR, maxR);
    singletonRange1.setRange(speedRange);
    freq1.add(77);
//    singleLoc1.add(calc.createPoint(12, 13));
//    singleLoc2.add(calc.createPoint(7, 7));
    singletonLength.add(12d);
    
    StoreGroup group1 = new StoreGroup("Speed data");
    group1.add(speedSeries1.toDocument());
    group1.add(speedIrregular.toDocument());
    group1.add(speedEarly1.toDocument());
    group1.add(speedSeries2.toDocument());
    list.add(group1);

//    IStoreGroup factors = new StoreGroup("Factors");
//    StockTypes.NonTemporal.SpeedMSec singletonRange2 =
//        new StockTypes.NonTemporal.SpeedMSec(RANGED_SPEED_SINGLETON, null);
//    double minR1 = 940d;
//    double maxR1 = 1050d;
//    Range speedRange1 = new Range(minR1, maxR1);
//    singletonRange2.setRange(speedRange1);
//    singletonRange2.add(998);
    Unit<Velocity> unit = Velocity.UNIT;

//    IQuantityCollection<Velocity> singletonSpeed =
//        new QuantityCollection<Velocity>("Ranged Speed", null, Velocity.UNIT);
//    double min = 0d;
//    double max =100d;
//    singletonSpeed.add(54);
//    singletonSpeed.setRange(new Range(min, max));

//    IQuantityCollection<Length> singletonLength1 =
//        new QuantityCollection<Length>("Ranged Speed", null, Length.UNIT);
//    Unit<Length> lUnit = Length.UNIT;
//    Measure<Double, Length> minL = Measure.valueOf(40d, lUnit);
//    Measure<Double, Length> maxL = Measure.valueOf(150d, lUnit);
//    singletonLength1.add(134);
//    singletonLength1.setRange(new QuantityRange<Length>(minL, maxL));

//    factors.add(singletonRange2);
//    factors.add(singletonSpeed);
//    factors.add(singletonLength1);
//
//    list.add(factors);

//    IStoreGroup compositeTrack = new StoreGroup(COMPOSITE_ONE);
//    compositeTrack.add(angle1);
//    compositeTrack.add(track2);
//    compositeTrack.add(freq1);
//    compositeTrack.add(speedSeries4);

//    list.add(compositeTrack);

    list.add(length1.toDocument());
    list.add(length2.toDocument());
    list.add(string1.toDocument());
    list.add(string2.toDocument());
    list.add(singleton1.toDocument());
    list.add(singletonRange1.toDocument());
    list.add(singletonLength.toDocument());
    list.add(timeIntervals.toDocument());
    list.add(timeStamps_1.toDocument());
    list.add(timeStamps_2.toDocument());
//    list.add(track1);
//    list.add(track2);
//    list.add(singleLoc1);
//    list.add(singleLoc2);
    list.add(speedSeries3.toDocument());

//    res.addAll(list);

    // perform an operation, so we have some audit trail
    List<Document> selection = new ArrayList<Document>();
    selection.add(speedSeries1.toDocument());
    selection.add(speedSeries2.toDocument());
    IContext context = new MockContext();
    Collection<ICommand> actions =
        new AddQuantityOperation().actionsFor(selection, list, context);
    Iterator<ICommand> addIter = actions.iterator();
//    addIter.next();
    ICommand addAction = addIter.next();
    addAction.execute();

    // and an operation using our speed factor
//    selection.clear();
//    selection.add(speedSeries1.toDocument());
//    selection.add(singleton1.toDocument());
//    Collection<ICommand> actions2 =
//        new MultiplyQuantityOperation().actionsFor(selection, list, context);
//    addAction = actions2.iterator().next();
//    addAction.execute();
//
//    // calculate the distance travelled
//    selection.clear();
//    selection.add(timeIntervals);
//    selection.add(singletonRange1);
//    Collection<ICommand> actions3 =
//        new MultiplyQuantityOperation().actionsFor(selection, list, context);
//    addAction = actions3.iterator().next();
//    addAction.execute();

    return list;
  }
}
