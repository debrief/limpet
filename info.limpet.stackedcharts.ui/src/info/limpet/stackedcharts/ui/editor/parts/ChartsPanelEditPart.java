package info.limpet.stackedcharts.ui.editor.parts;

import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.parts.ChartSetEditPart.ChartsWrapper;

/**
 * Represents the list of the charts contained in a {@link ChartSet}
 */
public class ChartsPanelEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure rectangle = new RectangleFigure();
    rectangle.setOutline(false);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 10;
    layout.verticalSpacing = 10;
    rectangle.setLayoutManager(layout);
    rectangle.setBackgroundColor(Display.getDefault().getSystemColor(
        SWT.COLOR_WIDGET_BACKGROUND));
    return rectangle;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected void refreshVisuals()
  {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List getModelChildren()
  {
    return ( getModel()).getCharts();
  }
  
  @Override
  public ChartsWrapper getModel()
  {
    return (ChartsWrapper)super.getModel();
  }
}
