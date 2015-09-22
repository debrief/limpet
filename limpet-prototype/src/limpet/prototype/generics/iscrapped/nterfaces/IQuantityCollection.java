package limpet.prototype.generics.iscrapped.nterfaces;

import javax.measure.Quantity;

public interface IQuantityCollection<T extends Quantity<T>> extends IBaseQuantityCollection<T> {

	public void add(Quantity<T> quantity);

}
