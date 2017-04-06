package info.limpet2;

import java.util.ArrayList;
import java.util.UUID;

public class StoreGroup extends ArrayList<IStoreItem> implements IStoreGroup
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String _name;
  private UUID _uuid;
  private IStoreGroup _parent;

  public StoreGroup(String name)
  {
    _name = name;
    _uuid = UUID.randomUUID();
  }
  
  

  @Override
  public boolean add(IStoreItem e)
  {
    // tell it who's the daddy
    e.setParent(this);
    
    // actually store it.
    return super.add(e);
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
  public String getName()
  {
    return _name;
  }

  @Override
  public void addChangeListener(IChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void fireDataChanged()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public UUID getUUID()
  {
    // TODO Auto-generated method stub
    return _uuid;
  }

  @Override
  public void dataChanged(IStoreItem subject)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void metadataChanged(IStoreItem subject)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void collectionDeleted(IStoreItem subject)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public IStoreItem get(final String name)
  {
    for (final IStoreItem item : this)
    {
      if (item.getName().equals(name))
      {
        // successS
        return item;
      }
      else if(item instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) item;
        IStoreItem match = group.get(name);
        if(match != null)
        {
          return match;
        }
      }
    }
    // nope, failed.
    return null;
  }

}
