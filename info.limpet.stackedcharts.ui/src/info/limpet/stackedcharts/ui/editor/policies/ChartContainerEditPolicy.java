package info.limpet.stackedcharts.ui.editor.policies;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.editor.commands.MoveChartCommand;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartsPanelEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

public class ChartContainerEditPolicy extends ContainerEditPolicy implements
    EditPolicy
{

  @Override
  public EditPart getTargetEditPart(Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      return getHost();
    }
    if (REQ_CREATE.equals(request.getType()))
    {
      return getHost();
    }
    return super.getTargetEditPart(request);
  }

  public ChartEditPart getHost()
  {
    return (ChartEditPart) super.getHost();
  }

  @Override
  protected Command getCreateCommand(CreateRequest request)
  {

    return null;
  }

  @Override
  protected Command getAddCommand(GroupRequest request)
  {
    @SuppressWarnings("rawtypes")
    List toAdd = request.getEditParts();

    CompoundCommand res = null;

    if (toAdd.size() > 0)
    {
      Object first = toAdd.get(0);
      if (first instanceof ChartEditPart)
      {
        res = new CompoundCommand();
        List<Chart> charts =
            ((ChartsPanelEditPart) getHost().getParent()).getModel()
                .getCharts();
        for (Object o : toAdd)
        {
          if (o instanceof ChartEditPart)
          {
            ChartEditPart chartEditPart = (ChartEditPart) o;
            final ChartEditPart hostPart = getHost();            
            int indexOfHost = charts.indexOf(hostPart.getModel());
            res.add(new MoveChartCommand(charts, chartEditPart.getModel(),
                indexOfHost++));
          }

        }

      }
    }
    return res;
  }

  @Override
  public void showTargetFeedback(Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      ChartEditPart axisEditPart = (ChartEditPart) getHost();
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
      ChartEditPart axisEditPart = getHost();
      IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(ChartEditPart.BACKGROUND_COLOR);
    }
  }

}
