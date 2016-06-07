package info.limpet.stackedcharts.ui.editor.drop;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

public class ProxyDropTargetListener implements TransferDropTargetListener
{

  private final TransferDropTargetListener[] listeners;

  public ProxyDropTargetListener(TransferDropTargetListener... listeners)
  {
    this.listeners = listeners;
  }

  @Override
  public void dragEnter(DropTargetEvent event)
  {
    for (TransferDropTargetListener listener : listeners)
    {
      listener.dragEnter(event);
    }

  }

  @Override
  public void dragLeave(DropTargetEvent event)
  {
    for (TransferDropTargetListener listener : listeners)
    {
      listener.dragLeave(event);
    }

  }

  @Override
  public void dragOperationChanged(DropTargetEvent event)
  {
    for (TransferDropTargetListener listener : listeners)
    {
      listener.dragOperationChanged(event);
    }

  }

  @Override
  public void dragOver(DropTargetEvent event)
  {
    for (TransferDropTargetListener listener : listeners)
    {
      listener.dragOver(event);
    }

  }

  @Override
  public void drop(DropTargetEvent event)
  {
    for (TransferDropTargetListener listener : listeners)
    {
      listener.drop(event);
    }

  }

  @Override
  public void dropAccept(DropTargetEvent event)
  {
    for (TransferDropTargetListener listener : listeners)
    {
      listener.dropAccept(event);
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
