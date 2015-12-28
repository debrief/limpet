package info.limpet.rcp.data_provider.data;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import info.limpet.UIProperty;
import info.limpet.rcp.propertyeditors.SliderPropertyDescriptor;

/**
 * A helper class that encapsulates some logic about certain property types.
 * Each subclass handles specific type and must implement
 * {@link #getDefaulValue(UIProperty)} and
 * {@link #doCreatePropertyDescriptor(String, UIProperty)} methods.
 */
abstract class PropertyTypeHandler
{

	/** can this handler handle a property of the specified type?
	 * 
	 * @param propertyType the type of the property
	 * @return yes/no
	 */
	abstract public boolean canHandle(Class<?> propertyType);

	/**
	 * @param propertyId
	 * @param metadata
	 * @return the Property descriptor
	 */
	protected abstract PropertyDescriptor doCreatePropertyDescriptor(
			String propertyId, UIProperty metadata);

	/**
	 * @param metadata
	 * @return default value
	 */
	abstract Object getDefaulValue(UIProperty metadata);

	/** create the property descriptor for the specified property
	 * 
	 * @param propertyId
	 * @param metadata
	 * @return
	 */
	final PropertyDescriptor createPropertyDescriptor(String propertyId,
			UIProperty metadata)
	{
		final PropertyDescriptor propertyDescriptor = doCreatePropertyDescriptor(
				propertyId, metadata);
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

		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
				UIProperty metadata)
		{
			return new TextPropertyDescriptor(propertyId, metadata.name());
		}

		@Override
		public boolean canHandle(Class<?> propertyType)
		{
			return propertyType.equals(String.class);
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

		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
				UIProperty metadata)
		{
			return new CheckboxPropertyDescriptor(propertyId, metadata.name());
		}

		@Override
		public boolean canHandle(Class<?> propertyType)
		{
			return "boolean".equals(propertyType.getName());
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

		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
				UIProperty metadata)
		{
			return new SliderPropertyDescriptor(propertyId, metadata.name(),
					metadata.min(), metadata.max());
		}

		@Override
		public boolean canHandle(Class<?> propertyType)
		{
			return "int".equals(propertyType.getName());
		}
	};
}
