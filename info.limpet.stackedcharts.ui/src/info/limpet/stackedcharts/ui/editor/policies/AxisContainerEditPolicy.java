package info.limpet.stackedcharts.ui.editor.policies;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetsFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.DatasetEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

public class AxisContainerEditPolicy extends ContainerEditPolicy implements
    EditPolicy
{

  @Override
  public EditPart getTargetEditPart(Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      return getHost();
    }
    return super.getTargetEditPart(request);
  }

  @Override
  protected Command getOrphanChildrenCommand(GroupRequest request)
  {
    List toRemove = request.getEditParts();
    Dataset[] datasets = new Dataset[toRemove.size()];
    int i = 0;
    for (Object o : toRemove)
    {
      datasets[i++] = (Dataset) ((DatasetEditPart) o).getModel();
    }
    return new DeleteDatasetsFromAxisCommand((DependentAxis) getHost()
        .getModel(), datasets);
  }

  @Override
  protected Command getCreateCommand(CreateRequest request)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Command getAddCommand(GroupRequest request)
  {
    List toAdd = request.getEditParts();
    Dataset[] datasets = new Dataset[toAdd.size()];
    int i = 0;
    for (Object o : toAdd)
    {
      datasets[i++] = (Dataset) ((DatasetEditPart) o).getModel();
    }
    return new AddDatasetsToAxisCommand((DependentAxis) getHost().getModel(),
        datasets);
  }

  @Override
  public void showTargetFeedback(Request request)
  {
    // highlight the Axis when user is about to drop a dataset on it
    if (REQ_ADD.equals(request.getType()))
    {
      AxisEditPart axisEditPart = (AxisEditPart) getHost();
      IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(ColorConstants.lightGray);
    }
  }

  @Override
  public void eraseTargetFeedback(Request request)
  {
    // remove the highlight
    if (REQ_ADD.equals(request.getType()))
    {
      AxisEditPart axisEditPart = (AxisEditPart) getHost();
      IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
    }
  }

}
