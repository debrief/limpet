package info.limpet.stackedcharts.editor.properties;

import info.limpet.stackedcharts.model.Chart;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class ChartPropertiesLabelProvider extends LabelProvider
{
  @Override
  public String getText(Object element)
  {    
    if (element instanceof StructuredSelection) {
      element = ((StructuredSelection)element).getFirstElement();
    }
    if (element instanceof EditPart) {
      element = ((EditPart)element).getModel();
    }
    
    if (element instanceof Chart) {
      return "Chart Properties";
    }
    
    return super.getText(element);
  }
}
