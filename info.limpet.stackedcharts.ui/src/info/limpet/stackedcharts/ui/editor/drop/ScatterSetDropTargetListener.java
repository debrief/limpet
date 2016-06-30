package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
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
abstract public class ScatterSetDropTargetListener extends
    CoreDropTargetListener
{

  protected AbstractGraphicalEditPart feedback;

  protected ScatterSetDropTargetListener(GraphicalViewer viewer)
  {
    super(viewer);
  }

  /**
   * wrap up the data change for the drop event
   * 
   * @param chart
   * @param scatterSets
   * @return
   */
  abstract protected Command createScatterCommand(Chart chart,
      List<ScatterSet> scatterSets);

  abstract protected Command createDatasetCommand(Chart chart,
      DependentAxis axis, List<Dataset> datasets);

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
      EditPart part = findPart(event);

      AbstractGraphicalEditPart target = (AbstractGraphicalEditPart) part;
      List<ScatterSet> scatterSets = new ArrayList<ScatterSet>();
      List<Dataset> datasets = new ArrayList<Dataset>();

      for (Object o : objects)
      {
        if (o instanceof ScatterSet)
        {
          scatterSets.add((ScatterSet) o);
        }
        else if (o instanceof Dataset)
        {
          datasets.add((Dataset) o);
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
            else if (item instanceof Dataset)
            {
              datasets.add((Dataset) item);
            }
          }
        }
      }

      // ok, now build up the commands necessary to 
      // make the changes
      
      final Command datasetCommand;
      if (datasets.size() > 0)
      {
        DependentAxis axis = null;
        Chart chart = null;

        // get the target - we need the chart
        if (target instanceof AxisEditPart)
        {
          axis = (DependentAxis) target.getModel();
          chart = (Chart) target.getParent().getModel();
        }
        else if (target instanceof ChartEditPart)
        {
          chart = (Chart) target.getModel();
        }

        datasetCommand = createDatasetCommand(chart, axis, datasets);
      }
      else
      {
        datasetCommand = null;
      }

      Command scatterCommand;
      if (scatterSets.size() > 0)
      {
        Chart chart = null;

        // get the target - we need the chart
        if (target instanceof AxisEditPart)
        {
          AxisEditPart axis = (AxisEditPart) target;
          chart = (Chart) axis.getParent().getModel();
        }
        else if (target instanceof ChartEditPart)
        {
          chart = (Chart) target.getModel();
        }

        scatterCommand = createScatterCommand(chart, scatterSets);
      }
      else
      {
        scatterCommand = null;
      }

      final Command command;
      if (scatterCommand != null && datasetCommand != null)
      {
        CompoundCommand compoundCommand = new CompoundCommand();
        compoundCommand.add(scatterCommand);
        compoundCommand.add(datasetCommand);
        command = compoundCommand;
      }
      else if (scatterCommand != null)
      {
        command = scatterCommand;
      }
      else if (datasetCommand != null)
      {
        command = datasetCommand;
      }
      else
      {
        command = null;
      }

      if (command != null)
      {
        getCommandStack().execute(command);
      }
    }

    feedback = null;
  }

}
