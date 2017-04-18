package info.limpet2;

import java.awt.geom.Point2D;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class LocationDocument extends Document
{
  
  public LocationDocument(ObjectDataset dataset, ICommand predecessor)
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
    
    ObjectDataset dataset = (ObjectDataset) this.getDataset();
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
        indexVal = "" + axisDataset.getElementDoubleAbs(iterator.index);
      }
      else
      {
        indexVal = "N/A";
      }

      res.append(indexVal + " : "
          + dataset.getElementDoubleAbs(iterator.index));
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

  public Point2D getLocation(int i)
  {
    ObjectDataset od = (ObjectDataset) _dataset;
    return (Point2D) od.getObject(i);
  }

  public MyStats stats()
  {
    return new MyStats();
  }

  public class MyStats
  {
    public double min()
    {
      DoubleDataset ds = (DoubleDataset) _dataset;
      return (Double) ds.min(true);
    }

    public double max()
    {
      DoubleDataset ds = (DoubleDataset) _dataset;
      return (Double) ds.max();
      
    }

    public double mean()
    {
      DoubleDataset ds = (DoubleDataset) _dataset;
      return (Double) ds.mean(true);
    }

    public double variance()
    {
      DoubleDataset ds = (DoubleDataset) _dataset;
      return (Double) ds.variance(true);
    }

    public double sd()
    {
      DoubleDataset ds = (DoubleDataset) _dataset;
      return (Double) ds.stdDeviation(true);
    }    
  }
}
