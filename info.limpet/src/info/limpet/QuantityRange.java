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

	public Measure<Double, T> getMinimum()
	{
		return _min;
	}

	public Measure<Double, T> getMaximum()
	{
		return _max;
	}


}
