package limpet.prototype.ian_generics;

public class TemporalObservation<T>
{
	long _time;
	T _observation;
	public TemporalObservation(long time, T observation)
	{
		_time = time;
		_observation = observation;
	}
}