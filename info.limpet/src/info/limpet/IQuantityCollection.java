package info.limpet;

import javax.measure.Quantity;

import tec.units.ri.quantity.QuantityRange;

public interface IQuantityCollection<T extends Quantity<T>> extends 
			IObjectCollection<Quantity<T>>, IBaseQuantityCollection<T>
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
	void setRange(QuantityRange<T> range);

	/** allow the range of this collection to be retrieved
	 * 
	 * @return range
	 */
	QuantityRange<T> getRange();
}
