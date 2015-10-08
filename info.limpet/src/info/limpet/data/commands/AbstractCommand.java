package info.limpet.data.commands;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractCommand<T extends ICollection> implements
		ICommand<T>
{
	final private String title;
	final private String description;
	final private boolean canUndo;
	final private boolean canRedo;
	final private IStore store;

	final protected List<T> inputs;
	final protected List<T> outputs;

	/**
	 * whether the command should recalculate if its children change
	 * 
	 */
	private boolean dynamic = true;
	final private String outputName;

	public AbstractCommand(String title, String description, String outputName,
			IStore store, boolean canUndo, boolean canRedo, List<T> inputs)
	{
		this.title = title;
		this.description = description;
		this.store = store;
		this.canUndo = canUndo;
		this.canRedo = canRedo;
		this.outputName = outputName;

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

	protected String getOutputName()
	{
		return outputName;
	}

	protected int getNonSingletonArrayLength(List<ICollection> inputs)
	{
		int size = 0;

		Iterator<ICollection> iter = inputs.iterator();
		while (iter.hasNext())
		{
			IQuantityCollection<?> thisC = (IQuantityCollection<?>) iter.next();
			if (thisC.size() > 1)
			{
				size = thisC.size();
				break;
			}
		}

		return size;
	}

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
				t.fireChanged();
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
	public String getTitle()
	{
		return title;
	}

	@Override
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

}
