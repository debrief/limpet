package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;

public class VerticalLabel extends Label
{

  @Override
  protected void paintFigure(Graphics graphics)
  {
    Image image = ImageUtilities.createRotatedImageOfString(getSubStringText(),
        getFont(), getForegroundColor(), getBackgroundColor());
    graphics.drawImage(image, new Point(getTextLocation()).translate(
        getLocation()));
    image.dispose();
  }

  @Override
  protected Dimension calculateLabelSize(Dimension txtSize)
  {
    return super.calculateLabelSize(txtSize).transpose();
  }

}
