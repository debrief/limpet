package info.limpet.stackedcharts.ui.editor.figures;

import info.limpet.stackedcharts.model.Chart;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
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
  private JFreeChartFigure chartFigure;
  private static volatile Font boldFont;

  public ChartFigure(Chart chart, ActionListener deleteListener)
  {
    setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    setOutline(false);
    setLayoutManager(new BorderLayout());
    RectangleFigure rectangleFigure = new RectangleFigure();

    rectangleFigure.setOutline(false);
    FlowLayout layout = new FlowLayout(true);
    layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
    layout.setMajorAlignment(FlowLayout.ALIGN_CENTER);
    rectangleFigure.setLayoutManager(layout);

    chartNameLabel = new Label();
    rectangleFigure.add(chartNameLabel);
    Button button = new Button("X");
    button.setToolTip(new Label("Remove this chart from the chart set"));
    button.addActionListener(deleteListener);
    rectangleFigure.add(button);

    add(rectangleFigure, BorderLayout.TOP);

    chartFigure = new JFreeChartFigure(chart);
    add(chartFigure, BorderLayout.CENTER);
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

  public void updateChart()
  {
    chartFigure.repaint();
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
