package limpet.prototype.generics.dinko.interfaces;


public interface ITemporalObjectCollection<T extends Object> extends IObjectCollection<T>, ITemporalCollection
{

	/** add this new item
	 * 
	 * @param time
	 * @param object
	 */
	public void add(long time, T object);

}
