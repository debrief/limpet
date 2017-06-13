package info.limpet.impl;

import info.limpet.ICommand;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.StringDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class StringDocument extends Document<String>
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
    return getName();
  }
  
  @Override
  public String toListing()
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
    throw new IllegalArgumentException("Not valid for collections of Strings");
  }

  public String getString(int i)
  {
    StringDataset od = (StringDataset) dataset;
    return od.getString(i);
  }

  @Override
  public Iterator<String> getIterator()
  {
    StringDataset od = (StringDataset) dataset;
    String[] strings = od.getData();
    Iterable<String> iterable = Arrays.asList(strings);
    return iterable.iterator();
  }

}
