package info.limpet.stackedcharts.editor.parts;

import java.util.List;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.graphics.Image;

import info.limpet.stackedcharts.editor.Activator;
import info.limpet.stackedcharts.editor.figures.VerticalLabel;
import info.limpet.stackedcharts.model.DependentAxis;

public class AxisEditPart extends AbstractGraphicalEditPart
{

  private static final Image AXIS_IMG = Activator.imageDescriptorFromPlugin(
      Activator.PLUGIN_ID, "icons/axis.png").createImage();

  private RectangleFigure datasetsPane;

  private VerticalLabel axisNameLabel;

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure figure = new RectangleFigure();
    figure.setOutline(false);
    FlowLayout layoutManager = new FlowLayout();
    layoutManager.setHorizontal(true);
    layoutManager.setStretchMinorAxis(true);
    figure.setLayoutManager(layoutManager);

    datasetsPane = new RectangleFigure();
    datasetsPane.setOutline(false);
    layoutManager = new FlowLayout();
    layoutManager.setHorizontal(true);
    layoutManager.setStretchMinorAxis(true);
    datasetsPane.setLayoutManager(layoutManager);
    figure.add(datasetsPane);

    figure.add(new Label(AXIS_IMG));
    axisNameLabel = new VerticalLabel();
    figure.add(axisNameLabel);

    return figure;
  }

  @Override
  protected void refreshVisuals()
  {
    axisNameLabel.setText(((DependentAxis) getModel()).getName());
  }

  @Override
  public IFigure getContentPane()
  {
    return datasetsPane;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new NonResizableEditPolicy());
  }

  @Override
  protected List getModelChildren()
  {
    return ((DependentAxis) getModel()).getDatasets();
  }
}
