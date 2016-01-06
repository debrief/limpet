package info.limpet.rcp.editors;

import info.limpet.IStore.IStoreItem;

import java.util.List;

public interface SelectionProvider
{
	public List<IStoreItem> getSelection();
}