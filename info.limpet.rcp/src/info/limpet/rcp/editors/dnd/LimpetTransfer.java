package info.limpet.rcp.editors.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import info.limpet.ICollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreGroup;
import info.limpet.rcp.data_provider.data.CollectionWrapper;
import info.limpet.rcp.data_provider.data.GroupWrapper;
import info.limpet.rcp.data_provider.data.LimpetWrapper;

public class LimpetTransfer extends ByteArrayTransfer
{

	private static final LimpetTransfer fInstance = new LimpetTransfer();
	private static final String TYPE_NAME = "limpet-transfer-format:" //$NON-NLS-1$
			+ System.currentTimeMillis() + ":" + fInstance.hashCode(); //$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	private LimpetTransfer()
	{
	}

	public static LimpetTransfer getInstance()
	{
		return fInstance;
	}

	@Override
	protected int[] getTypeIds()
	{
		return new int[]
		{ TYPEID };
	}

	@Override
	protected String[] getTypeNames()
	{
		return new String[]
		{ TYPE_NAME };
	}

	@Override
	protected void javaToNative(Object data, TransferData transferData)
	{
		if (!(data instanceof LimpetWrapper[]))
			return;

		LimpetWrapper[] limpetWrappers = (LimpetWrapper[]) data;
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(out);

			// write the number of elements
			dataOut.writeInt(limpetWrappers.length);

			// write each element
			for (int i = 0; i < limpetWrappers.length; i++)
			{
				writeLimpetWrapper(dataOut, limpetWrappers[i]);
			}

			// cleanup
			dataOut.close();
			out.close();
			byte[] bytes = out.toByteArray();
			super.javaToNative(bytes, transferData);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			// it's best to send nothing if there were problems
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	protected Object nativeToJava(TransferData transferData)
	{
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if (bytes == null)
			return null;
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		try
		{
			int count = in.readInt();
			@SuppressWarnings("rawtypes")
			List[] results = new ArrayList[count];
			for (int i = 0; i < count; i++)
			{
				results[i] = readWrappers(in);
			}
			return results;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	private List<LimpetWrapper> readWrappers(DataInputStream dataIn)
			throws IOException
	{
		int length = dataIn.readInt();
		byte bytes[] = new byte[length];
		dataIn.read(bytes);
		String xml = new String(bytes);
		IStore object = new XStreamHandler().fromXML(xml);
		List<LimpetWrapper> list = null;
		if (object instanceof InMemoryStore)
		{
			InMemoryStore store = (InMemoryStore) object;
			list = new ArrayList<LimpetWrapper>();
			final Iterator<IStoreItem> iter = store.iterator();
			while (iter.hasNext())
			{
				final IStoreItem item = iter.next();
				if (item instanceof ICollection)
				{
					list.add(new CollectionWrapper(null, (ICollection) item));
				}
				else if (item instanceof StoreGroup)
				{
					list.add(new GroupWrapper(null, (StoreGroup) item));
				}
			}
		}
		return list;
	}

	private static void writeLimpetWrapper(DataOutputStream dataOut,
			LimpetWrapper element) throws IOException
	{
		InMemoryStore store = new InMemoryStore();
		if (element instanceof GroupWrapper)
		{
			store.add(((GroupWrapper) element).getGroup());
		}
		else if (element instanceof CollectionWrapper)
		{
			store.add(((CollectionWrapper) element).getCollection());
		}
		String xml = new XStreamHandler().toXML(store);
		byte[] b = xml.getBytes("utf-8");
		dataOut.writeInt(b.length);
		dataOut.write(b);
	}
}
