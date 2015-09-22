package limpet.prototype.ian_generics;

import javax.measure.Quantity;

public interface ICoreQuantityCollection<T extends Quantity<T>>
{
	public Quantity<T> min();
	public Quantity<T> max();
	public Quantity<T> mean();
	public Quantity<T> sd();
}
