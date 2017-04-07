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
package info.limpet.data2;

import static javax.measure.unit.SI.METRE;
import info.limpet2.IStoreItem;
import info.limpet2.NumberDocument;
import info.limpet2.NumberDocumentBuilder;
import info.limpet2.analysis.AnalysisLibrary;
import info.limpet2.analysis.IAnalysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Length;

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
		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		NumberDocumentBuilder len1b = new NumberDocumentBuilder("lengths 1", METRE.asType(Length.class), null); 
		
		len1b.add(12d);
		len1b.add(13d);
		len1b.add(15d);
		len1b.add(21d);
		
		NumberDocument len1 = len1b.toDocument();

    selection.add(len1);

		// run the analysis
		ia.analyse(selection);
		
		
		// check the results
		outList(tList, vList);
		
		len1b.clear();
    len1b.add(12d);
    len1 = len1b.toDocument();
		
		selection.clear();
		selection.add(len1);
		ia.analyse(selection);

	}
	
//	public void testSingleObjectStats()
//	{
//		final List<String> tList = new ArrayList<String>();
//		final List<String> vList = new ArrayList<String>();
//
//		IAnalysis ia = new AnalysisLibrary()
//		{
//
//			@Override
//			protected void presentResults(List<String> titles, List<String> values)
//			{
//				tList.addAll(titles);
//				vList.addAll(values);
//			}
//		};
//		
//		// collate the data
//		List<IStoreItem> selection = new ArrayList<IStoreItem>();
//		ObjectCollection<String> len1 = new ObjectCollection<String>("some strings"); 
//		selection.add(len1);
//		
//		len1.add("a");
//		len1.add("b");
//		len1.add("c");
//		len1.add("a");
//		len1.add("b");
//		len1.add("a");
//		
//		// run the analysis
//		ia.analyse(selection);
//		
//		// check the results
////		assertEquals("enough titles", 1, tList.size());
////		assertEquals("enough values", 1, vList.size());
//		
//		outList(tList, vList);
//	}
	
	// TODO: reinstate once we store indexed objects
//	public void testTimeFrequencyStats()
//	{
//		final List<String> tList = new ArrayList<String>();
//		final List<String> vList = new ArrayList<String>();
//
//		TimeFrequencyBins tBins = new TimeFrequencyBins()
//		{
//			@Override
//			protected void presentResults(List<String> titles, List<String> values)
//			{
//				tList.addAll(titles);
//				vList.addAll(values);
//			}
//		};
//		
//		// collate the data
//		List<IStoreItem> selection = new ArrayList<IStoreItem>();
//		TemporalObjectCollection<String> len1 = new TemporalObjectCollection<String>("some strings"); 
//		selection.add(len1);
//		
//		long t = new Date().getTime();
//		
//		len1.add(t + 10000, "a");
//		len1.add(t + 20000, "b");
//		len1.add(t + 60000, "c");
//		len1.add(t + 120000, "a");
//		len1.add(t + 130000, "b");
//		len1.add(t + 180000, "a");
//		
//		// run the analysis
//		tBins.analyse(selection);
//		
//		// check the results
////		assertEquals("enough titles", 1, tList.size());
////		assertEquals("enough values", 1, vList.size());
//		
//		outList(tList, vList);
//	}
	
	private void outList(List<String> list, List<String> values)
	{
		System.out.println("================");
		Iterator<String> tIter = list.iterator();
		Iterator<String> vIter = values.iterator();
		while (tIter.hasNext())
		{
			StringBuffer output = new StringBuffer();
			String nextT = tIter.next();
			if (nextT.length() > 0)
			{
				output.append(nextT);
				output.append(":");
			}
			output.append(vIter.next());
			System.out.println(output.toString());
		}
	}

}
