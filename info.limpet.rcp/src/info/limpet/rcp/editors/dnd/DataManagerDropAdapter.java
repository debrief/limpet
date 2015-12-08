package info.limpet.rcp.editors.dnd;

import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.data.csv.CsvParser;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.InMemoryStore;
import info.limpet.rcp.Activator;
import info.limpet.rcp.data_provider.data.GroupWrapper;
import info.limpet.rcp.editors.LimpetDragListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

public class DataManagerDropAdapter extends ViewerDropAdapter
{

	private IStore _store;

	public DataManagerDropAdapter(Viewer viewer, IStore store)
	{
		super(viewer);
		this._store = store;
	}

	@Override
	public boolean performDrop(Object data)
	{
		if (!(data instanceof String[]) && !(data instanceof List[])
				&& (!(data instanceof String)))
		{
			return false;
		}
		if (data instanceof String[])
		{
			String[] fileNames = (String[]) data;
			return filesDropped(fileNames);
		}
		if (data instanceof String)
		{
			// ok, it's some data from the control itself.

			final boolean validTarget;
			final Object target = getCurrentTarget();
			if (target == null)
				validTarget = true;
			else if (target instanceof GroupWrapper)
				validTarget = true;
			else
				validTarget = false;

			if (!validTarget)
			{
				return false;
			}
			else
			{
				String str = (String) data;
				String[] items = str.split(LimpetDragListener.SEPARATOR);
				for (int i = 0; i < items.length; i++)
				{
					String thisS = items[i];

					// find the item
					IStoreItem item = _store.get(UUID.fromString(thisS));

					if (item == null)
					{
						Activator.logError(Status.ERROR,
								"Failed to find matching UUID, from another editor?", null);
						return false;
					}

					IStoreGroup hisParent = item.getParent();
					if (hisParent != null)
					{
						// remove the item from the current parent
						hisParent.remove(item);
					}
					else
					{
						if (_store instanceof InMemoryStore)
						{
							InMemoryStore mem = (InMemoryStore) _store;
							mem.remove(item);
						}
					}

					// sort out the object
					if (target instanceof GroupWrapper)
					{
						GroupWrapper group = (GroupWrapper) target;
						// and add to the new one
						group.getGroup().add(item);
					}
					else
					{
						// ok, put it at the top level
						_store.add(item);
					}
				}
				return true;
			}

		}
		// else if (data instanceof List[])
		// {
		// Object target = getCurrentTarget();
		// if (target == getSelectedObject())
		// {
		// return false;
		// }
		// if (target instanceof GroupWrapper)
		// {
		// for (@SuppressWarnings("rawtypes")
		// List list : (List[]) data)
		// {
		// for (Object object : list)
		// {
		// if (object instanceof GroupWrapper)
		// {
		// GroupWrapper groupWrapper = (GroupWrapper) object;
		// List<IStoreItem> l = new ArrayList<IStoreItem>();
		// StoreGroup storeGroup = new StoreGroup(groupWrapper.toString());
		// l.add(storeGroup);
		// storeGroup.addAll(groupWrapper.getGroup());
		// ((GroupWrapper) target).getGroup().addAll(l);
		// }
		// else if (object instanceof CollectionWrapper)
		// {
		// CollectionWrapper collectionWrapper = (CollectionWrapper) object;
		// ((GroupWrapper) target).getGroup().add(
		// collectionWrapper.getCollection());
		// }
		// }
		// }
		// changed();
		// return true;
		// }
		// }
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType)
	{
		boolean validTarget = (target == null) || (target instanceof GroupWrapper);

		return FileTransfer.getInstance().isSupportedType(transferType)
				|| TextTransfer.getInstance().isSupportedType(transferType)
				|| validTarget;
	}

	private boolean filesDropped(String[] fileNames)
	{
		if (fileNames != null)
		{
			for (int i = 0; i < fileNames.length; i++)
			{
				String fileName = fileNames[i];
				if (fileName != null && fileName.endsWith(".csv"))
				{
					try
					{
						parseCsv(fileName);
					}
					catch (IOException e)
					{
						MessageDialog.openWarning(getViewer().getControl().getShell(),
								"Warning", "Cannot drop '" + fileName
										+ "'. See log for more details");
						Activator.log(e);
						return false;
					}
				}
				else if (fileName != null && fileName.endsWith(".lap"))
				{
					// defer that
					// try
					// {
					// parseLap(fileName);
					// }
					// catch (IOException e)
					// {
					// MessageDialog.openWarning(getViewer().getControl().getShell(),
					// "Warning", "Cannot drop '" + fileName +
					// "'. See log for more details");
					// Activator.log(e);
					// return false;
					// }
				}
			}
		}
		return true;
	}

	private void parseCsv(String fileName) throws IOException
	{
		List<IStoreItem> collections = new CsvParser().parse(fileName);
		Object target = getCurrentTarget();
		if (target instanceof GroupWrapper)
		{
			((GroupWrapper) target).getGroup().addAll(collections);
		}
		else
		{
			_store.addAll(collections);
		}
		changed();
	}

	private void changed()
	{
		getViewer().refresh();
	}

	@SuppressWarnings("unused")
	private void parseLap(String fileName) throws IOException
	{
		Object target = getCurrentTarget();
		IStore store = new XStreamHandler().load(fileName);
		final List<IStoreItem> list = new ArrayList<IStoreItem>();
		if (store instanceof InMemoryStore && target instanceof GroupWrapper)
		{
			final Iterator<IStoreItem> iter = ((InMemoryStore) store).iterator();
			GroupWrapper groupWrapper = (GroupWrapper) target;
			while (iter.hasNext())
			{
				final IStoreItem item = iter.next();
				list.add(item);
			}
			groupWrapper.getGroup().addAll(list);
		}
		changed();
	}

}
