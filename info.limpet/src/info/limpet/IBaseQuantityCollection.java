package info.limpet;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;


public interface IBaseQuantityCollection<T extends Quantity>
{
	public Measurable<T> min();
	public Measurable<T> max();
	public Measurable<T> mean();
	public Measurable<T> variance();
	public Measurable<T> sd();
	Dimension getDimension();
	Unit<T> getUnits();
}
