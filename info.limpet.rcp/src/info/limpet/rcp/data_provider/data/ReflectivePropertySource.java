package info.limpet.rcp.data_provider.data;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import info.limpet.UIProperty;
import info.limpet.rcp.Activator;

public class ReflectivePropertySource implements IPropertySource
{

	private final Object object;
	private IPropertyDescriptor[] propertyDescriptors;
	private Map<String, PropertyDescriptor> descriptorPerProperty;

	public ReflectivePropertySource(Object object)
	{
		this.object = object;
	}

	@Override
	public Object getEditableValue()
	{
		return object;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (propertyDescriptors == null)
		{
			initPropertyDescriptors(object.getClass());
		}
		return propertyDescriptors;
	}

	private void initPropertyDescriptors(Class<?> cls)
	{

		descriptorPerProperty = new HashMap<String, PropertyDescriptor>();

		List<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();

		try
		{
			PropertyDescriptor[] beanPropertyDescriptors = Introspector.getBeanInfo(
					cls).getPropertyDescriptors();
			for (PropertyDescriptor beanPropertyDescriptor : beanPropertyDescriptors)
			{
				UIProperty annotation = beanPropertyDescriptor.getReadMethod()
						.getAnnotation(UIProperty.class);
				if (annotation != null)
				{

					org.eclipse.ui.views.properties.PropertyDescriptor descriptor = null;
					String propName = annotation.name();
					String propCategory = annotation.category();
					if (beanPropertyDescriptor.getWriteMethod() != null)
					{
						if (beanPropertyDescriptor.getPropertyType().equals(String.class))
						{
							descriptor = new TextPropertyDescriptor(
									beanPropertyDescriptor.getName(), propName);
						}
						else
						{
							// TODO: handle other property types, such as
							// number, boolean,
							// complex types, etc
						}
					}
					else
					{
						// read only descriptor
						descriptor = new org.eclipse.ui.views.properties.PropertyDescriptor(
								beanPropertyDescriptor.getName(), propName);
					}

					if (descriptor != null)
					{
						descriptor.setCategory(propCategory);
						result.add(descriptor);

						descriptorPerProperty.put(beanPropertyDescriptor.getName(),
								beanPropertyDescriptor);
					}

				}
			}
		}
		catch (IntrospectionException e)
		{
			Activator.logError(Status.ERROR,
					"Could not load property descriptors for class " + cls.getName(), e);

		}

		propertyDescriptors = result
				.toArray(new IPropertyDescriptor[result.size()]);
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		PropertyDescriptor descriptor = descriptorPerProperty.get(id);
		try
		{
			return descriptor.getReadMethod().invoke(object);
		}
		catch (Exception e)
		{
			Activator.logError(Status.ERROR, "Could not retrive value for property "
					+ id, e);
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(Object id)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValue(Object id, Object value)
	{
		PropertyDescriptor descriptor = descriptorPerProperty.get(id);
		try
		{
			descriptor.getWriteMethod().invoke(object, value);
		}
		catch (Exception e)
		{
			Activator.logError(Status.ERROR,
					"Could not set value for property " + id, e);
		}
	}

}
