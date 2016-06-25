package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.Shape;

public class DirectionalShape extends Shape
{

  private boolean vertical;

  public DirectionalShape()
  {
    super.setLayoutManager(new DirectionFlowLayout());
  }

  @Override
  protected void fillShape(Graphics graphics)
  {
  }

  @Override
  protected void outlineShape(Graphics graphics)
  {
  }

  public boolean isVertical()
  {
    return vertical;
  }

  public void setVertical(boolean vertical)
  {
    this.vertical = vertical;
    ((FlowLayout) getLayoutManager()).setHorizontal(!vertical);
  }

  @Override
  public void setLayoutManager(LayoutManager manager)
  {
    throw new UnsupportedOperationException("Layout manager is read-only");
  }
}
