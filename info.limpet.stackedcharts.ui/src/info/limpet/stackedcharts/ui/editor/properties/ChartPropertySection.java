package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class ChartPropertySection extends GenericPropertySection<Chart>
{
  @Override
  final protected
      List<info.limpet.stackedcharts.ui.editor.properties.GenericPropertySection.CombinedProperty>
      getProperties(EObject element)
  {
    final List<CombinedProperty> props = new ArrayList<CombinedProperty>();
    props.add(new CombinedProperty(StackedchartsPackageImpl.init()
        .getChart_Name(), element));
    return props;
  }
}