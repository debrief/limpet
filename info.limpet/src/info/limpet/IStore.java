package info.limpet;


import java.util.List;

/** a storage container for collections, stored in a tree structure.
 * 
 * @author ian
 *
 */
public interface IStore
{
	
	/** add the new collections at the root level
	 * 
	 * @param results
	 */
	void add(List<ICollection> results);
	
	/** retrieve the named collection
	 * 
	 * @param name
	 * @return
	 */
	ICollection get(String name);
}
