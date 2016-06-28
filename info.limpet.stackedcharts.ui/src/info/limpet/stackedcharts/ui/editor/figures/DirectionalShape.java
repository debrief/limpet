package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.Shape;

/**
 * A Draw2D container {@link Shape} that orders its children in row or in a column based on the
 * {@link #isVertical()} property. It uses internal layout, clients should not attempt to set new
 * {@link LayoutManager}.
 */
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

  /**
   * @see #setVertical(boolean)
   * @return
   */
  public boolean isVertical()
  {
    return vertical;
  }

  /**
   * @param vertical
   *          when <code>true</code> children will be laid out from bottom to top
   */
  public void setVertical(boolean vertical)
  {
    this.vertical = vertical;
    ((FlowLayout) getLayoutManager()).setHorizontal(!vertical);
  }

  /**
   * Not intended to be called
   */
  @Override
  public void setLayoutManager(LayoutManager manager)
  {
    throw new UnsupportedOperationException("Layout manager is read-only");
  }
}
