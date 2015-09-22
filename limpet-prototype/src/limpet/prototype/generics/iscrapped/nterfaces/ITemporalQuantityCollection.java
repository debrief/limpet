package limpet.prototype.generics.iscrapped.nterfaces;

import java.util.Iterator;

import javax.measure.Quantity;

public interface ITemporalQuantityCollection<T extends Quantity<T>> extends
		IBaseQuantityCollection<T>, ITemporalCollection
{

	public void add(long time, Quantity<T> quantity);
	
	/** produce interpolated value at specified time
	 * 
	 * @param time
	 * @return
	 */
	public Quantity<T> valueAt(long time, InterpolationMethod method);
	
	public interface Doublet<T extends Quantity<T>>
	{
		long getTime();
		Quantity<T> getObservation();
	}
	
	public Iterator<Doublet<T>> iterator();

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
