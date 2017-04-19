/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data2;

import info.limpet.IContext;
import info.limpet2.IChangeListener;
import info.limpet2.ICommand;
import info.limpet2.IStoreItem;
import info.limpet2.MockContext;
import info.limpet2.NumberDocument;
import info.limpet2.SampleData;
import info.limpet2.StoreGroup;
import info.limpet2.operations.arithmetic.simple.AddQuantityOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestDynamic extends TestCase
{

	private IContext context = new MockContext();

	public void testSingleQuantityStats()
	{
		// get some data
		StoreGroup store = new SampleData().getData(10);

		// ok, let's try one that works
		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		NumberDocument speedOne =  (NumberDocument) store.get(SampleData.SPEED_ONE);
		NumberDocument speedTwo =  (NumberDocument) store.get(SampleData.SPEED_TWO);
		selection.add(speedOne);
		selection.add(speedTwo);

		int storeSize = store.size();

		Collection<ICommand> actions = new AddQuantityOperation().actionsFor(
				selection, store, context);
		assertEquals("correct number of actions", 2, actions.size());
		Iterator<ICommand> addIter = actions.iterator();
		addIter.next();
		ICommand firstAction = addIter.next();
		assertEquals("correct action", "Add numeric values in provided series (interpolated)",
				firstAction.getName());

		// run the action
		firstAction.execute();

		// check we have new data
		assertEquals("new data created", storeSize + 1, store.size());

		// ok, get the new dataset
		NumberDocument resSeries = (NumberDocument) store
				.get("Sum of Speed One Time + Speed Two Time");
		assertNotNull(resSeries);

		// remember the units
		@SuppressWarnings("unused")
    final String resUnits = resSeries.getUnits().toString();

		// ok, play about with a change
		final List<String> events = new ArrayList<String>();
		IChangeListener listener = new IChangeListener()
		{
			@Override
			public void dataChanged(IStoreItem subject)
			{
				events.add("changed!");
			}

      @Override
      public void metadataChanged(IStoreItem subject)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
        // TODO Auto-generated method stub
        
      }
		};
		resSeries.addChangeListener(listener);
		assertEquals("empty at start", 0, events.size());

		// ok, now make a change in one of hte input collections
//		speedOne.fireDataChanged();
//		assertEquals("change received", 1, events.size());
//
//		// check the units haven't changed
//		assertEquals("units still valid", resUnits, resSeries.getUnits().toString());
//
//		// ok, now make another change in one of hte input collections
//		speedOne.fireDataChanged();
//		assertEquals("second change received", 2, events.size());
//
//		selection.clear();
//		selection.add(speedTwo);
//		selection.add(resSeries);
//
//		// ok - now for a further dependent calculation
//		actions = new AddQuantityOperation().actionsFor(selection, store, context);
//		addIter = actions.iterator();
//		addIter.next();
//		firstAction = addIter.next();
//		assertEquals("correct action", "Add numeric values in provided series (interpolated)",
//				firstAction.getName());
//
//		// ok, now create the new series
//		firstAction.execute();
//
//		// now check the output changed again
//		events.clear();
//
//		NumberDocument newResSeries = (NumberDocument) store
//				.get("Sum of Speed Two Time, Sum of Speed One Time, Speed Two Time");
//		assertNotNull("found new series");
//		newResSeries.addChangeListener(listener);
//
//		final String resUnits2 = newResSeries.getUnits().toString();
//
//		// ok, fire a change in speed one
//		speedOne.fireDataChanged();
//		assertEquals("change received", 2, events.size());
//
//		speedTwo.fireDataChanged();
//		assertEquals("change received", 5, events.size());
//
//		resSeries.fireDataChanged();
//		assertEquals("change received", 7, events.size());
//
//		// switch off dynamic update
//		Iterator<ICommand> cIter = resSeries.getDependents().iterator();
//		while (cIter.hasNext())
//		{
//			ICommand comm = (ICommand) cIter.next();
//			comm.setDynamic(false);
//		}
//
//		// check that only the change listener event gets fired, not
//		// the depedendent operation event.
//		resSeries.fireDataChanged();
//		assertEquals("change received", 8, events.size());
//
//		// switch off dynamic update
//		cIter = resSeries.getDependents().iterator();
//		while (cIter.hasNext())
//		{
//			ICommand comm = (ICommand) cIter.next();
//			comm.setDynamic(true);
//		}
//
//		// check we get two updates
//		resSeries.fireDataChanged();
//		assertEquals("change received", 10, events.size());
//
//		// check the data lengths
//		NumberDocument newResQ = (NumberDocument) newResSeries;
//		assertEquals("correct elements", 10, newResQ.size());
//		assertEquals("correct elements", 10, speedTwo.size());
//		assertEquals("correct elements", 10, resSeries.size());
//
//		// check the units haven't changed
//		assertEquals("units still valid", resUnits, resSeries.getUnits().toString());
//		assertEquals("units still valid", resUnits2, newResSeries.getUnits().toString());

	}
}
