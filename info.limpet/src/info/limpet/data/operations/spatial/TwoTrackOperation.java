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

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Location;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.CollectionComplianceTests.TimePeriod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;

public abstract class TwoTrackOperation implements IOperation<IStoreItem>
{

  public abstract static class DistanceOperation extends
      AbstractCommand<IStoreItem>
  {
    private final IBaseTemporalCollection _timeProvider;
    private final CollectionComplianceTests aTests =
        new CollectionComplianceTests();

    public DistanceOperation(List<IStoreItem> selection, IStore store,
        String title, String description, IBaseTemporalCollection timeProvider,
        IContext context)
    {
      super(title, description, store, false, false, selection, context);
      _timeProvider = timeProvider;
    }

    @Override
    public void execute()
    {
      // get the unit
      List<IStoreItem> outputs = new ArrayList<IStoreItem>();

      // put the names into a string
      String title = getOutputName();

      // ok, generate the new series
      IQuantityCollection<?> target =
          getOutputCollection(title, _timeProvider != null);

      outputs.add(target);

      // store the output
      super.addOutput(target);

      // start adding values.
      performCalc();

      // tell each series that we're a dependent
      Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        ICollection iCollection = (ICollection) iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      List<IStoreItem> res = new ArrayList<IStoreItem>();
      res.add(target);
      getStore().addAll(res);
    }

    protected abstract IQuantityCollection<?> getOutputCollection(
        String trackList, boolean isTemporal);

    @Override
    protected void recalculate()
    {
      // clear out the lists, first
      Iterator<IStoreItem> iter = getOutputs().iterator();
      while (iter.hasNext())
      {
        IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
        qC.getValues().clear();
      }

      // update the results
      performCalc();
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * @param unit
     */
    private void performCalc()
    {
      ICollection track1 = (ICollection) getInputs().get(0);
      ICollection track2 = (ICollection) getInputs().get(1);

      // get a calculator to use
      final GeodeticCalculator calc = GeoSupport.getCalculator();

      if (_timeProvider != null)
      {

        // and the bounding period
        Collection<ICollection> selection = new ArrayList<ICollection>();
        selection.add(track1);
        selection.add(track2);

        TimePeriod period = aTests.getBoundingTime(selection);

        // check it's valid
        if (period.invalid())
        {
          System.err.println("Insufficient coverage for datasets");
          return;
        }

        // ok, let's start by finding our time sync
        IBaseTemporalCollection times =
            aTests.getOptimalTimes(period, selection);

        // check we were able to find some times
        if (times == null)
        {
          System.err.println("Unable to find time source dataset");
          return;
        }

        // and now we can start looping through
        Iterator<Long> tIter = times.getTimes().iterator();
        while (tIter.hasNext())
        {
          long thisTime = (long) tIter.next();

          if ((thisTime >= period.getStartTime())
              && (thisTime <= period.getEndTime()))
          {

            Geometry locA = locationFor(track1, thisTime);
            Geometry locB = locationFor(track2, thisTime);

            if ((locA != null) && (locB != null))
            {
              calcAndStore(calc, (Point) locA, (Point) locB, thisTime);
            }
            else
            {
              System.err.println("Not calculating location at time:" + thisTime
                  + " - insufficient coverage");
            }
          }
        }
      }
      else
      {
        // ok, we're doing an indexed version
        // find one wiht more than one item
        final TemporalLocation primary;
        final TemporalLocation secondary;
        if (track1.size() > 1)
        {
          primary = (TemporalLocation) track1;
          secondary = (TemporalLocation) track2;
        }
        else
        {
          primary = (TemporalLocation) track2;
          secondary = (TemporalLocation) track1;
        }

        for (int j = 0; j < primary.size(); j++)
        {
          final Point locA, locB;

          locA = (Point) primary.getValues().get(j);

          if (secondary.size() > 1)
          {
            locB = (Point) secondary.getValues().get(j);
          }
          else
          {
            locB = (Point) secondary.getValues().get(0);
          }

          calcAndStore(calc, locA, locB, null);
        }
      }

    }

    private Geometry locationFor(ICollection track, final Long thisTime)
    {
      final Geometry locOne;
      if (track instanceof IBaseTemporalCollection)
      {
        TemporalLocation tl = (TemporalLocation) track;
        locOne = tl.interpolateValue(thisTime, InterpMethod.Linear);
      }
      else
      {
        NonTemporal.Location tl = (Location) track;
        locOne = tl.getValues().iterator().next();
      }
      return locOne;
    }

    protected abstract void calcAndStore(final GeodeticCalculator calc,
        final Point locA, final Point locB, Long time);
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean nonEmpty = getATests().nonEmpty(selection);
    boolean equalLength = getATests().allEqualLengthOrSingleton(selection);
    boolean canInterpolate =
        getATests().suitableForTimeInterpolation(selection);
    boolean onlyTwo = getATests().exactNumber(selection, 2);
    boolean hasContents = getATests().allHaveData(selection);

    return (nonEmpty && (equalLength || canInterpolate) && onlyTwo
        && getATests().allLocation(selection) && hasContents);
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

}
