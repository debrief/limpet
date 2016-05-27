package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.ui.editor.figures.ChartsetFigure;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.SWT;

/**
 * Represents header of a {@link ChartSet} object
 */
public class ChartSetHeaderEditPart extends AbstractGraphicalEditPart
{

  protected IndependentAxis getAxis()
  {
    return (IndependentAxis) getModel();
  }

  @Override
  protected IFigure createFigure()
  {

    return new ChartsetFigure();
  }

  @Override
  protected void refreshVisuals()
  {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;
    gridData.horizontalAlignment = SWT.CENTER;
    gridData.horizontalSpan = 10;
    gridData.verticalAlignment = SWT.FILL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
  }

  @Override
  public Object getModel()
  {
    return ((ChartSetEditPart.ChartSetWrapper) super.getModel()).getcChartSet();
  }

}
