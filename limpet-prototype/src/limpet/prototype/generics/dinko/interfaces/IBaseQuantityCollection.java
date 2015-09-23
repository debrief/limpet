package limpet.prototype.generics.dinko.interfaces;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

public interface IBaseQuantityCollection<T extends Quantity<T>>
{
	public Quantity<T> min();
	public Quantity<T> max();
	public Quantity<T> mean();
	public Quantity<T> variance();
	public Quantity<T> sd();
	Dimension getDimension();
	Unit<T> getUnits();
}
