package info.limpet.rcp.data_provider.data;

import java.util.ArrayList;

/**
 * utility class that stores a list of items, with a specific name
 * 
 * @author ian
 * 
 * @param <Object>
 */
public class NamedList extends ArrayList<Object> implements
		LimpetWrapper
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String _name;
	private final LimpetWrapper _parent;

	public NamedList(final LimpetWrapper parent, final String name)
	{
		_name = name;
		_parent = parent;
	}

	@Override
	public LimpetWrapper getParent()
	{
		return _parent;
	}

	@Override
	public java.lang.Object getSubject()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return _name;
	}

}