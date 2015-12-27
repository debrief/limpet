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

public interface ITemporalObjectCollection<T extends Object> extends
		IObjectCollection<T>, IBaseTemporalCollection
{

	/**
	 * add this new item
	 * 
	 * @param time
	 * @param object
	 */
	public void add(long time, T object);

	/**
	 * combination of a timestamp with an observation
	 * 
	 * @author ian
	 * 
	 * @param <T>
	 */
	public interface Doublet<T>
	{
		long getTime();

		T getObservation();
	}

}
