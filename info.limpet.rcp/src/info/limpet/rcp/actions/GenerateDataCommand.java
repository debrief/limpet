package info.limpet.rcp.actions;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import info.limpet.ICommand;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.GenerateDummyDataOperation;

public class GenerateDataCommand extends AbstractLimpetHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		GenerateDummyDataOperation operation = new GenerateDummyDataOperation(
				"small", 20);

		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), getStore(), getContext());
		commands.iterator().next().execute();
		return null;
	}

}
