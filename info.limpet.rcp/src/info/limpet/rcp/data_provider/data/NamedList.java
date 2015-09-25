package info.limpet.rcp.data_provider.data;

import java.util.ArrayList;

/** utility class that stores a list of items, with a particular name
 * 
 * @author ian
 *
 * @param <Object>
 */
@SuppressWarnings("hiding")
public class NamedList<Object> extends ArrayList<Object>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private String _name;
	
	public NamedList(String name)
	{
		_name = name;
	}
	
	public String toString()
	{
		return _name;
	}
	
}