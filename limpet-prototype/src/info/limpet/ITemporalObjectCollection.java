package info.limpet;

public interface ITemporalObjectCollection<T extends Object> extends
		IObjectCollection<T>, IBaseTemporalCollection
{

	/**
	 * add this new item
	 * 
	 * @param time
	 * @param object
	 */
	public void add(long time, T object);

	/**
	 * combination of a timestamp with an observation
	 * 
	 * @author ian
	 * 
	 * @param <T>
	 */
	public interface Doublet<T>
	{
		long getTime();

		T getObservation();
	}

}
