package info.limpet.rcp.editors;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreChangeListener;
import info.limpet.rcp.data_provider.data.DataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public class DataManagerEditor extends EditorPart implements StoreChangeListener
{

	private DataProviderEditorInput _dataProviderEditorInput;
	private TreeViewer viewer;
	private IMenuListener _menuListener;
	private Action action1;
	private Action refreshView;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		// FIXME we will support FileEditorInput, FileStoreEditorInput and
		// FileRevisionEditorInput
		if (!(input instanceof DataProviderEditorInput))
		{
			// throw new RuntimeException("Invalid input");
			// FIXME temporary workaround
			if (input instanceof IFileEditorInput)
			{
				input = new DataProviderEditorInput(new DataModel());
			}
		}

		_dataProviderEditorInput = (DataProviderEditorInput) input;
		
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty()
	{
		// TODO will be implemented
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		// TODO
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(_dataProviderEditorInput.getModel());
		viewer.setLabelProvider(new LimpetLabelProvider());
		viewer.setInput(new SampleData().getData());
		
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		
		IActionBars bars = getEditorSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());

		
		// ok, setup as listener
		InMemoryStore store = (InMemoryStore) viewer.getInput();
		
		if(store instanceof InMemoryStore)
		{
			InMemoryStore ms = (InMemoryStore) store;
			ms.addChangeListener(this);
		}

	}
	
	protected void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(refreshView);
	}

	private void makeActions()
	{

		action1 = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Action 1 executed on " + obj.toString());
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		refreshView = new Action()
		{
			public void run()
			{
				viewer.refresh();
			}
		};
		refreshView.setText("Refresh");
		refreshView.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
	}

	public void showMessage(String message)
	{
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Data Manager Editor", message);
	}

	protected IMenuListener createContextMenuListener()
	{
		return new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager menu)
			{
				setFocus();
				editorContextMenuAboutToShow(menu);
			}
		};
	}
	
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		// get any suitable objects from selection
		List<ICollection> selection = getSuitableObjects();
		
		// get the list of operations
		List<IOperation<?>> ops = OperationsLibrary.getOperations();

		// did we find anything?
		if (selection.size() > 0)
		{
			Iterator<IOperation<?>> oIter = ops.iterator();
			while (oIter.hasNext())
			{
				@SuppressWarnings("unchecked")
				final IOperation<ICollection> op = (IOperation<ICollection>) oIter.next();
				final IStore theStore = _dataProviderEditorInput.getModel().getStore();
				Collection<ICommand<ICollection>> matches = op.actionsFor(selection, theStore);
				 
				Iterator<ICommand<ICollection>> mIter = matches.iterator();
				while (mIter.hasNext())
				{
					final ICommand<info.limpet.ICollection> thisC = (ICommand<info.limpet.ICollection>) mIter
							.next();
					menu.add(new Action(thisC.getTitle()){

						@Override
						public void run()
						{
							thisC.execute();
						}});					
				}
			}
		}

		menu.add(new Separator());
		menu.add(refreshView);
	}

	private List<ICollection> getSuitableObjects()
	{
		ArrayList<ICollection> matches = new ArrayList<ICollection>();
		
		// ok, find the applicable operations
		ISelection sel = viewer.getSelection();
		IStructuredSelection str = (IStructuredSelection) sel;
		Iterator<?> iter = str.iterator();
		while (iter.hasNext())
		{
			Object object = (Object) iter.next();
			if (object instanceof ICollection)
			{
				matches.add((ICollection) object);
			}
			else if (object instanceof IAdaptable)
			{
				IAdaptable ada = (IAdaptable) object;
				Object match = ada.getAdapter(ICollection.class);
				if (match != null)
				{
					matches.add((ICollection) match);
				}
			}
		}
		
		return matches;
	}

	protected final IMenuListener getContextMenuListener()
	{
		if (_menuListener == null)
			_menuListener = createContextMenuListener();
		return _menuListener;
	}

	private void hookContextMenu()
	{
		String id = "#DataManagerEditor";
		MenuManager menuMgr = new MenuManager(id, id);
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(getContextMenuListener());
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		// TODO
	}

	@Override
	public void doSaveAs()
	{
		// TODO
	}

	@Override
	public void changed()
	{
		viewer.refresh();
	}

}
