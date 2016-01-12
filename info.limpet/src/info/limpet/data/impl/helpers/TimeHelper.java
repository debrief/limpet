/*****************************************************************************
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
 *****************************************************************************/
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
    {
      return (double) size() / duration();
    }
    else
    {
      return -1d;
    }
  }

  @Override
  public List<Long> getTimes()
  {
    return _times;
  }

}
