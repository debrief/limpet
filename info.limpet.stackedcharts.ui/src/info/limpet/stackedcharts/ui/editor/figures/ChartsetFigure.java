package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class ChartsetFigure extends DirectionalShape
{
  private static volatile Font boldFont;
  private DirectionalLabel chartsetHeader;

  public ChartsetFigure(ActionListener addChartHandler)
  {
    add(new Label(StackedchartsImages.getImage(
        StackedchartsImages.DESC_CHARTSET)));
    chartsetHeader = new DirectionalLabel();
    chartsetHeader.setText("Chart Set");
    chartsetHeader.setTextAlignment(PositionConstants.TOP);
    if (boldFont == null)
    {
      FontData fontData = Display.getCurrent().getActiveShell().getFont()
          .getFontData()[0];
      boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(),
          fontData.getHeight(), SWT.BOLD));
    }
    chartsetHeader.setFont(boldFont);
    add(chartsetHeader);

    Button button = new Button(StackedchartsImages.getImage(
        StackedchartsImages.DESC_ADD));
    button.setToolTip(new Label("Add new chart"));
    button.addActionListener(addChartHandler);
    add(button);

  }

  public void setVertical(boolean vertical)
  {
    super.setVertical(vertical);
    chartsetHeader.setVertical(vertical);
  }

}
