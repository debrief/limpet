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