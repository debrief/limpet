package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;

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
  private ArrayList<Double> _indices;
  private ArrayList<Double> _values;
  private ICommand _predecessor;
  private Range _range;
  private Unit<?> _indexUnits;

  public NumberDocumentBuilder(String name, Unit<?> units,
      ICommand predecessor, Unit<?> indexUnits)
  {
    _name = name;
    _units = units;
    _predecessor = predecessor;
    _values = new ArrayList<Double>();
    _indexUnits = indexUnits;
  }

  public int size()
  {
    return _values.size();
  }

  public void add(double index, double value)
  {
    add(value);

    if (_indices == null)
    {
      _indices = new ArrayList<Double>();
    }

    _indices.add(index);
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

      if (_indices != null)
      {
        // sort out the time axis
        DoubleDataset indexData =
            (DoubleDataset) DatasetFactory.createFromObject(_indices);
        final AxesMetadata index = new AxesMetadataImpl();
        index.initialize(1);
        index.setAxis(0, indexData);
        dataset.addMetadata(index);
      }

      res = new NumberDocument(dataset, _predecessor, _units);

      if (_range != null)
      {
        res.setRange(_range);
      }

      if (_indices != null)
      {
        if (_indexUnits == null)
        {
          System.err.println("Setting index, but do not have units");
        }

        // ok, set the index units
        res.setIndexUnits(_indexUnits);
      }
      else
      {
        if (_indexUnits != null)
        {
          throw new RuntimeException("Have index units, but no index");
        }
      }

    }

    return res;
  }

  public void clear()
  {
    _values.clear();
    if (_indices != null)
    {
      _indices.clear();
      _indices = null;
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
