package info.limpet.stackedcharts.editor.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ChartFigure extends RectangleFigure
{
  private Label chartNameLabel;

  public ChartFigure()
  {
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
    chartNameLabel.setText("Chart " + name);
  }
}
