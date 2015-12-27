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
package info.limpet.data.impl.helpers;

import java.util.ArrayList;
import java.util.List;

import info.limpet.IBaseTemporalCollection;


public class TimeHelper implements IBaseTemporalCollection
{

	private ArrayList<Long> _times;

	public TimeHelper(ArrayList<Long> times)
	{
		_times = times;
	}

	@Override
	public long start()
	{
		if (size() > 0)
		{
			return _times.get(0);
		}
		return -1;
	}

	private int size()
	{
		return _times.size();
	}


	@Override
	public long finish()
	{
		if (size() > 0)
		{
			return _times.get(size() - 1);
		}
		return -1;
	}

	@Override
	public long duration()
	{
		if (size() == 1)
		{
			return 0;
		}
		else if (size() > 1)
		{
			return _times.get(size() - 1) - _times.get(0);
		}
		return -1;
	}

	@Override
	public double rate()
	{
		if (size() > 1)
			return size() / duration();
		else
			return -1;
	}

	@Override
	public List<Long> getTimes()
	{
		return _times;
	}

}
