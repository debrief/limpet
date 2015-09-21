package limpet.prototype.ian_generics.quant_vs_non_quant;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;
import javax.measure.Unit;

import limpet.prototype.ian_generics.ITemporalCollection;

public class TemporalQuantityCollection<T extends Quantity<T>> extends
		QuantityCollection<Quantity<T>> implements ITemporalCollection {
	
	public TemporalQuantityCollection(String name, Unit<?> units) {
		super(name, units);
	}

	private ArrayList<Long> _times = new ArrayList<Long>();

	public void add(long time, Quantity<T> quantity) {
		// do some checking.
		// 1. this time should be equal to or newer than the last item
		if (size() > 0) {
			if (_times.get(_times.size() - 1) > time) {
				throw new RuntimeException(
						"Temporal quantities must arrive in time order");
			}
		}

		_times.add(time);
		super.add(quantity);
	}

	public Collection<Long> getTimes(){
		return _times;
	}
	
	@Override
	public boolean isTemporal() {
		return true;
	}
	
	@Override
	public long start() {
		if (size() > 0) {
			return _times.get(0);
		}
		return -1;
	}

	@Override
	public long finish() {
		if (size() > 0) {
			return _times.get(size() - 1);
		}
		return -1;
	}

	@Override
	public long duration() {
		if (size() == 1) {
			return 0;
		} else if (size() > 1) {
			return _times.get(size() - 1) - _times.get(0);
		}
		return -1;
	}

	@Override
	public double rate() {
		if (size() > 1)
			return size() / duration();
		else
			return -1;
	}
}