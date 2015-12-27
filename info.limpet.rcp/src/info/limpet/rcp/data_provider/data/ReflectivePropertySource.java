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

import info.limpet.UIProperty;
import info.limpet.rcp.Activator;

/**
 * An {@link IPropertySource} that uses {@link UIProperty} annotations to 
 * determine the set of {@link org.eclipse.ui.views.properties.PropertyDescriptor}s.
 *  
 */
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
					
					String propId = beanPropertyDescriptor.getName();					
					if (beanPropertyDescriptor.getWriteMethod() != null)
					{
						PropertyTypeHandler propertyTypeHandler = getPropertyTypeHandler(beanPropertyDescriptor.getPropertyType());
						descriptor = propertyTypeHandler.createPropertyDescriptor(propId, annotation);
					}
					else
					{
						// read only descriptor
						descriptor = new org.eclipse.ui.views.properties.PropertyDescriptor(
								propId, annotation.name());
						descriptor.setCategory(annotation.category());
					}

					result.add(descriptor);

					descriptorPerProperty.put(propId,
							beanPropertyDescriptor);
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
		PropertyDescriptor descriptor = descriptorPerProperty.get(id);
		if (descriptor.getWriteMethod() != null) {
			PropertyTypeHandler propertyTypeHandler = getPropertyTypeHandler(descriptor.getPropertyType());
			UIProperty annotation = descriptor.getReadMethod().getAnnotation(UIProperty.class);
			return getPropertyValue(id) != propertyTypeHandler.getDefaulValue(annotation);
		}
		return false;
	}

	@Override
	public void resetPropertyValue(Object id)
	{
		PropertyDescriptor descriptor = descriptorPerProperty.get(id);
		// editable property
		if (descriptor.getWriteMethod() != null) {
			PropertyTypeHandler propertyTypeHandler = getPropertyTypeHandler(descriptor.getPropertyType());
			// delegate to set property
			UIProperty annotation = descriptor.getReadMethod().getAnnotation(UIProperty.class);
			setPropertyValue(id, propertyTypeHandler.getDefaulValue(annotation));
		}

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
	
	/**
	 * @param propertyType a property type obtained from the {@link PropertyDescriptor}
	 * @return 
	 */
	public static PropertyTypeHandler getPropertyTypeHandler(Class<?> propertyType) {
		
		PropertyTypeHandler result = null;
		if (propertyType.equals(String.class))
		{
			result = PropertyTypeHandler.STRING;
		}
		else if ("boolean".equals(propertyType.getName()))
		{
			result = PropertyTypeHandler.BOOLEAN;
		}
		else if ("int".equals(propertyType.getName()))
		{
			result = PropertyTypeHandler.INTEGER;
		}						
		else
		{
			// TODO: handle other property types, such as
			// number, complex types, Wrapper types - Boolean, Integer, etc
			throw new UnsupportedOperationException("Property type not supported: " + propertyType);
		}
		return result;
	}
}
