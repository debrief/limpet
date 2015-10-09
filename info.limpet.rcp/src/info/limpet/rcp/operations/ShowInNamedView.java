package info.limpet.rcp.operations;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
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

public class ShowInNamedView implements IOperation<IStoreItem>
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

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			ICommand<IStoreItem> newC = new ShowInViewOperation(_title, selection,
					theId);
			res.add(newC);
		}

		return res;
	}

	protected boolean appliesTo(List<IStoreItem> selection)
	{
		return aTests.allCollections(selection) && aTests.nonEmpty(selection);
	}

	public static class ShowInViewOperation extends AbstractCommand<IStoreItem>
	{

		final private String _id;

		public ShowInViewOperation(String title, List<IStoreItem> selection,
				String id)
		{
			super(title, "Show selection in specified view", null, null, false,
					false, selection);
			_id = id;
		}

		@Override
		public void execute()
		{
			String secId = inputs.toString();

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
				cv.follow(inputs);

			}
		}

		@Override
		protected void recalculate()
		{
			// don't worry
		}

	}

}
