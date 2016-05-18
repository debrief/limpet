package info.limpet.stackedcharts.editor.parts;

import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.ChartSet;

public class ChartSetEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure rectangle = new RectangleFigure();
    rectangle.setBorder(new MarginBorder(10));
    rectangle.setOutline(false);
    GridLayout layout = new GridLayout();
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
  protected List getModelChildren()
  {
    return ((ChartSet) getModel()).getCharts();
  }
}
