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
package info.limpet;

public interface ITemporalObjectCollection<T extends Object> extends IObjectCollection<T>,
    IBaseTemporalCollection
{

  /**
   * add this new item
   * 
   * @param time
   * @param object
   */
  void add(long time, T object);

  /**
   * combination of a timestamp with an observation
   * 
   * @author ian
   * 
   * @param <T>
   */
  interface Doublet<T>
  {
    long getTime();

    T getObservation();
  }

}
