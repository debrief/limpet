package info.limpet.rcp.data_provider.data;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DataModel implements IStructuredContentProvider {
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		
		return new String[] { "Data One", "Data Two", "Data Three" };
		
		
//		// collate our data series
//		StockTypes.Temporal.Speed_MSec speedSeries1 = new StockTypes.Temporal.Speed_MSec("Speed One"); 
//		StockTypes.Temporal.Speed_MSec speedSeries2 = new StockTypes.Temporal.Speed_MSec("Speed Two"); 
//		StockTypes.Temporal.Speed_MSec speedSeries3 = new StockTypes.Temporal.Speed_MSec("Speed Three"); 
//		StockTypes.Temporal.Length_M length1 = new StockTypes.Temporal.Length_M("Length One"); 
//		StockTypes.Temporal.Length_M length2 = new StockTypes.Temporal.Length_M("Length Two");
//		ObjectCollection<String> string1 = new ObjectCollection<String>("String one");
//		ObjectCollection<String> string2 = new ObjectCollection<String>("String one");
//				
//	
//		for (int i = 1; i <= 10; i++)
//		{
//			speedSeries1.add(i);
//			speedSeries2.add(Math.sin(i));
//			speedSeries3.add(3 * Math.cos(i));
//			length1.add(i % 3);
//			length2.add(i % 5);
//			string1.add("" + i);
//			string2.add("" + (i % 3));
//		}
//
//		return new Object[] {speedSeries1, speedSeries2, speedSeries3, length1, length2, string1, string2};
	}
}