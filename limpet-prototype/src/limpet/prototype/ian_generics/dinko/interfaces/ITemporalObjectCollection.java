package limpet.prototype.ian_generics.dinko.interfaces;

import java.util.Collection;

public interface ITemporalObjectCollection<T extends Object> extends IObjectCollection<T>
{

	/** get the times of the observations
	 * 
	 * @return
	 */
	public Collection<Long> getTimes();

	/** add this new item
	 * 
	 * @param time
	 * @param object
	 */
	public void add(long time, T object);

}
