package info.limpet.stackedcharts.ui.editor.parts;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.figures.DatasetFigure;

public class DatasetEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{

  @Override
  protected IFigure createFigure()
  {
    return new DatasetFigure(this);
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        Dataset dataset = (Dataset) getHost().getModel();
        DependentAxis parent = (DependentAxis) getHost().getParent().getModel();
        DeleteDatasetFromAxisCommand cmd = new DeleteDatasetFromAxisCommand(
            dataset, parent);
        return cmd;
      }
    });
  }

  @Override
  protected void refreshVisuals()
  {
    Dataset dataset = (Dataset) getModel();
    ((DatasetFigure) getFigure()).setName("Dataset " + dataset.getName());
  }

  @Override
  public void actionPerformed(ActionEvent event)
  {
    Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
    if (deleteCommand != null)
    {
      CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
      commandStack.execute(deleteCommand);
    }
  }
}
