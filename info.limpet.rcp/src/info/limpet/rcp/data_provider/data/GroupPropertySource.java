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

import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class provides property sheet properties for ButtonElement.
 */
public class GroupPropertySource implements IPropertySource
{

	private static final String GROUP_NAME = "limpet.group.name";

	private IPropertyDescriptor[] propertyDescriptors;
	private final GroupWrapper _group;

	/**
	 * Creates a new ButtonElementPropertySource.
	 * 
	 * @param operationWrapper
	 *          the element whose properties this instance represents
	 */
	public GroupPropertySource(final GroupWrapper operationWrapper)
	{
		_group = operationWrapper;
	}

	@Override
	public Object getEditableValue()
	{
		return _group;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (propertyDescriptors == null)
		{
			List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();

			// Create a descriptor and set a category
			final PropertyDescriptor textDescriptor = new TextPropertyDescriptor(
					GROUP_NAME, "Name");
			textDescriptor.setCategory("Label");
			list.add(textDescriptor);

			propertyDescriptors = (IPropertyDescriptor[]) list.toArray(new IPropertyDescriptor[]{});
		}
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		if (prop.equals(GROUP_NAME))
			return _group.getGroup().getName();

		return null;
	}

	@Override
	public boolean isPropertySet(final Object id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(final Object id)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValue(final Object prop, final Object value)
	{
		
		final StoreGroup coll = _group.getGroup();

		if (prop.equals(GROUP_NAME))
			coll.setName((String) value);
	}

}