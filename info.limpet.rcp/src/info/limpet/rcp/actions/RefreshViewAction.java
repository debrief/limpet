package info.limpet.rcp.actions;

import org.eclipse.ui.IEditorPart;

import info.limpet.rcp.editors.DataManagerEditor;

public class RefreshViewAction extends AbstractLimpetAction
{

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
