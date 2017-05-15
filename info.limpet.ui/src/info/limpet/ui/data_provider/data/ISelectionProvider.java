package info.limpet.ui.data_provider.data;

import info.limpet.IStoreItem;

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
