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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import info.limpet.IContext;
import info.limpet.actions.AbstractLimpetAction;
import info.limpet.rcp.Activator;

public class ActionWrapper extends Action
{
	private AbstractLimpetAction limpetAction;

	public ActionWrapper(AbstractLimpetAction limpetAction)
	{
		this.limpetAction = limpetAction;
		setText(limpetAction.getText());
		setImageDescriptor(getImageDescriptor(limpetAction.getImageName()));
	}

	@Override
	public void run()
	{
		limpetAction.run();
	}

	private ImageDescriptor getImageDescriptor(String actionName)
	{
		switch (actionName)
		{
		case IContext.ADD_LAYER_ACTION_NAME:
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE);
		case IContext.COPY_CSV_TO_CLIPBOARD:
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED);
		case IContext.COPY_CSV_TO_FILE:
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT);
		case IContext.GENERATE_DATA:
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD);
		default:
			break;
		}
		return Activator.getImageDescriptor(actionName);
	}

}
