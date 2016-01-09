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

public interface ICollection extends IStoreItem
{
	String getName();
	void setName(String name);
	int getValuesCount();
	boolean isQuantity();
	boolean isTemporal();
	void setDescription(String description);
	String getDescription();


	// note: dependents and precedents are intended to be persistent,
	// change listeners aren't
	
	List<ICommand<?>> getDependents();
	ICommand<?> getPrecedent();
	void addDependent(ICommand<?> addQuantityValues);
	
	/** indicate that the collection has changed
	 *  Note: both registeered listeners and dependents are informed of the change
	 */
	void fireDataChanged();
	
	/** indicate that the collection's metadata has changed
	 *  Note: both registeered listeners and dependents are informed of the change
	 */
	void fireMetadataChanged();
	
	/** what type is stored in collection
	 * 
	 * @return
	 */
	Class<?> storedClass();
	
	/** tell listeners that it's about to be deleted
	 * 
	 */
	void beingDeleted();
}
