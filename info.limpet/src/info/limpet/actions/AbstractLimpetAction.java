package info.limpet.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import info.limpet.ICollection;
import info.limpet.IContext;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.csv.CsvGenerator;

public abstract class AbstractLimpetAction extends Action
{

	private IContext context;
	
	public AbstractLimpetAction(IContext context)
	{
		super();
		this.context = context;
	}
	
	protected ISelection getSelection()
	{
		return context.getSelection();
	}

	protected IStore getStore()
	{
		return context.getStore();
	}

	protected IContext getContext()
	{
		return context;
	}

	protected List<IStoreItem> getSuitableObjects()
	{
		ArrayList<IStoreItem> matches = new ArrayList<IStoreItem>();

		// ok, find the applicable operations
		ISelection sel = getSelection();
		if (!(sel instanceof IStructuredSelection))
		{
			return matches;
		}
		IStructuredSelection str = (IStructuredSelection) sel;
		Iterator<?> iter = str.iterator();
		while (iter.hasNext())
		{
			Object object = (Object) iter.next();
			if (object instanceof IStoreItem)
			{
				matches.add((IStoreItem) object);
			}
			else if (object instanceof IAdaptable)
			{
				IAdaptable ada = (IAdaptable) object;
				Object match = ada.getAdapter(IStoreItem.class);
				if (match != null)
				{
					matches.add((IStoreItem) match);
				}
			}
		}

		return matches;
	}

	protected String getCsvString()
	{
		List<IStoreItem> selection = getSuitableObjects();
		if (selection.size() == 1 && selection.get(0) instanceof ICollection)
		{
			return CsvGenerator.generate((ICollection) selection.get(0));
		}
		return null;
	}

}
