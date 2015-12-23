package info.limpet.rcp.data_provider.data;

/*
 *	Eclipse Development using GEF and EMF: NetworkEditor example
 * 
 * (c) Copyright IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 * ddean	Initial version
 */

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import info.limpet.rcp.Activator;

/**
 * A property descriptor for boolean values that uses the CheckboxCellEditor
 * 
 * @author ddean
 * 
 */
public class CheckboxPropertyDescriptor extends PropertyDescriptor
{
	private static Image CHECKED = Activator.getImageDescriptor("icons/checked.gif").createImage();
	private static Image UNCHECKED = Activator.getImageDescriptor("icons/unchecked.gif").createImage();
	
	/**
	 * The checkbox is actually emulated by having a custom label provider to 
	 * show corresponding image for each boolean state. Solution adopted from here: 
	 * http://www.vogella.com/tutorials/EclipseJFaceTable/article.html#jfaceeditor
	 */
	private static LabelProvider CHECKBOX_LABEL_PROVIDER = new LabelProvider() {
		public Image getImage(Object element) {
			return ((Boolean)element).booleanValue() ? CHECKED : UNCHECKED;			
		};
		public String getText(Object element) {
			// we don't need text here
			return null;
		};
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
			editor.setValidator(getValidator());
		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider()
	{
		return CHECKBOX_LABEL_PROVIDER;
	}
}