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

import java.util.Collection;
import java.util.List;


public interface IOperation<T extends IStoreItem>
{
	public Collection<ICommand<T>> actionsFor(List<T> selection, IStore destination, IContext context);
}
