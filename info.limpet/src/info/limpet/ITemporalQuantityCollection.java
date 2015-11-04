package info.limpet;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

//public interface IQuantityCollection<Q extends Quantity> extends 
//IObjectCollection<Measurable<Q>>, IBaseQuantityCollection<Q>


public interface ITemporalQuantityCollection<Q extends Quantity> extends
		ITemporalObjectCollection<Measurable<Q>>,IBaseQuantityCollection<Q>, IQuantityCollection<Q>
{

	public static enum InterpMethod{Linear, Nearest, Before, After};
	
	/** allow values to be added without explicitly specifying units
	 * 
	 * @param time timestamp
	 * @param value the value to add (cast to existing units)
	 */
	void add(long time, Number value);
	
	/** approximate the value to use at the supplied time stamp
	 * 
	 * @param time
	 * @param interpMethod
	 * @return
	 */
	Measurable<Q> interpolateValue(long time, InterpMethod interpMethod);

}
