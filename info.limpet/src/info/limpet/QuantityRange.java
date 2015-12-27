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
import javax.measure.Measure;
import javax.measure.quantity.Quantity;

public class QuantityRange<T extends Quantity>
{

	private Measure<Double, T> _min;
	private Measure<Double, T> _max;

	public QuantityRange(Measure<Double, T> min,
			Measure<Double, T> max)
	{
		_min = min;
		_max = max;
	}

	public Measurable<T> getMinimum()
	{
		return _min;
	}

	public Measurable<T> getMaximum()
	{
		return _max;
	}


}
