package limpet.prototype.generics.scrapped.temp_vs_non_temp;

import limpet.prototype.generics.iscrapped.nterfaces.ICollection;

abstract public class CoreCollection implements ICollection
{

	private String _name;

	public CoreCollection(String name)
	{
		_name = name;
	}

	@Override
	final public String name()
	{
		return _name;
	}
}
