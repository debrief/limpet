package info.limpet2;

import info.limpet.UIProperty;

import java.util.Iterator;

import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

public class NumberDocument extends Document
{
  private Unit<?> qType;
  private Range range;

  public NumberDocument(final DoubleDataset dataset, final ICommand predecessor,
      final Unit<?> qType)
  {
    super(dataset, predecessor);
    
    if(qType == null)
    {
     this.qType = Dimensionless.UNIT; 
    }
    else
    {
    this.qType = qType;
    }
  }

  public Unit<?> getType()
  {
    return qType;
  }
  
  public int getSize()
  {
    return size();
  }
//  @UIProperty(name = "Range", category = UIProperty.CATEGORY_METADATA,
//      visibleWhen = "size == 1")

  @UIProperty(name = "Range", category = UIProperty.CATEGORY_METADATA)
  public Range getRange()
  {
    return range;
  }

  public void setRange(Range range)
  {
    this.range = range;
  }

  public Unit<?> getUnits()
  {
    return qType;
  }
  
  private static class DoubleIterator implements Iterator<Double>
  {
    private double[] _data;
    private int _ctr;

    private DoubleIterator(double[] data)
    {
      _data = data;
      _ctr = 0;
    }

    @Override
    public boolean hasNext()
    {
      return _ctr < _data.length;
    }

    @Override
    public Double next()
    {
      return _data[_ctr++];
    }

    @Override
    public void remove()
    {
      throw new RuntimeException("Remove operation not provided for this iterator");
    }
    
  }
  
  public Iterator<Double> getIterator()
  {
    DoubleDataset od = (DoubleDataset) dataset;
    double[] data = od.getData();
    return new DoubleIterator(data);
  }

  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity()
  {
    return true;
  }

  public String toListing()
  {
    StringBuffer res = new StringBuffer();
    
    DoubleDataset dataset = (DoubleDataset) this.getDataset();
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
    Double res = null;
    
    // do we have axes?
    AxesMetadata index = dataset.getFirstMetadata(AxesMetadata.class);
    ILazyDataset indexDataLazy = index.getAxes()[0];
    try
    {
      Dataset indexData = DatasetUtils.sliceAndConvertLazyDataset(indexDataLazy);
      
      // check the target index is within the range      
      double lowerIndex = indexData.getDouble(0);
      int indexSize = indexData.getSize();
      double upperVal = indexData.getDouble(indexSize - 1);
      if(i >= lowerIndex && i <= upperVal)
      {
        // ok, in range
        DoubleDataset ds = (DoubleDataset) dataset;
        LongDataset indexes = (LongDataset) DatasetFactory.createFromObject(new Long[]{i});
        
        // perform the interpolation
        Dataset dOut = Maths.interpolate(indexData, ds, indexes, 0, 0);
        
        // get the single matching value out
        res = dOut.getDouble(0);
      }
    }
    catch (DatasetException e)
    {
      e.printStackTrace();
    }
    
    return res;
  }

  public double getValue(int i)
  {
    return  dataset.getDouble(i);
  }

  public void setUnits(Unit<?> unit)
  {
    qType = unit;
  }


  public MyStats stats()
  {
    return new MyStats();
  }

  public class MyStats
  {
    public double min()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.min(true);
    }

    public double max()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.max();
      
    }

    public double mean()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.mean(true);
    }

    public double variance()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.variance(true);
    }

    public double sd()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.stdDeviation(true);
    }    
  }

  public void replaceSingleton(double val)
  {
    DoubleDataset ds = (DoubleDataset) DatasetFactory.createFromObject(new double[]{val});
    ds.setName(getName());
    setDataset(ds);
    
    // ok share the good news
    fireDataChanged();
  }
}
