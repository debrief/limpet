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
package info.limpet.rcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class PlottingHelpers
{
  /** protected constructor, to prevent accidential initialisation
   * 
   */
  protected PlottingHelpers()
  {
    
  }
  
	private static final List<Color> COLS;
	static
	{
    Display display = Display.getCurrent();
    COLS = new ArrayList<Color>();
    for (int i = 3; i < 12; i++)
    {
      COLS.add(display.getSystemColor(i));
    }
	  
	}

	public static Color colorFor(String seriesName)
	{
		Color res = null;
		
		
		int thisIndex = Math.abs(seriesName.hashCode() % COLS.size());
		
		res = COLS.get(thisIndex);
		
		return res;
	}
}
