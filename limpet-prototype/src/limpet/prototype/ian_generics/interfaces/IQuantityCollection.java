package limpet.prototype.ian_generics.interfaces;

import javax.measure.Quantity;

public interface IQuantityCollection<T extends Quantity<T>> extends IBaseQuantityCollection<T> {

	public void add(Quantity<T> quantity);

}
