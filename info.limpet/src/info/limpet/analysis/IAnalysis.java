package info.limpet.analysis;

import info.limpet.IStore.IStoreItem;

import java.util.List;

public interface IAnalysis
{
	/** perform some analysis on the specified collections
	 * 
	 * @param selection
	 */
	public void analyse(List<IStoreItem> selection);

	public String getName();
}
