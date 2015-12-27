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

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;


public interface IBaseQuantityCollection<T extends Quantity>
{
	public Measurable<T> min();
	public Measurable<T> max();
	public Measurable<T> mean();
	public Measurable<T> variance();
	public Measurable<T> sd();
	Dimension getDimension();
	Unit<T> getUnits();
}
