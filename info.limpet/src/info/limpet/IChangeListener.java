package info.limpet;

import info.limpet.IStore.IStoreItem;

public interface IChangeListener
{
	/** the data in an item has changed
	 * 
	 * @param subject
	 */
	public void dataChanged(IStoreItem subject);
	
	/** an item has cosmetically changed (name, color, etc)
	 * 
	 * @param subject
	 */
	public void metadataChanged(IStoreItem subject);
	
	public void collectionDeleted(IStoreItem subject);
}
