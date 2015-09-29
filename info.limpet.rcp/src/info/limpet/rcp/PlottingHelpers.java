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
