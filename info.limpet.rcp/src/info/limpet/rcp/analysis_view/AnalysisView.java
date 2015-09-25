package info.limpet.rcp.analysis_view;

import info.limpet.ICollection;
import info.limpet.analysis.AnalysisLibrary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** display analysis overview of selection
 * 
 * @author ian
 *
 */
public class AnalysisView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "info.limpet.rcp.AnalysisView";

	private TableViewer viewer;
	private Action copyToClipboard;
	private ISelectionListener selListener;

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter
	{
	}

	/**
	 * The constructor.
	 */
	public AnalysisView()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(null);
		viewer.getTable().setHeaderVisible(true);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		
		// define the two columns
		TableViewerColumn colTitle = new TableViewerColumn(viewer, SWT.NONE);
		colTitle.getColumn().setWidth(150);
		colTitle.getColumn().setText("Title");
		colTitle.setLabelProvider(new ColumnLabelProvider() {
		  @SuppressWarnings("unchecked")
			@Override
		  public String getText(Object element) {
		  	ArrayList<String> p = (ArrayList<String>) element;
		    return p.get(0);
		  }
		});

		TableViewerColumn colValue = new TableViewerColumn(viewer, SWT.NONE);
		colValue.getColumn().setWidth(200);
		colValue.getColumn().setText("Value");
		colValue.setLabelProvider(new ColumnLabelProvider() {
		  @SuppressWarnings("unchecked")
			@Override
		  public String getText(Object element) {
		  	ArrayList<String> p = (ArrayList<String>) element;
		    return p.get(1);
		  }
		});
		
		//	register as selection listener
		selListener = new ISelectionListener()
		{
			public void selectionChanged(IWorkbenchPart part, ISelection selection)
			{
				newSelection(selection);
			}
		};
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selListener);
	}

	protected void newSelection(ISelection selection)
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

		if (res.size() > 0)
		{
			reportCollection(res);
		}
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selListener);

		super.dispose();
	}

	private void reportCollection(List<ICollection> res)
	{
		// clear the output
		final ArrayList<ArrayList<String>> resList = new ArrayList<ArrayList<String>>();

		AnalysisLibrary ana = new AnalysisLibrary()
		{

			@Override
			protected void presentResults(List<String> titles, List<String> values)
			{
				// produce two column list
				Iterator<String> tIter = titles.iterator();
				Iterator<String> vIter = values.iterator();
				while (tIter.hasNext())
				{
					ArrayList<String> thisRow= new ArrayList<String>();
					thisRow.add(tIter.next());
					thisRow.add(vIter.next());
					
					
					resList.add(thisRow);
				}
			}
		};
		ana.analyse(res);
		viewer.setInput(resList);
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				AnalysisView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(copyToClipboard);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(copyToClipboard);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(copyToClipboard);
	}

	private void makeActions()
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
		copyToClipboard.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}
	
	private void copyToClipboard()
	{
		Display display = Display.getCurrent();
    Clipboard clipboard = new Clipboard(display);
    StringBuffer output = new StringBuffer(); 
    @SuppressWarnings("unchecked")
		ArrayList<ArrayList<String>> list = (ArrayList<ArrayList<String>>) viewer.getInput();
    String separator = System.getProperty( "line.separator" );
    Iterator<ArrayList<String>> iter = list.iterator();
    while (iter.hasNext())
		{
			ArrayList<java.lang.String> arrayList = (ArrayList<java.lang.String>) iter
					.next();
    	output.append(arrayList.get(0));
    	output.append(", ");
    	output.append(arrayList.get(1));
    	output.append(separator);
		}
    
    clipboard.setContents(new Object[] { output.toString()},
            new Transfer[] { TextTransfer.getInstance() });
    clipboard.dispose();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
}