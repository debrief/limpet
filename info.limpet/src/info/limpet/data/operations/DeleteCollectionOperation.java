package info.limpet.data.operations;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DeleteCollectionOperation implements IOperation<IStoreItem>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			final String commandTitle;
			if (selection.size() == 1)
			{
				commandTitle = "Delete collection";
			}
			else
			{
				commandTitle = "Delete collections";
			}
			ICommand<IStoreItem> newC = new DeleteCollection(commandTitle,
					selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return (selection.size() > 0);
	}

	public static class DeleteCollection extends AbstractCommand<IStoreItem>
	{

		public DeleteCollection(String title, List<IStoreItem> selection,
				IStore store)
		{
			super(title, "Delete specific collections", null, store, false, false,
					selection);
		}

		@Override
		public void execute()
		{
			// tell each series that we're a dependent
			Iterator<IStoreItem> iter = inputs.iterator();
			while (iter.hasNext())
			{
				IStoreItem iCollection = iter.next();
				IStore store = getStore();
				if (store instanceof InMemoryStore)
				{
					InMemoryStore mem = (InMemoryStore) store;
					mem.remove(iCollection);
				}
			}
		}

		@Override
		protected void recalculate()
		{
			// don't worry
		}

	}

}
