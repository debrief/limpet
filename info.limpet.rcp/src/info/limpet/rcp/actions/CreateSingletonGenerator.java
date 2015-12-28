package info.limpet.rcp.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IStore;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.rcp.RCPContext;
import info.limpet.rcp.data_provider.data.GroupWrapper;
import info.limpet.rcp.editors.DataManagerEditor;

public abstract class CreateSingletonGenerator extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		return createSingletonGenerator(event);
	}

	protected Object createSingletonGenerator(ExecutionEvent event)
	{
		// get the name
		String name = "new " + getName();
		double value;

		InputDialog dlgName = new InputDialog(Display.getCurrent().getActiveShell(),
				"New variable", "Enter name for variable", "", null);
		if (dlgName.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			name = dlgName.getValue();
		}
		else
		{
			return null;
		}

		InputDialog dlgValue = new InputDialog(
				Display.getCurrent().getActiveShell(), "New variable",
				"Enter initial value for variable", "", null);
		if (dlgValue.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			String str = dlgValue.getValue();
			try
			{
				// get the new collection
				QuantityCollection<?> newData = generate(name);

				// add the new value
				value = Double.parseDouble(str);
				newData.add(value);

				// put the new collection in to the selected folder, or into root
				ISelection selection = getSelection();
				IStructuredSelection stru = (IStructuredSelection) selection;
				Object first = stru.getFirstElement();
				if (first instanceof GroupWrapper)
				{
					GroupWrapper gW = (GroupWrapper) first;
					gW.getGroup().add(newData);
				}
				else
				{
					// just store it at the top level
					IStore store = getStore();
					if (store != null)
					{
						store.add(newData);
					}
				}

			}
			catch (NumberFormatException e)
			{
				getContext().logError(Status.WARNING, "Failed to parse initial value",
						e);
			}
		}
		return null;
	}

	private IStore getStore()
	{
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor instanceof DataManagerEditor)
		{
			return ((DataManagerEditor) activeEditor).getStore();
		}
		return null;
	}

	private IContext getContext()
	{
		IEditorPart activeEditor = getActiveEditor();
		if (activeEditor instanceof DataManagerEditor)
		{
			return ((DataManagerEditor) activeEditor).getContext();
		}
		return new RCPContext();
	}

	private IEditorPart getActiveEditor()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		return activePage.getActiveEditor();
	}

	private ISelection getSelection()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		return activePage.getSelection();
	}

	protected abstract String getName();

	protected abstract QuantityCollection<?> generate(String name);

}
