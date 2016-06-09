package info.limpet.stackedcharts.ui.editor.figures;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class ChartFigure extends RectangleFigure
{
  private final Label chartNameLabel;
  private final JFreeChartFigure chartFigure;
  private static volatile Font boldFont;

  public ChartFigure(final Chart chart, final ActionListener deleteListener)
  {
    setPreferredSize(-1, 200);
    setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    setOutline(false);
    setLayoutManager(new BorderLayout());
    final RectangleFigure rectangleFigure = new RectangleFigure();

    rectangleFigure.setOutline(false);
    final FlowLayout layout = new FlowLayout(true);
    layout.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
    layout.setMajorAlignment(OrderedLayout.ALIGN_CENTER);
    rectangleFigure.setLayoutManager(layout);

    chartNameLabel = new Label();
    rectangleFigure.add(chartNameLabel);
    final Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove this chart from the chart set"));
    button.addActionListener(deleteListener);
    rectangleFigure.add(button);

    add(rectangleFigure, BorderLayout.TOP);

    chartFigure = new JFreeChartFigure(chart);
    add(chartFigure, BorderLayout.CENTER);
  }

  @Override
  protected void paintClientArea(final Graphics graphics)
  {
    super.paintClientArea(graphics);
    graphics.setForegroundColor(Display.getDefault().getSystemColor(
        SWT.COLOR_DARK_GRAY));

    final Rectangle clientArea = getClientArea();
    graphics.drawLine(clientArea.getBottomLeft().getTranslated(0, -1),
        clientArea.getBottomRight().getTranslated(0, -1));
  }

  public void setName(final String name)
  {
    chartNameLabel.setIcon(StackedchartsImages.getImage(StackedchartsImages.DESC_CHART));
    chartNameLabel.setText( name);
    // cache font for AxisNameFigure
    if (boldFont == null)
    {
      final FontData fontData = chartNameLabel.getFont().getFontData()[0];
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

}
