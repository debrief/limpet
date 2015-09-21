package limpet.prototype.ian_generics;

import java.util.ArrayList;
import java.util.Collection;

import javax.measure.Quantity;
import javax.measure.Unit;

abstract public class Temporal<T extends Object> extends CoreCollection
		implements ITemporalCollection {

	private ArrayList<TemporalObservation<T>> _values = new ArrayList<TemporalObservation<T>>();

	public Temporal(String name) {
		super(name);
	}

	@Override
	public long start() {
		if (size() > 0) {
			return _values.get(0).getTime();
		}
		return -1;
	}

	@Override
	public long finish() {
		if (size() > 0) {
			return _values.get(size() - 1).getTime();
		}
		return -1;
	}

	@Override
	public long duration() {
		if (size() == 1) {
			return 0;
		} else if (size() > 1) {
			return _values.get(size() - 1).getTime() - _values.get(0).getTime();
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

	public static class TemporalObservation<T> {
		private long _time;
		private T _observation;

		public TemporalObservation(long time, T observation) {
			_time = time;
			_observation = observation;
		}

		public long getTime() {
			return _time;
		}

		public T getObservation() {
			return _observation;
		}
	}

	public void add(long time, T observation) {
		// do some checking.
		// 1. this time should be equal to or newer than the last item
		if (size() > 0) {
			if (_values.get(_values.size() - 1)._time > time) {
				throw new RuntimeException(
						"Temporal quantities must arrive in time order");
			}
		}
		_values.add(new TemporalObservation<T>(time, observation));
	}

	public static class QuantityType<Q extends Quantity<?>> extends
			Temporal<Quantity<?>> {
		private Unit<?> _myUnits;

		public QuantityType(String name, Unit<?> units) {
			super(name);
			_myUnits = units;
		}

		@Override
		public boolean isQuantity() {
			return true;
		}

		@Override
		public void add(long time, Quantity<?> observation) {
			if (_myUnits != observation.getUnit()) {
				throw new RuntimeException("New data value in wrong units");
			}
			super.add(time, observation);
		}

	}

	public static class ObjectType extends Temporal<Object> {
		public ObjectType(String name) {
			super(name);
		}

		@Override
		public boolean isQuantity() {
			return false;
		}
	}

	@Override
	abstract public boolean isQuantity();

	public Collection<TemporalObservation<T>> getMeasurements() {
		return _values;
	}

	@Override
	public int size() {
		return _values.size();
	}

	@Override
	public boolean isTemporal() {
		return true;
	}

}
