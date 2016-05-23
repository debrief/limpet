package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl;

import java.util.ArrayList;
import java.util.List;

public class AbstractAxisPropertySection extends GenericPropertySection<AbstractAxis>
{
  protected List<CombinedProperty> getProperties()
  {
    List<CombinedProperty> props = new ArrayList<CombinedProperty>();
    
    // build up our list of properties
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Name()));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Color()));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Font()));

    // hey, try a boolean editor!
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Direction()));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_AutoScale()));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_AxisType()));
    
    return props;
  }
  
}