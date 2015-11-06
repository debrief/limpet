package info.limpet.rcp.data_provider.data;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.QuantityRange;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * This class provides property sheet properties for ButtonElement.
 */
public class CollectionPropertySource implements IPropertySource
{

	private static final String PROPERTY_NAME = "limpet.collection.name";
	private static final String PROPERTY_SIZE = "limpet.collection.size";
	private static final String PROPERTY_DESCRIPTION = "limpet.collection.description";
	private static final String PROPERTY_VALUE = "limpet.collection.value";
	private static final String PROPERTY_VALUE_SLIDER = "limpet.collection.value.slider";
	private static final String PROPERTY_UNITS = "limpet.collection.units";
	private static final String PROPERTY_RANGE = "limpet.collection.range";

	private IPropertyDescriptor[] propertyDescriptors;
	private final CollectionWrapper _collection;

	/**
	 * Creates a new ButtonElementPropertySource.
	 * 
	 * @param element
	 *          the element whose properties this instance represents
	 */
	public CollectionPropertySource(final CollectionWrapper element)
	{
		_collection = element;
	}

	@Override
	public Object getEditableValue()
	{
		return _collection;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (propertyDescriptors == null)
		{
			List<PropertyDescriptor> dList = new ArrayList<PropertyDescriptor>();

			final PropertyDescriptor textDescriptor = new TextPropertyDescriptor(
					PROPERTY_NAME, "Name");
			textDescriptor.setCategory("Label");
			final PropertyDescriptor sizeDescriptor = new PropertyDescriptor(
					PROPERTY_SIZE, "Size");
			sizeDescriptor.setCategory("Metadata");
			final PropertyDescriptor descriptionDescriptor = new TextPropertyDescriptor(
					PROPERTY_DESCRIPTION, "Description");
			descriptionDescriptor.setCategory("Label");

			// see if we want to add a value editor
			final ICollection coll = (ICollection) _collection.getCollection();
			if (coll.size() == 1)
			{
				// ok, just use a text editor
				final PropertyDescriptor valueDescriptor = new TextPropertyDescriptor(
						PROPERTY_VALUE, "Value");
				valueDescriptor.setCategory("Value");
				dList.add(valueDescriptor);

				// see if the type has any units
				if (coll instanceof IQuantityCollection)
				{
					IQuantityCollection<?> qc = (IQuantityCollection<?>) _collection
							.getCollection();
					if (qc.getUnits() != null)
					{
						final PropertyDescriptor unitsDescriptor = new PropertyDescriptor(
								PROPERTY_UNITS, "Units");
						unitsDescriptor.setCategory("Value");
						dList.add(unitsDescriptor);
					}

					// also provide a range descriptor
					final PropertyDescriptor rangeDescriptor = new TextPropertyDescriptor(
							PROPERTY_RANGE, "Range");
					rangeDescriptor.setCategory("Metadata");
					dList.add(rangeDescriptor);
				}

			}

			dList.add(textDescriptor);
			dList.add(sizeDescriptor);
			dList.add(descriptionDescriptor);

			propertyDescriptors = dList.toArray(new IPropertyDescriptor[]
			{});
		}
		return propertyDescriptors;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		final ICollection coll = (ICollection) _collection.getCollection();

		if (prop.equals(PROPERTY_NAME))
			return coll.getName();
		else if (prop.equals(PROPERTY_SIZE))
			return coll.size();
		else if (prop.equals(PROPERTY_DESCRIPTION))
			return coll.getDescription();
		else if (prop.equals(PROPERTY_VALUE))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<Quantity> qc = (IQuantityCollection<Quantity>) coll;
				Measurable<Quantity> first = getSingleton();
				if (first != null)
				{
					return "" + first.doubleValue(qc.getUnits());
				}
			}
		}
		else if (prop.equals(PROPERTY_RANGE))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				final String str;
				IQuantityCollection<Quantity> qc = (IQuantityCollection<Quantity>) coll;
				QuantityRange<Quantity> range = qc.getRange();
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
			}
		}
		else if (prop.equals(PROPERTY_VALUE_SLIDER))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				// ok - we have to create a composite object that includes both the
				// range and the current value - so it can be passed to the slider
				IQuantityCollection<Quantity> qc = (IQuantityCollection<Quantity>) coll;

				Measurable<Quantity> first = getSingleton();
				if (first != null)
				{

					return "" + first.doubleValue(qc.getUnits());
				}
			}
		}
		else if (prop.equals(PROPERTY_UNITS))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				// ok - we have to create a composite object that includes both the
				// range and the current value - so it can be passed to the slider
				IQuantityCollection<?> qc = (IQuantityCollection<?>) coll;
				return "" + qc.getUnits();
			}
		}

		return null;
	}

	private Measurable<Quantity> getSingleton()
	{
		Measurable<Quantity> res = null;
		@SuppressWarnings("unchecked")
		final IQuantityCollection<Quantity> tt = (IQuantityCollection<Quantity>) _collection
				.getCollection();
		if (tt instanceof IQuantityCollection)
		{
			IQuantityCollection<Quantity> iq = (IQuantityCollection<Quantity>) tt;
			res = iq.getValues().iterator().next();
		}

		return res;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setPropertyValue(final Object id, final Object value)
	{
		final String prop = (String) id;

		final ICollection coll = (ICollection) _collection.getCollection();

		if (prop.equals(PROPERTY_NAME))
			coll.setName((String) value);
		else if (prop.equals(PROPERTY_SIZE))
			throw new RuntimeException("Can't set size, silly");
		else if (prop.equals(PROPERTY_DESCRIPTION))
			coll.setDescription((String) value);
		else if (prop.equals(PROPERTY_VALUE))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> tt = (IQuantityCollection<?>) coll;
				tt.replaceSingleton(Double.parseDouble((String) value));

				// ok, fire changed!
				tt.fireDataChanged();
			}
		}
		else if (prop.equals(PROPERTY_VALUE_SLIDER))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> tt = (IQuantityCollection<?>) coll;

				// ok, extract the new value from the input object. It's probably
				// going to be a composite object that combines both range and value

				tt.replaceSingleton(Double.parseDouble((String) value));

				// ok, fire changed!
				tt.fireDataChanged();
			}
		}
		else if (prop.equals(PROPERTY_RANGE))
		{
			if (coll instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> tt = (IQuantityCollection<?>) coll;

				// try to get a range from the string
				String str = (String) value;
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
								QuantityRange newR = new QuantityRange(Measure.valueOf(minV,
										collUnits), Measure.valueOf(maxV, collUnits));
								tt.setRange(newR);
							}
						}
						catch (NumberFormatException fe)
						{
							System.err.println("Failed to extract number range: fe");
						}
					}
					else
					{
						System.err.println("Number format string not properly constructed:" + str  + " (should be 1:10)");
					}
				}
				// ok, fire changed!
				tt.fireMetadataChanged();
			}
		}

	}

}