package info.limpet;

import java.util.List;

public interface IObjectCollection<T extends Object> extends ICollection
{
	public List<T> getValues();
	public void add(T value);	
	public void clear();
	public void clearQuiet();
}
