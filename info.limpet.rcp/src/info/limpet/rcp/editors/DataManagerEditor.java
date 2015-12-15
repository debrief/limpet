package info.limpet.rcp.editors;

import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.operations.AddLayerOperation;
import info.limpet.data.operations.AddLayerOperation.StringProvider;
import info.limpet.data.operations.GenerateDummyDataOperation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreChangeListener;
import info.limpet.rcp.Activator;
import info.limpet.rcp.data_provider.data.DataModel;
import info.limpet.rcp.data_provider.data.GroupWrapper;
import info.limpet.rcp.editors.dnd.DataManagerDropAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.opengis.geometry.Geometry;

public class DataManagerEditor extends EditorPart
{

	private IStore _store;
	private TreeViewer viewer;
	private IMenuListener _menuListener;
	private Action refreshView;
	private Action generateData;
	private Action addLayer;
	private boolean _dirty = false;
	private DataModel _model;
	private StoreChangeListener _changeListener = new StoreChangeListener()
	{

		@Override
		public void changed()
		{
			// indicate the file is dirty
			_dirty = true;
			firePropertyChange(PROP_DIRTY);

			// and refresh the UI
			viewer.refresh();
		}
	};
	private Action createSingleton1;
	private Action createSingleton2;
	private Action createSingleton3;
	private Action createSingleton4;
	private Action createSingleton5;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		// FIXME we will support FileEditorInput, FileStoreEditorInput and
		// FileRevisionEditorInput
		if (input instanceof IFileEditorInput)
		{
			// just check if the document is empty
			IFileEditorInput iF = (IFileEditorInput) input;
			try
			{
				// ok, the file may be empty, do a quick check
				if (iF.getFile().exists() && iF.getFile().getContents().available() > 1)
				{
					_store = new XStreamHandler().load(((IFileEditorInput) input)
							.getFile());
				}
				else
				{
					// ok, it was empty. generate an empty store
					_store = new InMemoryStore();
				}
			}
			catch (IOException | CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		_store.addChangeListener(_changeListener);
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty()
	{
		return _dirty;
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
		_model = new DataModel();
		viewer.setContentProvider(_model);
		LabelProvider labelProvider = new LimpetLabelProvider();
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager()
				.getLabelDecorator();
		viewer.setLabelProvider(new DecoratingLabelProvider(labelProvider,
				decorator));
		viewer.setInput(_store);

		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();

		IActionBars bars = getEditorSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());

		configureDropSupport();
		configureDragSupport();
	}

	private void configureDragSupport()
	{
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[]
		{ TextTransfer.getInstance() };
		viewer.addDragSupport(ops, transfers, new LimpetDragListener(viewer));
	}

	/**
	 * sort out the drop target
	 */
	private void configureDropSupport()
	{
		final int dropOperation = DND.DROP_COPY | DND.DROP_MOVE;
		final Transfer[] dropTypes =
		{ FileTransfer.getInstance(), TextTransfer.getInstance() };
		viewer.addDropSupport(dropOperation, dropTypes, new DataManagerDropAdapter(
				viewer, _store));
	}

	protected void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(refreshView);
		manager.add(generateData);
		manager.add(addLayer);
	}

	private void makeActions()
	{
		generateData = new Action()
		{
			public void run()
			{
				generateData();
			}
		};
		generateData.setText("Generate data");
		generateData.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

		addLayer = new Action()
		{
			public void run()
			{
				addLayer();
			}
		};
		addLayer.setText("Add Layer");
		addLayer.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));

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

		createSingleton1 = createSingletonGenerator("dimensionless",
				new ItemGenerator()
				{
					public QuantityCollection<?> generate(String name)
					{
						return new StockTypes.NonTemporal.DimensionlessDouble(name);
					}
				});

		createSingleton2 = createSingletonGenerator("frequency",
				new ItemGenerator()
				{
					public QuantityCollection<?> generate(String name)
					{
						return new StockTypes.NonTemporal.Frequency_Hz(name);
					}
				});

		createSingleton3 = createSingletonGenerator("decibels", new ItemGenerator()
		{
			public QuantityCollection<?> generate(String name)
			{
				return new StockTypes.NonTemporal.AcousticStrength(name);
			}
		});

		createSingleton4 = createSingletonGenerator("speed (m/s)",
				new ItemGenerator()
				{
					public QuantityCollection<?> generate(String name)
					{
						return new StockTypes.NonTemporal.Speed_MSec(name);
					}
				});

		createSingleton5 = createLocationGenerator();

	}

	private static interface ItemGenerator
	{
		public QuantityCollection<?> generate(final String name);
	}

	private Action createSingletonGenerator(final String sType,
			final ItemGenerator generator)
	{
		final Action res = new Action()
		{
			public void run()
			{
				// get the name
				String name = "new " + sType;
				double value;

				InputDialog dlgName = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New variable", "Enter name for variable", "",
						null);
				if (dlgName.open() == Window.OK)
				{
					// User clicked OK; update the label with the input
					name = dlgName.getValue();
				}
				else
				{
					return;
				}

				InputDialog dlgValue = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New variable",
						"Enter initial value for variable", "", null);
				if (dlgValue.open() == Window.OK)
				{
					// User clicked OK; update the label with the input
					String str = dlgValue.getValue();
					try
					{
						// get the new collection
						QuantityCollection<?> newData = generator.generate(name);

						// add the new value
						value = Double.parseDouble(str);
						newData.add(value);

						// put the new collection in to the selected folder, or into root
						ISelection selection = viewer.getSelection();
						IStructuredSelection stru = (IStructuredSelection) selection;
						Object first = stru.getFirstElement();
						if (first instanceof GroupWrapper)
						{
							GroupWrapper gW = (GroupWrapper) first;
							gW.getGroup().add(newData);
						}
						else
						{
							// just store it at the top level
							_store.add(newData);
						}

					}
					catch (NumberFormatException e)
					{
						System.err.println("Failed to parse initial value");
						e.printStackTrace();
					}
				}
				else
				{
					return;
				}
			}
		};
		res.setText("Create single " + sType + " value");
		res.setImageDescriptor(Activator.getImageDescriptor("icons/variable.png"));

		return res;
	}

	protected void generateData()
	{
		GenerateDummyDataOperation operation = new GenerateDummyDataOperation(
				"small", 20);

		Object input = viewer.getInput();
		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), (IStore) input);
		commands.iterator().next().execute();
	}

	protected void addLayer()
	{
		IOperation<IStoreItem> operation = new AddLayerOperation(
				new StringProvider()
				{

					@Override
					public String getString(String title)
					{
						String res = null;
						InputDialog dlg = new InputDialog(Display.getCurrent()
								.getActiveShell(), title, null, null, null);
						if (dlg.open() == Window.OK)
						{
							// User clicked OK; update the label with the input
							res = dlg.getValue();
						}

						return res;
					}
				});

		Object input = viewer.getInput();
		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), (IStore) input);
		commands.iterator().next().execute();
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
		List<IStoreItem> selection = getSuitableObjects();

		// include some top level items
		showThisList(selection, menu, OperationsLibrary.getTopLevel());

		// now the tree of operations
		menu.add(new Separator());

		// get the list of operations
		HashMap<String, List<IOperation<?>>> ops = OperationsLibrary
				.getOperations();

		// did we find anything?
		Iterator<String> hIter = ops.keySet().iterator();

		while (hIter.hasNext())
		{
			// ok, we're in a menu grouping
			String name = (String) hIter.next();

			// create a new menu tier
			MenuManager newM = new MenuManager(name);
			menu.add(newM);

			// now loop through this set of operations
			List<IOperation<?>> values = ops.get(name);

			showThisList(selection, newM, values);
		}

		// and the generators
		MenuManager createMenu = new MenuManager("Create");
		menu.add(createMenu);
		createMenu.add(createSingleton1);
		createMenu.add(createSingleton2);
		createMenu.add(createSingleton3);
		createMenu.add(createSingleton4);
		createMenu.add(createSingleton5);

		menu.add(new Separator());
		menu.add(refreshView);

	}

	private void showThisList(List<IStoreItem> selection, IMenuManager newM,
			List<IOperation<?>> values)
	{
		Iterator<IOperation<?>> oIter = values.iterator();
		while (oIter.hasNext())
		{
			@SuppressWarnings("unchecked")
			final IOperation<IStoreItem> op = (IOperation<IStoreItem>) oIter.next();
			final IStore theStore = _store;
			Collection<ICommand<IStoreItem>> matches = op.actionsFor(selection,
					theStore);

			Iterator<ICommand<IStoreItem>> mIter = matches.iterator();
			while (mIter.hasNext())
			{
				final ICommand<IStoreItem> thisC = (ICommand<IStoreItem>) mIter.next();
				newM.add(new Action(thisC.getName())
				{
					@Override
					public void run()
					{
						thisC.execute();
					}
				});
			}
		}
	}

	private List<IStoreItem> getSuitableObjects()
	{
		ArrayList<IStoreItem> matches = new ArrayList<IStoreItem>();

		// ok, find the applicable operations
		ISelection sel = viewer.getSelection();
		IStructuredSelection str = (IStructuredSelection) sel;
		Iterator<?> iter = str.iterator();
		while (iter.hasNext())
		{
			Object object = (Object) iter.next();
			if (object instanceof IStoreItem)
			{
				matches.add((IStoreItem) object);
			}
			else if (object instanceof IAdaptable)
			{
				IAdaptable ada = (IAdaptable) object;
				Object match = ada.getAdapter(IStoreItem.class);
				if (match != null)
				{
					matches.add((IStoreItem) match);
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
		final IEditorInput input = getEditorInput();
		// FIXME we will support FileEditorInput, FileStoreEditorInput and
		// FileRevisionEditorInput
		if (input instanceof IFileEditorInput)
		{
			IFile file = ((IFileEditorInput) input).getFile();
			try
			{
				new XStreamHandler().save(_store, file);
				_dirty = false;
				firePropertyChange(PROP_DIRTY);
			}
			catch (CoreException | IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doSaveAs()
	{
		// TODO
	}

	@Override
	public void dispose()
	{
		super.dispose();
		_store.removeChangeListener(_changeListener);
	}

	private Action createLocationGenerator()
	{
		final Action res = new Action()
		{
			public void run()
			{
				// get the name
				String seriesName = "new single location";

				InputDialog dlgName = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New fixed location", "Enter name for location",
						"", null);
				if (dlgName.open() == Window.OK)
				{
					// User clicked OK; update the label with the input
					seriesName = dlgName.getValue();
				}
				else
				{
					return;
				}

				InputDialog dlgValue = new InputDialog(Display.getCurrent()
						.getActiveShell(), "New location",
						"Enter initial value for latitude", "", null);
				if (dlgValue.open() == Window.OK)
				{
					// User clicked OK; update the label with the input
					String strLat = dlgValue.getValue();

					// ok, now the second one
					dlgValue = new InputDialog(Display.getCurrent().getActiveShell(),
							"New location", "Enter initial value for longitude", "", null);
					if (dlgValue.open() == Window.OK)
					{
						// User clicked OK; update the label with the input
						String strLong = dlgValue.getValue();

						// ok, now the second one

						try
						{
							
							NonTemporal.Location newData = new NonTemporal.Location(seriesName);
							
							// add the new value
							double dblLat = Double.parseDouble(strLat);
							double dblLong = Double.parseDouble(strLong);
							
							Geometry newLoc = GeoSupport.getBuilder().createPoint(dblLong, dblLat);
							newData.add(newLoc);

							// put the new collection in to the selected folder, or into root
							ISelection selection = viewer.getSelection();
							IStructuredSelection stru = (IStructuredSelection) selection;
							Object first = stru.getFirstElement();
							if (first instanceof GroupWrapper)
							{
								GroupWrapper gW = (GroupWrapper) first;
								gW.getGroup().add(newData);
							}
							else
							{
								// just store it at the top level
								_store.add(newData);
							}

						}
						catch (NumberFormatException e)
						{
							System.err.println("Failed to parse initial value");
							e.printStackTrace();
							return;
						}
					}
				}
				else
				{
					return;
				}
			}
		};
		res.setText("Create single location");
		res.setImageDescriptor(Activator.getImageDescriptor("icons/variable.png"));

		return res;
	}

}
