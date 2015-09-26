package info.limpet.rcp.data_provider.data;

/**
 * objects that are used in the Limpet object tree
 * 
 * @author ian
 * 
 */
public interface LimpetWrapper
{
	/**
	 * retrieve the parent of the current object
	 * 
	 * @return
	 */
	public LimpetWrapper getParent();

	/**
	 * retrieve the pure limpet object that this instance is wrapping
	 * 
	 * @return
	 */
	public Object getSubject();
}