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

import java.util.List;

import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.store.IGroupWrapper;

public abstract class CreateSingletonGenerator extends AbstractLimpetAction
{

	public CreateSingletonGenerator(IContext context)
	{
		super(context);
		setText("Create single " + getName() + " value");
		setImageName("icons/variable.png");

	}

	@Override
	public void run()
	{
		// get the name
		String name = "new " + getName();
		double value;

		name = getContext().getInput("New variable", "Enter name for variable", "");
		if (name == null || name.isEmpty())
		{
			return;
		}

		String str = getContext().getInput("New variable",
				"Enter initial value for variable", "");
		if (str == null || str.isEmpty())
		{
			return;
		}
		try
		{
			// get the new collection
			QuantityCollection<?> newData = generate(name);

			// add the new value
			value = Double.parseDouble(str);
			newData.add(value);

			// put the new collection in to the selected folder, or into root
			List<IStoreItem> selection = getSelection();
			if (selection != null && selection.size() > 0
					&& selection.get(0) instanceof IGroupWrapper)
			{
				IGroupWrapper gW = (IGroupWrapper) selection.get(0);
				gW.getGroup().add(newData);
			}
			else
			{
				// just store it at the top level
				IStore store = getStore();
				if (store != null)
				{
					store.add(newData);
				}
			}

		}
		catch (NumberFormatException e)
		{
			getContext().logError(Status.WARNING, "Failed to parse initial value", e);
		}
	}

	protected abstract String getName();

	protected abstract QuantityCollection<?> generate(String name);

}
