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
package info.limpet.rcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class PlottingHelpers
{
	private static List<Color> _cols;

	public static Color colorFor(String seriesName)
	{
		Color res = null;
		if (_cols == null)
		{
			Display display = Display.getCurrent();
			_cols = new ArrayList<Color>();
			for (int i = 3; i < 12; i++)
			{
				_cols.add(display.getSystemColor(i));
			}
		}
		
		
		int thisIndex = Math.abs(seriesName.hashCode() % _cols.size());
		
		res = _cols.get(thisIndex);
		
		return res;
	}
}
