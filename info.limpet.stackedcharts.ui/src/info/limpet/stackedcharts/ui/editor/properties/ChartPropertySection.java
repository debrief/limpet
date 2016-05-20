package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl;
import info.limpet.stackedcharts.ui.editor.commands.ChartCommand;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class ChartPropertySection extends GenericPropertySection<Chart>
{
  private Text chartNameText;

  protected void createMyProperties(Composite parent,
      TabbedPropertySheetWidgetFactory factory)
  {
    factory.createLabel(parent, "Name:");
    chartNameText = factory.createText(parent, null);
    chartNameText.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusLost(FocusEvent e)
      {
        if (!chartNameText.getText().equals(element.getName()))
        {
          CommandStack commandStack =
              (CommandStack) getPart().getAdapter(CommandStack.class);

          commandStack.execute(new ChartCommand(element, StackedchartsPackageImpl
              .init().getChart_Name(), chartNameText.getText()));
        }
      }
    });
    GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).applyTo(chartNameText);
  }

  @Override
  public void refresh()
  {
    chartNameText.setText(element.getName());
  }
}