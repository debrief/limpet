package info.limpet.rcp.data_provider.data;

import info.limpet.IBaseQuantityCollection;
import info.limpet.QuantityRange;
import info.limpet.UIProperty;
import info.limpet.rcp.Activator;
import info.limpet.rcp.propertyeditors.SliderPropertyDescriptor;

import java.awt.geom.Point2D;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A helper class that encapsulates some logic about certain property types. Each subclass handles
 * specific type and must implement {@link #getDefaulValue(UIProperty)} and
 * {@link #doCreatePropertyDescriptor(String, UIProperty)} methods.
 */
public abstract class PropertyTypeHandler
{

  /**
   * can this handler handle a property of the specified type?
   * 
   * @param propertyType
   *          the type of the property
   * @return yes/no
   */
  public abstract boolean canHandle(Class<?> propertyType);

  /**
   * Some {@link CellEditor}s represent the model property with different type in the UI, for
   * example String for double values.
   * 
   * @param cellEditorValue
   *          the cell editor value
   * @param propertyOwner
   *          the object that owns the property
   * @return the model value
   * @throws Exception 
   */
  public Object toModelValue(Object cellEditorValue, Object propertyOwner) throws Exception
  {
    return cellEditorValue;
  }

  /**
   * Some {@link CellEditor}s represent the model property with different type in the UI, for
   * example String for double values.
   * 
   * @param modelValue
   *          the model value
   * @param propertyOwner
   *          the object that owns the property
   * @return the cell editor value
   */
  public Object toCellEditorValue(Object modelValue, Object properyOwner)
  {
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

  /**
   * create the property descriptor for the specified property
   * 
   * @param propertyId
   * @param metadata
   * @return
   */
  final PropertyDescriptor createPropertyDescriptor(String propertyId,
      UIProperty metadata)
  {
    final PropertyDescriptor propertyDescriptor =
        doCreatePropertyDescriptor(propertyId, metadata);
    if (!metadata.category().isEmpty()) {
      propertyDescriptor.setCategory(metadata.category());
    }
    return propertyDescriptor;
  }

  /**
   * A handler for String type properties
   */
  public static final PropertyTypeHandler STRING = new PropertyTypeHandler()
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
  public static final PropertyTypeHandler BOOLEAN = new PropertyTypeHandler()
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
  public static final PropertyTypeHandler INTEGER = new PropertyTypeHandler()
  {

    @Override
    public Object getDefaulValue(UIProperty metadata)
    {
      return metadata.defaultInt();
    }

    protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
        UIProperty metadata)
    {
      return new SliderPropertyDescriptor(propertyId, metadata.name(), metadata
          .min(), metadata.max());
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
  public static final PropertyTypeHandler DOUBLE = new PropertyTypeHandler()
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
      return Number.class == propertyType
          || "double".equals(propertyType.getName());
    }

    public Object toModelValue(Object cellEditorValue, Object propertyOwner)
    {
      return Double.parseDouble((String) cellEditorValue);
    };

    public Object toCellEditorValue(Object modelValue, Object propertyOwner)
    {
      return modelValue + "";
    };
  };

  /**
   * A handler for {@link Unit} typed properties
   */
  public static final PropertyTypeHandler UNIT = new PropertyTypeHandler()
  {

    @Override
    public Object getDefaulValue(UIProperty metadata)
    {
      return Unit.ONE;
    }

    protected PropertyDescriptor doCreatePropertyDescriptor(String propertyId,
        UIProperty metadata)
    {
      // TODO: ideally this would be a drop down list, where user can select
      // values.
      // In that case toModel and toUI would need to deal with indices
      return new TextPropertyDescriptor(propertyId, metadata.name());
    }

    @Override
    public boolean canHandle(Class<?> propertyType)
    {
      return propertyType == Unit.class;
    }

    public Object toModelValue(Object cellEditorValue, Object propertyOwner)
    {
      // TODO: here we should have some form of conversion from string to Unit
      // (perhaps reuse the logic already in CsvParser class)
      return cellEditorValue;
    };

    public Object toCellEditorValue(Object modelValue, Object propertyOwner)
    {
      return modelValue + "";
    };
  };

  /**
   * A handler for {@link QuantityRange} typed properties. This property type handler only makes
   * sense when propertyOwner is an {@link IBaseQuantityCollection} instance
   */
  public static final PropertyTypeHandler QUANTITY_RANGE =
      new PropertyTypeHandler()
      {

        @Override
        public Object getDefaulValue(UIProperty metadata)
        {
          return null;
        }

        protected PropertyDescriptor doCreatePropertyDescriptor(
            String propertyId, UIProperty metadata)
        {
          return new TextPropertyDescriptor(propertyId, metadata.name());
        }

        @Override
        public boolean canHandle(Class<?> propertyType)
        {
          return propertyType == QuantityRange.class;
        }

        @SuppressWarnings(
        {"unchecked", "rawtypes"})
        public Object
            toModelValue(Object cellEditorValue, Object propertyOwner) throws Exception
        {

          QuantityRange newR = null;

          IBaseQuantityCollection<?> tt =
              (IBaseQuantityCollection<?>) propertyOwner;
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
                  newR =
                      new QuantityRange(Measure.valueOf(minV, collUnits),
                          Measure.valueOf(maxV, collUnits));
                }
              }
              catch (NumberFormatException fe)
              {
                Activator.logError(Status.ERROR,
                    "Failed to extract number range", fe);
                throw fe;
              }
            }
            else
            {
              String message = "Number format string not properly constructed:" + str
                  + " (should be 1:10)";
              Activator.logError(Status.ERROR,
                  message, null);
              throw new Exception(message);
            }
          }

          return newR;
        };

        @SuppressWarnings("unchecked")
        public Object
            toCellEditorValue(Object modelValue, Object propertyOwner)
        {
          final String str;
          QuantityRange<Quantity> range = (QuantityRange<Quantity>) modelValue;
          IBaseQuantityCollection<Quantity> qc =
              (IBaseQuantityCollection<Quantity>) propertyOwner;
          if (range != null)
          {
            str =
                "" + range.getMinimum().longValue(qc.getUnits()) + " : "
                    + range.getMaximum().longValue(qc.getUnits());
          }
          else
          {
            str = " : ";
          }

          return str;
        };
      };

  /**
   * A handler for {@link Point2D} typed properties
   */
  public static final PropertyTypeHandler POINT2D = new PropertyTypeHandler()
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
      return propertyType == Point2D.class;
    }

    public Object toModelValue(Object cellEditorValue, Object propertyOwner) throws Exception
    {

      Point2D newL = null;

      // try to get a location from the string
      String str = (String) cellEditorValue;
      if (str.length() > 0)
      {
        // ok, split it up
        String[] bits = str.split(":");
        if (bits.length == 2)
        {
          String xStr = bits[0].trim();
          String yStr = bits[1].trim();
          try
          {
            double x = Double.parseDouble(xStr);
            double y = Double.parseDouble(yStr);

            newL = new Point2D.Double(y, x);
          }
          catch (NumberFormatException fe)
          {
            Activator.logError(Status.ERROR,
                "Failed to extract number location", fe);
            throw fe;
          }
        }
        else
        {
          String message = "Number format string not properly constructed:" + str
              + " (should be 1:10)";
          Activator.logError(Status.ERROR,
              message, null);
          throw new Exception(message);
        }
      }

      return newL;
    };

    public Object toCellEditorValue(Object modelValue, Object propertyOwner)
    {
      Point2D location = (Point2D) modelValue;
      return location.getY() + " : " + location.getX();
    };
  };
}
