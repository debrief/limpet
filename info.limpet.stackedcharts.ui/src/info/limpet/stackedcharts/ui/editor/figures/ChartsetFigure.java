package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.FlowLayout;
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
  private Label chartsetHeader;

  public ChartsetFigure()
  {
    setOutline(false);
    FlowLayout layout = new FlowLayout(false);
    layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
    setLayoutManager(layout);

    chartsetHeader = new Label();
    chartsetHeader.setText("Chart Set");

    chartsetHeader.setTextAlignment(PositionConstants.TOP);
    if (boldFont == null)
    {
      FontData fontData = Display.getCurrent().getActiveShell().getFont().getFontData()[0];
      boldFont =
          new Font(Display.getCurrent(), new FontData(fontData.getName(),
              fontData.getHeight(), SWT.BOLD));
    }

    chartsetHeader.setFont(boldFont);
    add(chartsetHeader);
  }


}
