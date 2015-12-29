package info.limpet.rcp.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import info.limpet.ICollection;
import info.limpet.IContext;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.csv.CsvGenerator;
import info.limpet.rcp.RCPContext;
import info.limpet.rcp.editors.DataManagerEditor;

public abstract class AbstractLimpetAction extends Action
{

	protected ISelection getSelection()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		return activePage.getSelection();
	}

	protected IStore getStore()
	{
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor instanceof DataManagerEditor)
		{
			return ((DataManagerEditor) activeEditor).getStore();
		}
		return null;
	}

	protected IContext getContext()
	{
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor instanceof DataManagerEditor)
		{
			return ((DataManagerEditor) activeEditor).getContext();
		}
		return new RCPContext();
	}

	protected IEditorPart getActiveEditor()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		return activePage.getActiveEditor();
	}

	protected List<IStoreItem> getSuitableObjects()
	{
		ArrayList<IStoreItem> matches = new ArrayList<IStoreItem>();

		// ok, find the applicable operations
		ISelection sel = getSelection();
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

	protected Shell getShell()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		return window.getShell();
	}

	protected String getCsvString()
	{
		List<IStoreItem> selection = getSuitableObjects();
		if (selection.size() == 1 && selection.get(0) instanceof ICollection)
		{
			return CsvGenerator.generate((ICollection) selection.get(0));
		}
		return null;
	}

}
