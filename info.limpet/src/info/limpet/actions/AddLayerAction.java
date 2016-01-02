package info.limpet.actions;

import java.util.Collection;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.AddLayerOperation;

public class AddLayerAction extends AbstractLimpetAction
{

	public AddLayerAction(IContext context)
	{
		super(context);
		setText("Add Layer");
		setImageDescriptor(context.getImageDescriptor(IContext.ADD_LAYER_ACTION_NAME));
	}

	@Override
	public void run()
	{
		IOperation<IStoreItem> operation = new AddLayerOperation();
		Collection<ICommand<IStoreItem>> commands = operation
				.actionsFor(getSuitableObjects(), getStore(), getContext());
		if (commands.size() < 1)
		{
			getContext().openWarning("Error", "Cannot run the action for current selection");
		}
		else
		{
			commands.iterator().next().execute();
		}
	}

}
