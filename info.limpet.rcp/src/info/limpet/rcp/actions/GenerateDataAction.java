package info.limpet.rcp.actions;

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import info.limpet.ICommand;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.GenerateDummyDataOperation;

public class GenerateDataAction extends AbstractLimpetAction
{

	public GenerateDataAction()
	{
		setText("Generate data");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
	}

	@Override
	public void run()
	{
		GenerateDummyDataOperation operation = new GenerateDummyDataOperation(
				"small", 20);

		Collection<ICommand<IStoreItem>> commands = operation
				.actionsFor(getSuitableObjects(), getStore(), getContext());
		if (commands.size() < 1)
		{
			MessageDialog.openWarning(getShell(), "Error",
					"Cannot run the action for current selection");
		}
		else
		{
			commands.iterator().next().execute();
		}
	}

}
