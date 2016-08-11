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

import info.limpet.data.store.StoreGroup.StoreChangeListener;

/**
 * a storage container for collections, stored in a tree structure.
 * 
 * @author ian
 * 
 */
public interface IStore extends IStoreGroup
{

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
  boolean add(IStoreItem items);


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
