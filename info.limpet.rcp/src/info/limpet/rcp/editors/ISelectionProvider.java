package info.limpet.rcp.editors;

import info.limpet.IStore.IStoreItem;

import java.util.List;

/**
 * interface for UI elements that are able to provide the current selection
 * 
 * @author ian
 * 
 */
public interface ISelectionProvider
{
	/**
	 * retrieve the current user selection
	 * 
	 * @return
	 */
	List<IStoreItem> getSelection();
}
