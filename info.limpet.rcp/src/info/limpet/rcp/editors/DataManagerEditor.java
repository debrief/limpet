package info.limpet.rcp.editors;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.operations.AddLayerOperation;
import info.limpet.data.operations.GenerateDummyDataOperation;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreChangeListener;
import info.limpet.data.store.InMemoryStore.StoreGroup;
import info.limpet.rcp.Activator;
import info.limpet.rcp.RCPContext;
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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.opengis.geometry.Geometry;
import org.osgi.framework.Bundle;

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
	private Action createDimensionless;
	private Action createFrequency;
	private Action createDecibels;
	private Action createSpeed;
	private Action createCourse;
	private Action createLocation;
	private IContext _context = new RCPContext();
	
	private IResourceChangeListener resourceChangeListener = new IResourceChangeListener()
	{

		@Override
		public void resourceChanged(IResourceChangeEvent event)
		{
			IResourceDelta delta = event.getDelta();
			final int eventType = event.getType();
			if (delta != null)
			{
				try
				{
					delta.accept(new IResourceDeltaVisitor()
					{

						@Override
						public boolean visit(IResourceDelta delta) throws CoreException
						{
							IResource resource = delta.getResource();
							if (resource instanceof IWorkspaceRoot)
							{
								return true;
							}
							if (resource instanceof IProject)
							{
								IEditorInput input = getEditorInput();
								if (input instanceof IFileEditorInput)
								{
									IProject project = ((IFileEditorInput) input).getFile()
											.getProject();
									if (resource.equals(project)
											&& (eventType == IResourceChangeEvent.PRE_DELETE || eventType == IResourceChangeEvent.PRE_CLOSE))
									{
										closeEditor();
										return false;
									}
								}
								return true;
							}
							if (resource instanceof IFolder)
							{
								return true;
							}
							if (resource instanceof IFile)
							{
								IEditorInput input = getEditorInput();
								if (input instanceof IFileEditorInput)
								{
									IFile file = ((IFileEditorInput) input).getFile();
									if (resource.equals(file)
											&& delta.getKind() == IResourceDelta.REMOVED)
									{
										IPath movedToPath = delta.getMovedToPath();
										if (movedToPath != null)
										{
											IResource path = ResourcesPlugin.getWorkspace().getRoot()
													.findMember(movedToPath);
											if (path instanceof IFile)
											{
												final FileEditorInput newInput = new FileEditorInput(
														(IFile) path);
												Display.getDefault().asyncExec(new Runnable()
												{

													@Override
													public void run()
													{
														setInputWithNotify(newInput);
														setPartName(newInput.getName());
													}
												});
											}
										}
										else
										{
											closeEditor();
										}
									} 
									if (resource.equals(file) && 
											(delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.CONTENT) != 0))
									{
										// TODO reload
									}
								}
							}
							return false;
						}

					});
				}
				catch (CoreException e)
				{
					log(e);
				}
			}
		}
	};

	private void closeEditor()
	{
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				getSite().getPage().closeEditor(DataManagerEditor.this, false);
			}
		});
	}
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

					// we need to loop down through the data, setting all of the listeners
					InMemoryStore ms = (InMemoryStore) _store;
					
					// check we didn't load an empty store
					ms.init();
					
					// and get hooked up
					Iterator<IStoreItem> iter = ms.iterator();
					while (iter.hasNext())
					{
						connectUp(iter.next(), null, ms);
					}
				}
				else
				{
					// ok, it was empty. generate an empty store
					_store = new InMemoryStore();
				}
			}
			catch (IOException | CoreException e)
			{
				log(e);
			}

		}
		_store.addChangeListener(_changeListener);
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	/**
	 * walk down through the object tree, connecting listeners as appropriate
	 * 
	 * @param next
	 * @param parent
	 * @param listener
	 */
	private void connectUp(IStoreItem next, IStoreGroup parent,
			IChangeListener listener)
	{
		if (next instanceof StoreGroup)
		{
			StoreGroup group = (StoreGroup) next;
			Iterator<IStoreItem> iter = group.iterator();
			while (iter.hasNext())
			{
				connectUp(iter.next(), group, group);
			}
		}

		next.addChangeListener(listener);
	}

	@Override
	public boolean isDirty()
	{
		return _dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return true;
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
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangeListener,
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
						| IResourceChangeEvent.POST_CHANGE);
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

		createDimensionless = createSingletonGenerator("dimensionless",
				new ItemGenerator()
				{
					public QuantityCollection<?> generate(String name)
					{
						return new StockTypes.NonTemporal.DimensionlessDouble(name);
					}
				});

		createFrequency = createSingletonGenerator("frequency", new ItemGenerator()
		{
			public QuantityCollection<?> generate(String name)
			{
				return new StockTypes.NonTemporal.Frequency_Hz(name);
			}
		});

		createDecibels = createSingletonGenerator("decibels", new ItemGenerator()
		{
			public QuantityCollection<?> generate(String name)
			{
				return new StockTypes.NonTemporal.AcousticStrength(name);
			}
		});

		createSpeed = createSingletonGenerator("speed (m/s)", new ItemGenerator()
		{
			public QuantityCollection<?> generate(String name)
			{
				return new StockTypes.NonTemporal.Speed_MSec(name);
			}
		});
		createCourse = createSingletonGenerator("course (degs)",
				new ItemGenerator()
				{
					public QuantityCollection<?> generate(String name)
					{
						return new StockTypes.NonTemporal.Angle_Degrees(name);
					}
				});
		createLocation = createLocationGenerator();
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
				getSuitableObjects(), (IStore) input, _context);
		commands.iterator().next().execute();
	}

	protected void addLayer()
	{
		IOperation<IStoreItem> operation = new AddLayerOperation();

		Object input = viewer.getInput();
		Collection<ICommand<IStoreItem>> commands = operation.actionsFor(
				getSuitableObjects(), (IStore) input, _context);
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
		createMenu.add(createDimensionless);
		createMenu.add(createFrequency);
		createMenu.add(createDecibels);
		createMenu.add(createSpeed);
		createMenu.add(createCourse);
		createMenu.add(createLocation);
		createMenu.add(addLayer);

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
					theStore, _context);

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
			doSaveAs(file, monitor);
		}
	}

	private void doSaveAs(IFile file, IProgressMonitor monitor)
	{
		try
		{
			new XStreamHandler().save(_store, file);
			_dirty = false;
			file.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			firePropertyChange(PROP_DIRTY);
		}
		catch (CoreException | IOException e)
		{
			log(e);
		}
	}

	private void log(Throwable t)
	{
		Bundle bundle = Platform.getBundle("info.limpet");
		if (bundle != null)
		{
			ILog log = Platform.getLog(bundle);
			if (log != null)
			{
				log.log(new Status(IStatus.WARNING, bundle.getSymbolicName(),
						t.getMessage(), t));
				return;
			}
		}
		t.printStackTrace();
	}

	@Override
	public void doSaveAs()
	{
		final SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
		dialog.setTitle("Save As");
		if (getEditorInput() instanceof IFileEditorInput)
		{
			IFileEditorInput input = (IFileEditorInput) getEditorInput();
			IFile file = input.getFile();
			dialog.setOriginalFile(file);
		}
		dialog.create();
		dialog.setMessage("Save file to another location.");
		if (dialog.open() == Window.OK)
		{
			final IPath path = dialog.getResult();
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			doSaveAs(file, new NullProgressMonitor());
			IFileEditorInput input = new FileEditorInput(file);
			setInput(input);
			setPartName(input.getName());
			viewer.refresh();
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
		if (_store != null)
		{
			_store.removeChangeListener(_changeListener);
		}
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

							NonTemporal.Location newData = new NonTemporal.Location(
									seriesName);

							// add the new value
							double dblLat = Double.parseDouble(strLat);
							double dblLong = Double.parseDouble(strLong);

							Geometry newLoc = GeoSupport.getBuilder().createPoint(dblLong,
									dblLat);
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
