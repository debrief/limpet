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
package info.limpet.operations;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.UIProperty;
import info.limpet.operations.spatial.GeoSupport;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

public abstract class AbstractCommand implements
    ICommand
{

  private final String title;
  private final String description;
  private final boolean canUndo;
  private final boolean canRedo;
  private final IStoreGroup store;

  private final List<IStoreItem> inputs;
  private final List<Document<?>> outputs;

  private IStoreGroup _parent;

  /**
   * whether the command should recalculate if its children change
   * 
   */
  private boolean dynamic = true;
  private transient UUID uuid;
  private final transient IContext context;

  public AbstractCommand(String title, String description, IStoreGroup store,
      boolean canUndo, boolean canRedo, List<IStoreItem> inputs, IContext context)
  {
    this.title = title;
    this.description = description;
    this.store = store;
    this.canUndo = canUndo;
    this.canRedo = canRedo;
    this.context = context;

    this.inputs = new ArrayList<IStoreItem>();
    this.outputs = new ArrayList<Document<?>>();

    // store any inputs, if we have any
    if (inputs != null)
    {
      this.getInputs().addAll(inputs);
    }
  }

  /**
   * provide access to the context object
   * 
   * @return the context object
   */
  protected final IContext getContext()
  {
    return context;
  }

  @Override
  public UUID getUUID()
  {
    if (uuid == null)
    {
      uuid = UUID.randomUUID();
    }
    return uuid;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + getUUID().hashCode();
    return result;
  }

  @Override
  public final boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    AbstractCommand other = (AbstractCommand) obj;
    if (!getUUID().equals(other.getUUID()))
    {
      return false;
    }
    return true;
  }

  /**
   * provide a name for the single output dataset
   * 
   * @return a string to use, or null to cancel the operation
   */
//  protected abstract String getOutputName();

  /**
   * convenience function, to return the datasets as a comma separated list
   * 
   * @return
   */
  protected String getSubjectList()
  {
    StringBuffer res = new StringBuffer();

    Iterator<IStoreItem> iter = (Iterator<IStoreItem>) getInputs().iterator();
    int ctr = 0;
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (ctr++ > 0)
      {
        res.append(", ");
      }
      res.append(storeItem.getName());
    }

    return res.toString();
  }

  protected int getNonSingletonArrayLength(List<IStoreItem> inputs)
  {
    int size = 0;

    Iterator<IStoreItem> iter = inputs.iterator();
    while (iter.hasNext())
    {
      IDocument<?> thisC = (IDocument<?>) iter.next();
      if (thisC.size() >= 1)
      {
        size = thisC.size();
        break;
      }
    }

    return size;
  }

  @UIProperty(name = "Dynamic updates", category = UIProperty.CATEGORY_LABEL)
  @Override
  public boolean getDynamic()
  {
    return dynamic;
  }

  @Override
  public void setDynamic(boolean dynamic)
  {
    this.dynamic = dynamic;
  }

  @Override
  public void metadataChanged(IStoreItem subject)
  {
    // TODO: do a more intelligent/informed processing of metadata changed
    dataChanged(subject);
  }

  @Override
  public IStoreGroup getParent()
  {
    return _parent;
  }

  @Override
  public final void setParent(IStoreGroup parent)
  {
    _parent = parent;
  }

  @Override
  public final void dataChanged(IStoreItem subject)
  {
    // are we doing live updates?
    if (dynamic)
    {
      // do the recalc
      recalculate(subject);
    }
  }

  protected abstract void recalculate(IStoreItem subject);

  @Override
  public void collectionDeleted(IStoreItem subject)
  {
  }

  public final IStoreGroup getStore()
  {
    return store;
  }

  @UIProperty(name = "Description", category = UIProperty.CATEGORY_LABEL)
  @Override
  public final String getDescription()
  {
    return description;
  }

  @Override
  public void execute()
  {
    // ok, register as a listener with the input files
    Iterator<IStoreItem> iter = getInputs().iterator();
    while (iter.hasNext())
    {
      IStoreItem t = iter.next();
      t.addChangeListener(this);
    }
  }

  @Override
  public void undo()
  {
    throw new UnsupportedOperationException(
        "Should not be called, undo not provided");
  }

  @Override
  public void redo()
  {
    throw new UnsupportedOperationException(
        "Should not be called, redo not provided");
  }

  @Override
  public final boolean canUndo()
  {
    return canUndo;
  }

  @Override
  public final boolean canRedo()
  {
    return canRedo;
  }

  @Override
  public final List<IStoreItem> getInputs()
  {
    return inputs;
  }

  @Override
  public final List<Document<?>> getOutputs()
  {
    return outputs;
  }

  public final void addOutput(Document<?> output)
  {
    getOutputs().add(output);
  }

  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  @Override
  public String getName()
  {
    return title;
  }

  @Override
  public final void addChangeListener(IChangeListener listener)
  {
    // TODO we should add change listener support
  }

  @Override
  public final void removeChangeListener(IChangeListener listener)
  {
    // TODO we should add change listener support
  }
  
  

  @Override
  public void addTransientChangeListener(IChangeListener listener)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeTransientChangeListener(
      IChangeListener collectionChangeListener)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fireDataChanged()
  {
    // hmm, we don't really implement this, because apps listen to the 
    // results collections, not the command.
    throw new RuntimeException("Not implemented");
  }
  

  final protected LocationDocument locationsFor(final LocationDocument track1,
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

  final protected LocationDocument locationsFor(final LocationDocument track,
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

  final protected NumberDocument numbersFor(final NumberDocument document,
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
      throw new IllegalArgumentException(e);
    }

    double[] data = ds.getData();
    return numbersFor(document, data);
  }


  final protected NumberDocument numbersFor(final NumberDocument document,
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
}
