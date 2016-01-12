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
package info.limpet.rcp.data_provider.data;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import info.limpet.ui.Activator;

/**
 * A property descriptor for boolean values that uses the CheckboxCellEditor
 * 
 * @author ddean
 * 
 */
public class CheckboxPropertyDescriptor extends PropertyDescriptor
{
  /**
   * The checkbox is actually emulated by having a custom label provider to show corresponding image
   * for each boolean state. Solution adopted from here:
   * http://www.vogella.com/tutorials/EclipseJFaceTable/article.html#jfaceeditor
   */
  private static final LabelProvider CHECKBOX_LABEL_PROVIDER =
      new LabelProvider()
      {

        /**
         * Use lazy loading for the images, since the class might be used in non-rcp (i.e. junit
         * environment)
         */
        private Image checked;
        private Image unchecked;

        public Image getImage(Object element)
        {
          return ((Boolean) element).booleanValue() ? getCheckedImage()
              : getUncheckedImage();
        };

        public String getText(Object element)
        {
          // we don't need text here
          return null;
        };

        private Image getCheckedImage()
        {
          if (checked == null)
          {
            checked =
                Activator.getImageDescriptor("icons/checked.gif").createImage();
          }
          return checked;
        }

        private Image getUncheckedImage()
        {
          if (unchecked == null)
          {
            unchecked =
                Activator.getImageDescriptor("icons/unchecked.gif")
                    .createImage();
          }
          return unchecked;
        }
      };

  /**
   * @param id
   * @param displayName
   */
  public CheckboxPropertyDescriptor(Object id, String displayName)
  {
    super(id, displayName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPr
   * opertyEditor(org.eclipse.swt.widgets.Composite)
   */
  public CellEditor createPropertyEditor(Composite parent)
  {
    CellEditor editor = new CheckboxCellEditor(parent);
    if (getValidator() != null)
    {
      editor.setValidator(getValidator());
    }
    return editor;
  }

  @Override
  public ILabelProvider getLabelProvider()
  {
    return CHECKBOX_LABEL_PROVIDER;
  }
}
