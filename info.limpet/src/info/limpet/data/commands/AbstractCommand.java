package info.limpet.data.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.UIProperty;

public abstract class AbstractCommand<T extends IStoreItem> implements
		ICommand<T>
{

	final private String title;
	final private String description;
	final private boolean canUndo;
	final private boolean canRedo;
	final private IStore store;

	final protected List<T> inputs;
	final protected List<T> outputs;

	IStoreGroup _parent;

	/**
	 * whether the command should recalculate if its children change
	 * 
	 */
	private boolean dynamic = true;
	transient private UUID uuid;
	private transient final IContext context;

	public AbstractCommand(String title, String description, IStore store,
			boolean canUndo, boolean canRedo, List<T> inputs, IContext context)
	{
		this.title = title;
		this.description = description;
		this.store = store;
		this.canUndo = canUndo;
		this.canRedo = canRedo;
		this.context = context;

		if (inputs != null)
		{
			this.inputs = new ArrayList<T>(inputs);
		}
		else
		{
			this.inputs = null;
		}
		this.outputs = new ArrayList<T>();
	}

	/**
	 * provide access to the context object
	 * 
	 * @return the context object
	 */
	protected IContext getContext()
	{
		return context;
	}

	@Override
	public UUID getUUID()
	{
		if (uuid == null)
		{
			uuid = UUID.randomUUID();
		}
		return uuid;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUUID() == null) ? 0 : getUUID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IStoreGroup other = (IStoreGroup) obj;
		if (getUUID() == null)
		{
			if (other.getUUID() != null)
				return false;
		}
		else if (!getUUID().equals(other.getUUID()))
			return false;
		return true;
	}

	/**
	 * provide a name for the single output dataset
	 * 
	 * @return a string to use, or null to cancel the operation
	 */
	abstract protected String getOutputName();

	/**
	 * convenience function, to return the datasets as a comma separated list
	 * 
	 * @return
	 */
	protected String getSubjectList()
	{
		StringBuffer res = new StringBuffer();

		@SuppressWarnings("unchecked")
		Iterator<IStoreItem> iter = (Iterator<IStoreItem>) getInputs().iterator();
		int ctr = 0;
		while (iter.hasNext())
		{
			IStore.IStoreItem storeItem = (IStore.IStoreItem) iter.next();
			if (ctr++ > 0)
			{
				res.append(", ");
			}
			res.append(storeItem.getName());
		}

		return res.toString();
	}

	protected int getNonSingletonArrayLength(List<IStoreItem> inputs)
	{
		int size = 0;

		Iterator<IStoreItem> iter = inputs.iterator();
		while (iter.hasNext())
		{
			IQuantityCollection<?> thisC = (IQuantityCollection<?>) iter.next();
			if (thisC.size() >= 1)
			{
				size = thisC.size();
				break;
			}
		}

		return size;
	}

	@UIProperty(name="Dynamic updates", category=UIProperty.CATEGORY_LABEL)
	@Override	
	public boolean getDynamic()
	{
		return dynamic;
	}

	@Override
	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	@Override
	public void metadataChanged(IStoreItem subject)
	{
		// TODO: do a more intelligent/informed processing of metadata changed
		dataChanged(subject);
	}

	@Override
	public IStoreGroup getParent()
	{
		return _parent;
	}

	@Override
	public void setParent(IStoreGroup parent)
	{
		_parent = parent;
	}

	@Override
	public void dataChanged(IStoreItem subject)
	{
		// are we doing live updates?
		if (dynamic)
		{
			// do the recalc
			recalculate();

			// now tell the outputs they have changed
			Iterator<T> iter = outputs.iterator();
			while (iter.hasNext())
			{
				T t = (T) iter.next();
				t.fireDataChanged();
			}
		}
	}

	abstract protected void recalculate();

	@Override
	public void collectionDeleted(IStoreItem subject)
	{
	}

	public IStore getStore()
	{
		return store;
	}

	@Override
	@UIProperty(name="Description", category=UIProperty.CATEGORY_LABEL)
	public String getDescription()
	{
		return description;
	}

	@Override
	public void execute()
	{
		// ok, register as a listener with the input files
		Iterator<T> iter = inputs.iterator();
		while (iter.hasNext())
		{
			T t = (T) iter.next();
			t.addChangeListener(this);
		}
	}

	@Override
	public void undo()
	{
		throw new UnsupportedOperationException(
				"Should not be called, undo not provided");
	}

	@Override
	public void redo()
	{
		throw new UnsupportedOperationException(
				"Should not be called, redo not provided");
	}

	@Override
	public boolean canUndo()
	{
		return canUndo;
	}

	@Override
	public boolean canRedo()
	{
		return canRedo;
	}

	public List<T> getInputs()
	{
		return inputs;
	}

	@Override
	public List<T> getOutputs()
	{
		return outputs;
	}

	public void addOutput(T output)
	{
		outputs.add(output);
	}

	@UIProperty(name="Name", category=UIProperty.CATEGORY_LABEL)
	@Override
	public String getName()
	{
		return title;
	}

	@Override
	public boolean hasChildren()
	{
		return true;
	}

	@Override
	public void addChangeListener(IChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeChangeListener(IChangeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void fireDataChanged()
	{
		// TODO Auto-generated method stub

	}

}
