package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GenerateDummyDataOperation implements IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();

	final private String _title;

	final private long _count;

	public GenerateDummyDataOperation(String title, long count)
	{
		_title = title;
		_count = count;
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			String thisTitle = "Generate " + _title + " dataset (" + _count + ")";
			ICommand<ICollection> newC = new GenerateDummyDataCommand(thisTitle,
					destination, _count);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean emptySelection = aTests.exactNumber(selection, 0);
		return emptySelection;
	}

	public static class GenerateDummyDataCommand extends
			AbstractCommand<ICollection>
	{
		final long _count;

		public GenerateDummyDataCommand(String title, IStore store, long count)
		{
			super(title, "Create some sample data", null, store, false, false, null);
			_count = count;
		}

		@Override
		public void execute()
		{
			InMemoryStore newData = new SampleData().getData(_count);
			Iterator<ICollection> iter = newData.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				getStore().add(iCollection);

			}
		}

		@Override
		protected void recalculate()
		{
			// don't worry
		}

	}

}
