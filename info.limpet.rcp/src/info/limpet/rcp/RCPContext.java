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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import info.limpet.IContext;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.rcp.editors.DataManagerEditor;

public class RCPContext implements IContext
{

	@Override
	public String getInput(String title, String description, String defaultText)
	{
		InputDialog dlgName = new InputDialog(Display.getCurrent().getActiveShell(),
				title, description, defaultText, null);
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

	private IEditorPart getActiveEditor()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		return activePage.getActiveEditor();
	}

	private Shell getShell()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		return window.getShell();
	}

	@Override
	public IStore getStore()
	{
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor instanceof DataManagerEditor)
		{
			return ((DataManagerEditor) activeEditor).getStore();
		}
		return null;
	}

	
	@Override
	public void openWarning(String title, String message)
	{
		MessageDialog.openWarning(getShell(), title, message);
	}

	@Override
	public void openInformation(String title, String message)
	{
		MessageDialog.openInformation(getShell(), title, message);
	}

	@Override
	public String getCsvFilename()
	{
		FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
		final String[] filterNames;
		final String[] filterExtensions;
		String filterPath = "";
		if (SWT.getPlatform().equals("win32"))
		{
			filterNames = new String[]
			{ "Csv File", "All Files (*.*)" };
			filterExtensions = new String[]
			{ "*.csv", "*.*" };
		}
		else
		{
			filterNames = new String[]
			{ "Csv File", "All Files (*)" };
			filterExtensions = new String[]
			{ "*.csv", "*" };
		}
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);
		dialog.setFilterPath(filterPath);
		dialog.setFileName("limpet_out.csv");
		return dialog.open();
	}

	@Override
	public boolean openQuestion(String title, String message)
	{
		return MessageDialog.openQuestion(getShell(), title, message);
	}

	@Override
	public void openError(String title, String message)
	{
		MessageDialog.openError(getShell(), title, message);
	}

	@Override
	public void log(Exception e)
	{
		Activator.log(e);
	}

	@Override
	public void placeOnClipboard(String text)
	{
		final Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[]
		{ text }, new Transfer[]
		{ textTransfer });
	}

	// suitable objects
	@Override
	public List<IStoreItem> getSelection()
	{
		List<IStoreItem> matches = new ArrayList<IStoreItem>();

		// ok, find the applicable operations
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		ISelection sel = activePage.getSelection();
		if (!(sel instanceof IStructuredSelection))
		{
			return matches;
		}
		IStructuredSelection str = (IStructuredSelection) sel;
		Iterator<?> iter = str.iterator();
		while (iter.hasNext())
		{
			Object object = (Object) iter.next();
			if (object instanceof IStoreItem)
			{
				matches.add((IStoreItem) object);
			}
			else if (object instanceof IAdaptable)
			{
				IAdaptable ada = (IAdaptable) object;
				Object match = ada.getAdapter(IStoreItem.class);
				if (match != null)
				{
					matches.add((IStoreItem) match);
				}
			}
		}

		return matches;
	}

}
