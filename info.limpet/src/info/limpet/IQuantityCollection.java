package info.limpet;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;


//public static interface IQuantityCollection<Q extends Quantity>
//{
//	void add(Measurable<Q> item);
//	void add(double newVal);
//}

public interface IQuantityCollection<Q extends Quantity> extends 
			IObjectCollection<Measurable<Q>>, IBaseQuantityCollection<Q>
{
	/** add a quantity, using the default units for this collection
	 * 
	 * @param value
	 */
	public void add(Number value);

	/** if this collection just contains a single value, we allow
	 * that value to be changed in-place
	 * 
	 * @param newValue
	 */
	public void replaceSingleton(double newValue);

	/** allow the range of this collection to be specified
	 * 
	 * @param range
	 */
	void setRange(QuantityRange<Q> range);

	/** allow the range of this collection to be retrieved
	 * 
	 * @return range
	 */
	QuantityRange<Q> getRange();
}
