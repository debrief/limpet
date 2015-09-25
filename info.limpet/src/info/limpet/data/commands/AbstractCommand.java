package info.limpet.data.commands;

import java.util.ArrayList;
import java.util.List;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IStore;

public abstract class AbstractCommand<T extends ICollection> implements ICommand<T>
{

	final private String _title;
	final private String _description;
	final private boolean _canUndo;
	final private boolean _canRedo;
	final private IStore _store;
	
	final protected List<T> _inputs;		
	final private List<T> _outputs;

	public AbstractCommand(String title, String description, IStore store, boolean canUndo, boolean canRedo, List<T> inputs)
	{
		_title = title;
		_description = description;
		_store = store;
		_canUndo = canUndo;		
		_canRedo = canRedo;
		
		_inputs = inputs;
		_outputs = new ArrayList<T>();
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
	abstract public void execute();

	@Override
	public void undo()
	{
		throw new UnsupportedOperationException("Should not be called, undo not provided");
	}

	@Override
	public void redo()
	{
		throw new UnsupportedOperationException("Should not be called, redo not provided");
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
