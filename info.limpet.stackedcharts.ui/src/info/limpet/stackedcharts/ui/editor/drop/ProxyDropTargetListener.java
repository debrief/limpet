package info.limpet.stackedcharts.ui.editor.drop;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

public class ProxyDropTargetListener implements TransferDropTargetListener
{

  private final DatasetDropTargetListener[] listeners;

  public ProxyDropTargetListener(DatasetDropTargetListener... listeners)
  {
    this.listeners = listeners;
  }

  @Override
  public void dragEnter(DropTargetEvent event)
  {
    for (DatasetDropTargetListener listener : listeners)
    {
      if(listener.isValid(event))
        listener.dragEnter(event);
      else
        listener.reset();
    }

  }

  @Override
  public void dragLeave(DropTargetEvent event)
  {
    for (DatasetDropTargetListener listener : listeners)
    {
      if(listener.isValid(event))
        listener.dragLeave(event);
      else
        listener.reset();
    }

  }

  @Override
  public void dragOperationChanged(DropTargetEvent event)
  {
    for (DatasetDropTargetListener listener : listeners)
    {
      if(listener.isValid(event))
        listener.dragOperationChanged(event);
      else
        listener.reset();
    }

  }

  @Override
  public void dragOver(DropTargetEvent event)
  {
    boolean match = false;
    for (DatasetDropTargetListener listener : listeners)
    {
      if(listener.isValid(event))
      {
        match = true;
        listener.dragOver(event);
      }
      else
      {
        listener.reset();
      }
    }
    if(!match)
    {
      event.detail = DND.DROP_NONE;
    }

  }

  @Override
  public void drop(DropTargetEvent event)
  {
    for (DatasetDropTargetListener listener : listeners)
    {
      if(listener.isValid(event))
        if(listener.isValid(event))
          listener.drop(event);
        else
          listener.reset();
    }

  }

  @Override
  public void dropAccept(DropTargetEvent event)
  {
    for (DatasetDropTargetListener listener : listeners)
    {
      if(listener.isValid(event))
        listener.dropAccept(event);
      else
        listener.reset();
    }

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
