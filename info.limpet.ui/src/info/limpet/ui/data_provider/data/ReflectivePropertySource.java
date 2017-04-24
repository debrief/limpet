package info.limpet.ui.data_provider.data;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Script;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import info.limpet.UIProperty;
import info.limpet.ui.Activator;

/**
 * An {@link IPropertySource} that uses {@link UIProperty} annotations to determine the set of
 * {@link org.eclipse.ui.views.properties.PropertyDescriptor}s.
 * 
 */
public class ReflectivePropertySource implements IPropertySource
{

  private final Object object;
  private IPropertyDescriptor[] propertyDescriptors;
  private Map<String, PropertyDescriptor> descriptorPerProperty;

  /**
   * a list of property handlers that we know about
   * 
   */
  private ArrayList<PropertyTypeHandler> myHandlers;

  public ReflectivePropertySource(Object object)
  {
    this.object = object;

    /**
     * define the handlers we know about. In the future this list could be extended by reading
     * extensions from the plugin.xml
     * 
     */
    myHandlers = new ArrayList<PropertyTypeHandler>();
    myHandlers.add(PropertyTypeHandler.STRING);
    myHandlers.add(PropertyTypeHandler.BOOLEAN);
    myHandlers.add(PropertyTypeHandler.INTEGER);
    myHandlers.add(PropertyTypeHandler.DOUBLE);
    myHandlers.add(PropertyTypeHandler.UNIT);
    myHandlers.add(PropertyTypeHandler.QUANTITY_RANGE);
    myHandlers.add(PropertyTypeHandler.GEOMETRY);
    // TODO: consider that some UI properties would like to provide custom
    // handler
    // or one that is not the default. Perhaps make the UIProperty annotation
    // provide the handler class as well if needed
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
      initPropertyDescriptors(object);
    }
    return propertyDescriptors;
  }

  private void initPropertyDescriptors(Object object)
  {

    descriptorPerProperty = new HashMap<String, PropertyDescriptor>();

    List<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();

    try
    {
      PropertyDescriptor[] beanPropertyDescriptors =
          Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
      for (PropertyDescriptor beanPropertyDescriptor : beanPropertyDescriptors)
      {
        final Method readMethod = beanPropertyDescriptor.getReadMethod();
        if(readMethod == null)
        {
          // don't need to report this, it's now expected behaviour
          // System.out.println("Can't find read method for:" + beanPropertyDescriptor.getDisplayName());
        }
        if (readMethod != null)
        {
          UIProperty annotation = readMethod.getAnnotation(UIProperty.class);
          if (annotation != null)
          {

            // skip descriptor if not visible
            if (!annotation.visibleWhen().isEmpty()
                && !evaluateVisibility(object, beanPropertyDescriptors,
                    annotation.visibleWhen()))
            {
              continue;
            }

            org.eclipse.ui.views.properties.PropertyDescriptor descriptor =
                null;

            String propId = beanPropertyDescriptor.getName();
            if (beanPropertyDescriptor.getWriteMethod() != null)
            {
              PropertyTypeHandler propertyTypeHandler =
                  getPropertyTypeHandler(beanPropertyDescriptor
                      .getPropertyType());
              descriptor =
                  propertyTypeHandler.createPropertyDescriptor(propId,
                      annotation);
            }
            else
            {
              // read only descriptor
              descriptor =
                  new org.eclipse.ui.views.properties.PropertyDescriptor(
                      propId, annotation.name());
              descriptor.setCategory(annotation.category());
            }

            result.add(descriptor);

            descriptorPerProperty.put(propId, beanPropertyDescriptor);
          }
        }
      }
    }
    catch (IntrospectionException e)
    {
      Activator.logError(Status.ERROR,
          "Could not load property descriptors for class "
              + object.getClass().getName(), e);

    }

    propertyDescriptors =
        result.toArray(new IPropertyDescriptor[result.size()]);
  }

  private boolean evaluateVisibility(Object object,
      PropertyDescriptor[] beanPropertyDescriptors, String visibleWhen)
  {
    Map<String, PropertyDescriptor> descriptorsMap =
        new HashMap<String, PropertyDescriptor>();
    for (PropertyDescriptor pd : beanPropertyDescriptors)
    {
      descriptorsMap.put(pd.getName(), pd);
    }

    JexlEngine jexl = new JexlBuilder().create();
    JexlExpression expression = jexl.createExpression(visibleWhen);
    JexlContext context = new MapContext();

    Set<List<String>> vars = ((Script) expression).getVariables();
    for (List<String> varList : vars)
    {
      for (String varName : varList)
      {
        PropertyDescriptor propertyDescriptor = descriptorsMap.get(varName);
        if (propertyDescriptor != null)
        {
          try
          {
            Object value = propertyDescriptor.getReadMethod().invoke(object);
            context.set(varName, value);
          }
          catch (Exception e)
          {
            Activator.logError(Status.ERROR,
                "Could not retrieve value for property " + varName, e);
          }
        }
      }
    }

    return (boolean) expression.evaluate(context);
  }

  @Override
  public Object getPropertyValue(Object id)
  {
    PropertyDescriptor descriptor = descriptorPerProperty.get(id);

    try
    {
      Object value = descriptor.getReadMethod().invoke(object);

      // editable properties use custom cell editor, thus value needs conversion
      if (descriptor.getWriteMethod() != null)
      {
        PropertyTypeHandler propertyTypeHandler =
            getPropertyTypeHandler(descriptor.getPropertyType());
        value = propertyTypeHandler.toCellEditorValue(value, object);
      }

      return value;
    }
    catch (Exception e)
    {
      Activator.logError(Status.ERROR, "Could not retrieve value for property "
          + id, e);
      return null;
    }
  }

  @Override
  public boolean isPropertySet(Object id)
  {
    PropertyDescriptor descriptor = descriptorPerProperty.get(id);
    if (descriptor.getWriteMethod() != null)
    {
      PropertyTypeHandler propertyTypeHandler =
          getPropertyTypeHandler(descriptor.getPropertyType());
      UIProperty annotation =
          descriptor.getReadMethod().getAnnotation(UIProperty.class);
      return getPropertyValue(id) != propertyTypeHandler
          .getDefaulValue(annotation);
    }
    return false;
  }

  @Override
  public void resetPropertyValue(Object id)
  {
    PropertyDescriptor descriptor = descriptorPerProperty.get(id);
    // editable property
    if (descriptor.getWriteMethod() != null)
    {
      PropertyTypeHandler propertyTypeHandler =
          getPropertyTypeHandler(descriptor.getPropertyType());
      // delegate to set property
      UIProperty annotation =
          descriptor.getReadMethod().getAnnotation(UIProperty.class);
      setPropertyValue(id, propertyTypeHandler.getDefaulValue(annotation));
    }

  }

  @Override
  public void setPropertyValue(Object id, Object value)
  {
    PropertyDescriptor descriptor = descriptorPerProperty.get(id);
    PropertyTypeHandler propertyTypeHandler =
        getPropertyTypeHandler(descriptor.getPropertyType());
    value = propertyTypeHandler.toModelValue(value, object);
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
   * @param propertyType
   *          a property type obtained from the {@link PropertyDescriptor}
   * @return
   */
  public PropertyTypeHandler getPropertyTypeHandler(Class<?> propertyType)
  {

    PropertyTypeHandler result = null;

    // loop through the handlers we know about
    Iterator<PropertyTypeHandler> hIter = myHandlers.iterator();
    while (hIter.hasNext())
    {
      PropertyTypeHandler thisP = hIter.next();
      if (thisP.canHandle(propertyType))
      {
        result = thisP;
        break;
      }
    }

    if (result == null)
    {
      // TODO: handle other property types, such as
      // number, complex types, Wrapper types - Boolean, Integer, etc
      throw new UnsupportedOperationException("Property type not supported: "
          + propertyType);
    }
    return result;
  }
}
