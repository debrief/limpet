package info.limpet.analysis;

import info.limpet.ICollection;

import java.util.List;

public abstract class SimpleDescriptive implements IAnalysis
{

	@Override
	public void analyse(List<ICollection> selection)
	{
		// 
	}

	abstract protected void presentResults(List<String> titles, List<String> values);
}
