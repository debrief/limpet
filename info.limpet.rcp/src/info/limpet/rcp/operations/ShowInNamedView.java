package info.limpet.rcp.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.rcp.core_view.CoreAnalysisView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowInNamedView implements IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();
	final private String theId;
	final private String _title;

	public ShowInNamedView(String title, String id)
	{
		_title = title;
		theId = id;
	}
	
	protected CollectionComplianceTests getTests()
	{
		return aTests;
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new ShowInViewOperation(_title, selection,
					theId);
			res.add(newC);
		}

		return res;
	}

	protected boolean appliesTo(List<ICollection> selection)
	{
		return aTests.nonEmpty(selection);
	}

	public static class ShowInViewOperation extends AbstractCommand<ICollection>
	{

		final private String _id;

		public ShowInViewOperation(String title, List<ICollection> selection,
				String id)
		{
			super(title, "Show selection in specified view", null, null, false,
					false, selection);
			_id = id;
		}

		@Override
		public void execute()
		{
			String secId = _inputs.toString();

			// create a new instance of the specified view
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();

			try
			{

				page.showView(_id, secId, IWorkbenchPage.VIEW_ACTIVATE);
			}
			catch (PartInitException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// try to recover the view
			IViewReference viewRef = page.findViewReference(_id, secId);
			IViewPart theView = viewRef.getView(true);

			// double check it's what we're after
			if (theView instanceof CoreAnalysisView)
			{
				CoreAnalysisView cv = (CoreAnalysisView) theView;

				// set follow selection to off
				cv.setFollow(false);

				// fire in the current selection
				cv.display(_inputs);

			}
		}

		@Override
		protected void recalculate()
		{
			// don't worry
		}

	}

}
