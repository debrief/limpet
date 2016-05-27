package info.limpet.stackedcharts.ui.editor.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.graphics.Image;

import info.limpet.stackedcharts.ui.editor.Activator;

public class StylingEditPart extends AbstractGraphicalEditPart
{

  /**
   * Standard Eclipse icon, source: http://eclipse-icons.i24.cc/eclipse-icons-07.html
   */
  private static final Image IMAGE = Activator.imageDescriptorFromPlugin(
      Activator.PLUGIN_ID, "icons/font.gif").createImage();

  @Override
  protected IFigure createFigure()
  {
    Label label = new Label(IMAGE);
    label.setToolTip(new Label("Click to view style properties"));
    return label;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
  }

}
