package info.limpet.stackedcharts.ui.editor.figures;

import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class ChartsetFigure extends RectangleFigure
{
  private static volatile Font boldFont;
  private DirectionalLabel chartsetHeader;

  public ChartsetFigure(ActionListener addChartHandler)
  {
    setOutline(false);
    GridLayout layout = new GridLayout();
    setLayoutManager(layout);

    Label icon = new Label(StackedchartsImages.getImage(
        StackedchartsImages.DESC_CHARTSET));
    add(icon);

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
    ((GridLayout) getLayoutManager()).numColumns = vertical ? 1 : getChildren()
        .size();
    chartsetHeader.setVertical(vertical);

    invalidate();
  }

}
