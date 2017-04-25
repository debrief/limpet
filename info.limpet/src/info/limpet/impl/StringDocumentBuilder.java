package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.StringDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class StringDocumentBuilder implements IDocumentBuilder
{
  final private String _name;
  final private List<String> _values;
  private ArrayList<Long> _indices;
  final private ICommand _predecessor;

  public StringDocumentBuilder(String name, ICommand predecessor)
  {
    _name = name;
    _predecessor = predecessor;
    _values = new ArrayList<String>();
  }

  public void add(String item, long index)
  {
    // sort out the observation
    add(item);

    // and now the index
    if (_indices == null)
    {
      _indices = new ArrayList<Long>();
    }

    _indices.add(index);
  }

  public void add(String item)
  {
    _values.add(item);
  }

  public StringDocument toDocument()
  {
    final StringDocument res;
    Object[] arr = _values.toArray();
    if (arr.length > 0)
    {
      StringDataset dataset =
           DatasetFactory.createFromObject(StringDataset.class,
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

      res = new StringDocument(dataset, _predecessor);
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