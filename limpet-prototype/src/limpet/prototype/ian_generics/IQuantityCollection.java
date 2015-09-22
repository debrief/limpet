package limpet.prototype.ian_generics;

import javax.measure.Quantity;

public interface IQuantityCollection<T extends Quantity<T>> extends ICoreQuantityCollection<T> {

	public void add(Quantity<T> quantity);

}
