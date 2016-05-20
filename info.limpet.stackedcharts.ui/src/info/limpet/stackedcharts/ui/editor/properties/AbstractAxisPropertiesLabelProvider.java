package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.AbstractAxis;

public class AbstractAxisPropertiesLabelProvider extends CorePropertiesLabelProvider
{
  protected String getMe(Object element)
  {
    if (element instanceof AbstractAxis) {
      return "Axis Properties";
    }
    else
    {
      return null;
    }
  }
}
