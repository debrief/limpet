/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package info.limpet;

import info.limpet.IStore.IStoreItem;

public interface IChangeListener
{
	/** the data in an item has changed
	 * 
	 * @param subject
	 */
	public void dataChanged(IStoreItem subject);
	
	/** an item has cosmetically changed (name, color, etc)
	 * 
	 * @param subject
	 */
	public void metadataChanged(IStoreItem subject);
	
	public void collectionDeleted(IStoreItem subject);
}
