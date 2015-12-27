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
package info.limpet.data.math;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Source: http://rosettacode.org/wiki/Averages/Simple_moving_average#Java
 */
public class SimpleMovingAverage
{

	private final Queue<Double> window = new LinkedList<Double>();
	private final int period;
	private double sum;

	public SimpleMovingAverage(int period)
	{
		assert period > 0 : "Period must be a positive integer";
		this.period = period;
	}

	public void newNum(double num)
	{
		sum += num;
		window.add(num);
		if (window.size() > period)
		{
			sum -= window.remove();
		}
	}

	public double getAvg()
	{
		if (window.isEmpty())
			return 0; // technically the average is undefined
		return sum / window.size();
	}

}
