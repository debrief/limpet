package info.limpet.stackedcharts.ui.editor.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

abstract public class GenericPropertySection<Element extends EObject> extends AbstractPropertySection
{

  protected Element element;

  public GenericPropertySection()
  {
  }
  
  
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage aTabbedPropertySheetPage)
  {
    super.createControls(parent, aTabbedPropertySheetPage);

    parent.setLayout(new GridLayout(2, false));

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    createMyProperties(parent, factory);
  }
  
  abstract protected void createMyProperties(Composite parent,
      TabbedPropertySheetWidgetFactory factory);


  @SuppressWarnings("unchecked")
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);

    if (selection instanceof StructuredSelection)
    {
      Object selected = ((StructuredSelection) selection).getFirstElement();
      if (selected instanceof EditPart)
      {
        selected = ((EditPart) selected).getModel();
        if (selected instanceof EObject)
        {
          element = (Element) selected;
          // ideally we should also attach listeners here to the chart, so that if name changes,
          // "refresh" would be invoked
        }
      }

    }
  }

  @Override
  abstract public void refresh();
}