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
package info.limpet.ui.propertyeditors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class SliderPropertyDescriptor extends PropertyDescriptor
{
  private int maxValue;
  private int minValue;

  /**
   * Creates an property descriptor with the given id and display name.
   * 
   * @param id
   *          the id of the property
   * @param displayName
   *          the name to display for the property
   */
  public SliderPropertyDescriptor(Object id, String displayName, int minValue,
      int maxValue)
  {
    super(id, displayName);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  /**
   * The <code>SliderPropertyDescriptor</code> implementation of this
   * <code>IPropertyDescriptor</code> method creates and returns a new <code>SliderCellEditor</code>
   * .
   * <p>
   * The editor is configured with the current validator if there is one.
   * </p>
   */
  public CellEditor createPropertyEditor(Composite parent)
  {
    CellEditor editor = new SliderCellEditor(parent, minValue, maxValue);
    if (getValidator() != null)
    {
      editor.setValidator(getValidator());
    }
    return editor;
  }

}
