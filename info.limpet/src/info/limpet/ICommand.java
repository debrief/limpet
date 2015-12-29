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
package info.limpet;

import info.limpet.IStore.IStoreItem;

import java.util.List;


/** encapsulation of some change to data
 * 
 * @author ian
 *
 */
public interface ICommand<T extends IStoreItem> extends IChangeListener, IStoreItem
{
	public String getDescription();
	public void execute();
	public void undo();
	public void redo();
	public boolean canUndo();
	public boolean canRedo();
	public List<T> getOutputs();
	public List<T> getInputs();
	boolean getDynamic();
	void setDynamic(boolean dynamic);
	
	final static String NEW_DATASET_MESSAGE = "Provide name for new dataset"; 
	
}
