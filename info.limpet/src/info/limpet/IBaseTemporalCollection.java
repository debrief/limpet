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

import java.util.List;

public interface IBaseTemporalCollection
{

	/**
	 * time of the first observation
	 * 
	 * @return
	 */
	public long start();

	/**
	 * time of the last observation
	 * 
	 * @return
	 */
	public long finish();

	/**
	 * time period between first & last measurement. 0 if just one observation, -1
	 * if set empty.
	 * 
	 * @return
	 */
	public long duration();

	/**
	 * number of observations per milli, across the whole collection
	 * 
	 * @return
	 */
	public double rate();
	
	/** retrieve the times
	 * 
	 * @return
	 */
	public List<Long> getTimes();
	
}