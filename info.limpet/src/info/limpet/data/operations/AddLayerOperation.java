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

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination, IContext context)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			final String thisTitle = "Add new folder";
			// hmm, see if a group has been selected
			ICommand<IStoreItem> newC = null;
			if (selection.size() == 1)
			{
				IStoreItem first = selection.get(0);
				if (first instanceof StoreGroup)
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

	protected static class AddLayerCommand extends AbstractCommand<IStoreItem>
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
			return getContext().getInput("Add layer", "Provide name for new folder", "");
		}

	}

}
