/*******************************************************************************
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
 *******************************************************************************/
package info.limpet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;

public interface IContext
{
	public static enum Status
	{
		INFO, WARNING, ERROR;
	}

	/**
	 * action names
	 */
	public static final String ADD_LAYER_ACTION_NAME = "addLayer";
	public static final String COPY_CSV_TO_CLIPBOARD = "copyScvToClipboard";
	public static final String COPY_CSV_TO_FILE = "copyScvToFile";
	public static final String GENERATE_DATA = "generateData";
	public static final String REFRESH_VIEW = "refreshView";
	
	/** get a string from the user, or null if the user cancelled the operation
	 * 
	 * @param title  shown in the dialog heading
	 * @param description what the text is being input for
	 * @param defaultText the text to pre-populate the input box
	 * @return user-entered string, or null for cancel
	 */
	public String getInput(String title, String description, String defaultText);

	/** indicate this warning
	 * 
	 * @param string
	 */
	public void logError(Status status, String message, Exception e);

	/**
	 * returns current selection
	 * 
	 * @return selection
	 */
	public ISelection getSelection();

	/**
	 * returns storage container for collections
	 * 
	 * @return store
	 */
	public IStore getStore();
	
	/**
	 * returns image descriptor for action
	 * 
	 * @param actionName - action name
	 * @return image descriptor
	 */
	public ImageDescriptor getImageDescriptor(String actionName);

	public void openWarning(String title, String message);

	public void openInformation(String title, String message);

	public String getCsvFilename();

	public boolean openQuestion(String title, String message);

	public void openError(String title, String message);

	public void log(Exception e);

	public void refresh();
}
