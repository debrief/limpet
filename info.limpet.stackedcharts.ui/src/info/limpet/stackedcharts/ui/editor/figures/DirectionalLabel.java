package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

/**
 * The label is a standard horizontal label by default and it can change orientation to vertical
 *
 */
public class DirectionalLabel extends Label
{

  private boolean vertical;

//  private DirectionalLabel()
//  {
//  }
  
  @Override
  protected void paintFigure(Graphics graphics)
  {
    if (vertical)
    {

      String subStringText = getSubStringText();
      if (!subStringText.isEmpty())
      {
        Image image = ImageUtilities.createRotatedImageOfString(subStringText,
            getFont(), getForegroundColor(), getBackgroundColor());
        graphics.drawImage(image, new Point(getTextLocation()).translate(
            getLocation()));
        image.dispose();
      }
    }
    else
    {
      super.paintFigure(graphics);
    }
  }

  @Override
  protected Dimension calculateLabelSize(Dimension txtSize)
  {
    Dimension labelSize = super.calculateLabelSize(txtSize);
    if (vertical)
    {
      labelSize = labelSize.transpose();
    }
    return labelSize;
  }

  public void setVertical(boolean vertical)
  {
    this.vertical = vertical;
    repaint();
  }

}
