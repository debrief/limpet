package info.limpet.stackedcharts.editor.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ArrowFigure extends Figure
{
  public ArrowFigure()
  {
    setPreferredSize(20, -1);
    Color color = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
    setBackgroundColor(color);
    setForegroundColor(color);
  }

  @Override
  protected void paintFigure(Graphics graphics)
  {
    super.paintFigure(graphics);
    Rectangle clientArea = getClientArea();
    Point top = clientArea.getTop();
    graphics.drawLine(clientArea.getBottom(), top);

    PointList points = new PointList();
    points.addPoint(top);
    points.addPoint(top.getCopy().translate(4, 10));
    points.addPoint(top.getCopy().translate(-4, 10));
    graphics.fillPolygon(points);
  }
}
