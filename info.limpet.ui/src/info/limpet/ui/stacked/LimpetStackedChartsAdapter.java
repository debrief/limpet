package info.limpet.ui.stacked;

import info.limpet.ICollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;
import info.limpet.ui.data_provider.data.CollectionWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class LimpetStackedChartsAdapter implements IStackedAdapter
{

  @SuppressWarnings({"unchecked"})
  @Override
  public List<Dataset> convert(Object data)
  {
    List<Dataset> res  = null;
    
    // we should have already checked, but just
    // double-check we can handle it
    if(canConvert(data))
    {
      // have a look at the type
      if(data instanceof CollectionWrapper)
      {
        CollectionWrapper cw = (CollectionWrapper) data;
        ICollection collection = cw.getCollection();
        if(collection.isQuantity() && collection.isTemporal())
        {
          // get ready to store the data
          StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
          Dataset dataset  = factory.createDataset();
          dataset.setName(collection.getName());

          TemporalQuantityCollection<Quantity> tqc = (TemporalQuantityCollection<Quantity>) collection;
          final Unit<Quantity> hisUnits = tqc.getUnits();
          Iterator<Long> times = tqc.getTimes().iterator();
          Iterator<?> values = tqc.getValues().iterator();
          while(times.hasNext())
          {
            long thisTime = times.next();
            Measurable<Quantity> meas = (Measurable<Quantity>) values.next();            
            DataItem item = factory.createDataItem();
            item.setIndependentVal(thisTime);
            Double value = meas.doubleValue(hisUnits);
            item.setDependentVal(value);
            
            // and store it
            dataset.getMeasurements().add(item);
          }
          
          // have we got a results object yet?
          if(res == null)
          {
            res = new ArrayList<Dataset>();
          }
              
          res.add(dataset);
        }
        
        // now store the data
        // hook up listener
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
    
    return res;
  }

}
