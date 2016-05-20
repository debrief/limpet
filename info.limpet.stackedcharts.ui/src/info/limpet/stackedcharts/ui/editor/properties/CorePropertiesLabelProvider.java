package info.limpet.stackedcharts.ui.editor.properties;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public abstract class CorePropertiesLabelProvider extends LabelProvider
{
  @Override
  public final String getText(Object element)
  {
    if (element instanceof StructuredSelection)
    {
      element = ((StructuredSelection) element).getFirstElement();
    }
    if (element instanceof EditPart)
    {
      element = ((EditPart) element).getModel();
    }
    
    // ok, see if this is us
    final String testMe = getMe(element);
    if (testMe != null)
    {
      return testMe;
    }
    else
    {
      return super.getText(element);
    }
  }

  protected abstract String getMe(Object element);
}
