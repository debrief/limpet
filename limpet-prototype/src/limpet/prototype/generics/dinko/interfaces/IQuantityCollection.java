package limpet.prototype.generics.dinko.interfaces;

import java.util.Collection;

import javax.measure.Quantity;

public interface IQuantityCollection<T extends Quantity<T>> extends IObjectCollection<Quantity<T>>
{
	Collection<Quantity<T>> getValues();

}
