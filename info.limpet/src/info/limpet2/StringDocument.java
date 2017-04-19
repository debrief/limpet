package info.limpet2;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.StringDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class StringDocument extends Document
{
  
  public StringDocument(StringDataset dataset, ICommand predecessor)
  {
    super(dataset, predecessor);
  }

  public boolean isQuantity()
  {
    return false;
  }

  @Override
  public String toString()
  {
    StringBuffer res = new StringBuffer();
    
    StringDataset dataset = (StringDataset) this.getDataset();
    final AxesMetadata axesMetadata =
        dataset.getFirstMetadata(AxesMetadata.class);
    final IndexIterator iterator = dataset.getIterator();

    final DoubleDataset axisDataset;
    if (axesMetadata != null && axesMetadata.getAxes().length > 0)
    {
      DoubleDataset doubleAxis = null;
      try
      {
        ILazyDataset rawAxis = axesMetadata.getAxes()[0];
        Dataset axis = DatasetUtils.sliceAndConvertLazyDataset(rawAxis);
        doubleAxis = DatasetUtils.cast(DoubleDataset.class, axis);
      }
      catch (DatasetException e)
      {
        e.printStackTrace();
      }
      axisDataset = doubleAxis != null ? doubleAxis : null;
    }
    else
    {
      axisDataset = null;
    }

    res.append(dataset.getName() + ":\n");
    while (iterator.hasNext())
    {
      final String indexVal;
      if (axisDataset != null)
      {
        indexVal = "" + axisDataset.getString(iterator.index);
      }
      else
      {
        indexVal = "N/A";
      }

      res.append(indexVal + " : "
          + dataset.getString(iterator.index));
      res.append(";");
    }
    res.append("\n");
    
    return res.toString();
  }

  public Double interpolateValue(long i, InterpMethod linear)
  {
    // TODO: implement positional interpolation
    throw new RuntimeException("Not yet implemented");
//    Double res = null;
//    
//    // do we have axes?
//    AxesMetadata index = _dataset.getFirstMetadata(AxesMetadata.class);
//    ILazyDataset indexDataLazy = index.getAxes()[0];
//    try
//    {
//      Dataset indexData = DatasetUtils.sliceAndConvertLazyDataset(indexDataLazy);
//      
//      // check the target index is within the range      
//      double lowerIndex = indexData.getDouble(0);
//      int indexSize = indexData.getSize();
//      double upperVal = indexData.getDouble(indexSize - 1);
//      if(i >= lowerIndex && i <= upperVal)
//      {
//        // ok, in range
//        DoubleDataset ds = (DoubleDataset) _dataset;
//        LongDataset indexes = (LongDataset) DatasetFactory.createFromObject(new Long[]{i});
//        
//        // perform the interpolation
//        Dataset dOut = Maths.interpolate(indexData, ds, indexes, 0, 0);
//        
//        // get the single matching value out
//        res = dOut.getDouble(0);
//      }
//    }
//    catch (DatasetException e)
//    {
//      e.printStackTrace();
//    }
//    
//    return res;
  }

  public String getString(int i)
  {
    StringDataset od = (StringDataset) _dataset;
    return od.getString(i);
  }

}
