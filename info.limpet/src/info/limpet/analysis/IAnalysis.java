package info.limpet.analysis;

import info.limpet.ICollection;

import java.util.List;

public interface IAnalysis
{
	/** perform some analysis on the specified collections
	 * 
	 * @param selection
	 */
	public void analyse(List<ICollection> selection);

	public String getName();
}
