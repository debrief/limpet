package info.limpet.data.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.store.InMemoryStore.StoreGroup;

public class AddLayerOperation implements IOperation<IStoreItem>
{

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination, IContext context)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			final String thisTitle = "Add new layer";
			// hmm, see if a group has been selected
			ICommand<IStoreItem> newC = null;
			if (selection.size() == 1)
			{
				IStoreItem first = selection.get(0);
				if (first instanceof IStoreGroup)
				{
					StoreGroup group = (StoreGroup) first;
					newC = new AddLayerCommand(thisTitle, group, destination, context);
				}
			}

			if (newC == null)
			{
				newC = new AddLayerCommand(thisTitle, destination, context);
			}

			if (newC != null)
			{
				res.add(newC);
			}
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return true;
	}

	public static class AddLayerCommand extends AbstractCommand<IStoreItem>
	{
		private StoreGroup _group;

		public AddLayerCommand(String title, IStore store, IContext context)
		{
			super(title, "Add a new layer", store, false, false, null, context);
		}

		public AddLayerCommand(String title, StoreGroup group, IStore store,
				IContext context)
		{
			this(title, store, context);
			_group = group;
		}

		@Override
		public void execute()
		{
			// get the String
			String string = getOutputName();

			if (string != null)
			{
				IStoreGroup newGroup = new StoreGroup(string);

				if (_group != null)
				{
					_group.add(newGroup);
				}
				else
				{
					getStore().add(newGroup);

				}
			}
		}

		@Override
		protected void recalculate()
		{
			// don't worry
		}

		@Override
		protected String getOutputName()
		{
			return getContext().getInput("Add layer", NEW_DATASET_MESSAGE, "");
		}

	}

}
