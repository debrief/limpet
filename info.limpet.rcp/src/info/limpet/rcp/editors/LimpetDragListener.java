package info.limpet.rcp.editors;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

import info.limpet.rcp.data_provider.data.LimpetWrapper;
import info.limpet.rcp.editors.dnd.LimpetTransfer;

public class LimpetDragListener extends DragSourceAdapter
{
	private StructuredViewer viewer;

	public LimpetDragListener(StructuredViewer viewer)
	{
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event)
	{
		if (!event.doit)
			return;
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragSetData(DragSourceEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		@SuppressWarnings("unchecked")
		LimpetWrapper[] wrappers = (LimpetWrapper[]) selection.toList()
				.toArray(new LimpetWrapper[selection.size()]);
		if (LimpetTransfer.getInstance().isSupportedType(event.dataType))
		{
			event.data = wrappers;
		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragStart(DragSourceEvent event)
	{
		event.doit = !viewer.getSelection().isEmpty();
	}
}