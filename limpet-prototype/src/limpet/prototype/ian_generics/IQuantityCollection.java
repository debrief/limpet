package limpet.prototype.ian_generics;

import javax.measure.Quantity;

public interface IQuantityCollection<Q extends Quantity<?>>
{
	public Q min();
	public Q max();
	public Q mean();
	public Q sd();
}
