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

import info.limpet.IContext;

public class CopyCsvToClipboardAction extends AbstractLimpetAction
{

	public CopyCsvToClipboardAction(IContext context)
	{
		super(context);
		setText("Copy CSV to Clipboard");
		setImageName(IContext.COPY_CSV_TO_CLIPBOARD);
	}

	@Override
	public void run()
	{
		String csv = getCsvString();
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

}
