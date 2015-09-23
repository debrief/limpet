package info.limpet.data;

import info.limpet.ICollection;
import info.limpet.analysis.AnalysisLibrary;
import info.limpet.analysis.IAnalysis;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.samples.StockTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestAnalysis extends TestCase
{
	public void testSingleQuantityStats()
	{
		final List<String> tList = new ArrayList<String>();
		final List<String> vList = new ArrayList<String>();

		IAnalysis ia = new AnalysisLibrary()
		{
			@Override
			protected void presentResults(List<String> titles, List<String> values)
			{
				tList.addAll(titles);
				vList.addAll(values);
			}
		};
		
		// collate the data
		List<ICollection> selection = new ArrayList<ICollection>();
		StockTypes.NonTemporal.Length_M len_1 = new StockTypes.NonTemporal.Length_M("lengths 1"); 
		selection.add(len_1);
		
		len_1.add(12d);
		len_1.add(13d);
		len_1.add(15d);
		len_1.add(21d);
		
		// run the analysis
		ia.analyse(selection);
		
		// check the results
//		assertEquals("enough titles", 0, tList.size());
//		assertEquals("enough values", 0, tList.size());
		
		outList("Quantity descriptive", tList, vList);

	}
	
	public void testSingleObjectStats()
	{
		final List<String> tList = new ArrayList<String>();
		final List<String> vList = new ArrayList<String>();

		IAnalysis ia = new AnalysisLibrary()
		{

			@Override
			protected void presentResults(List<String> titles, List<String> values)
			{
				tList.addAll(titles);
				vList.addAll(values);
			}
		};
		
		// collate the data
		List<ICollection> selection = new ArrayList<ICollection>();
		ObjectCollection<String> len_1 = new ObjectCollection<String>("some strings"); 
		selection.add(len_1);
		
		len_1.add("a");
		len_1.add("b");
		len_1.add("c");
		len_1.add("a");
		len_1.add("b");
		len_1.add("a");
		
		// run the analysis
		ia.analyse(selection);
		
		// check the results
//		assertEquals("enough titles", 1, tList.size());
//		assertEquals("enough values", 1, vList.size());
		
		outList("Object descriptive", tList, vList);
	}
	
	private void outList(String title, List<String> list, List<String> values)
	{
		System.out.println("================");
		Iterator<String> tIter = list.iterator();
		Iterator<String> vIter = values.iterator();
		while (tIter.hasNext())
		{
			StringBuffer output = new StringBuffer();
			String nextT = tIter.next();
			if(nextT.length()>0)
			{
				output.append(nextT);
				output.append(":");
			}
			output.append(vIter.next());
			System.out.println(output.toString());
		}
	}

}
