package info.limpet;

import javax.measure.Quantity;

public interface ITemporalQuantityCollection<T extends Quantity<T>> extends
		ITemporalObjectCollection<Quantity<T>>,IBaseQuantityCollection<T>, IQuantityCollection<T>
{

}
