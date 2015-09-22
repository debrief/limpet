package limpet.prototype.ian_generics;

import javax.measure.Quantity;

public interface ITemporalQuantityCollection<T extends Quantity<T>> extends
		ICoreQuantityCollection<T>, ITemporalCollection
{

	public void add(long time, Quantity<T> quantity);
	
	/** produce interpolated value at specified time
	 * 
	 * @param time
	 * @return
	 */
	public Quantity<T> valueAt(long time, InterpolationMethod method);

	/** choice of how to calculate an interpolated value
	 * 
	 * @author ian
	 *
	 */
	public static enum InterpolationMethod
	{
		LINEAR
	}
}
