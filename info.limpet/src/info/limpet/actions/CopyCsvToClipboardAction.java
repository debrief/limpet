/*******************************************************************************
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
 *******************************************************************************/
package info.limpet.actions;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.csv.CsvGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CopyCsvToClipboardAction implements IOperation<IStoreItem>
{


	/** encapsulate creating a location into a command
	 * 
	 * @author ian
	 *
	 */
	public static class CopyCsvToClipboardCommand extends AbstractCommand<IStoreItem>
	{
		private List<IStoreItem> _selection;
		
		public static String getCsvString(List<IStoreItem> selection)
		{
			if (selection.size() == 1 && selection.get(0) instanceof ICollection)
			{
				return CsvGenerator.generate((ICollection) selection.get(0));
			}
			return null;
		}
		
				
		public CopyCsvToClipboardCommand(String title, List<IStoreItem> selection, IStore store,
				IContext context)
		{
			super(title, "Export selection to clipboard as CSV", store, false, false, null, context);
			_selection = selection;
		}
		
		@Override
		public void execute()
		{
			String csv = getCsvString(_selection);
			if (csv != null && !csv.isEmpty())
			{
				getContext().placeOnClipboard(csv); 
			}
			else
			{
				getContext().openInformation("Data Manager Editor",
						"Cannot copy current selection");
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
			return getContext().getInput("Create location", NEW_DATASET_MESSAGE, "");
		}
		
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination, IContext context)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			// hmm, see if we have a single collection selected
			ICommand<IStoreItem> newC = null;
			if (selection.size() == 1)
			{
					newC = new CopyCsvToClipboardCommand("Export to CSV", selection, destination, context);
					res.add(newC);
			}
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return (selection.size() == 1 && selection.get(0) instanceof ICollection);
	}	
	
}
