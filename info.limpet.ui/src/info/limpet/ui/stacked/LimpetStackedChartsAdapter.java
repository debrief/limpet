package info.limpet.ui.stacked;

import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedAdapter;
import info.limpet.ui.data_provider.data.CollectionWrapper;

public class LimpetStackedChartsAdapter implements IStackedAdapter
{

  @Override
  public Dataset convert(Object data)
  {
    Dataset res = null;
    
    // we should have already checked, but just
    // double-check we can handle it
    if(canConvert(data))
    {
      // have a look at the type
      if(data instanceof TemporalQuantityCollection<?>)
      {
        // convert to Dataset
        @SuppressWarnings("unused")
        TemporalQuantityCollection<?> coll = (TemporalQuantityCollection<?>) data; 
        StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
        res  = factory.createDataset();
        
        // now store the data
        
        // hook up listener
      }
      //DUMMY PLEASE REMOVE
      if(data instanceof CollectionWrapper)
      {
        StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();
        res  = factory.createDataset();
        res.setName("DnD-DUMMY");
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
    if(data instanceof TemporalQuantityCollection<?>)
    {
      res = true;
    }
    if(data instanceof CollectionWrapper)
    {
      res = true;
    }
    
    return res;
  }

}
