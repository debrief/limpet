package info.limpet.stackedcharts.ui.editor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.ChartSet;

public class ChartSetEditPart extends AbstractGraphicalEditPart
{

  /**
   * Wraps the charts, so that they are displayed in a separate container and not together with the
   * shared axis.
   */
  public static class ChartsWrapper
  {
    private final List charts;

    public ChartsWrapper(List charts)
    {
      this.charts = charts;
    }

    public List getCharts()
    {
      return charts;
    }
  }

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure rectangle = new RectangleFigure();
    rectangle.setOutline(false);
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 10;
    gridLayout.marginWidth = 10;
    rectangle.setLayoutManager(gridLayout);
    rectangle.setBackgroundColor(Display.getDefault().getSystemColor(
        SWT.COLOR_WIDGET_BACKGROUND));
    return rectangle;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @SuppressWarnings(
  {"rawtypes", "unchecked"})
  @Override
  protected List getModelChildren()
  {
    // 2 model children - the charts, displayed in a separate container and the shared (independent
    // axis) shown on the bottom
    List modelChildren = new ArrayList<>();
    ChartSet chartSet = (ChartSet) getModel();
    modelChildren.add(new ChartsWrapper(chartSet.getCharts()));
    modelChildren.add(chartSet.getSharedAxis());
    return modelChildren;
  }
}
