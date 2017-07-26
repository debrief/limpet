/*****************************************************************************
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
 *****************************************************************************/
package info.limpet.ui.editors;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreGroup.StoreChangeListener;
import info.limpet.IStoreItem;
import info.limpet.data.persistence.xml.XStreamHandler;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.OperationsLibrary;
import info.limpet.operations.admin.AddLayerOperation;
import info.limpet.operations.admin.GenerateDummyDataOperation;
import info.limpet.ui.RCPContext;
import info.limpet.ui.data_provider.data.DataManagerDropAdapter;
import info.limpet.ui.data_provider.data.DataModel;
import info.limpet.ui.data_provider.data.LimpetWrapper;

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
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.osgi.framework.Bundle;

public class DataManagerEditor extends EditorPart
{

  /**
   * interface for something that can process something selected in the UI. Initially used for key
   * handlers
   * 
   * @author Ian
   * 
   */
  private static interface ItemProcessor
  {
    public void processThis(LimpetWrapper wrapper);

    public void clicked();
  }

  /**
   * utility class for handling key presses on the editor
   * 
   * @author Ian
   * 
   */
  private class ViewerKeyAdapter extends KeyAdapter
  {
    private final int keyCode;
    private final int stateMask;
    private final ItemProcessor processor;
    private final ISelectionProvider provider;

    public ViewerKeyAdapter(final int keyCode, final int stateMask,
        final ISelectionProvider provider, final ItemProcessor processor)
    {
      this.keyCode = keyCode;
      this.stateMask = stateMask;
      this.processor = processor;
      this.provider = provider;
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
      if ((e.stateMask & stateMask) != 0 || stateMask == SWT.NONE
          && e.keyCode == keyCode)
      {
        // ok, fire the generic event
        processor.clicked();

        final StructuredSelection sel =
            (StructuredSelection) provider.getSelection();
        final Iterator<?> sIter = sel.iterator();
        while (sIter.hasNext())
        {
          final Object object = sIter.next();
          if (object instanceof LimpetWrapper)
          {
            final LimpetWrapper wrapper = (LimpetWrapper) object;
            processor.processThis(wrapper);
          }
        }
      }
    }
  }

  public static void showThisList(final List<IStoreItem> selection,
      final IMenuManager newM, final List<IOperation> operations,
      final IStoreGroup theStore, final IContext context,
      final Runnable listener)
  {
    final int GROUP_NUM = 2;

    final Iterator<IOperation> oIter = operations.iterator();
    while (oIter.hasNext())
    {
      final IOperation op = oIter.next();
      final Collection<ICommand> matches =
          op.actionsFor(selection, theStore, context);

      IMenuManager thisMenu = newM;

      // do we have lots of them?
      if (matches.size() >= GROUP_NUM)
      {
        // get a short name for the command
        final String theName = matches.iterator().next().getDescription();

        // ok, put them into a submenu
        thisMenu = new MenuManager(theName);

        // and add it to the main menu
        newM.add(thisMenu);
      }

      final Iterator<ICommand> mIter = matches.iterator();
      while (mIter.hasNext())
      {
        final ICommand thisC = mIter.next();
        thisMenu.add(new Action(thisC.getName())
        {
          @Override
          public void run()
          {
            thisC.execute();

            // do we have a listener?
            if (listener != null)
            {
              listener.run();
            }
          }
        });
      }
    }
  }

  private IStoreGroup _store;
  private TreeViewer viewer;
  private IMenuListener _menuListener;
  private Action refreshView;
  private Action generateData;
  private Action addFolder;
  private boolean _dirty = false;

  private DataModel _model;

  private final StoreChangeListener _changeListener = new StoreChangeListener()
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

  private final IContext _context = new RCPContext();

  private final IResourceChangeListener resourceChangeListener =
      new IResourceChangeListener()
      {

        @Override
        public void resourceChanged(final IResourceChangeEvent event)
        {
          final IResourceDelta delta = event.getDelta();
          final int eventType = event.getType();
          if (delta != null)
          {
            try
            {
              delta.accept(new IResourceDeltaVisitor()
              {

                @Override
                public boolean visit(final IResourceDelta delta)
                    throws CoreException
                {
                  final IResource resource = delta.getResource();
                  if (resource instanceof IWorkspaceRoot)
                  {
                    return true;
                  }
                  if (resource instanceof IProject)
                  {
                    final IEditorInput input = getEditorInput();
                    if (input instanceof IFileEditorInput)
                    {
                      final IProject project =
                          ((IFileEditorInput) input).getFile().getProject();
                      final boolean isPreDelete =
                          eventType == IResourceChangeEvent.PRE_DELETE;
                      final boolean isPreClose =
                          eventType == IResourceChangeEvent.PRE_CLOSE;
                      if (resource.equals(project)
                          && (isPreDelete || isPreClose))
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
                    final IEditorInput input = getEditorInput();
                    if (input instanceof IFileEditorInput)
                    {
                      final IFile file = ((IFileEditorInput) input).getFile();
                      if (resource.equals(file)
                          && delta.getKind() == IResourceDelta.REMOVED)
                      {
                        final IPath movedToPath = delta.getMovedToPath();
                        if (movedToPath != null)
                        {
                          final IResource path =
                              ResourcesPlugin.getWorkspace().getRoot()
                                  .findMember(movedToPath);
                          if (path instanceof IFile)
                          {
                            final FileEditorInput newInput =
                                new FileEditorInput((IFile) path);
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
                      final boolean resChanged =
                          delta.getKind() == IResourceDelta.CHANGED;
                      final boolean contentChanged =
                          (delta.getFlags() & IResourceDelta.CONTENT) != 0;
                      if (resource.equals(file)
                          && (resChanged && contentChanged))
                      {
                        reload();
                      }
                    }
                  }
                  return false;
                }

              });
            }
            catch (final CoreException e)
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

  private void configureDragSupport()
  {
    final int ops = DND.DROP_COPY | DND.DROP_MOVE;
    final Transfer[] transfers = new Transfer[]
    {TextTransfer.getInstance(), LocalSelectionTransfer.getTransfer()};
    viewer.addDragSupport(ops, transfers, new LimpetDragListener(viewer));
  }

  /**
   * sort out the drop target
   */
  private void configureDropSupport()
  {
    final int dropOperation = DND.DROP_COPY | DND.DROP_MOVE;
    final Transfer[] dropTypes =
    {FileTransfer.getInstance(), TextTransfer.getInstance()};
    viewer.addDropSupport(dropOperation, dropTypes, new DataManagerDropAdapter(
        viewer, _store));
  }

  private void configureKeys(final TreeViewer viewer)
  {
    final ItemProcessor deleteAction = new ItemProcessor()
    {
      @Override
      public void processThis(final LimpetWrapper wrapper)
      {
        final Object subject = wrapper.getSubject();
        if (subject instanceof IDocument<?>)
        {
          final IDocument<?> doc = (IDocument<?>) subject;
          doc.beingDeleted();

          // and detach it from the parent, if it hasn't
          // already been deleted
          if (doc.getParent() != null)
          {
            doc.getParent().remove(doc);
          }
        }
        else if (subject instanceof IStoreGroup)
        {
          final IStoreGroup item = (IStoreGroup) subject;
          item.getParent().remove(item);
        }
      }

      @Override
      public void clicked()
      {
        // ignore, we don't use it
      }
    };
    final ItemProcessor renameAction = new ItemProcessor()
    {
      @Override
      public void processThis(final LimpetWrapper wrapper)
      {
        final Object subject = wrapper.getSubject();
        if (subject instanceof IDocument<?>)
        {
          final IDocument<?> doc = (IDocument<?>) subject;

          // ask for a new name
          final String oldName = doc.getName();

          final String res =
              _context.getInput("Rename document",
                  "Please provide an new name", oldName);

          if (res != null)
          {
            doc.setName(res);
          }
        }
        else if (subject instanceof StoreGroup)
        {
          final StoreGroup group = (StoreGroup) subject;
          // ask for a new name
          final String oldName = group.getName();

          final String res =
              _context.getInput("Rename document",
                  "Please provide an new name", oldName);

          if (res != null)
          {
            group.setName(res);
          }
        }
      }
      @Override
      public void clicked()
      {
        // ignore, we don't use it
      }

    };

    final ItemProcessor refreshAction = new ItemProcessor()
    {
      @Override
      public void processThis(LimpetWrapper wrapper)
      {
        // ignore, we don't use it
      }

      @Override
      public void clicked()
      {
        // ok, fire the refresh event
        refreshView.run();
      }
      
    };
    
    viewer.getControl().addKeyListener(
        new ViewerKeyAdapter(SWT.DEL, 0, viewer, deleteAction));
    viewer.getControl().addKeyListener(
        new ViewerKeyAdapter(SWT.F2, 0, viewer, renameAction));
    viewer.getControl().addKeyListener(
        new ViewerKeyAdapter(SWT.F5, 0, viewer, refreshAction));
  }

  /**
   * walk down through the object tree, connecting listeners as appropriate
   * 
   * @param next
   * @param parent
   * @param listener
   */
  private void connectUp(final IStoreItem next, final IChangeListener listener)
  {
    if (next instanceof IStoreGroup)
    {
      final IStoreGroup group = (IStoreGroup) next;
      final Iterator<IStoreItem> iter = group.iterator();
      while (iter.hasNext())
      {
        connectUp(iter.next(), group);
      }
    }

    next.addTransientChangeListener(listener);
  }

  protected IMenuListener createContextMenuListener()
  {
    return new IMenuListener()
    {
      @Override
      public void menuAboutToShow(final IMenuManager menu)
      {
        setFocus();
        editorContextMenuAboutToShow(menu);
      }
    };
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    _model = new DataModel();
    viewer.setContentProvider(_model);
    final LabelProvider labelProvider = new LimpetLabelProvider();
    final ILabelDecorator decorator =
        PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
    viewer.setLabelProvider(new DecoratingLabelProvider(labelProvider,
        decorator));
    viewer.setInput(_store);

    getSite().setSelectionProvider(viewer);
    makeActions();
    hookContextMenu();

    final IActionBars bars = getEditorSite().getActionBars();
    fillLocalToolBar(bars.getToolBarManager());

    configureDropSupport();
    configureDragSupport();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(
        resourceChangeListener,
        IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
            | IResourceChangeEvent.POST_CHANGE);

    // and the key listener
    configureKeys(viewer);
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

  @Override
  public void doSave(final IProgressMonitor monitor)
  {
    final IEditorInput input = getEditorInput();
    // FIXME we will support FileEditorInput, FileStoreEditorInput and
    // FileRevisionEditorInput
    if (input instanceof IFileEditorInput)
    {
      final IFile file = ((IFileEditorInput) input).getFile();
      doSaveAs(file, monitor);
    }
  }

  @Override
  public void doSaveAs()
  {
    final SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
    dialog.setTitle("Save As");
    if (getEditorInput() instanceof IFileEditorInput)
    {
      final IFileEditorInput input = (IFileEditorInput) getEditorInput();
      final IFile file = input.getFile();
      dialog.setOriginalFile(file);
    }
    dialog.create();
    dialog.setMessage("Save file to another location.");
    if (dialog.open() == Window.OK)
    {
      final IPath path = dialog.getResult();
      final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      doSaveAs(file, new NullProgressMonitor());
      final IFileEditorInput input = new FileEditorInput(file);
      setInput(input);
      setPartName(input.getName());
      viewer.refresh();
    }
  }

  private void doSaveAs(final IFile file, final IProgressMonitor monitor)
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

  protected void editorContextMenuAboutToShow(final IMenuManager menu)
  {
    // get any suitable objects from selection
    final List<IStoreItem> selection = getSuitableObjects();

    // include some top level items
    showThisList(selection, menu, OperationsLibrary.getTopLevel(), _store,
        _context, null);

    // now the tree of operations
    menu.add(new Separator());

    // get the list of operations
    final HashMap<String, List<IOperation>> ops =
        OperationsLibrary.getOperations();

    // and the RCP-specific operations
    final HashMap<String, List<IOperation>> rcpOps =
        RCPOperationsLibrary.getOperations();
    ops.putAll(rcpOps);

    // did we find anything?
    final Iterator<String> hIter = ops.keySet().iterator();

    while (hIter.hasNext())
    {
      // ok, we're in a menu grouping
      final String name = hIter.next();

      // create a new menu tier
      final MenuManager newM = new MenuManager(name);
      menu.add(newM);

      // now loop through this set of operations
      final List<IOperation> values = ops.get(name);

      showThisList(selection, newM, values, _store, _context, null);
    }

    menu.add(new Separator());
    menu.add(refreshView);

  }

  protected void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(refreshView);
    manager.add(generateData);
    manager.add(addFolder);
  }

  public IContext getContext()
  {
    return _context;
  }

  protected final IMenuListener getContextMenuListener()
  {
    if (_menuListener == null)
    {
      _menuListener = createContextMenuListener();
    }
    return _menuListener;
  }

  public IStoreGroup getStore()
  {
    return _store;
  }

  private List<IStoreItem> getSuitableObjects()
  {
    final ArrayList<IStoreItem> matches = new ArrayList<IStoreItem>();

    // ok, find the applicable operations
    final ISelection sel = viewer.getSelection();
    final IStructuredSelection str = (IStructuredSelection) sel;
    final Iterator<?> iter = str.iterator();
    while (iter.hasNext())
    {
      final Object object = iter.next();
      if (object instanceof IStoreItem)
      {
        matches.add((IStoreItem) object);
      }
      else if (object instanceof IAdaptable)
      {
        final IAdaptable ada = (IAdaptable) object;
        final Object match = ada.getAdapter(IStoreItem.class);
        if (match != null)
        {
          matches.add((IStoreItem) match);
        }
      }
    }

    return matches;
  }

  private void hookContextMenu()
  {
    final String id = "#DataManagerEditor";
    final MenuManager menuMgr = new MenuManager(id, id);
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(getContextMenuListener());
    final Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    // We shouldn't register menu because we will contribute menus
    // using separate extension point
    // getSite().registerContextMenu(menuMgr, viewer);
  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input)
      throws PartInitException
  {
    // FIXME we will support FileEditorInput, FileStoreEditorInput and
    // FileRevisionEditorInput
    load(input);
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
    return true;
  }

  private void load(final IEditorInput input)
  {
    if (input instanceof IFileEditorInput)
    {
      // just check if the document is empty
      final IFileEditorInput iF = (IFileEditorInput) input;
      try
      {
        // ok, the file may be empty, do a quick check
        if (iF.getFile().exists() && iF.getFile().getContents().available() > 1)
        {
          _store =
              new XStreamHandler().load(((IFileEditorInput) input).getFile());

          // we need to loop down through the data, setting all of the listeners
          final StoreGroup ms = (StoreGroup) _store;

          // and get hooked up
          final Iterator<IStoreItem> iter = ms.iterator();
          while (iter.hasNext())
          {
            connectUp(iter.next(), ms);
          }
        }
        else
        {
          // ok, it was empty. generate an empty store
          _store = new StoreGroup("Store");
        }
      }
      catch (IOException | CoreException e)
      {
        log(e);
      }

    }
    _store.addChangeListener(_changeListener);
  }

  private void log(final Throwable t)
  {
    final Bundle bundle = Platform.getBundle("info.limpet");
    if (bundle != null)
    {
      final ILog log = Platform.getLog(bundle);
      if (log != null)
      {
        log.log(new Status(IStatus.WARNING, bundle.getSymbolicName(), t
            .getMessage(), t));
        return;
      }
    }
    t.printStackTrace();
  }

  private void makeActions()
  {

    // our operation wrapper needs to be able to get the selection, help it out
    final info.limpet.ui.data_provider.data.ISelectionProvider provider =
        new info.limpet.ui.data_provider.data.ISelectionProvider()
        {
          @Override
          public List<IStoreItem> getSelection()
          {
            return getSuitableObjects();
          }
        };

    addFolder =
        new OperationWrapper(new AddLayerOperation(), "Add folder", PlatformUI
            .getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_PASTE), _context, _store, provider);

    generateData =
        new OperationWrapper(new GenerateDummyDataOperation("Small", 20),
            "Create dummy data", PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD),
            _context, _store, provider);

    // refresh view is purely UI. So, we can implement it here
    refreshView =
        new Action("Refresh View", PlatformUI.getWorkbench().getSharedImages()
            .getImageDescriptor(ISharedImages.IMG_TOOL_REDO))
        {

          @Override
          public void run()
          {
            refresh();
          }
        };

  }

  public void refresh()
  {
    if (!viewer.getControl().isDisposed())
    {
      viewer.refresh();
    }
  }

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

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  public void showMessage(final String message)
  {
    MessageDialog.openInformation(viewer.getControl().getShell(),
        "Data Manager Editor", message);
  }
}
