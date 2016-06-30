package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.ScatterSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * base for classes supporting the drop process, including establishing if the target is valid
 * 
 * @author ian
 * 
 */
abstract public class ScatterSetDropTargetListener extends CoreDropTargetListener
{

  protected AbstractGraphicalEditPart feedback;

  protected ScatterSetDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }


  /** wrap up the data change for the drop event
   * 
   * @param axis
   * @param datasets
   * @return
   */
  abstract protected Command createCommand(AbstractGraphicalEditPart axis,
      List<ScatterSet> scatterSets);

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
      List<ScatterSet> scatterSets = new ArrayList<ScatterSet>(objects.size());
      for (Object o : objects)
      {
        if (o instanceof ScatterSet)
        {
          scatterSets.add((ScatterSet) o);
        }
        else if (o instanceof List<?>)
        {
          List<?> list = (List<?>) o;
          for (Iterator<?> iter = list.iterator(); iter.hasNext();)
          {
            Object item = (Object) iter.next();
            if (item instanceof ScatterSet)
            {
              scatterSets.add((ScatterSet) item);
            }
          }
        }
      }

      Command command = createCommand(axis, scatterSets);
      getCommandStack().execute(command);
    }

    feedback = null;
  }

}
