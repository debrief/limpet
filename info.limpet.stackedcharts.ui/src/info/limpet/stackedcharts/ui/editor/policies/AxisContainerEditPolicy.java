package info.limpet.stackedcharts.ui.editor.policies;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.impl.ChartImpl;
import info.limpet.stackedcharts.ui.editor.commands.MoveAxisCommand;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetsFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.DatasetEditPart;

import java.util.Iterator;
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
    @SuppressWarnings("rawtypes")
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
    @SuppressWarnings("rawtypes")
    List toAdd = request.getEditParts();
    
    Command res = null;
    
    // have a peek, to see if it's a dataset, or an axis
    if(toAdd.size() > 0)
    {
      Object first = toAdd.get(0);
      if(first instanceof DatasetEditPart)
      {
        Dataset[] datasets = new Dataset[toAdd.size()];
        int i = 0;
        for (Object o : toAdd)
        {
          datasets[i++] = (Dataset) ((DatasetEditPart) o).getModel();
        }
        res = new AddDatasetsToAxisCommand((DependentAxis) getHost().getModel(),
            datasets);
      }
      else if(first instanceof AxisEditPart)
      {
        // find the listing we belong to
        DependentAxis axis = (DependentAxis) getHost().getModel();
        
        // find out which list (min/max) this axis is currently on
        EList<DependentAxis> destination = getHostListFor(axis);
       
        // ok, did we find it?
        if(destination != null)
        {
          DependentAxis[] axes = new DependentAxis[toAdd.size()];
          int i=0;
          for (Object o : toAdd)
          {
            axes[i++] = (DependentAxis) ((AxisEditPart) o).getModel();
          }

          res = new MoveAxisCommand(destination, axes);
        }
      }      
    }
    return res;
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

  /** convenience class to find the relevant list (min/max axes) for the 
   * supplied axis
   * 
   * @param axis
   * @return
   */
  public static EList<DependentAxis> getHostListFor(DependentAxis axis)
  {
    ChartImpl chart = (ChartImpl) axis.eContainer();
    // ok, find out which item this is in
    final EList<DependentAxis> minAxes = chart.getMinAxes();
    final EList<DependentAxis> maxAxes = chart.getMaxAxes();
    EList<DependentAxis> destination = null;
    Iterator<DependentAxis> iter = minAxes.iterator();
    while (iter.hasNext())
    {
      DependentAxis thisD = (DependentAxis) iter.next();
      if(thisD.equals(axis))
      {
        // ok, it's currently on the min axis
        destination = minAxes;
        break;
      }
    }
    iter = maxAxes.iterator();
    while (iter.hasNext())
    {
      DependentAxis thisD = (DependentAxis) iter.next();
      if(thisD.equals(axis))
      {
        // ok, it's currently on the min axis
        destination = maxAxes;
        break;
      }
    }
    
    return destination;
  }
  
}
