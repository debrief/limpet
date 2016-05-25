package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class ChartFigure extends RectangleFigure
{
  private Label chartNameLabel;
  private static volatile Font boldFont;

  public ChartFigure()
  {
    setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    setOutline(false);
    setLayoutManager(new BorderLayout());
    chartNameLabel = new Label();
    add(chartNameLabel, BorderLayout.TOP);
    Label previewLabel = new Label("Preview");
    previewLabel.setForegroundColor(Display.getDefault().getSystemColor(
        SWT.COLOR_RED));
    add(previewLabel, BorderLayout.CENTER);
  }

  public void setName(String name)
  {
    chartNameLabel.setText("Chart: " + name);
     // cache font for AxisNameFigure
    if (boldFont == null)
    {
      FontData fontData = chartNameLabel.getFont().getFontData()[0];
      boldFont =
          new Font(Display.getCurrent(), new FontData(fontData.getName(),
              fontData.getHeight(), SWT.BOLD));
    }
    chartNameLabel.setFont(boldFont);
  }

  @Override
  protected void paintClientArea(Graphics graphics)
  {
    super.paintClientArea(graphics);
    graphics.setForegroundColor(Display.getDefault().getSystemColor(
        SWT.COLOR_DARK_GRAY));

    Rectangle clientArea = getClientArea();
    graphics.drawLine(clientArea.getBottomLeft().getTranslated(0, -1),
        clientArea.getBottomRight().getTranslated(0, -1));
  }
}
