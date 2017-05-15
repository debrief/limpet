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
package info.limpet.ui.data_provider.data;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.ui.data_provider.data.ISelectionProvider;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class OperationWrapper extends Action
{
	private final IOperation limpetAction;
	private final IContext context;
	private final IStoreGroup store;
	private final ISelectionProvider provider;

	public OperationWrapper(IOperation limpetAction, String title,
			ImageDescriptor imageDescriptor, IContext context, IStoreGroup store,
			ISelectionProvider provider)
	{
		this.limpetAction = limpetAction;
		this.context = context;
		this.store = store;
		this.provider = provider;
		setText(title);
		setImageDescriptor(imageDescriptor);
	}

	@Override
	public void run()
	{
		Collection<ICommand> ops = limpetAction.actionsFor(
				provider.getSelection(), store, context);
		if (ops != null && ops.size() == 1)
		{
			ICommand first = ops.iterator().next();
			first.execute();
		}
		else
		{
			context.openWarning("Error",
					"Cannot run the action for current selection");
		}
	}
}
