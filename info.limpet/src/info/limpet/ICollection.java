package info.limpet;

import java.util.List;

public interface ICollection
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

	public void addChangeListener(IChangeListener listener);
	public void removeChangeListener(IChangeListener listener);
	
	/** indicate that the collection has changed
	 *  Note: both registeered listeners and dependents are informed of the change
	 */
	public void fireChanged();
	
	/** what type is stored in collection
	 * 
	 * @return
	 */
	Class<?> storedClass();
}