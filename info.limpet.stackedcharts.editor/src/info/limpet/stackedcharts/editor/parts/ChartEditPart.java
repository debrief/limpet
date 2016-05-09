package info.limpet.stackedcharts.editor.parts;

import info.limpet.stackedcharts.editor.figures.ChartFigure;
import info.limpet.stackedcharts.model.Chart;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class ChartEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected IFigure createFigure()
  {
    return new ChartFigure();
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected List getModelChildren()
  {
//    return ((Chart) getModel()).getAxes();
    return super.getModelChildren();
  }

  @Override
  protected void refreshVisuals()
  {
    String name = ((Chart) getModel()).getName();
    ((ChartFigure) getFigure()).setName(name);
  }
}
