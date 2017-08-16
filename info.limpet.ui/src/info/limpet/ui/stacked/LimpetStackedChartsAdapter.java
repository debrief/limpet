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

    public CollectionChangeListener(NumberDocument collection, Dataset subject)
    {
      _dataset = subject;
      _collection = collection;
      _collection.addTransientChangeListener(this);
    }

    @Override
    public void dataChanged(IStoreItem subject)
    {
      StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // ok, repopulate the dataset
      populateDataset(factory, _collection, _dataset);
    }

    @Override
    public void metadataChanged(IStoreItem subject)
    {
      // ignore metadata change
    }

    @Override
    public void collectionDeleted(IStoreItem subject)
    {
      _collection.removeTransientChangeListener(this);
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
    Iterator<Double> times = tqc.getIndexIterator();
    Iterator<Double> values = tqc.getIterator();
    while (times.hasNext() && values.hasNext())
    {
      double thisTime = times.next();
      final double value = values.next();
      final DataItem item = factory.createDataItem();
      item.setIndependentVal(thisTime);
      item.setDependentVal(value);

      // and store it
      dataset.getMeasurements().add(item);
    }
  }

  private Color getRandomColor()
  {
    final Random random = new Random();
    final float h = random.nextFloat();
    final float s = random.nextFloat();
    final float b = 0.8f + ((1f - 0.8f) * random.nextFloat());
    return Color.getHSBColor(h, s, b);
  }

  @Override
  public List<Dataset> convertToDataset(Object data)
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
        DocumentWrapper cw = (DocumentWrapper) data;
        processDocumentWrapper(res, cw, factory, plainS);
      }
      else if (data instanceof GroupWrapper)
      {
        GroupWrapper groupW = (GroupWrapper) data;
        processGroupWrapper(res, groupW);
      }
      else if (data instanceof IDocument)
      {
        IDocument<?> coll = (IDocument<?>) data;
        processDocument(res, coll, factory, plainS);
      }
      else if (data instanceof List)
      {
        List<?> list = (List<?>) data;
        for (Object item : list)
        {
          List<Dataset> items = convertToDataset(item);
          if (items != null)
          {
            res.addAll(items);
          }
        }
      }
    }

    return res;
  }

  private void processDocument(List<Dataset> res, IDocument<?> coll,
      StackedchartsFactoryImpl factory, Styling plainS)
  {
    if (coll.isQuantity() && coll.isIndexed())
    {
      Dataset dataset = factory.createDataset();
      populateDataset(factory, (NumberDocument) coll, dataset);

      // setup the listener
      NumberDocument tempColl = (NumberDocument) coll;
      @SuppressWarnings("unused")
      CollectionChangeListener listener =
          new CollectionChangeListener(tempColl, dataset);

      // give it some style
      dataset.setStyling(plainS);

      // collate the results
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
      final IStoreItem thisI = (IStoreItem) cIter.next();
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

  private void processDocumentWrapper(List<Dataset> res, DocumentWrapper cw,
      StackedchartsFactoryImpl factory, Styling plainS)
  {
    IDocument<?> collection = cw.getDocument();
    if (collection.isQuantity() && collection.isIndexed())
    {
      NumberDocument qq = (NumberDocument) collection;
      final Dataset dataset = factory.createDataset();
      populateDataset(factory, qq, dataset);

      // ok, register a listener for collection changes
      @SuppressWarnings("unused")
      CollectionChangeListener newListener =
          new CollectionChangeListener(qq, dataset);

      // give it some style
      dataset.setStyling(plainS);

      res.add(dataset);
    }
  }

  @Override
  public boolean canConvertToDataset(Object data)
  {
    boolean res = false;

    // have a look at the type
    if (data instanceof DocumentWrapper)
    {
      DocumentWrapper cw = (DocumentWrapper) data;
      IDocument<?> collection = cw.getDocument();
      if (collection.isQuantity() && collection.isIndexed())
      {
        res = true;
      }
      else if (collection.isQuantity() && !collection.isIndexed())
      {
        // check if its' a series of timestampes
        NumberDocument qc = (NumberDocument) collection;
        Unit<?> units = qc.getUnits();
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
      List<?> list = (List<?>) data;
      for (Object item : list)
      {
        boolean thisRes = canConvertToDataset(item);
        if (!thisRes)
        {
          break;
        }
      }
      res = true;

    }

    return res;
  }

  @SuppressWarnings(
  {})
  @Override
  public List<ScatterSet> convertToScatterSet(Object data)
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
        DocumentWrapper cw = (DocumentWrapper) data;
        IDocument<?> collection = cw.getDocument();
        if (collection.isQuantity() && !collection.isIndexed())
        {
          // check if its' a series of timestampes
          NumberDocument qc = (NumberDocument) collection;
          Unit<?> units = qc.getUnits();
          if (units.equals(Duration.UNIT) && qc.size() > 0)
          {
            ScatterSet scatter = factory.createScatterSet();
            scatter.setName(qc.getName());
            final Iterator<Double> times = qc.getIndexIterator();
            while (times.hasNext())
            {
              final double time = times.next();
              Datum datum = factory.createDatum();
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
        List<?> list = (List<?>) data;
        for (Object item : list)
        {
          List<ScatterSet> items = convertToScatterSet(item);
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

  @Override
  public boolean canConvertToScatterSet(Object data)
  {
    boolean res = false;

    // have a look at the type
    if (data instanceof DocumentWrapper)
    {
      DocumentWrapper cw = (DocumentWrapper) data;
      IDocument<?> collection = cw.getDocument();
      if (collection.isQuantity() && !collection.isIndexed())
      {
        // check if its' a series of timestampes
        NumberDocument qc = (NumberDocument) collection;
        Unit<?> units = qc.getUnits();
        if (units.equals(Duration.UNIT))
        {
          res = true;
        }
      }
    }
    else if (data instanceof List)
    {
      List<?> list = (List<?>) data;
      for (Object item : list)
      {
        boolean thisRes = canConvertToScatterSet(item);
        if (!thisRes)
        {
          break;
        }
      }
      res = true;

    }

    return res;
  }
}
