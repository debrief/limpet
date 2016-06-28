package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;

/**
 * A {@link DirectionalShape} that has a {@link Label} and an icon on the left.
 *
 */
public class DirectionalIconLabel extends DirectionalShape
{
  private final DirectionalLabel label;

  public DirectionalIconLabel(Image icon)
  {
    add(new Label(icon));
    this.label = new DirectionalLabel();
    add(getLabel());
  }

  public DirectionalLabel getLabel()
  {
    return label;
  }

  @Override
  public void setVertical(boolean vertical)
  {
    super.setVertical(vertical);
    label.setVertical(vertical);
  }
}
