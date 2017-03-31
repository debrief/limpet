package info.limpet2;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.metadata.AxesMetadata;

public class NumberDocument extends Document
{
  private Unit<?> _qType;
  private Range _range;

  public NumberDocument(DoubleDataset dataset, ICommand predecessor,
      Unit<?> qType)
  {
    super(dataset, predecessor);
    _qType = qType;
  }

  public Unit<?> getType()
  {
    return _qType;
  }

  public Range getRange()
  {
    return _range;
  }

  public void setRange(Range range)
  {
    _range = range;
  }

  public Unit<?> getUnits()
  {
    return _qType;
  }

  public boolean isQuantity()
  {
    return true;
  }

  @Override
  public String toString()
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

}
