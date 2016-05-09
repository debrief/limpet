package info.limpet.stackedcharts.editor.figures;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;

public class ChartFigure extends RectangleFigure
{
  private Label chartNameLabel;

  public ChartFigure()
  {
    setLayoutManager(new FlowLayout());
    chartNameLabel = new Label();
    add(chartNameLabel);
  }

  public void setName(String name)
  {
    chartNameLabel.setText("Chart " + name);
  }
}
