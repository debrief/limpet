package limpet.prototype.ian_generics;

public interface ITemporalObjectCollection<T extends Object> extends ITemporalCollection{
	public void add(long time, T object);
}
