package info.limpet.rcp.actions;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.AddLayerOperation;

public class AddLayerCommand extends AbstractLimpetHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		return addLayer();
	}

	private Object addLayer()
	{
		IOperation<IStoreItem> operation = new AddLayerOperation();
		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), getStore(), getContext());
		commands.iterator().next().execute();
		return null;
	}
}
