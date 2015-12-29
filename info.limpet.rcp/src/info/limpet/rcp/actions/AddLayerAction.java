package info.limpet.rcp.actions;

import java.util.Collection;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.AddLayerOperation;

public class AddLayerAction extends AbstractLimpetAction
{

	public AddLayerAction()
	{
		setText("Add Layer");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
	}

	@Override
	public void run()
	{
		IOperation<IStoreItem> operation = new AddLayerOperation();
		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), getStore(), getContext());
		commands.iterator().next().execute();
	}

}
