package info.limpet.data;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.data.impl.CoreChangeListener;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class TestDynamic extends TestCase
{
	public void testSingleQuantityStats()
	{
		// get some data
		InMemoryStore store = new SampleData().getData();
		
		
		// ok, let's try one that works		
		List<ICollection> selection = new ArrayList<ICollection>();
		ICollection speedOne = store.get(SampleData.SPEED_ONE);
		selection.add(speedOne);
		selection.add(store.get(SampleData.SPEED_TWO));

		int storeSize = store.size();
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(selection, store );
		ICommand<?> firstAction = actions.iterator().next();
		assertEquals("correct action", "Add series", firstAction.getTitle());
		
		// run the action
		firstAction.execute();
		
		// check we have new data
		assertEquals("new data created", storeSize + 1, store.size());
		
		// ok, get the new dataset
		ICollection resSeries = store.get(AddQuantityOperation.AddQuantityValues.SUM_OF_INPUT_SERIES);
		assertNotNull(resSeries);
		
		// ok, play about with a change
		final List<String> events = new ArrayList<String>();
		resSeries.addChangeListener(new CoreChangeListener(){

			@Override
			public void dataChanged()
			{
				events.add("changed!");
			}});
		assertEquals("empty at start", 0, events.size());
		
		// ok, now make a change in one of hte input collections
		speedOne.fireChanged();
		assertEquals("change received", 1, events.size());
		

		
	}
}
