package info.limpet;


import java.util.List;

/** a storage container for collections, stored in a tree structure.
 * 
 * @author ian
 *
 */
public interface IStore
{
	/** place the new collections alongside the specified target
	 * 
	 * @param target
	 * @param results
	 */
	void addAlongside(ICollection target, List<ICollection> results);
	
	/** add the new collections at the root level
	 * 
	 * @param results
	 */
	void add(List<ICollection> results);
	
	/** get all the collections
	 * 
	 * @return
	 */
	List<ICollection> getAll();
}
