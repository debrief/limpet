package info.limpet2;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;

public class NumberDocument extends Document
{
  private Unit<?> _qType;
  private Range _range;

  public NumberDocument(DoubleDataset dataset, ICommand predecessor, Unit<?> qType)
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

}
