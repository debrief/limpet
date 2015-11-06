package info.limpet;

import info.limpet.IStore.IStoreItem;

import java.util.List;

public interface ICollection extends IStoreItem
{
	public String getName();
	public void setName(String name);
	public int size();
	public boolean isQuantity();
	public boolean isTemporal();
	public abstract void setDescription(String description);
	public abstract String getDescription();

	// note: dependents and precedents are intended to be persistent,
	// change listeners aren't
	
	public abstract List<ICommand<?>> getDependents();
	public abstract ICommand<?> getPrecedent();
	public void addDependent(ICommand<?> addQuantityValues);
	
	/** indicate that the collection has changed
	 *  Note: both registeered listeners and dependents are informed of the change
	 */
	public void fireDataChanged();
	
	/** indicate that the collection's metadata has changed
	 *  Note: both registeered listeners and dependents are informed of the change
	 */
	public void fireMetadataChanged();
	
	/** what type is stored in collection
	 * 
	 * @return
	 */
	Class<?> storedClass();
	
	/** tell listeners that it's about to be deleted
	 * 
	 */
	public void beingDeleted();
}