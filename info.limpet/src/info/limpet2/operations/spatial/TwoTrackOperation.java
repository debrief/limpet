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
package info.limpet2.operations.spatial;

import info.limpet.IContext;
import info.limpet2.Document;
import info.limpet2.ILocations;
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.LocationDocument;
import info.limpet2.LocationDocumentBuilder;
import info.limpet2.NumberDocument;
import info.limpet2.NumberDocumentBuilder;
import info.limpet2.operations.AbstractCommand;
import info.limpet2.operations.CollectionComplianceTests;
import info.limpet2.operations.CollectionComplianceTests.TimePeriod;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

public abstract class TwoTrackOperation implements IOperation
{

  public abstract static class DistanceOperation extends AbstractCommand
  {
    private final Document _timeProvider;
    private final CollectionComplianceTests aTests =
        new CollectionComplianceTests();
    final protected NumberDocumentBuilder _builder;
    final private Unit<?> _outputUnits;

    public DistanceOperation(final List<IStoreItem> selection,
        final IStoreGroup store, final String title, final String description,
        final Document timeProvider, final IContext context, Unit<?> outputUnits)
    {
      super(title, description, store, false, false, selection, context);
      _timeProvider = timeProvider;
      _outputUnits = outputUnits;
      _builder = new NumberDocumentBuilder(title, _outputUnits, null);
    }

    protected abstract void calcAndStore(final IGeoCalculator calc,
        final Point2D locA, final Point2D locB, Long time);

    @Override
    public void execute()
    {
      // start adding values.
      final DoubleDataset dataset = performCalc();

      // name the output
      dataset.setName(getOutputName());

      // now create the output dataset
      final NumberDocument output =
          new NumberDocument(dataset, this, _outputUnits);

      // store the output
      super.addOutput(output);

      // tell each series that we're a dependent
      final Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        final Document iCollection = (Document) iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      getStore().add(output);

      // tell the output it's been updated (by now it should
      // have a full set of listeners
      output.fireDataChanged();
    }

    /** convert the output to a document
     * 
     * @return
     */
    private DoubleDataset getOutputDocument()
    {
      return (DoubleDataset) _builder.toDocument().getDataset();
    }


    /**
     * produce a name for the output document
     * 
     * @return
     */
    abstract protected String getOutputName();

    /** reset the builder
     * 
     */
    private void init()
    {
      _builder.clear();
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     */
    private DoubleDataset performCalc()
    {
      // clear the output data
      init();

      final LocationDocument track1 = (LocationDocument) getInputs().get(0);
      final LocationDocument track2 = (LocationDocument) getInputs().get(1);

      // get a calculator to use
      final IGeoCalculator calc = GeoSupport.getCalculator();

      final LocationDocument interp1;
      final LocationDocument interp2;
      final Document times;

      if (_timeProvider != null)
      {

        // and the bounding period
        final Collection<IStoreItem> selection = new ArrayList<IStoreItem>();
        selection.add(track1);
        selection.add(track2);

        final TimePeriod period = aTests.getBoundingRange(selection);

        // check it's valid
        if (period.invalid())
        {
          throw new IllegalArgumentException(
              "Insufficient coverage for datasets");
        }

        // ok, let's start by finding our time sync
        times = aTests.getOptimalTimes(period, selection);

        // check we were able to find some times
        if (times == null)
        {
          throw new IllegalArgumentException(
              "Unable to find time source dataset");
        }

        // ok, produce the sets of intepolated positions, at the specified times
        interp1 = locationsFor(track1, times);
        interp2 = locationsFor(track2, times);
      }
      else
      {
        interp1 = track1;
        interp2 = track2;
        times = null;
      }

      final Iterator<Point2D> t1Iter = interp1.getLocationIterator();
      final Iterator<Point2D> t2Iter = interp2.getLocationIterator();
      final Iterator<Long> timeIter;
      if (times != null)
      {
        timeIter = times.getIndices();
      }
      else
      {
        timeIter = null;
      }
      while (t1Iter.hasNext())
      {
        final Point2D p1 = t1Iter.next();
        final Point2D p2 = t2Iter.next();
        final Long time;
        if (timeIter != null)
        {
          time = timeIter.next();
        }
        else
        {
          time = null;
        }
        calcAndStore(calc, p1, p2, time);
      }

      return getOutputDocument();
    }

    @Override
    protected void recalculate(final IStoreItem subject)
    {
      // clear out the lists, first
      final DoubleDataset ds = performCalc();
      final Document output = getOutputs().get(0);
      output.setDataset(ds);

      // and fire updates
      output.fireDataChanged();
    }
  }

  public static LocationDocument locationsFor(final LocationDocument track1,
      final Document times)
  {
    // ok, get the time values
    final AxesMetadata axis =
        times.getDataset().getFirstMetadata(AxesMetadata.class);
    final ILazyDataset lazyds = axis.getAxes()[0];
    LongDataset ds = null;
    try
    {
      ds = (LongDataset) DatasetUtils.sliceAndConvertLazyDataset(lazyds);
    }
    catch (final DatasetException e)
    {
      throw new RuntimeException(e);
    }

    // ok, put the lats & longs into arrays
    final ArrayList<Double> latVals = new ArrayList<Double>();
    final ArrayList<Double> longVals = new ArrayList<Double>();
    final ArrayList<Long> timeVals = new ArrayList<Long>();

    final Iterator<Point2D> lIter = track1.getLocationIterator();
    final Iterator<Long> tIter = track1.getIndices();
    while (lIter.hasNext())
    {
      final long thisT = tIter.next();
      final Point2D pt = lIter.next();

      latVals.add(pt.getY());
      longVals.add(pt.getX());
      timeVals.add(thisT);
    }

    final DoubleDataset latDataset =
        DatasetFactory.createFromObject(DoubleDataset.class, latVals);
    final DoubleDataset longDataset =
        DatasetFactory.createFromObject(DoubleDataset.class, longVals);
    final LongDataset timeDataset =
        DatasetFactory.createFromObject(LongDataset.class, timeVals);

    final DoubleDataset latInterpolated =
        (DoubleDataset) Maths.interpolate(timeDataset, latDataset, ds, 0, 0);
    final DoubleDataset longInterpolated =
        (DoubleDataset) Maths.interpolate(timeDataset, longDataset, ds, 0, 0);

    // ok, now we need to re-create a locations document
    final LocationDocumentBuilder ldb =
        new LocationDocumentBuilder("Interpolated locations", null);
    for (int i = 0; i < ds.getSize(); i++)
    {
      final Point2D pt =
          new Point2D.Double(longInterpolated.getDouble(i), latInterpolated
              .getDouble(i));
      ldb.add(pt, ds.getLong(i));
    }

    return ldb.toDocument();
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean nonEmpty = getATests().nonEmpty(selection);
    final boolean equalLength =
        getATests().allEqualLengthOrSingleton(selection);
    final boolean canInterpolate =
        getATests().suitableForIndexedInterpolation(selection);
    final boolean onlyTwo = getATests().exactNumber(selection, 2);
    final boolean hasContents = getATests().allHaveData(selection);
    final boolean equalOrInterp = equalLength || canInterpolate;
    final boolean allLocation = getATests().allLocation(selection);

    return nonEmpty && equalOrInterp && onlyTwo && allLocation && hasContents;
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * utility operation to extract the location datasets from the selection (walking down into groups
   * as necessary)
   * 
   * @param selection
   * @return
   */
  protected List<IStoreItem> getLocationDatasets(
      final List<IStoreItem> selection)
  {
    final List<IStoreItem> collatedTracks = new ArrayList<IStoreItem>();

    // hmm, they may be composite tracks - extract the location data
    final Iterator<IStoreItem> sIter = selection.iterator();
    while (sIter.hasNext())
    {
      final IStoreItem iStoreItem = sIter.next();
      if (iStoreItem instanceof IStoreGroup)
      {
        final IStoreGroup group = (IStoreGroup) iStoreItem;
        final Iterator<IStoreItem> kids = group.iterator();
        while (kids.hasNext())
        {
          final IStoreItem thisItem = kids.next();
          if (thisItem instanceof ILocations)
          {
            final IStoreItem thisI = thisItem;
            collatedTracks.add(thisI);
          }
        }
      }
      else if (iStoreItem instanceof ILocations)
      {
        collatedTracks.add(iStoreItem);
      }
    }
    return collatedTracks;
  }

}
