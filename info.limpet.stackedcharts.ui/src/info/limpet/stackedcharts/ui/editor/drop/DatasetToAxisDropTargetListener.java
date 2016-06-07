package info.limpet.stackedcharts.ui.editor.drop;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.ui.editor.commands.AddDatasetsToAxisCommand;
import info.limpet.stackedcharts.ui.editor.parts.AxisEditPart;

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
    if (event.detail == DND.DROP_DEFAULT)
    {
      if ((event.operations & DND.DROP_COPY) != 0)
      {
        event.detail = DND.DROP_COPY;
      }
      else
      {
        event.detail = DND.DROP_COPY;
      }
    }
    for (int i = 0; i < event.dataTypes.length; i++)
    {
      if (LocalSelectionTransfer.getTransfer().isSupportedType(
          event.dataTypes[i]))
      {
        event.currentDataType = event.dataTypes[i];

        if (event.detail != DND.DROP_COPY)
        {
          event.detail = DND.DROP_COPY;
        }
        break;
      }
    }
  }

  @Override
  public void drop(DropTargetEvent event)
  {
    if (LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType))
    {
      StructuredSelection selection =
          (StructuredSelection) LocalSelectionTransfer.getTransfer()
              .getSelection();
      if (selection.isEmpty())
      {
        event.detail = DND.DROP_NONE;
        return;
      }
      EditPart findObjectAt = findPart(event);

      if (findObjectAt instanceof AxisEditPart)
      {
        List<Dataset> datasets = new ArrayList<Dataset>(selection.size());
        for (Object o : selection.toArray())
        {
          if (o instanceof Dataset)
          {
            datasets.add((Dataset) o);
          }
          else if(o instanceof List<?>)
          {
            List<?> list = (List<?>) o;
            for (Iterator<?> iter = list.iterator(); iter.hasNext();)
            {
              Object item = (Object) iter.next();
              if(item instanceof Dataset)
              {
                datasets.add((Dataset) item);
              }
            }
          }
        }
        AxisEditPart axisEditPart = (AxisEditPart) findObjectAt;
        AddDatasetsToAxisCommand addDatasetsToAxisCommand =
            new AddDatasetsToAxisCommand((DependentAxis) axisEditPart
                .getModel(), datasets.toArray(new Dataset[datasets.size()]));

        final CommandStack commandStack =
            viewer.getEditDomain().getCommandStack();
        commandStack.execute(addDatasetsToAxisCommand);

      }
    }
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
    if (findObjectAt instanceof AxisEditPart)
    {
      if (feedback != findObjectAt)
      {
        removeFeedback(feedback);
        feedback = (AxisEditPart) findObjectAt;

      }
      addFeedback(feedback);
    }
    else
    {
      removeFeedback(feedback);
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
    if (event.detail == DND.DROP_DEFAULT)
    {
      if ((event.operations & DND.DROP_COPY) != 0)
      {
        event.detail = DND.DROP_COPY;
      }
      else
      {
        event.detail = DND.DROP_COPY;
      }
    }
    if (LocalSelectionTransfer.getTransfer().isSupportedType(
        event.currentDataType))
    {
      if (event.detail != DND.DROP_COPY)
      {
        event.detail = DND.DROP_COPY;
      }
    }

  }

  @Override
  public void dragLeave(DropTargetEvent event)
  {
    removeFeedback(feedback);

  }

  @Override
  public void dragEnter(DropTargetEvent event)
  {
    //

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
