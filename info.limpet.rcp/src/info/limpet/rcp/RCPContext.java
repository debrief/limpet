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
package info.limpet.rcp;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import info.limpet.IContext;

public class RCPContext implements IContext
{

	@Override
	public String getInput(String title, String description, String defaultText)
	{
		InputDialog dlgName = new InputDialog(
				Display.getCurrent().getActiveShell(), title, description, defaultText,
				null);
		if (dlgName.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			return dlgName.getValue();
		}
		else
		{
			return null;
		}

	}

	@Override
	public void logError(Status status, String message, Exception e)
	{
		final int statCode;

		switch (status)
		{
		case INFO:
			statCode = org.eclipse.core.runtime.Status.INFO;
			break;
		case WARNING:
			statCode = org.eclipse.core.runtime.Status.WARNING;
			break;
		case ERROR:
			statCode = org.eclipse.core.runtime.Status.ERROR;
			break;
		default:
			statCode = org.eclipse.core.runtime.Status.ERROR;
			Activator.logError(statCode,
					"RCPContext failed to recognise status code:" + status, null);
		}

		Activator.logError(statCode, message, e);
	}

}
