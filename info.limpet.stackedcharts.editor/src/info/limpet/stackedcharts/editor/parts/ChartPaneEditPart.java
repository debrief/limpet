package info.limpet.stackedcharts.editor.parts;

import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import info.limpet.stackedcharts.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.model.Chart;

public class ChartPaneEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure figure = new RectangleFigure();
    FlowLayout layoutManager = new FlowLayout();
    layoutManager.setHorizontal(true);
    layoutManager.setStretchMinorAxis(true);
    figure.setLayoutManager(layoutManager);
    return figure;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected void refreshVisuals()
  {
    ChartEditPart.ChartPanePosition pos = (ChartPanePosition) getModel();
    IFigure figure = getFigure();
    if (pos == ChartPanePosition.LEFT)
    {
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
          BorderLayout.LEFT);
    }
    else
    {
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
          BorderLayout.RIGHT);
    }
  }

  @Override
  protected List getModelChildren()
  {

    Chart chart = (Chart) getParent().getModel();
    ChartEditPart.ChartPanePosition pos = (ChartPanePosition) getModel();
    return pos == ChartPanePosition.LEFT ? chart.getMinAxes() : chart
        .getMaxAxes();
  }
}
