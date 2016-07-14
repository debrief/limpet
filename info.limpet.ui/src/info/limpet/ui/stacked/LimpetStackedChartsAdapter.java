package info.limpet.ui.stacked;

import info.limpet.IBaseQuantityCollection;
import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;
import info.limpet.ui.data_provider.data.CollectionWrapper;
import info.limpet.ui.data_provider.data.GroupWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class LimpetStackedChartsAdapter implements IStackedAdapter
{

  protected static class CollectionChangeListener implements IChangeListener
  {

    private final TemporalQuantityCollection<Quantity> _collection;
    private final Dataset _dataset;

    public CollectionChangeListener(TemporalQuantityCollection<Quantity> collection, Dataset subject)
    {
      _dataset = subject;
      _collection = collection;
      _collection.addChangeListener(this);
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
      _collection.removeChangeListener(this);
    }
    
  }
  
  protected static void populateDataset(final StackedchartsFactoryImpl factory, final TemporalQuantityCollection<Quantity> tqc, final Dataset dataset)
  {
    // get ready to store the data
    dataset.setName(tqc.getName() + "(" + tqc.getUnits() + ")");
    
    // and the units
    dataset.setUnits(tqc.getUnits().toString());
    
    // clear the dataset
    dataset.getMeasurements().clear();

    final Unit<Quantity> hisUnits = tqc.getUnits();
    Iterator<Long> times = tqc.getTimes().iterator();
    Iterator<?> values = tqc.getValues().iterator();
    while(times.hasNext())
    {
      long thisTime = times.next();
      @SuppressWarnings("unchecked")
      final Measurable<Quantity> meas = (Measurable<Quantity>) values.next();            
      final DataItem item = factory.createDataItem();
      item.setIndependentVal(thisTime);
      final Double value = meas.doubleValue(hisUnits);
      item.setDependentVal(value);
      
      // and store it
      dataset.getMeasurements().add(item);
    }    
  }
  
  @SuppressWarnings({"unchecked"})
  @Override
  public List<Object> convert(Object data)
  {
    List<Object> res  = null;
     
    // we should have already checked, but just
    // double-check we can handle it
    if(canConvert(data))
    {
      final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

      // have a look at the type
      if(data instanceof CollectionWrapper)
      {
        CollectionWrapper cw = (CollectionWrapper) data;
        ICollection collection = cw.getCollection();
        if(collection.isQuantity() && collection.isTemporal())
        {
          
          TemporalQuantityCollection<Quantity> qq = (TemporalQuantityCollection<Quantity>) collection;
          final Dataset dataset = factory.createDataset();
          populateDataset(factory, qq, dataset);
          
          // ok, register a listener for collection changes    
          @SuppressWarnings("unused")
          CollectionChangeListener newListener = new CollectionChangeListener(qq, dataset);
          
          // have we got a results object yet?
          if(res == null)
          {
            res = new ArrayList<Object>();
          }

          // give it some style
          dataset.setStyling(factory.createPlainStyling());              

          res.add(dataset);
        }
        else if(collection.isQuantity() && !collection.isTemporal())
        {
          // check if its' a series of timestampes
          QuantityCollection<?> qc = (QuantityCollection<?>) collection;
          Unit<?> units = qc.getUnits();
          if(units.equals(Duration.UNIT))
          {
            // ok, create scatter set
            if(qc.getValuesCount() > 0)
            {
              ScatterSet scatter = factory.createScatterSet();
              final List<?> values = qc.getValues();
              for(Object value: values)
              {
                Measurable<Duration> val = (Measurable<Duration>) value;
                double time = val.doubleValue((Unit<Duration>) qc.getUnits());
                Datum datum = factory.createDatum();
                datum.setVal(time);
                scatter.getDatums().add(datum);
              }
              // do we have a results store?
              if(res == null)
              {
                res = new ArrayList<Object>();
              }
              
              // ok, store it
              res.add(scatter);
            }
          }
        }

        
        // now store the data
        // hook up listener
      } 
      else if(data instanceof GroupWrapper)
      {
        GroupWrapper groupW = (GroupWrapper) data;
        IStoreGroup group = groupW.getGroup();
        Iterator<IStoreItem> cIter = group.iterator();
        while (cIter.hasNext())
        {
          IStoreItem thisI = (IStoreItem) cIter.next();
          if(thisI instanceof ICollection)
          {
            ICollection thisC = (ICollection) thisI;
            if(thisC.isQuantity() && thisC.isTemporal())              
            {
               List<Object> newItems = convert(thisC);
               
               if(newItems != null && newItems.size() > 0)
               {
                 if(res == null)
                 {
                   res = new ArrayList<Object>();
                 }
                 res.addAll(newItems);
               }
            }
          }
          
        }
      }
      else if(data instanceof ICollection)
      {
        ICollection coll = (ICollection) data;
        if(coll.isQuantity() && coll.isTemporal())
        {
          Dataset dataset = factory.createDataset();          
          populateDataset(factory, (TemporalQuantityCollection<Quantity>) coll, dataset);
          
          // setup the listener          
          TemporalQuantityCollection<Quantity> tempColl = (TemporalQuantityCollection<Quantity>) coll;
          @SuppressWarnings("unused")
          CollectionChangeListener listener = new CollectionChangeListener(tempColl , dataset);

          // give it some style
          dataset.setStyling(factory.createPlainStyling());              
          
          // collate the results          
          if(res == null)
          {
            res= new ArrayList<Object>();
          }
          res.add(dataset);
        }
      }
      else if(data instanceof List)
      {
        List<?> list = (List<?>) data;
        for(Object item: list)
        {
          List<Object> items = convert(item);
          if(items != null)
          {
            if(res == null)
            {
              res = new ArrayList<Object>();              
            }
            res.addAll(items);
          }
        }
      }
    }
    
    return res;
  }

  @Override
  public boolean canConvert(Object data)
  {
    boolean res = false;

    // have a look at the type
    if(data instanceof CollectionWrapper)
    {
      CollectionWrapper cw = (CollectionWrapper) data;
      ICollection collection = cw.getCollection();
      if(collection.isQuantity() && collection.isTemporal())
      {
        res = true;
      }
      else if(collection.isQuantity() && !collection.isTemporal())
      {
        // check if its' a series of timestampes
        IBaseQuantityCollection<?> qc = (IBaseQuantityCollection<?>) collection;
        Unit<?> units = qc.getUnits();
        if(units.equals(Duration.UNIT))
        {
          res = true;
        }
      }
    }
    else if(data instanceof GroupWrapper)
    {
      res = true;
    }
    else if(data instanceof ITemporalQuantityCollection<?>)
    {
      res = true;
    }
    else if(data instanceof List)
    {
      List<?> list =  (List<?>) data;
      for(Object item: list)
      {
        boolean thisRes = canConvert(item);
        if(!thisRes)
        {
          break;
        }
      }
      res = true;
      
    }
    
    return res;
  }

}
