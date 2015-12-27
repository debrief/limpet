/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
