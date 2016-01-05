/*******************************************************************************
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
 *******************************************************************************/
package info.limpet.rcp.data_provider.data;

import java.util.ArrayList;

/**
 * utility class that stores a list of items, with a specific name
 * 
 * @author ian
 * 
 * @param <Object>
 */
public class NamedList extends ArrayList<Object> implements
		LimpetWrapper
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String _name;
	private final LimpetWrapper _parent;

	public NamedList(final LimpetWrapper parent, final String name)
	{
		_name = name;
		_parent = parent;
	}

	@Override
	public LimpetWrapper getParent()
	{
		return _parent;
	}

	@Override
	public java.lang.Object getSubject()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return _name;
	}

}