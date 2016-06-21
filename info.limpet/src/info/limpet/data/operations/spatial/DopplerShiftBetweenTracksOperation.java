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
package info.limpet.data.operations.spatial;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import info.limpet.data.impl.samples.StockTypes.Temporal.FrequencyHz;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.data.store.StoreGroup;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

public class DopplerShiftBetweenTracksOperation implements
    IOperation<IStoreItem>
{
  public static class DSOperation extends AbstractCommand<IStoreItem>
  {

    private static final String SOUND_SPEED = "SOUND_SPEED";
    private static final String LOC = "LOC";
    private static final String SPEED = "SPEED";
    private static final String COURSE = "COURSE";
    private static final String FREQ = "FREQ";
    private static final String TX = "TX_";

    /**
     * let the class organise a tidy set of data, to collate the assorted datasets
     * 
     */
    private transient HashMap<String, ICollection> _data;

    /**
     * nominated transmitter
     * 
     */
    private final StoreGroup _tx;

    /**
     * nominated receivers
     * 
     */
    private final List<TrackProvider> _allTracks;

    private final CollectionComplianceTests aTests =
        new CollectionComplianceTests();

    /**
     * way to make singleton locations (that don't have course/speed) look like tracks
     * 
     * @author ian
     * 
     */
    public interface TrackProvider
    {
      Point2D getLocationAt(long time);

      double getCourseAt(long time);

      double getSpeedAt(long time);

      String getName();

      /**
       * @param dsOperation
       */
      void addDependent(ICommand<IStoreItem> dsOperation);

    }

    protected static class SingletonWrapper implements TrackProvider
    {

      private final String _name;
      private Location _dataset;

      public SingletonWrapper(String name, Location loc)
      {
        _name = name;
        _dataset = loc;
      }

      @Override
      public Point2D getLocationAt(long time)
      {
        return _dataset.getValues().iterator().next();
      }

      @Override
      public double getCourseAt(long time)
      {
        return 0;
      }

      @Override
      public double getSpeedAt(long time)
      {
        return 0;
      }

      @Override
      public String getName()
      {
        return _name;
      }

      /*
       * (non-Javadoc)
       * 
       * @see info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation
       * .DSOperation.TrackProvider#addDependent(info.limpet.IOperation)
       */
      @Override
      public void addDependent(ICommand<IStoreItem> operation)
      {
        _dataset.addDependent(operation);
      }

    }

    protected static class CompositeTrackWrapper implements TrackProvider
    {

      private final String _name;

      private CollectionComplianceTests aTests =
          new CollectionComplianceTests();
      private IQuantityCollection<?> _course;
      private IQuantityCollection<?> _speed;
      private TemporalLocation _location;

      public CompositeTrackWrapper(IStoreGroup track, String name)
      {
        _name = name;

        // assign the components
        _course =
            aTests.collectionWith(track, Angle.UNIT.getDimension(), false);
        _speed =
            aTests.collectionWith(track, Velocity.UNIT.getDimension(), false);

        Iterator<IStoreItem> iter = track.iterator();
        while (iter.hasNext())
        {
          IStoreItem iStoreItem = (IStoreItem) iter.next();
          if (iStoreItem instanceof TemporalLocation)
          {
            _location = (TemporalLocation) iStoreItem;
          }
        }
      }

      /*
       * (non-Javadoc)
       * 
       * @see info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation
       * .DSOperation.TrackProvider#addDependent(info.limpet.IOperation)
       */
      @Override
      public void addDependent(ICommand<IStoreItem> operation)
      {
        _course.addDependent(operation);
        _speed.addDependent(operation);
        _location.addDependent(operation);
      }

      @Override
      public Point2D getLocationAt(long time)
      {
        return aTests.locationFor((ICollection) _location, time);
      }

      @Override
      public double getCourseAt(long time)
      {
        return aTests.valueAt(_course, time, RADIAN.asType(Angle.class));
      }

      @Override
      public double getSpeedAt(long time)
      {
        return aTests.valueAt(_speed, time, METRE.divide(SECOND).asType(
            Velocity.class));
      }

      @Override
      public String getName()
      {
        return _name;
      }

    }

    /**
     * find any selection items that we can use as tracks
     * 
     * @param ignoreMe
     * @param selection
     * @param aTests
     * @return
     */
    public static List<TrackProvider> getTracks(IStoreGroup ignoreMe,
        List<IStoreItem> selection, CollectionComplianceTests aTests)
    {
      List<TrackProvider> res = new ArrayList<TrackProvider>();

      Iterator<IStoreItem> iter = selection.iterator();
      while (iter.hasNext())
      {
        IStoreItem item = iter.next();
        if (item != ignoreMe)
        {
          if (item instanceof Location)
          {
            Location loc = (Location) item;
            res.add(new SingletonWrapper(loc.getName(), loc));
          }
          else if (item instanceof IStoreGroup)
          {
            // CHECK IF IT'S SUITABLE AS A TRACK. IF NOT, SEE IF IT JUST
            // CONTAINS LOCATIONS
            // - THEN ADD THEM ALL
            //
            IStoreGroup grp = (IStoreGroup) item;

            // see if this is a composite track
            // or, is this a conventional track
            if (aTests.isATrack(grp))
            {
              res.add(new CompositeTrackWrapper(grp, grp.getName()));
            }
            else
            {
              // see if this is a group of non-temporal locations
              Iterator<IStoreItem> iter2 = grp.iterator();
              while (iter2.hasNext())
              {
                IStoreItem iStoreItem = (IStoreItem) iter2.next();
                if (iStoreItem instanceof ICollection)
                {
                  ICollection coll = (ICollection) iStoreItem;
                  if (coll.getValuesCount() == 1)
                  {
                    if (coll instanceof Location)
                    {
                      final Location loc = (Location) coll;
                      res.add(new SingletonWrapper(coll.getName(), loc));
                    }
                  }
                }
              }
            }
          }
        }
      }
      return res;
    }

    public DSOperation(final StoreGroup tx, final IStore store,
        final String title, final String description,
        final List<IStoreItem> selection, IContext context)
    {
      super(title, description, store, true, true, selection, context);
      _tx = tx;

      // create the list of non-tx tracks
      _allTracks = getTracks(tx, selection, aTests);
    }

    @Override
    public void execute()
    {
      // store the data in an accessible way
      organiseData();

      // get the unit
      final List<IStoreItem> outputs = new ArrayList<IStoreItem>();

      // create the output dataset
      Iterator<TrackProvider> oIter = _allTracks.iterator();
      while (oIter.hasNext())
      {
        TrackProvider storeGroup = oIter.next();
        if (storeGroup != _tx)
        {
          // put the names into a string
          final String title =
              getOutputNameFor(_tx.getName(), storeGroup.getName());

          // ok, generate the new series
          final IQuantityCollection<?> target = getOutputCollection(title);

          outputs.add(target);

          // store the output
          super.addOutput(target);
        }
      }

      // start adding values.
      performCalc(outputs);

      // tell each series that we're a dependent
      final Iterator<ICollection> iter = _data.values().iterator();
      while (iter.hasNext())
      {
        final ICollection iCollection = iter.next();

        // sometimes a dataset is optional, so double-check we aren't
        // looking at a null dataset
        if (iCollection != null)
        {
          iCollection.addDependent(this);
        }
      }
      // and for the receiver tracks.
      oIter = _allTracks.iterator();
      while (oIter.hasNext())
      {
        TrackProvider track = (TrackProvider) oIter.next();
        track.addDependent(this);
      }

      // ok, done
      getStore().addAll(super.getOutputs());
    }

    @Override
    protected String getOutputName()
    {
      throw new RuntimeException("Get output name not implemented for Doppler");
      // return getContext().getInput("Doppler shift between tracks",
      // NEW_DATASET_MESSAGE,
      // "Doppler shift between " + _tx.getName() + " and " + _rx.getName());
    }

    @Override
    public void undo()
    {
      // ok, remove the calculated dataset
      IStoreItem results = getOutputs().iterator().next();
      IStore store = getStore();
      if (store instanceof StoreGroup)
      {
        StoreGroup im = (StoreGroup) store;
        im.remove(results);
      }
    }

    @Override
    public void redo()
    {
      IStoreItem results = getOutputs().iterator().next();
      IStore store = getStore();
      if (store instanceof StoreGroup)
      {
        StoreGroup im = (StoreGroup) store;
        im.add(results);
      }
    }

    public HashMap<String, ICollection> getDataMap()
    {
      return _data;
    }

    protected IQuantityCollection<?> getOutputCollection(final String title)
    {
      return new StockTypes.Temporal.FrequencyHz(title, this);
    }

    protected String getOutputNameFor(final String tx, String rx)
    {
      return "Doppler shift between " + tx + " and " + rx;
    }

    public void organiseData()
    {
      if (_data == null)
      {
        // ok, we need to collate the data
        _data = new HashMap<String, ICollection>();

        final CollectionComplianceTests tests = new CollectionComplianceTests();

        // ok, transmitter data
        _data.put(TX + FREQ, tests.collectionWith(_tx, Frequency.UNIT
            .getDimension(), true));
        _data.put(TX + COURSE, tests.collectionWith(_tx, SI.RADIAN
            .getDimension(), true));
        _data.put(TX + SPEED, tests.collectionWith(_tx, METRE.divide(SECOND)
            .getDimension(), true));
        _data.put(TX + LOC, tests.someHaveLocation(_tx));

        // and the sound speed
        _data.put(SOUND_SPEED, tests.collectionWith(getInputs(), METRE.divide(
            SECOND).getDimension(), false));
      }
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     * @param outputs
     */
    private void performCalc(final List<IStoreItem> outputs)
    {
      // just check we've been organised (if we've been loaded from persistent
      // storage)
      organiseData();

      // and the bounding period
      final TimePeriod period = aTests.getBoundingTime(_data.values());

      // check it's valid
      if (period.invalid())
      {
        System.err.println("Insufficient coverage for datasets");
        return;
      }

      // ok, let's start by finding our time sync
      final IBaseTemporalCollection times =
          aTests.getOptimalTimes(period, _data.values());

      // check we were able to find some times
      if (times == null)
      {
        System.err.println("Unable to find time source dataset");
        return;
      }

      // keep a list of updated tracks
      List<ICollection> updated = new ArrayList<ICollection>();

      final IGeoCalculator calc = GeoSupport.getCalculator();

      // ok, now loop through the receivers
      Iterator<TrackProvider> rIter = _allTracks.iterator();
      while (rIter.hasNext())
      {
        TrackProvider trackProvider = (TrackProvider) rIter.next();

        // find the relevant outputs dataset
        String thisOutName =
            getOutputNameFor(_tx.getName(), trackProvider.getName());

        Iterator<IStoreItem> oIter = getOutputs().iterator();
        FrequencyHz thisOutput = null;
        while (oIter.hasNext() && thisOutput == null)
        {
          FrequencyHz tmpOutput = (FrequencyHz) oIter.next();
          if (tmpOutput.getName().equals(thisOutName)
              && (tmpOutput.getValuesCount() == 0))
          {
            thisOutput = tmpOutput;
          }
        }

        if (thisOutput == null)
        {
          continue;
        }
            
        // and now we can start looping through
        final Iterator<Long> tIter = times.getTimes().iterator();
        while (tIter.hasNext())
        {
          final long thisTime = tIter.next();

          if (thisTime >= period.getStartTime()
              && thisTime <= period.getEndTime())
          {
            // ok, now collate our data
            final Point2D txLoc =
                aTests.locationFor(_data.get(TX + LOC), thisTime);

            final double txCourseRads =
                aTests.valueAt(_data.get(TX + COURSE), thisTime, SI.RADIAN);

            final double txSpeedMSec =
                aTests.valueAt(_data.get(TX + SPEED), thisTime,
                    SI.METERS_PER_SECOND);

            final double freq =
                aTests.valueAt(_data.get(TX + FREQ), thisTime, SI.HERTZ);

            final double soundSpeed =
                aTests.valueAt(_data.get(SOUND_SPEED), thisTime,
                    SI.METERS_PER_SECOND);

            final Point2D rxLoc = trackProvider.getLocationAt(thisTime);
            final double rxCourseRads = trackProvider.getCourseAt(thisTime);
            final double rxSpeedMSec = trackProvider.getSpeedAt(thisTime);

            // check we have locations. During some property editing we receive
            // recalc call
            // after old value is removed, and before new value is added.
            if (txLoc != null && rxLoc != null)
            {
              // now find the bearing between them
              double angleDegs = calc.getAngleBetween(txLoc, rxLoc);

              if (angleDegs < 0)
              {
                angleDegs += 360;
              }

              final double angleRads = Math.toRadians(angleDegs);

              // ok, and the calculation
              final double shifted =
                  calcPredictedFreqSI(soundSpeed, txCourseRads, rxCourseRads,
                      txSpeedMSec, rxSpeedMSec, angleRads, freq);

              // see if we have an output collection for this input one.
              thisOutput.add(thisTime, shifted);

              if (!updated.contains(thisOutput))
              {
                updated.add(thisOutput);
              }
            }
          }
        }
      }
      Iterator<ICollection> updates = updated.iterator();
      while (updates.hasNext())
      {
        ICollection iCollection = (ICollection) updates.next();
        iCollection.fireDataChanged();
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // do we know which subject this relates to?
      // just one of our input datasets has changed
      boolean handled = false;
      final Iterator<IStoreItem> iter = getOutputs().iterator();
      final String nameToRemove =
          getOutputNameFor(_tx.getName(), subject.getName());
      while (iter.hasNext())
      {
        final IQuantityCollection<?> qC =
            (IQuantityCollection<?>) iter.next();
        if (qC.getName().equals(nameToRemove))
        {
          qC.clearQuiet();
          handled = true;
          break;
        }
      }
      
      // did we manage a precision surgical removal?
      if (!handled)
      {
        // clear out all the lists, first
        Iterator<IStoreItem> iter2 = getOutputs().iterator();
        while (iter2.hasNext())
        {
          final IQuantityCollection<?> qC =
              (IQuantityCollection<?>) iter2.next();
          qC.clearQuiet();
        }
      }

      // update the results
      performCalc(getOutputs());
    }

  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  @Override
  public Collection<ICommand<IStoreItem>> actionsFor(
      final List<IStoreItem> selection, final IStore destination,
      IContext context)
  {
    final Collection<ICommand<IStoreItem>> res =
        new ArrayList<ICommand<IStoreItem>>();
    if (appliesTo(selection))
    {
      // get the list of tracks
      ArrayList<StoreGroup> trackList = aTests.getChildTrackGroups(selection);

      // ok, loop through them
      Iterator<StoreGroup> iter = trackList.iterator();
      while (iter.hasNext())
      {
        StoreGroup thisG = (StoreGroup) iter.next();
        final boolean hasFrequency =
            aTests.collectionWith(thisG, Frequency.UNIT.getDimension(), true) != null;
        if (hasFrequency)
        {
          final ICommand<IStoreItem> newC =
              new DSOperation(thisG, destination,
                  "Doppler between tracks (from " + thisG.getName() + ")",
                  "Calculate doppler between two tracks", selection, context);
          res.add(newC);
        }
      }
    }

    return res;
  }

  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    // ok, check we have two collections
    final boolean allTracks = aTests.getNumberOfTracks(selection) >= 2;
    final boolean someHaveFreq =
        aTests.collectionWith(selection, Frequency.UNIT.getDimension(), true) != null;
    final boolean topLevelSpeed =
        aTests.collectionWith(selection, METRE.divide(SECOND).getDimension(),
            false) != null;

    return allTracks && someHaveFreq && topLevelSpeed;
  }

  /**
   * 
   * @param speedOfSound
   * @param osHeadingRads
   * @param tgtHeadingRads
   * @param osSpeed
   * @param tgtSpeed
   * @param bearing
   * @param fNought
   * @return
   */
  private static double calcPredictedFreqSI(final double speedOfSound,
      final double osHeadingRads, final double tgtHeadingRads,
      final double osSpeed, final double tgtSpeed, final double bearing,
      final double fNought)
  {
    final double relB = bearing - osHeadingRads;

    // note - contrary to some publications TSL uses the
    // angle along the bearing, not the angle back down the bearing (ATB).
    final double angleOffTheOtherB = tgtHeadingRads - bearing;

    final double valOSL = Math.cos(relB) * osSpeed;
    final double valTSL = Math.cos(angleOffTheOtherB) * tgtSpeed;

    final double freq =
        fNought * (speedOfSound + valOSL) / (speedOfSound + valTSL);

    return freq;
  }
}
