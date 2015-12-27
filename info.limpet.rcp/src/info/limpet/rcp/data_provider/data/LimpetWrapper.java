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
package info.limpet.rcp.data_provider.data;

/**
 * objects that are used in the Limpet object tree
 * 
 * @author ian
 * 
 */
public interface LimpetWrapper
{
	/**
	 * retrieve the parent of the current object
	 * 
	 * @return
	 */
	public LimpetWrapper getParent();

	/**
	 * retrieve the pure limpet object that this instance is wrapping
	 * 
	 * @return
	 */
	public Object getSubject();
}