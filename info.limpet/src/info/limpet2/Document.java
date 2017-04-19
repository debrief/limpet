package info.limpet2;

import info.limpet.UIProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;

abstract public class Document implements IStoreItem
{
  
  // TODO: long-term, find a better place for this
  public static enum InterpMethod
  {
    Linear, Nearest, Before, After
  };
  
  /** _dataset isn't final, sincew we replace it when the 
   * document is re-calculated 
   */
  protected IDataset _dataset;
  final protected ICommand _predecessor;
  private List<IChangeListener> _changeListeners =
      new ArrayList<IChangeListener>();
  private IStoreGroup _parent;
  final private UUID _uuid;
  private List<ICommand> _dependents = new ArrayList<ICommand>();

  public Document(IDataset dataset, ICommand predecessor)
  {
    _dataset = dataset;
    _predecessor = predecessor;
    _uuid = UUID.randomUUID();
  }
  
  public IDataset getDataset()
  {
    return _dataset;
  }
  
  public void setDataset(IDataset dataset)
  {
    _dataset = dataset;
  }
  
  /** tell listeners that it's about to be deleted
   * 
   */
  public void beingDeleted()
  {
    if (_changeListeners != null)
    {
      // tell any standard listeners
      for(IChangeListener thisL: _changeListeners)
      {
        thisL.collectionDeleted(this);
      }
    }

    // now tell the dependents
    Iterator<ICommand> iter = _dependents.iterator();
    while (iter.hasNext())
    {
      ICommand iC = (ICommand) iter.next();
      iC.collectionDeleted(this);
    }
  }
  
  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  public String getName()
  {
    return _dataset.getName();
  }

  public void setName(String name)
  {
    _dataset.setName(name);
  }
  
  @Override
  public IStoreGroup getParent()
  {
    return _parent;
  }

  @Override
  public void setParent(IStoreGroup parent)
  {
    _parent = parent;
  }

  @Override
  public void addChangeListener(IChangeListener listener)
  {
    _changeListeners.add(listener);
  }

  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    _changeListeners.remove(listener);
  }

  @Override
  public void fireDataChanged()
  {
    for(final IChangeListener thisL: _changeListeners)
    {
      thisL.dataChanged(this);
    }
  }

  @Override
  public UUID getUUID()
  {
    return _uuid;
  }

  @UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
  public int size()
  {
    return _dataset.getSize();
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
    final AxesMetadata am = _dataset.getFirstMetadata(AxesMetadata.class);
    
    // is it a time axis?
    return am != null;
  }

  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity()
  {
    return false;
  }
  
  public ICommand getPrecedent()
  {
    return _predecessor;
  }

  public void addDependent(ICommand command)
  {
    _dependents .add(command);
  }

  public List<ICommand> getDependents()
  {
    return _dependents;
  }
  
  /** temporarily use this - until we're confident about replacing child Dataset objects
   * 
   */
  public void clearQuiet()
  {
    _dataset = null;
  }

  @Override
  public String toString()
  {
    return _dataset.toString();
  }
  
  
}
