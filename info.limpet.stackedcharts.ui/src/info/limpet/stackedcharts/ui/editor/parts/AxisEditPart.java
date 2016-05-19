package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;
import info.limpet.stackedcharts.ui.editor.figures.VerticalLabel;

import java.util.List;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

public class AxisEditPart extends AbstractGraphicalEditPart
{

  private RectangleFigure datasetsPane;

  private VerticalLabel axisNameLabel;

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure figure = new RectangleFigure();
    figure.setOutline(false);
    GridLayout layoutManager = new GridLayout(3, false);
    // zero margin, in order to connect the dependent axes to the shared one
    layoutManager.marginHeight = 0;
    layoutManager.marginWidth = 0;
    figure.setLayoutManager(layoutManager);

    datasetsPane = new RectangleFigure();
    datasetsPane.setOutline(false);
    FlowLayout datasetsPaneLayout = new FlowLayout();
    datasetsPaneLayout.setHorizontal(true);
    datasetsPaneLayout.setStretchMinorAxis(true);
    datasetsPane.setLayoutManager(datasetsPaneLayout);
    figure.add(datasetsPane);

    ArrowFigure arrowFigure = new ArrowFigure();
    layoutManager.setConstraint(arrowFigure, new GridData(GridData.FILL,
        GridData.FILL, false, true));
    figure.add(arrowFigure);

    axisNameLabel = new VerticalLabel();
    layoutManager.setConstraint(axisNameLabel, new GridData(GridData.FILL,
        GridData.FILL, true, true));
    figure.add(axisNameLabel);

    return figure;
  }

  @Override
  protected void refreshVisuals()
  {
    axisNameLabel.setText(((DependentAxis) getModel()).getName());

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        new GridData(GridData.CENTER, GridData.FILL, false, true));
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
