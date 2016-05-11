package info.limpet.stackedcharts.editor.parts;

import info.limpet.stackedcharts.model.ChartSet;

import java.util.List;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class ChartSetEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure rectangle = new RectangleFigure();
    rectangle.setOutline(false);
    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setMinorSpacing(20);
    flowLayout.setHorizontal(false);
    rectangle.setLayoutManager(flowLayout);
    return rectangle;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected List getModelChildren()
  {
    return ((ChartSet) getModel()).getCharts();
  }
}
