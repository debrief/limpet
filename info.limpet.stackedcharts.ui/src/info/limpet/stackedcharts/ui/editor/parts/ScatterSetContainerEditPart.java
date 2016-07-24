package info.limpet.stackedcharts.ui.editor.parts;

import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalLabel;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;
import info.limpet.stackedcharts.ui.editor.figures.ScatterSetContainerFigure;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ScatterSetContainer;

public class ScatterSetContainerEditPart extends AbstractGraphicalEditPart
{

  private ScatterSetContainerFigure scatterSetContainerFigure;
  private DirectionalLabel titleLabel;

  @Override
  protected IFigure createFigure()
  {
    DirectionalShape figure = new DirectionalShape();
    titleLabel = new DirectionalLabel();
    figure.add(titleLabel);
    titleLabel.setIcon(PlatformUI.getWorkbench().getSharedImages().getImage(
        ISharedImages.IMG_OBJ_ELEMENT));
    titleLabel.setText("Scattered sets");
    
    scatterSetContainerFigure = new ScatterSetContainerFigure();
    figure.add(scatterSetContainerFigure);
    return figure;
  }

  @Override
  public IFigure getContentPane()
  {
    return scatterSetContainerFigure;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected List getModelChildren()
  {
    return (ScatterSetContainer) getModel();
  }

  @Override
  protected void refreshVisuals()
  {
    final DirectionalShape figure =
        (DirectionalShape) getFigure();

    ChartSet chartSet = ((Chart) getParent().getModel()).getParent();
    boolean vertical = chartSet.getOrientation() == Orientation.VERTICAL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, vertical
        ? BorderLayout.BOTTOM : BorderLayout.RIGHT);

    figure.setVertical(!vertical);
    scatterSetContainerFigure.setVertical(!vertical);
    titleLabel.setVertical(!vertical);
  }
}
