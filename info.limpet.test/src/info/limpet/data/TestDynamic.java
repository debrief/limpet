package info.limpet.data;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.CoreChangeListener;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestDynamic extends TestCase
{

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void testSingleQuantityStats()
	{
		// get some data
		InMemoryStore store = new SampleData().getData(10);

		// ok, let's try one that works
		List<ICollection> selection = new ArrayList<ICollection>();
		ICollection speedOne = (ICollection) store.get(SampleData.SPEED_ONE);
		ICollection speedTwo = (ICollection) store.get(SampleData.SPEED_TWO);
		selection.add(speedOne);
		selection.add(speedTwo);

		int storeSize = store.size();

		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(
				selection, store);
		ICommand<?> firstAction = actions.iterator().next();
		assertEquals("correct action", "Sum of input series (interpolated)", firstAction.getName());

		// run the action
		firstAction.execute();

		// check we have new data
		assertEquals("new data created", storeSize + 1, store.size());

		// ok, get the new dataset
		ICollection resSeries = (ICollection) store.get("Sum of input series (interpolated)");
		assertNotNull(resSeries);

		// remember the units
		IQuantityCollection<?> iq = (IQuantityCollection<?>) resSeries;
		final String resUnits = iq.getUnits().toString();
		
		// ok, play about with a change
		final List<String> events = new ArrayList<String>();
		CoreChangeListener listener = new CoreChangeListener()
		{
			@Override
			public void dataChanged(IStoreItem subject)
			{
				events.add("changed!");
			}
		};
		resSeries.addChangeListener(listener);
		assertEquals("empty at start", 0, events.size());

		// ok, now make a change in one of hte input collections
		speedOne.fireDataChanged();
		assertEquals("change received", 1, events.size());
		
		// check the units haven't changed
		assertEquals("units still valid", resUnits, iq.getUnits().toString());

		// ok, now make another change in one of hte input collections
		speedOne.fireDataChanged();
		assertEquals("second change received", 2, events.size());

		selection.clear();
		selection.add(speedTwo);
		selection.add(resSeries);

		// ok - now for a further dependent calculation
		actions = new AddQuantityOperation("output2").actionsFor(selection, store);
		firstAction = actions.iterator().next();
		assertEquals("correct action", "output2 (interpolated)", firstAction.getName());

		// ok, now create the new series
		firstAction.execute();

		// now check the output changed again
		events.clear();

		ICollection newResSeries = (ICollection) store.get("output2 (interpolated)");
		assertNotNull("found new series");
		newResSeries.addChangeListener(listener);

		IQuantityCollection<?> iq2 = (IQuantityCollection<?>) newResSeries;
		final String resUnits2 = iq2.getUnits().toString();

		// ok, fire a change in speed one
		speedOne.fireDataChanged();
		assertEquals("change received", 2, events.size());

		speedTwo.fireDataChanged();
		assertEquals("change received", 5, events.size());

		resSeries.fireDataChanged();
		assertEquals("change received", 7, events.size());
		
		// switch off dynamic update
		Iterator<ICommand<?>> cIter = resSeries.getDependents().iterator();
		while (cIter.hasNext())
		{
			ICommand<?> comm = (ICommand<?>) cIter.next();
			comm.setDynamic(false);
		}
		
		// check that only the change listener event gets fired, not
		// the depedendent operation event.
		resSeries.fireDataChanged();
		assertEquals("change received", 8, events.size());

		// switch off dynamic update
		cIter = resSeries.getDependents().iterator();
		while (cIter.hasNext())
		{
			ICommand<?> comm = (ICommand<?>) cIter.next();
			comm.setDynamic(true);
		}

		// check we get two updates
		resSeries.fireDataChanged();
		assertEquals("change received", 10, events.size());

		
		// check the data lengths
		IQuantityCollection<?> newResQ = (IQuantityCollection<?>) newResSeries;
		assertEquals("correct elements", 10, newResQ.size());
		assertEquals("correct elements", 10, speedTwo.size());
		assertEquals("correct elements", 10, resSeries.size());
		
		// check the units haven't changed
		assertEquals("units still valid", resUnits, iq.getUnits().toString());
		assertEquals("units still valid", resUnits2, iq2.getUnits().toString());


		
	}
}
