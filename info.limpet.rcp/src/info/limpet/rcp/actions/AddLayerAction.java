package info.limpet.rcp.actions;

import java.util.Collection;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.AddLayerOperation;

public class AddLayerAction extends AbstractLimpetAction
{

	@Override
	public void run()
	{
		IOperation<IStoreItem> operation = new AddLayerOperation();
		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), getStore(), getContext());
		commands.iterator().next().execute();
	}

}
