/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet;

import java.util.List;
import java.util.UUID;

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

		/** if this object has children
		 * 
		 * @return
		 */
		public boolean hasChildren();
		
		/** find the layer that contains this collection (or null if applicable)
		 * 
		 * @return parent collection, or null
		 */
		public IStoreGroup getParent();
		
		/** set the parent object for this collection
		 * 
		 * @param parent
		 */
		public void setParent(IStoreGroup parent);
		
		public String getName();

		public void addChangeListener(IChangeListener listener);

		public void removeChangeListener(IChangeListener listener);

		/**
		 * indicate that the collection has changed Note: both registeered listeners
		 * and dependents are informed of the change
		 */
		public void fireDataChanged();

		public UUID getUUID();
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

	/**
	 * retrieve the data item with the specified UUID
	 * 
	 * @param uuid
	 *          the item we're looking for
	 * @return the matching item (or null)
	 */
	IStoreItem get(UUID uuid);

	void addChangeListener(StoreChangeListener listener);

	void removeChangeListener(StoreChangeListener listener);
}
