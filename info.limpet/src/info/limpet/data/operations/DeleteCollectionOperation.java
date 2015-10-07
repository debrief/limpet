package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DeleteCollectionOperation implements IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
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
			ICommand<ICollection> newC = new DeleteCollection(commandTitle,
					selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		return (selection.size() > 0);
	}

	public static class DeleteCollection extends AbstractCommand<ICollection>
	{

		public DeleteCollection(String title, List<ICollection> selection,
				IStore store)
		{
			super(title, "Delete specific collections", null, store, false, false,
					selection);
		}

		@Override
		public void execute()
		{
			// tell each series that we're a dependent
			Iterator<ICollection> iter = inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = iter.next();
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
