package info.limpet.data.commands;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractCommand<T extends ICollection> implements
		ICommand<T>
{
	final private String _title;
	final private String _description;
	final private boolean _canUndo;
	final private boolean _canRedo;
	final private IStore _store;

	final protected List<T> _inputs;
	final protected List<T> _outputs;

	/** whether the command should recalculate if its children change
	 * 
	 */
	private boolean _dynamic = true;
	final private String _outputName;

	public AbstractCommand(String title, String description, String outputName, IStore store,
			boolean canUndo, boolean canRedo, List<T> inputs)
	{
		_title = title;
		_description = description;
		_store = store;
		_canUndo = canUndo;
		_canRedo = canRedo;
		_outputName = outputName;

		_inputs = new ArrayList<T>(inputs);
		_outputs = new ArrayList<T>();
	}
	
	protected String getOutputName()
	{
		return _outputName;
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
		return _dynamic;
	}

	@Override
	public void setDynamic(boolean dynamic)
	{
		this._dynamic = dynamic;
	}

	@Override
	public void dataChanged(ICollection subject)
	{
		// are we doing live updates?
		if (_dynamic)
		{
			// do the recalc
			recalculate();

			// now tell the outputs they have changed
			Iterator<T> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				T t = (T) iter.next();
				t.fireChanged();
			}
		}
	}

	abstract protected void recalculate();

	@Override
	public void collectionDeleted(ICollection subject)
	{
	}

	public IStore getStore()
	{
		return _store;
	}

	@Override
	public String getTitle()
	{
		return _title;
	}

	@Override
	public String getDescription()
	{
		return _description;
	}

	@Override
	public void execute()
	{
		// ok, register as a listener with the input files
		Iterator<T> iter = _inputs.iterator();
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
		return _canUndo;
	}

	@Override
	public boolean canRedo()
	{
		return _canRedo;
	}

	public List<T> getInputs()
	{
		return _inputs;
	}

	@Override
	public List<T> getOutputs()
	{
		return _outputs;
	}

	public void addOutput(T output)
	{
		_outputs.add(output);
	}

}
