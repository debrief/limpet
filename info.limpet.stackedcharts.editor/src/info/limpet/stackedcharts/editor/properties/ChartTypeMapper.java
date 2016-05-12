package info.limpet.stackedcharts.editor.properties;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

public class ChartTypeMapper extends AbstractTypeMapper
{

  @Override
  public Class mapType(Object object)
  {
    if (object instanceof EditPart)
    {
      return ((EditPart) object).getModel().getClass();
    }
    return super.mapType(object);
  }

}
