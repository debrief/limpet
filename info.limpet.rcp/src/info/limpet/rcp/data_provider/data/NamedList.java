package info.limpet.rcp.data_provider.data;

import java.util.ArrayList;

/** utility class that stores a list of items, with a particular name
 * 
 * @author ian
 *
 * @param <Object>
 */
@SuppressWarnings("hiding")
public class NamedList<Object> extends ArrayList<Object> implements LimpetWrapper
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private String _name;
	private final LimpetWrapper _parent;
	
	public NamedList(LimpetWrapper parent, String name)
	{
		_name = name;
		_parent = parent;
	}
	
	public String toString()
	{
		return _name;
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
	
}