package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.editor.commands.ChangeChartNameCommand;

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

public class ChartPropertySection extends AbstractPropertySection
{

  private Chart chart;
  private Text chartNameText;

  public ChartPropertySection()
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
    chartNameText = factory.createText(parent, null);
    chartNameText.addFocusListener(new FocusAdapter()
    {

      @Override
      public void focusLost(FocusEvent e)
      {
        if (!chartNameText.getText().equals(chart.getName()))
        {
          CommandStack commandStack =
              (CommandStack) getPart().getAdapter(CommandStack.class);

          commandStack.execute(new ChangeChartNameCommand(chart, chartNameText
              .getText()));
        }
      }
    });
    GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).applyTo(chartNameText);
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
        if (selected instanceof Chart)
        {
          chart = (Chart) selected;
          // ideally we should also attach listeners here to the chart, so that if name changes,
          // "refresh" would be invoked
        }
      }

    }
  }

  @Override
  public void refresh()
  {
    chartNameText.setText(chart.getName());
  }
}