package info.limpet.data.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.data.impl.ListenerHelper;

public class InMemoryStore implements IStore, IChangeListener
{

	List<IStoreItem> _store = new ArrayList<IStoreItem>();

	private transient List<StoreChangeListener> _listeners = new ArrayList<StoreChangeListener>();

	public static class StoreGroup extends ArrayList<IStoreItem> implements
			IStoreItem, IStoreGroup, IChangeListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String _name;
		private IStoreGroup _parent;
		transient private UUID uuid;

		// note: we make the change support listeners transient, since
		// they refer to UI elements that we don't persist
		private transient ListenerHelper _changeSupport;

		public StoreGroup(String name)
		{
			_name = name;
		}

		@Override
		public UUID getUUID()
		{
			if (uuid == null)
			{
				uuid = UUID.randomUUID();
			}
			return uuid;
		}

		@Override
		public boolean add(IStoreItem e)
		{
			e.setParent(this);
			
			// ok, start listening to this item
			e.addChangeListener(this);
			
			boolean res = super.add(e);
			
			fireDataChanged();
			
			return res;
		}

		@Override
		public boolean remove(Object o)
		{
			if(o instanceof IStoreItem)
			{
				IStoreItem si = (IStoreItem) o;
				si.setParent(null);

				si.removeChangeListener(this);
				
			}
			
			boolean res = super.remove(o);
			
			// ok, fire an update.
			fireDataChanged();
			
			return res;
		}

		@Override
		public String getName()
		{
			return _name;
		}

		
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			StoreGroup other = (StoreGroup) obj;
			if (_name == null)
			{
				if (other._name != null)
					return false;
			}
			else if (!_name.equals(other._name))
				return false;
			return true;
		}

		@Override
		public boolean hasChildren()
		{
			return size() > 0;
		}

		public List<IStoreItem> children()
		{
			return this;
		}

		public void setName(String value)
		{
			_name = value;
			
			// and tell any listeners
			fireDataChanged();
		}

		protected void initListeners()
		{
			if (_changeSupport == null)
			{
				_changeSupport = new ListenerHelper();
			}
		}

		@Override
		public void addChangeListener(IChangeListener listener)
		{
			initListeners();

			_changeSupport.add(listener);
		}

		@Override
		public void removeChangeListener(IChangeListener listener)
		{
			initListeners();

			_changeSupport.remove(listener);
		}

		@Override
		public void fireDataChanged()
		{
			if (_changeSupport != null)
			{
				// tell any standard listeners
				_changeSupport.fireDataChange(this);
			}
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
		public void dataChanged(IStoreItem subject)
		{
			fireDataChanged();
		}

		@Override
		public void metadataChanged(IStoreItem subject)
		{
			fireDataChanged();
		}

		@Override
		public void collectionDeleted(IStoreItem subject)
		{
			fireDataChanged();
		}
	}

	private Object readResolve()
	{
		_listeners = new ArrayList<StoreChangeListener>();
		return this;
	}

	public static interface StoreChangeListener
	{
		public void changed();
	}

	public void addChangeListener(StoreChangeListener listener)
	{
		_listeners.add(listener);
	}

	public void removeChangeListener(StoreChangeListener listener)
	{
		_listeners.remove(listener);
	}

	protected void fireModified()
	{
		Iterator<StoreChangeListener> iter = _listeners.iterator();
		while (iter.hasNext())
		{
			InMemoryStore.StoreChangeListener listener = (InMemoryStore.StoreChangeListener) iter
					.next();
			listener.changed();
		}
	}

	@Override
	public void addAll(List<IStoreItem> results)
	{
		// add the items individually, so we can register as a listener
		Iterator<IStoreItem> iter = results.iterator();
		while (iter.hasNext())
		{
			IStoreItem iCollection = iter.next();
			add(iCollection);
		}

		fireModified();
	}

	@Override
	public void add(IStoreItem results)
	{
		_store.add(results);

		// register as a listener with the results object
		if (results instanceof ICollection)
		{
			ICollection coll = (ICollection) results;
			coll.addChangeListener(this);
		}
		else if(results instanceof IStoreGroup)
		{
			IStoreGroup group = (IStoreGroup) results;
			group.addChangeListener(this);
		}

		fireModified();
	}

	public int size()
	{
		return _store.size();
	}

	@Override
	public IStoreItem get(String name)
	{
		IStoreItem res = null;
		Iterator<IStoreItem> iter = _store.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof StoreGroup)
			{
				StoreGroup group = (StoreGroup) item;
				Iterator<IStoreItem> iter2 = group.iterator();
				while (iter2.hasNext())
				{
					IStore.IStoreItem thisI = (IStore.IStoreItem) iter2.next();
					if (name.equals(thisI.getName()))
					{
						res = thisI;
						break;
					}
				}
			}
			if (name.equals(item.getName()))
			{
				res = item;
				break;
			}
		}
		return res;
	}
	

	@Override
	public IStoreItem get(UUID uuid)
	{
		IStoreItem res = null;
		Iterator<IStoreItem> iter = _store.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof StoreGroup)
			{
				StoreGroup group = (StoreGroup) item;
				Iterator<IStoreItem> iter2 = group.iterator();
				while (iter2.hasNext())
				{
					IStore.IStoreItem thisI = (IStore.IStoreItem) iter2.next();
					if (uuid.equals(thisI.getUUID()))
					{
						res = thisI;
						break;
					}
				}
			}
			if (uuid.equals(item.getUUID()))
			{
				res = item;
				break;
			}
		}
		return res;
	}


	public Iterator<IStoreItem> iterator()
	{
		return _store.iterator();
	}

	public void clear()
	{
		// stop listening to the collections individually
		// - defer the clear until the end,
		// so we don't get concurrent modification
		Iterator<IStoreItem> iter = _store.iterator();
		while (iter.hasNext())
		{
			IStoreItem iC = iter.next();
			if (iC instanceof ICollection)
			{
				ICollection coll = (ICollection) iC;
				coll.removeChangeListener(this);
			}
		}

		_store.clear();
		fireModified();
	}

	public void remove(IStoreItem item)
	{
		_store.remove(item);

		// stop listening to this one
		if (item instanceof ICollection)
		{
			ICollection collection = (ICollection) item;
			collection.removeChangeListener(this);

			// ok, also tell it that it's being deleted
			collection.beingDeleted();
		}

		fireModified();
	}

	@Override
	public void dataChanged(IStoreItem subject)
	{
		fireModified();
	}

	@Override
	public void metadataChanged(IStoreItem subject)
	{
		dataChanged(subject);
	}

	@Override
	public void collectionDeleted(IStoreItem subject)
	{
	}
}
