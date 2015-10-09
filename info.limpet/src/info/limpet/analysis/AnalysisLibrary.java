package info.limpet.analysis;

import info.limpet.IStore.IStoreItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract public class AnalysisLibrary extends CoreAnalysis
{
	final List<IAnalysis> _library;
	final List<String> _titles = new ArrayList<String>();
	final List<String> _values = new ArrayList<String>();

	public AnalysisLibrary()
	{
		super("Library of analysis routines");

		_library = new ArrayList<IAnalysis>();

		_library.add(new GeneralDescription()
		{
			protected void presentResults(List<String> titles, List<String> values)
			{
				output(getName(), titles, values);
			}
		});
		_library.add(new SimpleDescriptiveQuantity()
		{
			protected void presentResults(List<String> titles, List<String> values)
			{
				output(getName(), titles, values);
			}
		});
		_library.add(new SimpleDescriptiveObject()
		{
			protected void presentResults(List<String> titles, List<String> values)
			{
				output(getName(), titles, values);
			}
		});
		_library.add(new QuantityFrequencyBins()
		{
			protected void presentResults(List<String> titles, List<String> values)
			{
				output(getName(), titles, values);
			}
		});
		_library.add(new ObjectFrequencyBins()
		{
			protected void presentResults(List<String> titles, List<String> values)
			{
				output(getName(), titles, values);
			}
		});

	}

	@Override
	public void analyse(List<IStoreItem> selection)
	{
		// clear the lists
		_titles.clear();
		_values.clear();

		Iterator<IAnalysis> iter = _library.iterator();
		while (iter.hasNext())
		{
			// get the next analysis library
			IAnalysis iAnalysis = (IAnalysis) iter.next();

			// and run this analysis
			iAnalysis.analyse(selection);
		}

		presentResults(_titles, _values);

	}

	protected void output(String title, List<String> titles, List<String> values)
	{
		if (titles.size() > 0)
		{
			_titles.add("= " + title + " =");
			_values.add("");
			_titles.addAll(titles);
			_values.addAll(values);
		}
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);

}
