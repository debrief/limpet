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
package info.limpet.data.operations.arithmetic;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.store.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DeleteCollectionOperation implements IOperation<IStoreItem>
{
	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination, IContext context)
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
					selection, destination, context);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return selection.size() > 0;
	}

	public static class DeleteCollection extends AbstractCommand<IStoreItem>
	{

		public DeleteCollection(String title, List<IStoreItem> selection,
				IStore store, IContext context)
		{
			super(title, "Delete specific collections", store, false, false, selection,
					context);
		}

		@Override
		public void execute()
		{
			// tell each series that we're a dependent
			Iterator<IStoreItem> iter = getInputs().iterator();
			while (iter.hasNext())
			{
				IStoreItem iCollection = iter.next();
				
				// do we know the parent?
				IStoreGroup parent = iCollection.getParent();
				if (parent != null)
				{
					parent.remove(iCollection);
				}
				else
				{
					// hmm, must be at the top level
					IStore store = getStore();
					if (store instanceof StoreGroup)
					{
						StoreGroup mem = (StoreGroup) store;
						mem.remove(iCollection);
					}
				}				
			}
		}

		@Override
		protected void recalculate(IStoreItem subject)
		{
			// don't worry
		}

		@Override
		protected String getOutputName()
		{
			// special case, don't worry
			return null;
		}

	}

}
