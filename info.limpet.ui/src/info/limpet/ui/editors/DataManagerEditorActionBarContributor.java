/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.ui.editors;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

public class DataManagerEditorActionBarContributor extends
    EditorActionBarContributor
{

  @SuppressWarnings("unused")
  private DataManagerEditor _activeEditor;

  public DataManagerEditorActionBarContributor()
  {
    super();
  }

  /**
   * Sets the active editor for the contributor.
   * 
   * @param targetEditor
   *          the new target editor
   */
  public void setActiveEditor(IEditorPart targetEditor)
  {
    if (targetEditor instanceof DataManagerEditor)
    {
      _activeEditor = (DataManagerEditor) targetEditor;
    }
    else
    {
      _activeEditor = null;
    }
  }

}
