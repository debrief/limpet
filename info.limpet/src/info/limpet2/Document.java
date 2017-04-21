package info.limpet2;

import info.limpet.UIProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;

abstract public class Document implements IStoreItem
{

  // TODO: long-term, find a better place for this
  public static enum InterpMethod
  {
    Linear, Nearest, Before, After
  };

  /**
   * _dataset isn't final, sincew we replace it when the document is re-calculated
   */
  protected IDataset dataset;
  final protected ICommand predecessor;
  private List<IChangeListener> changeListeners =
      new ArrayList<IChangeListener>();
  private IStoreGroup parent;
  final private UUID uuid;
  private List<ICommand> dependents = new ArrayList<ICommand>();

  public Document(IDataset dataset, ICommand predecessor)
  {
    this.dataset = dataset;
    this.predecessor = predecessor;
    uuid = UUID.randomUUID();
  }

  public IDataset getDataset()
  {
    return dataset;
  }

  public void setDataset(IDataset dataset)
  {
    this.dataset = dataset;
  }

  /**
   * tell listeners that it's about to be deleted
   * 
   */
  public void beingDeleted()
  {
    if (changeListeners != null)
    {
      // tell any standard listeners
      for (IChangeListener thisL : changeListeners)
      {
        thisL.collectionDeleted(this);
      }
    }

    // now tell the dependents
    Iterator<ICommand> iter = dependents.iterator();
    while (iter.hasNext())
    {
      ICommand iC = (ICommand) iter.next();
      iC.collectionDeleted(this);
    }
  }

  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  public String getName()
  {
    return dataset.getName();
  }

  public void setName(String name)
  {
    dataset.setName(name);
  }

  @Override
  public IStoreGroup getParent()
  {
    return parent;
  }

  @Override
  public void setParent(IStoreGroup parent)
  {
    this.parent = parent;
  }

  @Override
  public void addChangeListener(IChangeListener listener)
  {
    changeListeners.add(listener);
  }

  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    changeListeners.remove(listener);
  }

  @Override
  public void fireDataChanged()
  {
    for (final IChangeListener thisL : changeListeners)
    {
      thisL.dataChanged(this);
    }
    for(final ICommand thisL: dependents)
    {
      thisL.dataChanged(this);
    }
  }

  @Override
  public UUID getUUID()
  {
    return uuid;
  }

  @UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
  public int size()
  {
    return dataset.getSize();
  }

  public Class<?> storedClass()
  {
    // no, I don't know how we do this :-)
    return null;
  }

  @UIProperty(name = "Indexed", category = UIProperty.CATEGORY_LABEL)
  public boolean isIndexed()
  {
    // is there an axis?
    final AxesMetadata am = dataset.getFirstMetadata(AxesMetadata.class);

    // is it a time axis?
    return am != null;
  }

  private static class LongIterator implements Iterator<Long>
  {
    private long[] _data;
    private int _ctr;

    private LongIterator(long[] data)
    {
      _data = data;
      _ctr = 0;
    }

    @Override
    public boolean hasNext()
    {
      return _ctr < _data.length;
    }

    @Override
    public Long next()
    {
      return _data[_ctr++];
    }

    @Override
    public void remove()
    {
      throw new RuntimeException(
          "Remove operation not provided for this iterator");
    }

  }

  public Iterator<Long> getIndices()
  {
    LongIterator res = null;

    if (isIndexed())
    {
      final AxesMetadata am = dataset.getFirstMetadata(AxesMetadata.class);

      ILazyDataset ds = am.getAxes()[0];
      try
      {
        LongDataset dd =
            (LongDataset) DatasetUtils.sliceAndConvertLazyDataset(ds);
        long[] items = dd.getData();
        res = new LongIterator(items);
      }
      catch (DatasetException e)
      {
        throw new RuntimeException(e);
      }
    }

    return res;
  }

  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity()
  {
    return false;
  }

  public ICommand getPrecedent()
  {
    return predecessor;
  }

  public void addDependent(ICommand command)
  {
    dependents.add(command);
  }

  public List<ICommand> getDependents()
  {
    return dependents;
  }

  /**
   * temporarily use this - until we're confident about replacing child Dataset objects
   * 
   */
  public void clearQuiet()
  {
    dataset = null;
  }

  @Override
  public String toString()
  {
    return dataset.toString();
  }

}
