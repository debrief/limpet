package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;
import info.limpet.stackedcharts.ui.editor.parts.AxisLandingPadEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart;
import info.limpet.stackedcharts.ui.editor.parts.ChartPaneEditPart;
import info.limpet.stackedcharts.ui.view.adapter.AdapterRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

public class DatasetToAxisDropTargetListener implements
    TransferDropTargetListener
{
  private final GraphicalViewer viewer;
  private AxisEditPart feedback;

  public DatasetToAxisDropTargetListener(GraphicalViewer viewer)
  {
    this.viewer = viewer;
  }

  @Override
  public void dropAccept(DropTargetEvent event)
  {
  }

  protected boolean canDrop(final AxisEditPart axis, ISelection selection)
  {
    boolean canDrop = false;
    AdapterRegistry adapter = new AdapterRegistry();
    if (selection instanceof StructuredSelection)
    {
      for (Object obj : ((StructuredSelection) selection).toArray())
      {
        if (adapter.canConvert(obj))
        {
          final List<Dataset> validDatasets = new ArrayList<Dataset>();
          List<Dataset> convert = adapter.convert(obj);
          if (convert.size() == 0)
          {
            continue;
          }
          for (Dataset dataset : convert)
          {

            addIfPossible(axis, validDatasets, dataset);
          }

          canDrop = validDatasets.size() > 0;
          break;
        }
      }
    }

    return canDrop;
  }

  protected void addIfPossible(final AxisEditPart axis,
      final List<Dataset> datasets, final Dataset dataset)
  {
    // check the axis
    ChartPaneEditPart parent = (ChartPaneEditPart) axis.getParent();
    ChartEditPart chartEdit = (ChartEditPart) parent.getParent();
    Chart chart = chartEdit.getModel();

    Iterator<DependentAxis> minIt = chart.getMinAxes().iterator();
    while (minIt.hasNext())
    {
      DependentAxis dAxis = (DependentAxis) minIt.next();
      Iterator<Dataset> dIter = dAxis.getDatasets().iterator();
      while (dIter.hasNext())
      {
        Dataset thisD = (Dataset) dIter.next();
        if (thisD.getName().equals(dataset.getName()))
        {
          // ok, we can't add it
          System.err.println("Not adding dataset - duplicate name");
          return;
        }
      }
    }

    Iterator<DependentAxis> maxIt = chart.getMaxAxes().iterator();
    while (maxIt.hasNext())
    {
      DependentAxis dAxis = (DependentAxis) maxIt.next();
      Iterator<Dataset> dIter = dAxis.getDatasets().iterator();
      while (dIter.hasNext())
      {
        Dataset thisD = (Dataset) dIter.next();
        if (thisD.getName().equals(dataset.getName()))
        {
          // ok, we can't add it
          System.err.println("Not adding dataset - duplicate name");
          return;
        }
      }
    }

    datasets.add(dataset);
  }

  public List<Object> convertSelection(StructuredSelection selection)
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

      List<Object> dragObjects = convertSelection(sel);

      EditPart findObjectAt = findPart(event);

      if (findObjectAt instanceof AxisEditPart)
      {
        AxisEditPart axis = (AxisEditPart) findObjectAt;
        List<Dataset> datasets = new ArrayList<Dataset>(dragObjects.size());
        for (Object o : dragObjects)
        {
          if (o instanceof Dataset)
          {
            addIfPossible(axis, datasets, (Dataset) o);
          }
          else if (o instanceof List<?>)
          {
            List<?> list = (List<?>) o;
            for (Iterator<?> iter = list.iterator(); iter.hasNext();)
            {
              Object item = (Object) iter.next();
              if (item instanceof Dataset)
              {
                addIfPossible(axis, datasets, (Dataset) item);
              }
            }
          }
        }
        AddDatasetsToAxisCommand addDatasetsToAxisCommand =
            new AddDatasetsToAxisCommand((DependentAxis) axis.getModel(),
                datasets.toArray(new Dataset[datasets.size()]));

        final CommandStack commandStack =
            viewer.getEditDomain().getCommandStack();
        commandStack.execute(addDatasetsToAxisCommand);

      }
    }
    feedback = null;
  }

  private EditPart findPart(DropTargetEvent event)
  {
    org.eclipse.swt.graphics.Point cP =
        viewer.getControl().toControl(event.x, event.y);
    EditPart findObjectAt = viewer.findObjectAt(new Point(cP.x, cP.y));
    return findObjectAt;
  }

  @Override
  public void dragOver(DropTargetEvent event)
  {
    EditPart findObjectAt = findPart(event);

    if (feedback == findObjectAt)
    {
      return;
    }

    if (findObjectAt instanceof AxisEditPart
        && LocalSelectionTransfer.getTransfer().isSupportedType(
            event.currentDataType)

        && canDrop((AxisEditPart) findObjectAt, LocalSelectionTransfer
            .getTransfer().getSelection()))
    {

      removeFeedback(feedback);
      feedback = (AxisEditPart) findObjectAt;

      addFeedback(feedback);

    }
    else
    {
      removeFeedback(feedback);
      feedback = null;
    }
  }

  private void addFeedback(AxisEditPart figure)
  {
    if (figure != null)
    {
      figure.getFigure().setBackgroundColor(ColorConstants.lightGray);
    }
  }

  private void removeFeedback(AxisEditPart figure)
  {
    if (figure != null)
    {
      figure.getFigure().setBackgroundColor(AxisEditPart.BACKGROUND_COLOR);
    }
  }

  @Override
  public void dragOperationChanged(DropTargetEvent event)
  {
  }

  @Override
  public void dragLeave(DropTargetEvent event)
  {
    removeFeedback(feedback);
    feedback = null;
  }

  @Override
  public void dragEnter(DropTargetEvent event)
  {
  }

  @Override
  public boolean isEnabled(DropTargetEvent event)
  {
    return LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType);
  }

  @Override
  public Transfer getTransfer()
  {
    return LocalSelectionTransfer.getTransfer();
  }
}
