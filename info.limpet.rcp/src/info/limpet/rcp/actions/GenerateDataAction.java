package info.limpet.rcp.actions;

import java.util.Collection;

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

		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), getStore(), getContext());
		commands.iterator().next().execute();
	}

}
