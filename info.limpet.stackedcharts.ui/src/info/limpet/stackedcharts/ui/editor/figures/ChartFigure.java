package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class ChartFigure extends RectangleFigure
{
  private final DirectionalLabel chartNameLabel;
  private final JFreeChartFigure chartFigure;
  private static volatile Font boldFont;
  private final RectangleFigure titleFigure;

  public ChartFigure(final Chart chart, final ActionListener deleteListener)
  {
    setPreferredSize(-1, 200);
    setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    setOutline(false);
    BorderLayout topLayout = new BorderLayout();
    setLayoutManager(topLayout);
    titleFigure = new RectangleFigure();

    titleFigure.setOutline(false);
    final GridLayout titleLayout = new GridLayout();
    titleFigure.setLayoutManager(titleLayout);

    Label icon = new Label();
    icon.setIcon(StackedchartsImages.getImage(StackedchartsImages.DESC_CHART));
    titleFigure.add(icon);

    chartNameLabel = new DirectionalLabel();
    titleFigure.add(chartNameLabel);
    final Button button = new Button(StackedchartsImages.getImage(
        StackedchartsImages.DESC_DELETE));
    button.setToolTip(new Label("Remove this chart from the chart set"));
    button.addActionListener(deleteListener);
    titleFigure.add(button);

    add(titleFigure);

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
    chartNameLabel.setText(name);
    // cache font for AxisNameFigure
    if (boldFont == null)
    {
      final FontData fontData = chartNameLabel.getFont().getFontData()[0];
      boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(),
          fontData.getHeight(), SWT.BOLD));
    }
    chartNameLabel.setFont(boldFont);
  }

  public void updateChart()
  {
    chartFigure.repaint();
  }

  public void setVertical(boolean vertical)
  {
    BorderLayout topLayout = (BorderLayout) getLayoutManager();
    GridLayout titleLayout = (GridLayout) titleFigure.getLayoutManager();
    if (vertical)
    {
      topLayout.setConstraint(titleFigure, BorderLayout.TOP);
      titleLayout.numColumns = titleFigure.getChildren().size();
      chartNameLabel.setVertical(false);
    }
    else
    {
      topLayout.setConstraint(titleFigure, BorderLayout.LEFT);
      titleLayout.numColumns = 1;
      chartNameLabel.setVertical(true);
    }

    topLayout.invalidate();
    titleLayout.invalidate();

    repaint();

  }
}
