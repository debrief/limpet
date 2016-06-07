package info.limpet.ui.stacked;

import info.limpet.ICollection;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;
import info.limpet.ui.data_provider.data.CollectionWrapper;
import info.limpet.ui.data_provider.data.GroupWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class LimpetStackedChartsAdapter implements IStackedAdapter
{

  protected Dataset convertDataset(StackedchartsFactoryImpl factory, TemporalQuantityCollection<Quantity> tqc)
  {

    // get ready to store the data
    Dataset dataset  = factory.createDataset();
    dataset.setName(tqc.getName() + "(" + tqc.getUnits() + ")");

    final Unit<Quantity> hisUnits = tqc.getUnits();
    Iterator<Long> times = tqc.getTimes().iterator();
    Iterator<?> values = tqc.getValues().iterator();
    while(times.hasNext())
    {
      long thisTime = times.next();
      @SuppressWarnings("unchecked")
      Measurable<Quantity> meas = (Measurable<Quantity>) values.next();            
      DataItem item = factory.createDataItem();
      item.setIndependentVal(thisTime);
      Double value = meas.doubleValue(hisUnits);
      item.setDependentVal(value);
      
      // and store it
      dataset.getMeasurements().add(item);
    }
    
    return dataset;
  }
  
  @SuppressWarnings({"unchecked"})
  @Override
  public List<Dataset> convert(Object data)
  {
    List<Dataset> res  = null;
    
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
          Dataset dataset = convertDataset(factory, qq);
          // have we got a results object yet?
          if(res == null)
          {
            res = new ArrayList<Dataset>();
          }
          
          // give it some style
          dataset.setStyling(factory.createPlainStyling());
              
          res.add(dataset);
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
               List<Dataset> newItems = convert(thisC);
               
               if(newItems != null && newItems.size() > 0)
               {
                 if(res == null)
                 {
                   res = new ArrayList<Dataset>();
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
          Dataset newD = convertDataset(factory, (TemporalQuantityCollection<Quantity>) coll);
          if(newD != null)
          {
            if(res == null)
            {
              res= new ArrayList<Dataset>();
            }
            res.add(newD);
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
    }
    else if(data instanceof GroupWrapper)
    {
      res = true;
    }
    else if(data instanceof ITemporalQuantityCollection<?>)
    {
      res = true;
    }
    
    return res;
  }

}
