package info.limpet.rcp.data_provider.data;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.rcp.propertyeditors.SliderPropertyDescriptor;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Quantity;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import tec.units.ri.quantity.QuantityRange;

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
			if (_collection.getCollection().size() == 1)
			{
				// get the first item
				final Quantity<?> first = getSingleton();
				boolean found = false;

				// does this have a range?
				if(_collection.getCollection().isQuantity())
				{
					QuantityCollection<?> qC = (QuantityCollection<?>) _collection.getCollection();
					QuantityRange<?> range = qC.getRange();
					if(range != null)
					{
						found = true;
						
						// ok - here we should create a descriptor for a control
						// that uses a slider between a range of values
						
						// temporarily - just use a text descriptor
						Number max = range.getMaximum().getValue();
						Number min = range.getMinimum().getValue();
						final PropertyDescriptor valueDescriptor = new SliderPropertyDescriptor(
								PROPERTY_VALUE_SLIDER, "Value", min.intValue(), max.intValue());
						valueDescriptor.setCategory("Value");
						dList.add(valueDescriptor);
					}
				}
				
				if(!found)
				{
					// ok, just use a text editor
					final PropertyDescriptor valueDescriptor = new TextPropertyDescriptor(
							PROPERTY_VALUE, "Value");
					valueDescriptor.setCategory("Value");
					dList.add(valueDescriptor);
				}
				
				// see if the type has any units
				if (first.getUnit() != null)
				{
					final PropertyDescriptor unitsDescriptor = new PropertyDescriptor(
							PROPERTY_UNITS, "Units");
					unitsDescriptor.setCategory("Value");
					dList.add(unitsDescriptor);
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

	@Override
	public Object getPropertyValue(final Object id)
	{
		final String prop = (String) id;

		if (prop.equals(PROPERTY_NAME))
			return _collection.getCollection().getName();
		else if (prop.equals(PROPERTY_SIZE))
			return _collection.getCollection().size();
		else if (prop.equals(PROPERTY_DESCRIPTION))
			return _collection.getCollection().getDescription();
		else if (prop.equals(PROPERTY_VALUE))
		{
			ICollection theColl = _collection.getCollection();
			if (theColl instanceof IQuantityCollection<?>)
			{
				Quantity<?> first = getSingleton();
				if (first != null)
				{
					return "" + first.getValue();
				}
			}
		}
		else if (prop.equals(PROPERTY_VALUE_SLIDER))
		{
			ICollection theColl = _collection.getCollection();
			if (theColl instanceof IQuantityCollection<?>)
			{
				// ok - we have to create a composite object that includes both the 
				// range and the current value - so it can be passed to the slider
				
				Quantity<?> first = getSingleton();
				if (first != null)
				{
					return "" + first.getValue();
				}
			}
		}
		else if (prop.equals(PROPERTY_UNITS))
		{
			Quantity<?> first = getSingleton();
			if (first != null)
			{
				return "" + first.getUnit().toString();
			}
		}

		return null;
	}

	private Quantity<?> getSingleton()
	{
		Quantity<?> res = null;
		final IObjectCollection<?> tt = (IObjectCollection<?>) _collection
				.getCollection();
		if (tt instanceof IQuantityCollection<?>)
		{
			IQuantityCollection<?> iq = (IQuantityCollection<?>) tt;
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

	@Override
	public void setPropertyValue(final Object id, final Object value)
	{
		final String prop = (String) id;

		if (prop.equals(PROPERTY_NAME))
			_collection.getCollection().setName((String) value);
		else if (prop.equals(PROPERTY_SIZE))
			throw new RuntimeException("Can't set size, silly");
		else if (prop.equals(PROPERTY_DESCRIPTION))
			_collection.getCollection().setDescription((String) value);
		else if (prop.equals(PROPERTY_VALUE))
		{
			ICollection theColl = _collection.getCollection();
			if (theColl instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> tt = (IQuantityCollection<?>) theColl;
				tt.replaceSingleton(Double.parseDouble((String) value));
				
				// ok, fire changed!
				tt.fireChanged();
			}
		}
		else if (prop.equals(PROPERTY_VALUE_SLIDER))
		{
			ICollection theColl = _collection.getCollection();
			if (theColl instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> tt = (IQuantityCollection<?>) theColl;
				
				// ok, extract the new value from the input object. It's probably 
				// going to be a composite object that combines both range and value
				
				tt.replaceSingleton(Double.parseDouble((String) value));
				
				// ok, fire changed!
				tt.fireChanged();
			}
		}

	}

}