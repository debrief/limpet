package info.limpet.rcp.data_provider.data;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import info.limpet.UIProperty;
import info.limpet.rcp.propertyeditors.SliderPropertyDescriptor;

/**
 * A helper class that encapsulates some logic about certain property types.
 * Each subclass handles specific type and must implement {@link #getDefaulValue(UIProperty)} 
 * and {@link #doCreatePropertyDescriptor(String, UIProperty)} methods. 
 */
abstract class PropertyTypeHandler
{
	
	/**
	 * @param propertyId
	 * @param metadata
	 * @return the Property descriptor 
	 */
	protected abstract PropertyDescriptor doCreatePropertyDescriptor(String propertyId, UIProperty metadata);
	
	/**
	 * @param metadata
	 * @return default value 
	 */
	abstract Object getDefaulValue(UIProperty metadata);

	final PropertyDescriptor createPropertyDescriptor(String propertyId, UIProperty metadata) {
		PropertyDescriptor propertyDescriptor = doCreatePropertyDescriptor(propertyId, metadata);
		propertyDescriptor.setCategory(metadata.category());
		return propertyDescriptor;
	}

	/**
	 * A handler for String type properties
	 */
	static final PropertyTypeHandler STRING = new PropertyTypeHandler()
	{
		
		@Override
		public Object getDefaulValue(UIProperty metadata)
		{
			return metadata.defaultString();
		}
		
		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId, UIProperty metadata)
		{
			return new TextPropertyDescriptor(propertyId, metadata.name());
		}
	};
	
	/**
	 * A handler for boolean type properties
	 */
	static final PropertyTypeHandler BOOLEAN = new PropertyTypeHandler()
	{
		
		@Override
		public Object getDefaulValue(UIProperty metadata)
		{
			return metadata.defaultBoolean();
		}
		
		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId, UIProperty metadata)
		{
			return new CheckboxPropertyDescriptor(propertyId, metadata.name());
		}
	};
	
	/**
	 * A handler for int type properties
	 */
	static final PropertyTypeHandler INTEGER = new PropertyTypeHandler()
	{
		
		@Override
		public Object getDefaulValue(UIProperty metadata)
		{
			return metadata.defaultInt();
		}
		
		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId, UIProperty metadata)
		{
			return new SliderPropertyDescriptor(propertyId, metadata.name(), metadata.min(), metadata.max());
		}
	};
}
