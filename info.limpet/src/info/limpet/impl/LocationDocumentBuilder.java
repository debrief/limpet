package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class LocationDocumentBuilder implements IDocumentBuilder<Point2D>
{
  final private String _name;
  final private List<Point2D> _values;
  private ArrayList<Double> _indices;
  final private ICommand _predecessor;
  private Unit<?> _indexUnits;

  public LocationDocumentBuilder(String name, ICommand predecessor, Unit<?> indexUnits)
  {
    _name = name;
    _predecessor = predecessor;
    _values = new ArrayList<Point2D>();
    _indexUnits = indexUnits;
  }

  public void add(double index, Point2D point)
  {
    // sort out the observation
    add(point);

    // and now the index
    if (_indices == null)
    {
      _indices = new ArrayList<Double>();
    }

    _indices.add(index);
  }

  public void add(Point2D point)
  {
    _values.add(point);
  }

  public LocationDocument toDocument()
  {
    final LocationDocument res;
    Object[] arr = _values.toArray();
    if (arr.length > 0)
    {
      ObjectDataset dataset =
          (ObjectDataset) DatasetFactory.createFromObject(ObjectDataset.class,
              arr, null);
      dataset.setName(_name);

      // do we have any indices to add?
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

      res = new LocationDocument(dataset, _predecessor);
      
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
    else
    {
      res = null;
    }
    return res;
  }

  public void clear()
  {
    _values.clear();
  }
}