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
package info.limpet.operations.spatial.msa;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.CollectionComplianceTests.TimePeriod;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

public class BistaticAngleOperation implements IOperation
{

  public abstract static class BistaticAngleCommand extends AbstractCommand
  {
    private final IDocument<?> _timeProvider;
    private final CollectionComplianceTests aTests =
        new CollectionComplianceTests();
    final protected NumberDocumentBuilder _builder;
    final private Unit<?> _outputUnits;

    public BistaticAngleCommand(final List<IStoreItem> selection,
        final IStoreGroup store, final IDocument<?> timeProvider,
        final IContext context, Unit<?> outputUnits)
    {
      super("Bistatic angle at:" + selection.get(1).getName(),
          "Calculate bistatic angle from " + selection.get(0).getName()
              + " to:" + selection.get(1).getName(), store, false, false,
          selection, context);
      _timeProvider = timeProvider;
      _outputUnits = outputUnits;
      final Unit<?> indexUnits =
          _timeProvider == null ? null : SampleData.MILLIS;
      _builder =
          new NumberDocumentBuilder("Bistatic angle at:"
              + selection.get(1).getName(), _outputUnits, null, indexUnits);
    }

    protected void calcAndStore(final IGeoCalculator calc, final Point2D tx,
        final Point2D target, Double heading, Double time)
    {
      // now find the range between them
      final double thisDist = calc.getAngleBetween(tx, target);

      if (time != null)
      {
        _builder.add(time, thisDist);
      }
      else
      {
        _builder.add(thisDist);
      }
    }

    @Override
    public void execute()
    {
      // get the output
      final DoubleDataset dataset = performCalc();

      // name the output
      dataset.setName(getOutputName());

      // now create the output dataset
      final NumberDocument output =
          new NumberDocument(dataset, this, _outputUnits);
      if (output.isIndexed())
      {
        output.setIndexUnits(_builder.getIndexUnits());
      }

      // store the output
      super.addOutput(output);

      // tell each series that we're a dependent
      final Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        final IStoreGroup group = (IStoreGroup) iter.next();
        Iterator<IStoreItem> iter2 = group.iterator();
        while (iter2.hasNext())
        {
          final IDocument<?> iCollection = (IDocument<?>) iter2.next();
          iCollection.addDependent(this);
        }
      }

      // ok, done
      getStore().add(output);

      // tell the output it's been updated (by now it should
      // have a full set of listeners
      output.fireDataChanged();
    }

    /**
     * convert the output to a document
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
    protected String getOutputName()
    {
      return "Bistatic angle at " + getInputs().get(1).getName();
    }

    /**
     * reset the builder
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

      // get the tracks
      final IStoreGroup tx = (IStoreGroup) getInputs().get(0);
      final IStoreGroup target = (IStoreGroup) getInputs().get(1);

      // get the location datasets
      final LocationDocument tx_track = aTests.getFirstLocation(tx);
      final LocationDocument tgt_track = aTests.getFirstLocation(target);
      final List<IStoreItem> tgtDocs = new ArrayList<IStoreItem>();
      tgtDocs.addAll(target);
      final NumberDocument tgt_hdg =
          aTests.findCollectionWith(tgtDocs, SampleData.DEGREE_ANGLE.asType(
              Angle.class).getDimension(), true);

      // get a calculator to use
      final IGeoCalculator calc = GeoSupport.getCalculator();

      final IDocument<?> times;

      if (_timeProvider == null)
      {
        return null;
      }

      // and the bounding period
      final Collection<IStoreItem> selection = new ArrayList<IStoreItem>();
      selection.add(tx_track);
      selection.add(tgt_track);
      selection.add(tgt_hdg);

      final TimePeriod period = aTests.getBoundingRange(selection);

      // check it's valid
      if (period.invalid())
      {
        throw new IllegalArgumentException("Insufficient coverage for datasets");
      }

      // ok, let's start by finding our time sync
      times = aTests.getOptimalIndex(period, selection);

      // check we were able to find some times
      if (times == null)
      {
        throw new IllegalArgumentException("Unable to find time source dataset");
      }

      // ok, produce the sets of intepolated positions, at the specified times
      final LocationDocument interp_tx =
          locationsFor(tx_track, (Document<?>) times);
      final LocationDocument interp_tgt =
          locationsFor(tgt_track, (Document<?>) times);
      final NumberDocument interp_headings =
          headingsFor(tgt_hdg, (Document<?>) times);

      final Iterator<Point2D> t1Iter = interp_tx.getLocationIterator();
      final Iterator<Point2D> t2Iter = interp_tgt.getLocationIterator();
      final Iterator<Double> hdgIter = interp_headings.getIterator();
      final Iterator<Double> timeIter = times.getIndex();

      while (t1Iter.hasNext())
      {
        final Point2D p1 = t1Iter.next();
        final Point2D p2 = t2Iter.next();
        final double heading = hdgIter.next();
        final Double time = timeIter.next();
        calcAndStore(calc, p1, p2, heading, time);
      }

      return getOutputDocument();
    }

    @Override
    protected void recalculate(final IStoreItem subject)
    {
      // clear out the lists, first
      final DoubleDataset ds = performCalc();
      final Document<?> output = getOutputs().get(0);
      output.setDataset(ds);

      // and fire updates
      output.fireDataChanged();
    }
  }

  public static NumberDocument headingsFor(final NumberDocument document,
      final Document<?> times)
  {
    // ok, get the time values
    final AxesMetadata axis =
        times.getDataset().getFirstMetadata(AxesMetadata.class);
    final ILazyDataset lazyds = axis.getAxes()[0];
    DoubleDataset ds = null;
    try
    {
      ds = (DoubleDataset) DatasetUtils.sliceAndConvertLazyDataset(lazyds);
    }
    catch (final DatasetException e)
    {
      throw new RuntimeException(e);
    }

    double[] data = ds.getData();
    return headingsFor(document, data);
  }

  public static LocationDocument locationsFor(final LocationDocument track1,
      final Document<?> times)
  {
    // ok, get the time values
    final AxesMetadata axis =
        times.getDataset().getFirstMetadata(AxesMetadata.class);
    final ILazyDataset lazyds = axis.getAxes()[0];
    DoubleDataset ds = null;
    try
    {
      ds = (DoubleDataset) DatasetUtils.sliceAndConvertLazyDataset(lazyds);
    }
    catch (final DatasetException e)
    {
      throw new RuntimeException(e);
    }

    double[] data = ds.getData();
    return locationsFor(track1, data);
  }

  public static LocationDocument locationsFor(final LocationDocument track,
      final double[] times)
  {
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(times);

    final Unit<?> indexUnits = times == null ? null : SampleData.MILLIS;
    final LocationDocumentBuilder ldb;

    // ok, put the lats & longs into arrays
    final ArrayList<Double> latVals = new ArrayList<Double>();
    final ArrayList<Double> longVals = new ArrayList<Double>();
    final ArrayList<Double> timeVals = new ArrayList<Double>();

    // special processing. If the document is a singleton, then
    // we just keep re-using the same position
    if (track.size() == 1)
    {
      ldb =
          new LocationDocumentBuilder("Interpolated locations", null,
              indexUnits);
      Point2D pt = track.getLocationIterator().next();
      for (double t : times)
      {
        ldb.add(t, pt);
      }
    }
    else
    {

      final Iterator<Point2D> lIter = track.getLocationIterator();
      final Iterator<Double> tIter = track.getIndex();
      while (lIter.hasNext())
      {
        final double thisT = tIter.next();
        final Point2D pt = lIter.next();

        latVals.add(pt.getY());
        longVals.add(pt.getX());
        timeVals.add(thisT);
      }

      final DoubleDataset latDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, latVals);
      final DoubleDataset DoubleDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, longVals);
      final DoubleDataset timeDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, timeVals);

      final DoubleDataset latInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, latDataset, ds, 0, 0);
      final DoubleDataset longInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, DoubleDataset, ds, 0,
              0);

      // ok, now we need to re-create a locations document
      ldb =
          new LocationDocumentBuilder("Interpolated locations", null,
              indexUnits);
      for (int i = 0; i < ds.getSize(); i++)
      {
        final Point2D pt =
            GeoSupport.getCalculator().createPoint(
                longInterpolated.getDouble(i), latInterpolated.getDouble(i));
        ldb.add(ds.getLong(i), pt);
      }
    }

    return ldb.toDocument();
  }

  public static NumberDocument headingsFor(final NumberDocument document,
      final double[] times)
  {
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(times);

    final Unit<?> indexUnits = times == null ? null : SampleData.MILLIS;
    final NumberDocumentBuilder ldb;

    // ok, put the lats & longs into arrays
    final ArrayList<Double> headings = new ArrayList<Double>();
    final ArrayList<Double> timeVals = new ArrayList<Double>();

    // special processing. If the document is a singleton, then
    // we just keep re-using the same position
    if (headings.size() == 1)
    {
      ldb =
          new NumberDocumentBuilder("Interpolated headings", document
              .getUnits(), null, indexUnits);
      double pt = document.getIterator().next();
      for (double t : times)
      {
        ldb.add(t, pt);
      }
    }
    else
    {

      final Iterator<Double> lIter = document.getIterator();
      final Iterator<Double> tIter = document.getIndex();
      while (lIter.hasNext())
      {
        final double thisT = tIter.next();
        final double pt = lIter.next();

        headings.add(pt);
        timeVals.add(thisT);
      }

      final DoubleDataset hdgDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, headings);
      final DoubleDataset timeDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, timeVals);

      final DoubleDataset hdgInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, hdgDataset, ds, 0, 0);
      final DoubleDataset timeInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, timeDataset, ds, 0, 0);

      // ok, now we need to re-create a locations document
      ldb =
          new NumberDocumentBuilder("Interpolated locations", document
              .getUnits(), null, indexUnits);
      for (int i = 0; i < ds.getSize(); i++)
      {
        ldb.add(timeInterpolated.getDouble(i), hdgInterpolated.getDouble(i));
      }
    }

    return ldb.toDocument();
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  protected List<IStoreItem> getAsList(Collection<IStoreGroup> selection)
  {
    // check the datasets overlap.
    final Iterator<IStoreGroup> iter = selection.iterator();
    final List<IStoreItem> datasets = new ArrayList<IStoreItem>();
    while (iter.hasNext())
    {
      final IStoreGroup track = iter.next();
      final Iterator<IStoreItem> iter2 = track.iterator();
      while (iter2.hasNext())
      {
        final IStoreItem iStoreItem = (IStoreItem) iter2.next();
        datasets.add(iStoreItem);
      }
    }

    return datasets;
  }

  protected boolean appliesTo(final List<IStoreItem> datasets)
  {
    // ok, now check they overlap
    final boolean nonEmpty = getATests().nonEmpty(datasets);
    final boolean equalLength = getATests().allEqualLengthOrSingleton(datasets);
    final boolean canInterpolate =
        getATests().suitableForIndexedInterpolation(datasets);
    final boolean atLeast4 = datasets.size() >= 4;
    final boolean hasContents = getATests().allHaveData(datasets);
    final boolean equalOrInterp = equalLength || canInterpolate;

    return nonEmpty && equalOrInterp && atLeast4 && hasContents;
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * utility operation to extract the tracks from the selection (walking down into groups as
   * necessary)
   * 
   * @param selection
   * @return
   */
  protected List<IStoreGroup>
      getSuitableTracks(final List<IStoreItem> selection)
  {
    final List<IStoreGroup> collatedTracks = new ArrayList<IStoreGroup>();

    // hmm, they may be composite tracks - extract the location data
    final Iterator<IStoreItem> sIter = selection.iterator();
    while (sIter.hasNext())
    {
      final IStoreItem iStoreItem = sIter.next();
      if (iStoreItem instanceof IStoreGroup)
      {
        final IStoreGroup group = (IStoreGroup) iStoreItem;
        if (aTests.isATrack(group, false, true))
        {
          collatedTracks.add(group);
        }
      }
    }
    return collatedTracks;
  }

  public List<ICommand> actionsFor(List<IStoreItem> rawSelection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();

    // get some tracks
    List<IStoreGroup> collatedTracks = getSuitableTracks(rawSelection);
    List<IStoreItem> datasets = getAsList(collatedTracks);

    if (appliesTo(datasets))
    {
      // hmm, find the time provider
      final IDocument<?> timeProvider =
          getATests().getLongestIndexedCollection(datasets);

      final IStoreGroup item1 = collatedTracks.get(0);
      final IStoreGroup item2 = collatedTracks.get(1);

      final ArrayList<IStoreItem> perm1 = new ArrayList<IStoreItem>();
      perm1.add(item1);
      perm1.add(item2);

      final ArrayList<IStoreItem> perm2 = new ArrayList<IStoreItem>();
      perm2.add(item2);
      perm2.add(item1);

      ICommand comm1 =
          new BistaticAngleCommand(perm1, destination, timeProvider, context,
              SampleData.DEGREE_ANGLE.asType(Angle.class))
          {

            @Override
            protected String getOutputName()
            {
              return getContext().getInput("Generate bearing",
                  NEW_DATASET_MESSAGE,
                  "Bearing between " + super.getSubjectList());
            }

          };

      ICommand comm2 =
          new BistaticAngleCommand(perm2, destination, timeProvider, context,
              SampleData.DEGREE_ANGLE.asType(Angle.class))
          {

            @Override
            protected String getOutputName()
            {
              return getContext().getInput("Generate bearing",
                  NEW_DATASET_MESSAGE,
                  "Bearing between " + super.getSubjectList());
            }
          };

      res.add(comm1);
      res.add(comm2);

    }

    return res;
  }
}
