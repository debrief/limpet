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
package info.limpet.ui.data_provider.data2;

/**
 * objects that are used in the Limpet object tree
 * 
 * @author ian
 * 
 */
public interface LimpetWrapper
{
  /**
   * retrieve the parent of the current object
   * 
   * @return the parent of the current object (to let us walk the tree)
   */
  LimpetWrapper getParent();

  /**
   * retrieve the pure limpet object that this instance is wrapping
   * 
   * @return the wrapped entity
   */
  Object getSubject();
}
