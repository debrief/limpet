package info.limpet2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class LocationDocumentBuilder implements IDocumentBuilder
{
  final private String _name;
  final private List<Point2D> _values;
  private ArrayList<Long> _times;
  final private ICommand _predecessor;

  public LocationDocumentBuilder(String name, ICommand predecessor)
  {
    _name = name;
    _predecessor = predecessor;
    _values = new ArrayList<Point2D>();
  }

  public void add(Point2D point, long index)
  {
    // sort out the observation
    add(point);

    // and now the index
    if (_times == null)
    {
      _times = new ArrayList<Long>();
    }

    _times.add(index);
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
      if (_times != null)
      {
        // sort out the time axis
        LongDataset timeData =
            (LongDataset) DatasetFactory.createFromObject(_times);
        final AxesMetadata timeAxis = new AxesMetadataImpl();
        timeAxis.initialize(1);
        timeAxis.setAxis(0, timeData);
        dataset.addMetadata(timeAxis);
      }

      res = new LocationDocument(dataset, _predecessor);
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