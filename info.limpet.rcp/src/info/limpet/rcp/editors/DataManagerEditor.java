/*******************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package info.limpet.rcp.editors;

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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import info.limpet.IChangeListener;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.IStoreGroup;
import info.limpet.actions.AddLayerAction;
import info.limpet.actions.CopyCsvToClipboardAction;
import info.limpet.actions.CreateCourseAction;
import info.limpet.actions.CreateDecibelsAction;
import info.limpet.actions.CreateDimensionlessAction;
import info.limpet.actions.CreateFrequencyAction;
import info.limpet.actions.CreateLocationAction;
import info.limpet.actions.CreateSpeedAction;
import info.limpet.actions.ExportCsvToFileAction;
import info.limpet.actions.GenerateDataAction;
import info.limpet.actions.RefreshViewAction;
import info.limpet.data.operations.admin.OperationsLibrary;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreChangeListener;
import info.limpet.data.store.InMemoryStore.StoreGroup;
import info.limpet.rcp.RCPContext;
import info.limpet.rcp.data_provider.data.DataModel;
import info.limpet.rcp.editors.dnd.DataManagerDropAdapter;

public class DataManagerEditor extends EditorPart
{

	private IStore _store;
	private TreeViewer viewer;
	private IMenuListener _menuListener;
	private Action refreshView;
	private Action copyCsvToClipboard;
	private Action copyCsvToFile;
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
									if (resource.equals(file)
											&& (delta.getKind() == IResourceDelta.CHANGED && (delta
													.getFlags() & IResourceDelta.CONTENT) != 0))
									{
										reload();
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

	private void reload()
	{
		if (_store != null)
		{
			_store.removeChangeListener(_changeListener);
		}
		load(getEditorInput());
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				viewer.setInput(_store);
				viewer.refresh();
			}
		});

	}

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
		load(input);
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	private void load(IEditorInput input)
	{
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
		generateData = new ActionWrapper(new GenerateDataAction(_context));
		addLayer = new ActionWrapper(new AddLayerAction(_context));
		refreshView = new ActionWrapper(new RefreshViewAction(_context));
		copyCsvToClipboard = new ActionWrapper(new CopyCsvToClipboardAction(_context));
		copyCsvToFile = new ActionWrapper(new ExportCsvToFileAction(_context));
		createDimensionless = new ActionWrapper(new CreateDimensionlessAction(_context));
		createFrequency = new ActionWrapper(new CreateFrequencyAction(_context));
		createDecibels = new ActionWrapper(new CreateDecibelsAction(_context));
		createSpeed = new ActionWrapper(new CreateSpeedAction(_context));
		createCourse = new ActionWrapper(new CreateCourseAction(_context));
		createLocation = new ActionWrapper(new CreateLocationAction(_context));
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
		
		// and the RCP-specific operations
		HashMap<String, List<IOperation<?>>> rcpOps = RCPOperationsLibrary.getOperations();
		ops.putAll(rcpOps);

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

		if (selection.size() == 1 && selection.get(0) instanceof ICollection)
		{
			menu.add(new Separator());
			menu.add(copyCsvToClipboard);
			menu.add(copyCsvToFile);
		}

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
		// We shouldn't register menu because we will contribute menus
		// using separate extension point
		//getSite().registerContextMenu(menuMgr, viewer);
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
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					resourceChangeListener);
			new XStreamHandler().save(_store, file);
			_dirty = false;
			file.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			firePropertyChange(PROP_DIRTY);
		}
		catch (CoreException | IOException e)
		{
			log(e);
		}
		finally
		{
			ResourcesPlugin.getWorkspace().addResourceChangeListener(
					resourceChangeListener,
					IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
							| IResourceChangeEvent.POST_CHANGE);
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
				log.log(new Status(IStatus.WARNING, bundle.getSymbolicName(), t
						.getMessage(), t));
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
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);
		if (_store != null)
		{
			_store.removeChangeListener(_changeListener);
		}
	}

	public IStore getStore()
	{
		return _store;
	}

	public IContext getContext()
	{
		return _context;
	}

	public void refresh()
	{
		if (!viewer.getControl().isDisposed())
		{
			viewer.refresh();
		}
	}
}
