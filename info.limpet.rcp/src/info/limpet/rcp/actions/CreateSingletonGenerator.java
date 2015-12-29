package info.limpet.rcp.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import info.limpet.IContext.Status;
import info.limpet.IStore;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.rcp.data_provider.data.GroupWrapper;

public abstract class CreateSingletonGenerator extends AbstractLimpetHandler
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

	protected abstract String getName();

	protected abstract QuantityCollection<?> generate(String name);

}
