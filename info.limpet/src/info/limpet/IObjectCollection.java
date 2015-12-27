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

import java.util.List;

public interface IObjectCollection<T extends Object> extends ICollection
{
	public List<T> getValues();
	public void add(T value);	
	public void clear();
	public void clearQuiet();
}
