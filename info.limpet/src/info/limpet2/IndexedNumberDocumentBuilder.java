package info.limpet2;

import java.util.ArrayList;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class IndexedNumberDocumentBuilder
{
  private String _name;
  private Unit<?> _units;
  private ArrayList<Long> _times;
  private ArrayList<Double> _values;
  private ICommand _predecessor;

  public IndexedNumberDocumentBuilder(String name, Unit<?> units, ICommand predecessor)
  {
    _name = name;
    _units = units;
    _predecessor = predecessor;

    _times = new ArrayList<Long>();
    _values = new ArrayList<Double>();

  }

  public void add(long time, double value)
  {
    _times.add(time);
    _values.add(value);
  }
  
  public NumberDocument toDocument()
  {
    DoubleDataset dataset = (DoubleDataset) DatasetFactory.createFromObject(_values);
    dataset.setName(_name);
    
    // sort out the time axis
    LongDataset timeData = (LongDataset) DatasetFactory.createFromObject(_times);
    final AxesMetadata timeAxis = new AxesMetadataImpl();
    timeAxis.initialize(1);      
    timeAxis.setAxis(0, timeData);      
    dataset.addMetadata(timeAxis);
    
    NumberDocument res = new NumberDocument(dataset, _predecessor, _units);
    return res;
  }

  public void clear()
  {
    _times.clear();
    _values.clear();
  }
}
