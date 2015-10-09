package info.limpet.data.operations;

import info.limpet.ICommand;
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
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			String thisTitle = "Add new layer";
			ICommand<IStoreItem> newC = new AddLayerCommand(thisTitle, destination,
					_stringProvider);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return true;
	}

	public static class AddLayerCommand extends AbstractCommand<IStoreItem>
	{
		final StringProvider _stringProvider;

		public AddLayerCommand(String title, IStore store,
				StringProvider stringProvider)
		{
			super(title, "Add a new layer", null, store, false, false, null);
			_stringProvider = stringProvider;
		}

		@Override
		public void execute()
		{
			// get the String
			String string = _stringProvider.getString("Name for new layer");

			if (string != null)
			{
				StoreGroup group = new StoreGroup(string);
				getStore().add(group);
			}
		}

		@Override
		protected void recalculate()
		{
			// don't worry
		}

	}

}
