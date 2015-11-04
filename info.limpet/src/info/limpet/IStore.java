package info.limpet;

import java.util.List;

import info.limpet.data.store.InMemoryStore.StoreChangeListener;

/**
 * a storage container for collections, stored in a tree structure.
 * 
 * @author ian
 * 
 */
public interface IStore
{
	
	public static interface IStoreItem
	{
		public String getName();
		public boolean hasChildren();
		
		public void addChangeListener(IChangeListener listener);
		public void removeChangeListener(IChangeListener listener);
		
		/** indicate that the collection has changed
		 *  Note: both registeered listeners and dependents are informed of the change
		 */
		public void fireDataChanged();
	}

	/**
	 * add the new collections at the root level
	 * 
	 * @param results
	 */
	void addAll(List<IStoreItem> items);

	/**
	 * add the new collections at the root level
	 * 
	 * @param results
	 */
	void add(IStoreItem items);

	/**
	 * retrieve the named collection
	 * 
	 * @param name
	 * @return
	 */
	IStoreItem get(String name);

	void addChangeListener(StoreChangeListener listener);

	void removeChangeListener(StoreChangeListener listener);
}
