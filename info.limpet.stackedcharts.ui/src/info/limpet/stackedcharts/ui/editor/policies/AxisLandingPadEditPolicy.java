package info.limpet.stackedcharts.ui.editor.policies;

import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddAxisToChartCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.AxisLandingPadEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

public class AxisLandingPadEditPolicy extends ContainerEditPolicy implements
    EditPolicy
{

  @Override
  public void eraseTargetFeedback(final Request request)
  {
    // remove the highlight
    if (REQ_ADD.equals(request.getType()))
    {
      final AxisLandingPadEditPart axisEditPart =
          (AxisLandingPadEditPart) getHost();
      final IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
    }
  }

  @Override
  protected Command getAddCommand(final GroupRequest request)
  {
    @SuppressWarnings("rawtypes")
    final List toAdd = request.getEditParts();

    Command res = null;

    if (toAdd.size() > 0)
    {
      final Object first = toAdd.get(0);
      if (first instanceof AxisEditPart)
      {
        // find the landing side
        final AxisLandingPadEditPart landingPadEditPart =
            (AxisLandingPadEditPart) getHost();
        final ChartPaneEditPart.AxisLandingPad pad =
            (ChartPaneEditPart.AxisLandingPad) landingPadEditPart.getModel();

        // find out which list (min/max) this axis is currently on
        final EList<DependentAxis> destination =
            pad.getPos() == ChartPanePosition.LEFT ? pad.getChart()
                .getMinAxes() : pad.getChart().getMaxAxes();

        // ok, did we find it?
        if (destination != null)
        {
          final DependentAxis[] axes = new DependentAxis[toAdd.size()];
          int i = 0;
          for (final Object o : toAdd)
          {
            axes[i++] = (DependentAxis) ((AxisEditPart) o).getModel();
          }

          res = new AddAxisToChartCommand(destination, axes);
        }
      }
    }
    return res;
  }

  @Override
  protected Command getCreateCommand(final CreateRequest request)
  {
    return null;
  }

  @Override
  public EditPart getTargetEditPart(final Request request)
  {
    if (REQ_ADD.equals(request.getType()))
    {
      return getHost();
    }
    return super.getTargetEditPart(request);
  }

  @Override
  public void showTargetFeedback(final Request request)
  {
    // highlight the Axis when user is about to drop a dataset on it
    if (REQ_ADD.equals(request.getType()))
    {
      final AxisLandingPadEditPart axisEditPart =
          (AxisLandingPadEditPart) getHost();
      final IFigure figure = axisEditPart.getFigure();
      figure.setBackgroundColor(ColorConstants.lightGray);
    }
  }

}
