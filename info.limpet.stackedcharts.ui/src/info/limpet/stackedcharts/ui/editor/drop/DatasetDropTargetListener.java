package info.limpet.stackedcharts.ui.editor.drop;



import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DropTargetEvent;

public interface DatasetDropTargetListener extends TransferDropTargetListener
{

  boolean isValid(DropTargetEvent  event);
  void reset();
  
}
