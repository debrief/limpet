package info.limpet.rcp.editors;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

public class DataManagerEditorActionBarContributor
		extends EditorActionBarContributor
{

	protected DataManagerEditor _activeEditor;
	
	public DataManagerEditorActionBarContributor() {
		super();
	}

	/**
	 * Sets the active editor for the contributor.
	 * @param targetEditor the new target editor
	 */
	public void setActiveEditor(IEditorPart targetEditor) {
		if (targetEditor instanceof DataManagerEditor) {
			_activeEditor = (DataManagerEditor) targetEditor;
		} else {
			_activeEditor = null;
		}
	}

}
