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

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

//public interface IQuantityCollection<Q extends Quantity> extends 
//IObjectCollection<Measurable<Q>>, IBaseQuantityCollection<Q>

public interface ITemporalQuantityCollection<Q extends Quantity> extends
    ITemporalObjectCollection<Measurable<Q>>, IBaseQuantityCollection<Q>, IQuantityCollection<Q>
{

  enum InterpMethod
  {
    Linear, Nearest, Before, After
  };

  /**
   * allow values to be added without explicitly specifying units
   * 
   * @param time
   *          timestamp
   * @param value
   *          the value to add (cast to existing units)
   */
  void add(long time, Number value);

  /**
   * approximate the value to use at the supplied time stamp
   * 
   * @param time
   * @param interpMethod
   * @return
   */
  Measurable<Q> interpolateValue(long time, InterpMethod interpMethod);

}
