package info.limpet.rcp.actions;

import java.util.Collection;

import info.limpet.ICommand;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.GenerateDummyDataOperation;

public class GenerateDataAction extends AbstractLimpetAction
{

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
