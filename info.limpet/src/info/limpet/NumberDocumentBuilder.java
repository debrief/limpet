package info.limpet;

import java.util.ArrayList;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class NumberDocumentBuilder implements IDocumentBuilder
{
  private String _name;
  private Unit<?> _units;
  private ArrayList<Double> _times;
  private ArrayList<Double> _values;
  private ICommand _predecessor;
  private Range _range;

  public NumberDocumentBuilder(String name, Unit<?> units, ICommand predecessor)
  {
    _name = name;
    _units = units;
    _predecessor = predecessor;
    _values = new ArrayList<Double>();
  }

  public int size()
  {
    return _values.size();
  }
  
  public void add(double time, double value)
  {
    add(value);

    if (_times == null)
    {
      _times = new ArrayList<Double>();
    }

    _times.add(time);
  }

  public void add(double value)
  {
    _values.add(value);
  }

  public NumberDocument toDocument()
  {
    final NumberDocument res;
    if (_values.size() == 0)
    {
      res = null;
    }
    else
    {
      DoubleDataset dataset =
          (DoubleDataset) DatasetFactory.createFromObject(_values);
      dataset.setName(_name);

      if (_times != null)
      {
        // sort out the time axis
        DoubleDataset timeData =
            (DoubleDataset) DatasetFactory.createFromObject(_times);
        final AxesMetadata timeAxis = new AxesMetadataImpl();
        timeAxis.initialize(1);
        timeAxis.setAxis(0, timeData);
        dataset.addMetadata(timeAxis);
      }

      res = new NumberDocument(dataset, _predecessor, _units);

      if (_range != null)
      {
        res.setRange(_range);
      }
    }

    return res;
  }

  public void clear()
  {
    _values.clear();
    if (_times != null)
    {
      _times.clear();
      _times = null;
    }
    _range = null;
  }

  public void setRange(Range range)
  {
    _range = range;
  }

  public Unit<?> getUnits()
  {
    return _units;
  }
}
