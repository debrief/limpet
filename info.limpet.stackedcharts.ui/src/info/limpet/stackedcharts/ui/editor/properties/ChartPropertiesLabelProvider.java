package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.Chart;

public class ChartPropertiesLabelProvider extends CorePropertiesLabelProvider
{
  
  protected String getMe(Object element)
  {
    if (element instanceof Chart) {
      return "Chart Properties";
    }
    else
    {
      return null;
    }
  }
}
