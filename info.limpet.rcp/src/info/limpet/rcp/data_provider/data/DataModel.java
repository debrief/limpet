package info.limpet.rcp.data_provider.data;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DataModel implements ITreeContentProvider
{
	private IStore _store;

	public DataModel()
	{
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput)
	{
		if (newInput instanceof IStore)
		{
			_store = (IStore) newInput;
		}
		else
		{
			_store = null;
		}
	}

	public void dispose()
	{
	}

	public Object[] getElements(Object parent)
	{

		List<CollectionWrapper> list = new ArrayList<CollectionWrapper>();

		Iterator<ICollection> iter = _store.getAll().iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = iter.next();
			list.add(new CollectionWrapper(null, iCollection));
		}

		return list.toArray();
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Object[] getChildren(Object parentElement)
	{
		List<Object> res = new ArrayList<Object>();

		if (parentElement instanceof CollectionWrapper)
		{
			// see if it has predecessors or successors
			CollectionWrapper cw = (CollectionWrapper) parentElement;
			ICollection coll = cw.getCollection();

			ICommand<?> prec = coll.getPrecedent();
			if (prec != null)
			{
				NamedList dList = new NamedList(cw, "Precedents");
				dList.add(new CommandWrapper(dList, prec));
				res.add(dList);
			}

			List<ICommand> dep = coll.getDependents();
			if (dep != null)
			{
				NamedList dList = new NamedList(cw, "Dependents");
				Iterator<ICommand> dIter = dep.iterator();
				while (dIter.hasNext())
				{
					ICommand thisI = (ICommand) dIter.next();
					dList.add(new CommandWrapper(dList, thisI));
				}

				// did we find any?
				if (dList.size() > 0)
				{
					res.add(dList);
				}
			}
		}
		else if (parentElement instanceof CommandWrapper)
		{
			// see if it has predecessors or successors
			CommandWrapper cw = (CommandWrapper) parentElement;
			ICommand coll = cw.getCommand();

			List<ICollection> inp = coll.getInputs();
			if (inp != null)
			{
				NamedList dList = new NamedList(cw, "Inputs");
				Iterator<ICollection> dIter = inp.iterator();
				while (dIter.hasNext())
				{
					ICollection thisI = (ICollection) dIter.next();
					dList.add(new CollectionWrapper(dList, thisI));
				}
				// did we find any?
				if (dList.size() > 0)
				{
					res.add(dList);
				}
			}

			List<ICollection> outp = coll.getOutputs();
			if (outp != null)
			{
				NamedList dList = new NamedList(cw, "Outputs");
				Iterator<ICollection> dIter = outp.iterator();
				while (dIter.hasNext())
				{
					ICollection thisI = (ICollection) dIter.next();
					dList.add(new CollectionWrapper(dList, thisI));
				}
				// did we find any?
				if (dList.size() > 0)
				{
					res.add(dList);
				}
			}
		}
		else if (parentElement instanceof List)
		{
			List list = (List) parentElement;
			Iterator iter = list.iterator();
			while (iter.hasNext())
			{
				res.add(iter.next());
			}
		}

		Object[] resArray = res.toArray();
		return resArray;
	}

	public Object getParent(Object element)
	{
		return null;
	}

	/**
	 * walk back up object tree, to see if the provided element is the top level
	 * element (so it has null as parent)
	 * 
	 * @param element
	 *          the object we're considering
	 * @return yes/no for if it's at the top of the folder tree
	 */
	protected boolean alreadyShown(LimpetWrapper element)
	{
		final LimpetWrapper lookingFor = element;
		LimpetWrapper current = element;
		boolean found = false;
		boolean walking = true;

		while (walking)
		{
			current = current.getParent();
			if (current == null)
			{
				walking = false;
			}
			else if (current.getSubject() == lookingFor.getSubject())
			{
				found = true;
				walking = false;
			}
		}

		return found;
	}

	public boolean hasChildren(Object element)
	{
		boolean res = false;

		if (element instanceof LimpetWrapper)
		{
			LimpetWrapper core = (LimpetWrapper) element;

			// has it already been shown?
			if (!alreadyShown(core))
			{
				if (element instanceof CollectionWrapper)
				{
					// see if it has predecessors or successors
					CollectionWrapper cw = (CollectionWrapper) element;
					ICollection coll = cw.getCollection();

					boolean hasDependents = coll.getDependents().size() > 0;
					boolean hasPrecedents = coll.getPrecedent() != null;
					res = (hasDependents || hasPrecedents);
				}
				else if (element instanceof CommandWrapper)
				{
					// see if it has predecessors or successors
					CommandWrapper cw = (CommandWrapper) element;
					ICommand<?> comm = cw.getCommand();

					res = ((comm.getInputs().size() > 0) || (comm.getOutputs().size() > 0));
				}
				else if (element instanceof ArrayList)
				{
					ArrayList<?> ar = (ArrayList<?>) element;
					return ar.size() > 0;
				}
			}
		}

		return res;
	}

}