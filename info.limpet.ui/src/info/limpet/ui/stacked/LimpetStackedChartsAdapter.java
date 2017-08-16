package info.limpet.ui.stacked;

import info.limpet.IChangeListener;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;
import info.limpet.ui.data_provider.data.DocumentWrapper;
import info.limpet.ui.data_provider.data.GroupWrapper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

public class LimpetStackedChartsAdapter implements IStackedDatasetAdapter,
    IStackedScatterSetAdapter
{

  protected static class CollectionChangeListener implements IChangeListener
  {

    private final NumberDocument _collection;
    private final Dataset _dataset;

    public CollectionChangeListener(final NumberDocument collection,
        final Dataset subject)
    {
      _dataset = subject;
      _collection = collection;
      _collection.addTransientChangeListener(this);
    }

    @Override
    public void collectionDeleted(final IStoreItem subject)
    {
      _collection.removeTransientChangeListener(this);
    }

    @Override
    public void dataChanged(final IStoreItem subject)
    {
      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // ok, repopulate the dataset
      populateDataset(factory, _collection, _dataset);
    }

    @Override
    public void metadataChanged(final IStoreItem subject)
    {
      // ignore metadata change
    }

  }

  protected static void populateDataset(final StackedchartsFactoryImpl factory,
      final NumberDocument tqc, final Dataset dataset)
  {
    // get ready to store the data
    dataset.setName(tqc.getName() + "(" + tqc.getUnits() + ")");

    // and the units
    dataset.setUnits(tqc.getUnits().toString());

    // clear the dataset
    dataset.getMeasurements().clear();
    final Iterator<Double> times = tqc.getIndexIterator();
    final Iterator<Double> values = tqc.getIterator();
    while (times.hasNext() && values.hasNext())
    {
      final double thisTime = times.next();
      final double value = values.next();
      final DataItem item = factory.createDataItem();
      item.setIndependentVal(thisTime);
      item.setDependentVal(value);

      // and store it
      dataset.getMeasurements().add(item);
    }
  }

  @Override
  public boolean canConvertToDataset(final Object data)
  {
    boolean res = false;

    // have a look at the type
    if (data instanceof DocumentWrapper)
    {
      final DocumentWrapper cw = (DocumentWrapper) data;
      final IDocument<?> collection = cw.getDocument();
      if (collection.isQuantity() && collection.isIndexed())
      {
        res = true;
      }
      else if (collection.isQuantity() && !collection.isIndexed())
      {
        // check if its' a series of timestampes
        final NumberDocument qc = (NumberDocument) collection;
        final Unit<?> units = qc.getUnits();
        if (units.equals(Duration.UNIT))
        {
          res = true;
        }
      }
    }
    else if (data instanceof GroupWrapper)
    {
      res = true;
    }
    else if (data instanceof NumberDocument)
    {
      res = true;
    }
    else if (data instanceof List)
    {
      final List<?> list = (List<?>) data;
      for (final Object item : list)
      {
        final boolean thisRes = canConvertToDataset(item);
        if (!thisRes)
        {
          break;
        }
      }
      res = true;

    }

    return res;
  }

  @Override
  public boolean canConvertToScatterSet(final Object data)
  {
    boolean res = false;

    // have a look at the type
    if (data instanceof DocumentWrapper)
    {
      final DocumentWrapper cw = (DocumentWrapper) data;
      final IDocument<?> collection = cw.getDocument();
      if (collection.isQuantity() && !collection.isIndexed())
      {
        // check if its' a series of timestampes
        final NumberDocument qc = (NumberDocument) collection;
        final Unit<?> units = qc.getUnits();
        if (units.equals(Duration.UNIT))
        {
          res = true;
        }
      }
    }
    else if (data instanceof List)
    {
      final List<?> list = (List<?>) data;
      for (final Object item : list)
      {
        final boolean thisRes = canConvertToScatterSet(item);
        if (!thisRes)
        {
          break;
        }
      }
      res = true;

    }

    return res;
  }

  @Override
  public List<Dataset> convertToDataset(final Object data)
  {
    final List<Dataset> res = new ArrayList<Dataset>();

    // we should have already checked, but just
    // double-check we can handle it
    if (canConvertToDataset(data))
    {
      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // have a look at the type
      final PlainStyling plainS = factory.createPlainStyling();

      // give it a color
      plainS.setColor(getRandomColor());

      if (data instanceof DocumentWrapper)
      {
        final DocumentWrapper cw = (DocumentWrapper) data;
        processDocumentWrapper(res, cw, factory, plainS);
      }
      else if (data instanceof GroupWrapper)
      {
        final GroupWrapper groupW = (GroupWrapper) data;
        processGroupWrapper(res, groupW);
      }
      else if (data instanceof IDocument)
      {
        final IDocument<?> coll = (IDocument<?>) data;
        processDocument(res, coll, factory, plainS);
      }
      else if (data instanceof List)
      {
        final List<?> list = (List<?>) data;
        for (final Object item : list)
        {
          final List<Dataset> items = convertToDataset(item);
          if (items != null)
          {
            res.addAll(items);
          }
        }
      }
    }

    return res;
  }

  @SuppressWarnings(
  {})
  @Override
  public List<ScatterSet> convertToScatterSet(final Object data)
  {
    List<ScatterSet> res = null;

    // we should have already checked, but just
    // double-check we can handle it
    if (canConvertToScatterSet(data))
    {
      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // have a look at the type
      if (data instanceof DocumentWrapper)
      {
        final DocumentWrapper cw = (DocumentWrapper) data;
        final IDocument<?> collection = cw.getDocument();
        if (collection.isQuantity() && !collection.isIndexed())
        {
          // check if its' a series of timestampes
          final NumberDocument qc = (NumberDocument) collection;
          final Unit<?> units = qc.getUnits();
          if (units.equals(Duration.UNIT) && qc.size() > 0)
          {
            final ScatterSet scatter = factory.createScatterSet();
            scatter.setName(qc.getName());
            final Iterator<Double> times = qc.getIndexIterator();
            while (times.hasNext())
            {
              final double time = times.next();
              final Datum datum = factory.createDatum();
              datum.setVal(time);
              scatter.getDatums().add(datum);
            }
            // do we have a results store?
            if (res == null)
            {
              res = new ArrayList<ScatterSet>();
            }

            // ok, store it
            res.add(scatter);
          }
        }

        // now store the data
        // hook up listener
      }
      else if (data instanceof List)
      {
        final List<?> list = (List<?>) data;
        for (final Object item : list)
        {
          final List<ScatterSet> items = convertToScatterSet(item);
          if (items != null)
          {
            if (res == null)
            {
              res = new ArrayList<ScatterSet>();
            }
            res.addAll(items);
          }
        }
      }
    }

    return res;
  }

  private Color getRandomColor()
  {
    final Random random = new Random();
    final float h = random.nextFloat();
    final float s = random.nextFloat();
    final float b = 0.8f + ((1f - 0.8f) * random.nextFloat());
    return Color.getHSBColor(h, s, b);
  }

  private void processDocument(final List<Dataset> res,
      final IDocument<?> coll, final StackedchartsFactoryImpl factory,
      final Styling plainS)
  {
    if (coll.isQuantity() && coll.isIndexed())
    {
      final Dataset dataset = factory.createDataset();
      populateDataset(factory, (NumberDocument) coll, dataset);

      // setup the listener
      final NumberDocument tempColl = (NumberDocument) coll;
      @SuppressWarnings("unused")
      final CollectionChangeListener listener =
          new CollectionChangeListener(tempColl, dataset);

      // give it some style
      dataset.setStyling(plainS);

      // collate the results
      res.add(dataset);
    }
  }

  private void processDocumentWrapper(final List<Dataset> res,
      final DocumentWrapper cw, final StackedchartsFactoryImpl factory,
      final Styling plainS)
  {
    final IDocument<?> collection = cw.getDocument();
    if (collection.isQuantity() && collection.isIndexed())
    {
      final NumberDocument qq = (NumberDocument) collection;
      final Dataset dataset = factory.createDataset();
      populateDataset(factory, qq, dataset);

      // ok, register a listener for collection changes
      @SuppressWarnings("unused")
      final CollectionChangeListener newListener =
          new CollectionChangeListener(qq, dataset);

      // give it some style
      dataset.setStyling(plainS);

      res.add(dataset);
    }
  }

  private void processGroupWrapper(final List<Dataset> res,
      final GroupWrapper groupW)
  {
    final IStoreGroup group = groupW.getGroup();
    final Iterator<IStoreItem> cIter = group.iterator();
    while (cIter.hasNext())
    {
      final IStoreItem thisI = cIter.next();
      if (thisI instanceof IDocument)
      {
        final IDocument<?> thisC = (IDocument<?>) thisI;
        if (thisC.isQuantity() && thisC.isIndexed())
        {
          final List<Dataset> newItems = convertToDataset(thisC);

          if (newItems != null && newItems.size() > 0)
          {
            res.addAll(newItems);
          }
        }
      }
    }
  }
}
