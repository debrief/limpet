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

public interface IStoreGroup extends IChangeListener
{

	public boolean hasChildren();

	public boolean add(IStoreItem item);
	
	public boolean remove(Object item);
	
	public void addChangeListener(IChangeListener listener);

	public void removeChangeListener(IChangeListener listener);

}
