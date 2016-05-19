package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class StackedChartsEditPartFactory implements EditPartFactory
{

  @Override
  public EditPart createEditPart(EditPart context, Object model)
  {
    EditPart editPart = null;

    if (model instanceof ChartSet)
    {
      editPart = new ChartSetEditPart();
    }
    else if (model instanceof Chart)
    {
      editPart = new ChartEditPart();
    }
    else if (model instanceof ChartEditPart.ChartPanePosition)
    {
      editPart = new ChartPaneEditPart();
    }
    else if (model instanceof DependentAxis)
    {
      editPart = new AxisEditPart();
    }
    else if (model instanceof Dataset)
    {
      editPart = new DatasetEditPart();
    }

    if (editPart != null)
    {
      editPart.setModel(model);
    }

    return editPart;
  }

}
