package info.limpet.rcp.actions;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import info.limpet.rcp.editors.DataManagerEditor;

public class RefreshViewAction extends AbstractLimpetAction
{

	public RefreshViewAction()
	{
		setText("Refresh");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
	}

	@Override
	public void run()
	{
		IEditorPart editor = getActiveEditor();
		if (editor instanceof DataManagerEditor)
		{
			((DataManagerEditor)editor).refresh();
		}
	}

}
