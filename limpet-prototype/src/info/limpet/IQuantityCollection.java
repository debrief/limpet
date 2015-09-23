package info.limpet;

import javax.measure.Quantity;

public interface IQuantityCollection<T extends Quantity<T>> extends 
			IObjectCollection<Quantity<T>>, IBaseQuantityCollection<T>
{
}
