package info.limpet;

import javax.measure.quantity.Quantity;


public interface ITemporalQuantityCollection<T extends Quantity> extends
		ITemporalObjectCollection<T>,IBaseQuantityCollection<T>, IQuantityCollection<T>
{

	/** allow values to be added without explicitly specifying units
	 * 
	 * @param time timestamp
	 * @param value the value to add (cast to existing units)
	 */
	void add(long time, Number value);

}
