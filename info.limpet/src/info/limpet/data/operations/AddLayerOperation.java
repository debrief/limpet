package info.limpet.data.operations;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddLayerOperation implements IOperation<IStoreItem>
{

	public static interface StringProvider
	{
		public String getString(String title);
	}

	private final StringProvider _stringProvider;

	public AddLayerOperation(StringProvider stringProvider)
	{
		_stringProvider = stringProvider;
	}

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
				if (first instanceof StoreGroup)
				{
					StoreGroup group = (StoreGroup) first;
					newC = new AddLayerCommand(thisTitle, group, destination,
							_stringProvider, context);
				}
			}

			if (newC == null)
			{
				newC = new AddLayerCommand(thisTitle, destination, _stringProvider, context);
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
		// TODO - ditch concept of StringProvider
		final StringProvider _stringProvider;
		private StoreGroup _group;

		public AddLayerCommand(String title, IStore store,
				StringProvider stringProvider, IContext context)
		{
			super(title, "Add a new layer", store, false, false, null, context);
			_stringProvider = stringProvider;
		}

		public AddLayerCommand(String title, StoreGroup group, IStore store,
				StringProvider stringProvider, IContext context)
		{
			this(title, store, stringProvider, context);
			_group = group;
		}

		@Override
		public void execute()
		{
			// get the String
			String string = getOutputName();

			if (string != null)
			{
				StoreGroup newGroup = new StoreGroup(string);

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
