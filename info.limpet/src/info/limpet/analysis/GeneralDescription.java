package info.limpet.analysis;

import info.limpet.ICollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GeneralDescription extends CoreAnalysis
{

	public GeneralDescription()
	{
		super("General Description");
	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	@Override
	public void analyse(List<ICollection> selection)
	{
		List<String> titles = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		// check compatibility
		if (appliesTo(selection))
		{
			if (selection.size() == 1)
			{
				// ok, let's go for it.
				for (Iterator<ICollection> iter = selection.iterator(); iter.hasNext();)
				{
					ICollection thisC = (ICollection) iter.next();

					titles.add("Collection");
					values.add(thisC.getName());
					titles.add("Size");
					values.add("" + thisC.size());
					titles.add("Temporal");
					values.add("" + thisC.isTemporal());
					titles.add("Quantity");
					values.add("" + thisC.isQuantity());
					
				}
			}
		}

		if (titles.size() > 0)
			presentResults(titles, values);

	}

	private boolean appliesTo(List<ICollection> selection)
	{
		return true;
	}

	abstract protected void presentResults(List<String> titles,
			List<String> values);
}
