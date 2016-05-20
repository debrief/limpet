package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class AbstractAxisPropertySection extends GenericPropertySection<AbstractAxis>
{
  protected List<CombinedProperty> getProperties(EObject element)
  {
    List<CombinedProperty> props = new ArrayList<CombinedProperty>();
    
    // build up our list of properties
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Name(), element));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Color(), element));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Font(), element));

    // hey, try a boolean editor!
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_Direction(), element));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_AutoScale(), element));
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getAbstractAxis_AxisType(), element));
    
    return props;
  }
  
}