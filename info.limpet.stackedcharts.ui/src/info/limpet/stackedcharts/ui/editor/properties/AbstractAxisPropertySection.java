package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl;
import info.limpet.stackedcharts.ui.editor.commands.ChartCommand;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class AbstractAxisPropertySection extends AbstractPropertySection
{

  private AbstractAxis axis;
  private Text axisNameText;

  public AbstractAxisPropertySection()
  {
  }

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage aTabbedPropertySheetPage)
  {
    super.createControls(parent, aTabbedPropertySheetPage);

    parent.setLayout(new GridLayout(2, false));

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    factory.createLabel(parent, "Name:");
    axisNameText = factory.createText(parent, null);
    axisNameText.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusLost(FocusEvent e)
      {
        if (!axisNameText.getText().equals(axis.getName()))
        {
          CommandStack commandStack =
              (CommandStack) getPart().getAdapter(CommandStack.class);

          commandStack.execute(new ChartCommand(axis, StackedchartsPackageImpl
              .init().getAbstractAxis_Name(), axisNameText.getText()));
        }
      }
    });
    GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).applyTo(axisNameText);
  }

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
        if (selected instanceof AbstractAxis)
        {
          axis = (AbstractAxis) selected;
          // ideally we should also attach listeners here to the chart, so that if name changes,
          // "refresh" would be invoked
        }
      }

    }
  }

  @Override
  public void refresh()
  {
    axisNameText.setText(axis.getName());
  }
}