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
package info.limpet.rcp.editors;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class OperationWrapper extends Action
{
	private IOperation<IStoreItem> limpetAction;
	private IContext context;
	private IStore store;

	public OperationWrapper(IOperation<IStoreItem> limpetAction, String title, ImageDescriptor imageDescriptor, IContext context, IStore store)
	{
		this.limpetAction = limpetAction;
		this.context = context;
		this.store = store;
		setText(title);
		setImageDescriptor(imageDescriptor);
	}

	@Override
	public void run()
	{
		Collection<ICommand<IStoreItem>> ops = limpetAction.actionsFor(context.getSelection(), store, context);
		if((ops != null) && (ops.size() == 1))
		{
			ICommand<IStoreItem> first = ops.iterator().next();
			first.execute();
		}
		else
		{
			context.openWarning("Error", "Cannot run the action for current selection");
		}
	}
}
