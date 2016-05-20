package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.AbstractAxis;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class AbstractAxisPropertiesLabelProvider extends LabelProvider
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
    
    if (element instanceof AbstractAxis) {
      return "Axis Properties";
    }
    
    return super.getText(element);
  }
}
