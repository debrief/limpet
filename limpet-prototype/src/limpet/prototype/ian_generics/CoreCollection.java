package limpet.prototype.ian_generics;

abstract public class CoreCollection implements ICollection {

	private String _name;

	public CoreCollection(String name)
	{
		_name = name;
	}
	
	@Override
	final public String name() {
		return _name;
	}
}
