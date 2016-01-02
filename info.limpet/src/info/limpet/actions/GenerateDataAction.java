package info.limpet.actions;

import java.util.Collection;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.GenerateDummyDataOperation;

public class GenerateDataAction extends AbstractLimpetAction
{

	public GenerateDataAction(IContext context)
	{
		super(context);
		setText("Generate data");
		setImageDescriptor(context.getImageDescriptor(IContext.GENERATE_DATA));
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
			getContext().openWarning("Error", "Cannot run the action for current selection");
		}
		else
		{
			commands.iterator().next().execute();
		}
	}

}
