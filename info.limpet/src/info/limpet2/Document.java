package info.limpet2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.january.dataset.IDataset;

public class Document implements IStoreItem
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
  private List<IChangeListener> _dependents = new ArrayList<IChangeListener>();

  public Document(IDataset dataset, ICommand predecessor)
  {
    _dataset = dataset;
    _predecessor = predecessor;
    _uuid = UUID.randomUUID();
  }
  
  public String getName()
  {
    return _dataset.getName();
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

  public int size()
  {
    return _dataset.getSize();
  }

  public Class<?> storedClass()
  {
    // no, I don't know how we do this :-)
    return null;
  }

  public boolean isTemporal()
  {
    // is there an axis?
    
    // is it a time axis?
    return false;
  }

  public boolean isQuantity()
  {
    return false;
  }

  public void addDependent(ICommand command)
  {
    _dependents .add(command);
  }

  /** temporarily use this - until we're confident about replacing child Dataset objects
   * 
   */
  public void clearQuiet()
  {
    _dataset = null;
  }
}
