package info.limpet.stackedcharts.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class StackedChartsEditPartFactory implements EditPartFactory
{

  @Override
  public EditPart createEditPart(EditPart context, Object model)
  {
    EditPart editPart = null;
    
    if (model instanceof ChartSet) {
      editPart = new ChartSetEditPart();
    } else if (model instanceof Chart) {
      editPart = new ChartEditPart();
    }

    
    if (editPart != null) {
      editPart.setModel(model);
    }
    
    return editPart;
  }

}
