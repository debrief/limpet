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

import info.limpet.ICollection;
import info.limpet.IContext;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.csv.CsvGenerator;

public abstract class AbstractLimpetAction 
{

	private IContext context;
	private String text;
	private String imageName;
	
	public AbstractLimpetAction(IContext context)
	{
		super();
		this.context = context;
	}
	
	protected List<IStoreItem> getSelection()
	{
		return context.getSelection();
	}

	protected IStore getStore()
	{
		return context.getStore();
	}

	protected IContext getContext()
	{
		return context;
	}

	protected String getCsvString()
	{
		List<IStoreItem> selection = getSelection();
		if (selection.size() == 1 && selection.get(0) instanceof ICollection)
		{
			return CsvGenerator.generate((ICollection) selection.get(0));
		}
		return null;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public abstract void run();

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}
	
}
