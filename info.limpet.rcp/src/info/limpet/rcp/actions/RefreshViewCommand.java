package info.limpet.rcp.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;

import info.limpet.rcp.editors.DataManagerEditor;

public class RefreshViewCommand extends AbstractLimpetHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IEditorPart editor = getActiveEditor();
		if (editor instanceof DataManagerEditor)
		{
			((DataManagerEditor)editor).refresh();
		}
		return null;
	}

}
