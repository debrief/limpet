package info.limpet.stackedcharts.editor.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import info.limpet.stackedcharts.editor.figures.VerticalLabel;
import info.limpet.stackedcharts.model.Dataset;

public class DatasetEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected IFigure createFigure()
  {
    VerticalLabel verticalLabel = new VerticalLabel();
    verticalLabel.setTextAlignment(PositionConstants.TOP);
    return verticalLabel;
  }

  @Override
  protected void createEditPolicies()
  {
  }

  @Override
  protected void refreshVisuals()
  {
    Dataset dataset = (Dataset) getModel();
    ((VerticalLabel) getFigure()).setText("Dataset " + dataset.getName());
  }
}
