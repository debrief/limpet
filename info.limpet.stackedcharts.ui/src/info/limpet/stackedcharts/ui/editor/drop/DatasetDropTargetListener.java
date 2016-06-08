package info.limpet.stackedcharts.ui.editor.drop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;
import info.limpet.stackedcharts.ui.view.adapter.AdapterRegistry;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * base for classes supporting the drop process, including establishing if the target is valid
 * 
 * @author ian
 * 
 */
abstract public class DatasetDropTargetListener implements
    TransferDropTargetListener
{

  private final GraphicalViewer viewer;
  protected AbstractGraphicalEditPart feedback;

  protected DatasetDropTargetListener(GraphicalViewer viewer)
  {
    this.viewer = viewer;
  }

  /**
   * whether this listener applies to this event
   * 
   * @param event
   * @return
   */
  abstract boolean appliesTo(DropTargetEvent event);
  

  /** wrap up the data change for the drop event
   * 
   * @param axis
   * @param datasets
   * @return
   */
  abstract protected Command createCommand(AbstractGraphicalEditPart axis,
      List<Dataset> datasets);


  /**
   * find the object being passed over
   * 
   * @param event
   *          the event
   * @param viewer
   *          our figure
   * @return the nearest edit part
   */
  final protected EditPart findPart(DropTargetEvent event)
  {
    org.eclipse.swt.graphics.Point cP =
        viewer.getControl().toControl(event.x, event.y);
    EditPart findObjectAt = viewer.findObjectAt(new Point(cP.x, cP.y));
    return findObjectAt;
  }

  protected void addFeedback(AbstractGraphicalEditPart figure)
  {
    if (figure != null)
    {
      figure.getFigure().setBackgroundColor(ColorConstants.lightGray);
    }
  }

  protected void removeFeedback(AbstractGraphicalEditPart figure)
  {
    if (figure != null)
    {
      figure.getFigure().setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
    }
  }

  protected CommandStack getCommandStack()
  {
    return viewer.getEditDomain().getCommandStack();
  }

  protected List<Object> convertSelection(StructuredSelection selection)
  {
    AdapterRegistry adapter = new AdapterRegistry();
    List<Object> element = new ArrayList<Object>();
    for (Object object : selection.toArray())
    {
      if (adapter.canConvert(object))
      {
        element.add(adapter.convert(object));
      }
    }

    return element;
  }

  protected static boolean canDropSelection(final Chart chart, ISelection selection)
  {
    boolean canDrop = true;
    AdapterRegistry adapter = new AdapterRegistry();
    if (selection instanceof StructuredSelection)
    {

      // check the selection
      for (Object obj : ((StructuredSelection) selection).toArray())
      {
        if (adapter.canConvert(obj))
        {
          List<Dataset> convert = adapter.convert(obj);
          if (convert.size() == 0)
          {
            continue;
          }
          for (Dataset dataset : convert)
          {
            if (!canDropDataset(chart, dataset))
            {
              canDrop = false;
              break;
            }
          }
        }
      }
    }

    return canDrop;
  }

  protected static boolean datasetAlreadyExistsOnTheseAxes(
      final Iterator<DependentAxis> axes, final String name)
  {
    boolean exists = false;

    while (axes.hasNext())
    {
      final DependentAxis dAxis = (DependentAxis) axes.next();
      Iterator<Dataset> dIter = dAxis.getDatasets().iterator();
      while (dIter.hasNext())
      {
        Dataset thisD = (Dataset) dIter.next();
        if (name.equals(thisD.getName()))
        {
          // ok, we can't add it
          System.err.println("Not adding dataset - duplicate name");
          exists = true;
          break;
        }
      }
    }

    return exists;
  }

  protected static boolean canDropDataset(final Chart chart, final Dataset dataset)
  {
    boolean possible = true;

    // check the axis
    final Iterator<DependentAxis> minIter = chart.getMinAxes().iterator();
    final Iterator<DependentAxis> maxIter = chart.getMaxAxes().iterator();

    if (datasetAlreadyExistsOnTheseAxes(minIter, dataset.getName())
        || datasetAlreadyExistsOnTheseAxes(maxIter, dataset.getName()))
    {
      possible = false;
    }

    return possible;
  }


  @Override
  public final void dragOver(DropTargetEvent event)
  {
    EditPart target = findPart(event);

    if (feedback == target)
    {
      // drop out, we're still passing over the same object
      return;
    }

    if (LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType))
    {
      // get the chart model
      AbstractGraphicalEditPart axis = (AbstractGraphicalEditPart) target;
      final ChartPaneEditPart parent = (ChartPaneEditPart) axis.getParent();
      final ChartEditPart chartEdit = (ChartEditPart) parent.getParent();
      final Chart chart = chartEdit.getModel();

      if (canDropSelection(chart, LocalSelectionTransfer.getTransfer()
          .getSelection()))
      {
        removeFeedback(feedback);
        feedback = (AbstractGraphicalEditPart) target;
        addFeedback(feedback);
        event.detail = DND.DROP_COPY;
      }
      else
      {
        removeFeedback(feedback);
        feedback = null;
        event.detail = DND.DROP_NONE;
      }
    }
    else
    {
      removeFeedback(feedback);
      feedback = null;
      event.detail = DND.DROP_NONE;
    }

  }

  @Override
  final public void dragOperationChanged(DropTargetEvent event)
  {
  }

  @Override
  final public void dragLeave(DropTargetEvent event)
  {
    removeFeedback(feedback);
    feedback = null;
  }

  @Override
  final public void dragEnter(DropTargetEvent event)
  {
  }

  @Override
  final public boolean isEnabled(DropTargetEvent event)
  {
    return LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType);
  }

  @Override
  final public Transfer getTransfer()
  {
    return LocalSelectionTransfer.getTransfer();
  }
  
  final public void reset()
  {
    removeFeedback(feedback);
    feedback = null;
  }

  @Override
  final public void dropAccept(DropTargetEvent event)
  {
  }


  @Override
  public void drop(DropTargetEvent event)
  {
    if (LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType))
    {
      StructuredSelection sel =
          (StructuredSelection) LocalSelectionTransfer.getTransfer()
              .getSelection();
      if (sel.isEmpty())
      {
        event.detail = DND.DROP_NONE;
        return;
      }
      List<Object> objects = convertSelection(sel);
      EditPart target = findPart(event);

      AbstractGraphicalEditPart axis = (AbstractGraphicalEditPart) target;
      List<Dataset> datasets = new ArrayList<Dataset>(objects.size());
      for (Object o : objects)
      {
        if (o instanceof Dataset)
        {
          datasets.add((Dataset) o);
        }
        else if (o instanceof List<?>)
        {
          List<?> list = (List<?>) o;
          for (Iterator<?> iter = list.iterator(); iter.hasNext();)
          {
            Object item = (Object) iter.next();
            if (item instanceof Dataset)
            {
              datasets.add((Dataset) item);
            }
          }
        }
      }

      Command command = createCommand(axis, datasets);
      getCommandStack().execute(command);
    }

    feedback = null;
  }

}
