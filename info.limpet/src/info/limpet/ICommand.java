/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
