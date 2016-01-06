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

import java.util.Collection;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.operations.GenerateDummyDataOperation;

public class GenerateDataAction extends AbstractLimpetAction
{

	public GenerateDataAction(IContext context)
	{
		super(context);
		setText("Generate data");
		setImageName(IContext.GENERATE_DATA);
	}

	@Override
	public void run()
	{
		GenerateDummyDataOperation operation = new GenerateDummyDataOperation(
				"small", 20);

		Collection<ICommand<IStoreItem>> commands = operation
				.actionsFor(getSelection(), getStore(), getContext());
		if (commands.size() < 1)
		{
			getContext().openWarning("Error", "Cannot run the action for current selection");
		}
		else
		{
			commands.iterator().next().execute();
		}
	}

}
