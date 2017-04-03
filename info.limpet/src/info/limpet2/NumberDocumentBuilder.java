package info.limpet2;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;


public class NumberDocumentBuilder
{
  final private String _name;
  final private Unit<?> _units;
  final private List<Double> _values;
  final private ICommand _predecessor;
  @SuppressWarnings("unused")
  private Range _range;

  public NumberDocumentBuilder(String name, Unit<?> units, ICommand predecessor)
  {
    _name = name;
    _units = units;
    _predecessor = predecessor;
    _values = new ArrayList<Double>();
  }

  public void add(double value)
  {
    _values.add(value);
  }

  public void setRange(Range range)
  {
    _range = range;
  }

  public Document toDocument()
  {
    DoubleDataset dataset = (DoubleDataset) DatasetFactory.createFromObject(_values);
    dataset.setName(_name);
    NumberDocument res = new NumberDocument(dataset, _predecessor, _units);
    return res;
  }
}