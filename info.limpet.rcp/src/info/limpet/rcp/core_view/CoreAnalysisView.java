package info.limpet.rcp.core_view;

import info.limpet.ICollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public abstract class CoreAnalysisView extends ViewPart
{

	private Action copyToClipboard;
	private Action followSelection;
	private ISelectionListener selListener;
	protected CollectionComplianceTests aTests;

	public CoreAnalysisView()
	{
		super();

		aTests = new CollectionComplianceTests();

	}

	protected void newSelection(ISelection selection)
	{
		if (followSelection.isChecked())
		{
			List<ICollection> res = new ArrayList<ICollection>();
			if (selection instanceof StructuredSelection)
			{
				StructuredSelection str = (StructuredSelection) selection;

				// check if it/they are suitable
				Iterator<?> iter = str.iterator();
				while (iter.hasNext())
				{
					Object object = (Object) iter.next();
					if (object instanceof IAdaptable)
					{
						IAdaptable ad = (IAdaptable) object;
						ICollection coll = (ICollection) ad.getAdapter(ICollection.class);
						if (coll != null)
						{
							res.add(coll);
						}
					}
				}
			}

			// have we found any?
			if (res.size() > 0)
			{
				// do they apply to me?
				if (appliesToMe(res, aTests))
				{
					// ok, display them
					display(res);
				}
			}
		}
	}

	/**
	 * determine if this set of collections are suitable for displaying
	 * 
	 * @param res
	 * @param aTests2
	 * @return
	 */
	abstract protected boolean appliesToMe(List<ICollection> res,
			CollectionComplianceTests aTests2);

	/**
	 * show this set of collections
	 * 
	 * @param res
	 */
	abstract protected void display(List<ICollection> res);

	protected void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(copyToClipboard);
		manager.add(followSelection);
		manager.add(new Separator());
	}

	protected void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(copyToClipboard);
		manager.add(followSelection);
	}

	protected void copyToClipboard()
	{
		Display display = Display.getCurrent();
		Clipboard clipboard = new Clipboard(display);
		String output = getTextForClipboard();

		clipboard.setContents(new Object[]
		{ output }, new Transfer[]
		{ TextTransfer.getInstance() });
		clipboard.dispose();
	}

	protected void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void setupListener()
	{
		// register as selection listener
		selListener = new ISelectionListener()
		{
			public void selectionChanged(IWorkbenchPart part, ISelection selection)
			{
				newSelection(selection);
			}
		};
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selListener);
	}

	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(selListener);

		super.dispose();
	}

	abstract protected String getTextForClipboard();

	protected void makeActions()
	{
		copyToClipboard = new Action()
		{
			public void run()
			{
				copyToClipboard();
			}
		};
		copyToClipboard.setText("Copy to clipboard");
		copyToClipboard.setToolTipText("Copy analysis to clipboard");
		copyToClipboard.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		followSelection = new Action("Follow selection", SWT.TOGGLE)
		{
			public void run()
			{
				// don't worry, we can ignore the events
			}
		};
		followSelection.setChecked(true);
		followSelection.setToolTipText("Link with selection");
		followSelection.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

	}

	protected void fillContextMenu(IMenuManager manager)
	{
		manager.add(copyToClipboard);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}