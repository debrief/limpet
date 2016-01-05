package info.limpet.rcp.data_provider.data;

import info.limpet.IQuantityCollection;
import info.limpet.QuantityRange;
import info.limpet.UIProperty;
import info.limpet.rcp.propertyeditors.SliderPropertyDescriptor;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

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
	 * Some {@link CellEditor}s represent the model property with different 
	 * type in the UI, for example String for double values.
	 * @param cellEditorValue the cell editor value
	 * @param propertyOwner the object that owns the property
	 * @return the model value
	 */
	protected Object toModelValue(Object cellEditorValue, Object propertyOwner) {
		return cellEditorValue;
	}
	
	/**
	 * Some {@link CellEditor}s represent the model property with different 
	 * type in the UI, for example String for double values.
	 * @param modelValue the model value
	 * @param propertyOwner the object that owns the property
	 * @return the cell editor value
	 */
	protected Object toCellEditorValue(Object modelValue, Object properyOwner) {
		return modelValue;
	}
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
	
	/**
	 * A handler for double primitive type and Number type properties.
	 */
	static final PropertyTypeHandler DOUBLE = new PropertyTypeHandler()
	{

		@Override
		public Object getDefaulValue(UIProperty metadata)
		{
			return metadata.defaultDouble();
		}

		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
				UIProperty metadata)
		{
			return new TextPropertyDescriptor(propertyId, metadata.name());
		}

		@Override
		public boolean canHandle(Class<?> propertyType)
		{
			return Number.class == propertyType || "double".equals(propertyType.getName());
		}
		
		protected Object toModelValue(Object cellEditorValue, Object propertyOwner) {
			return Double.parseDouble((String) cellEditorValue);
		};
		
		protected Object toCellEditorValue(Object modelValue, Object propertyOwner) {
			return modelValue + "";
		};
	};
	
	/**
	 * A handler for {@link Unit} typed properties
	 */
	static final PropertyTypeHandler UNIT = new PropertyTypeHandler()
	{

		@Override
		public Object getDefaulValue(UIProperty metadata)
		{
			return Unit.ONE;
		}

		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
				UIProperty metadata)
		{
			// TODO: ideally this would be a drop down list, where user can select values.
			// In that case toModel and toUI would need to deal with indices
			return new TextPropertyDescriptor(propertyId, metadata.name());
		}

		@Override
		public boolean canHandle(Class<?> propertyType)
		{
			return propertyType == Unit.class;
		}
		
		protected Object toModelValue(Object cellEditorValue, Object propertyOwner) {
			// TODO: here we should have some form of conversion from string to Unit 
			// (perhaps reuse the logic already in CsvParser class) 
			return cellEditorValue;
		};
		
		protected Object toCellEditorValue(Object modelValue, Object propertyOwner) {
			return modelValue + "";
		};
	};

	/**
	 * A handler for {@link QuantityRange} typed properties
	 */
	static final PropertyTypeHandler QUANTITY_RANGE = new PropertyTypeHandler()
	{

		@Override
		public Object getDefaulValue(UIProperty metadata)
		{
			return null;
		}

		protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
				UIProperty metadata)
		{
			return new TextPropertyDescriptor(propertyId, metadata.name());
		}

		@Override
		public boolean canHandle(Class<?> propertyType)
		{
			return propertyType == QuantityRange.class;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected Object toModelValue(Object cellEditorValue, Object propertyOwner) {
			
			QuantityRange newR = null;
			
			IQuantityCollection<?> tt = (IQuantityCollection<?>) propertyOwner;
			// try to get a range from the string
			String str = (String) cellEditorValue;
			if (str.length() > 0)
			{
				// ok, split it up
				String[] bits = str.split(":");
				if (bits.length == 2)
				{
					String min = bits[0].trim();
					String max = bits[1].trim();
					try
					{
						double minV = Double.parseDouble(min);
						double maxV = Double.parseDouble(max);
						if (maxV > minV)
						{
							Unit<?> collUnits = tt.getUnits();
							newR = new QuantityRange(Measure.valueOf(minV,
									collUnits), Measure.valueOf(maxV, collUnits));
						}
					}
					catch (NumberFormatException fe)
					{
						System.err.println("Failed to extract number range: fe");
					}
				}
				else
				{
					System.err.println("Number format string not properly constructed:"
							+ str + " (should be 1:10)");
				}
			}
			
			return newR; 
		};
		
		@SuppressWarnings("unchecked")
		protected Object toCellEditorValue(Object modelValue, Object propertyOwner) {
			final String str;
			QuantityRange<Quantity> range = (QuantityRange<Quantity>) modelValue;
			IQuantityCollection<Quantity> qc = (IQuantityCollection<Quantity>) propertyOwner; 
			if (range != null)
			{
				str = "" + range.getMinimum().longValue(qc.getUnits()) + " : "
						+ range.getMaximum().longValue(qc.getUnits());
			}
			else
			{
				str = " : ";
			}

			return str;
		};
	};

}
