package info.limpet.stackedcharts.ui.view;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.StackedchartsEditControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

public class StackedChartsView extends ViewPart implements
    ITabbedPropertySheetPageContributor, ISelectionProvider
{

  public static final int CHART_VIEW = 1;
  public static final int EDIT_VIEW = 2;

  public static final String ID = "info.limpet.StackedChartsView";

  private StackedPane stackedPane;

  // effects
  protected TransitionManager transitionManager = null;
  private Composite chartHolder;
  private Composite editorHolder;
  private ChartSet charts;
  private AtomicBoolean initEditor = new AtomicBoolean(true);

  private StackedchartsEditControl chartEditor;
  private TabbedPropertySheetPage propertySheetPage;

  private List<ISelectionChangedListener> selectionListeners =
      new ArrayList<ISelectionChangedListener>();

  @Override
  public void createPartControl(Composite parent)
  {

    getViewSite().setSelectionProvider(this);//setup proxy selection provider
    stackedPane = new StackedPane(parent);

    stackedPane.add(CHART_VIEW, createChartView());
    stackedPane.add(EDIT_VIEW, createEditView());

    propertySheetPage = new TabbedPropertySheetPage(this);
    selectView(CHART_VIEW);
    contributeToActionBars();

    // Drop Support for *.stackedcharts
    connectFileDropSupport(stackedPane);

    // see https://github.com/debrief/limpet/issues/265
    if (!System.getProperty("os.name").toLowerCase().contains("mac"))
    {
      transitionManager = new TransitionManager(new Transitionable()
      {
        public void addSelectionListener(SelectionListener listener)
        {
          stackedPane.addSelectionListener(listener);
        }

        public Control getControl(int index)
        {
          return stackedPane.getControl(index);
        }

        public Composite getComposite()
        {
          return stackedPane;
        }

        public int getSelection()
        {
          return stackedPane.getActiveControlKey();
        }

        public void setSelection(int index)
        {
          stackedPane.showPane(index, false);
        }

        public double getDirection(int toIndex, int fromIndex)
        {
          return toIndex == CHART_VIEW ? Transition.DIR_RIGHT
              : Transition.DIR_LEFT;
        }
      });
      // new SlideTransition(_tm)
      transitionManager.setTransition(new CubicRotationTransition(
          transitionManager));
    }

  }

  protected void connectFileDropSupport(Control compoent)
  {
    DropTarget target =
        new DropTarget(compoent, DND.DROP_MOVE | DND.DROP_COPY
            | DND.DROP_DEFAULT);
    final FileTransfer fileTransfer = FileTransfer.getInstance();
    target.setTransfer(new Transfer[]
    {fileTransfer});
    target.addDropListener(new DropTargetListener()
    {

      @Override
      public void dropAccept(DropTargetEvent event)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void drop(DropTargetEvent event)
      {

        if (fileTransfer.isSupportedType(event.currentDataType))
        {
          String[] files = (String[]) event.data;

          // *.stackedcharts
          if (files.length == 1 && files[0].endsWith("stackedcharts"))
          {
            File file = new File(files[0]);
            Resource resource =
                new ResourceSetImpl().createResource(URI.createURI(file.toURI()
                    .toString()));
            try
            {
              resource.load(new HashMap<>());
              ChartSet chartsSet = (ChartSet) resource.getContents().get(0);
              setModel(chartsSet);
            }
            catch (IOException e)
            {
              e.printStackTrace();
              MessageDialog.openError(Display.getCurrent().getActiveShell(),
                  "Error", e.getMessage());

            }

          }
        }

      }

      @Override
      public void dragOver(DropTargetEvent event)
      {

      }

      @Override
      public void dragOperationChanged(DropTargetEvent event)
      {
        if (event.detail == DND.DROP_DEFAULT)
        {
          if ((event.operations & DND.DROP_COPY) != 0)
          {
            event.detail = DND.DROP_COPY;
          }
          else
          {
            event.detail = DND.DROP_NONE;
          }
        }
        if (fileTransfer.isSupportedType(event.currentDataType))
        {
          if (event.detail != DND.DROP_COPY)
          {
            event.detail = DND.DROP_NONE;
          }
        }

      }

      @Override
      public void dragLeave(DropTargetEvent event)
      {

      }

      @Override
      public void dragEnter(DropTargetEvent event)
      {
        if (event.detail == DND.DROP_DEFAULT)
        {
          if ((event.operations & DND.DROP_COPY) != 0)
          {
            event.detail = DND.DROP_COPY;
          }
          else
          {
            event.detail = DND.DROP_NONE;
          }
        }
        for (int i = 0; i < event.dataTypes.length; i++)
        {
          if (fileTransfer.isSupportedType(event.dataTypes[i]))
          {
            event.currentDataType = event.dataTypes[i];
            // files should only be copied
            if (event.detail != DND.DROP_COPY)
            {
              event.detail = DND.DROP_NONE;
            }
            break;
          }
        }

      }
    });
  }

  public void selectView(int view)
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      // if switch to edit mode make sure to init editor
      if (view == EDIT_VIEW)
      {
        initEditorView();
      }
      stackedPane.showPane(view);
      // fire selection change to refresh properties view
      fireSelectionChnaged();

    }

  }

  private void initEditorView()
  {
    if (initEditor.getAndSet(false))
    {
      // remove any existing base items
      if (editorHolder != null)
      {
        for (Control control : editorHolder.getChildren())
        {
          control.dispose();
        }
      }
      // create gef base editor
      chartEditor = new StackedchartsEditControl(editorHolder);
      // proxy editor selection to view site
      chartEditor.getViewer().addSelectionChangedListener(
          new ISelectionChangedListener()
          {

            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
              fireSelectionChnaged();

            }
          });
      chartEditor.setModel(charts);
      editorHolder.pack(true);
      editorHolder.getParent().layout();
    }
  }

  public void setModel(ChartSet charts)
  {
    this.charts = charts;
    // mark editor to recreate
    initEditor.set(true);

    // remove any existing base items on view holder
    if (chartHolder != null)
    {
      for (Control control : chartHolder.getChildren())
      {
        control.dispose();
      }
    }
    // remove any existing base items on editor holder
    if (editorHolder != null)
    {
      for (Control control : editorHolder.getChildren())
      {
        control.dispose();
      }
    }

    // and now repopulate
    JFreeChart chart = ChartBuilder.build(charts);
    @SuppressWarnings("unused")
    ChartComposite _chartComposite =
        new ChartComposite(chartHolder, SWT.NONE, chart, true)
        {
          @Override
          public void mouseUp(MouseEvent event)
          {
            super.mouseUp(event);
            JFreeChart c = getChart();
            if (c != null)
            {
              c.setNotify(true); // force redraw
            }
          }
        };

    chartHolder.pack(true);
    chartHolder.getParent().layout();
    selectView(CHART_VIEW);
  }

  protected Control createChartView()
  {

    chartHolder = new Composite(stackedPane, SWT.NONE);
    chartHolder.setLayout(new FillLayout());

    return chartHolder;
  }

  protected Control createEditView()
  {
    editorHolder = new Composite(stackedPane, SWT.NONE);
    editorHolder.setLayout(new FillLayout());

    return editorHolder;
  }

  @Override
  public void setFocus()
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      stackedPane.forceFocus();
    }

  }

  protected void fillLocalPullDown(IMenuManager manager)
  {

  }

  protected void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(new Action("Edit")
    {
      @Override
      public void run()
      {
        if (stackedPane.getActiveControlKey() == CHART_VIEW)
        {
          selectView(EDIT_VIEW);
          setText("Chart");
          manager.update(true);
        }
        else
        {
          // recreate the model
          // TODO: let's not re-create the model each time we revert
          // to the view mode. let's create listeners, so the
          // chart has discrete updates in response to
          // model changes
          setModel(charts);

          selectView(CHART_VIEW);
          setText("Edit");

          manager.update(true);
        }
      }
    });
  }

  protected void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  @SuppressWarnings("rawtypes")
  public Object getAdapter(Class type)
  {

    
    if (type == CommandStack.class)
    {
      //if view mode is editor,Proxy CommandStack object from editor domain
      if (!initEditor.get() && stackedPane.getActiveControlKey() == EDIT_VIEW)
      {
        return chartEditor.getViewer().getEditDomain().getCommandStack();
      }
    }
    if (type == IPropertySheetPage.class)
    {
      return propertySheetPage;
    }
    return super.getAdapter(type);
  }

  @Override
  public String getContributorId()
  {
    return getViewSite().getId();
  }

  @Override
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionListeners.add(listener);

  }

  @Override
  public ISelection getSelection()
  {
    if (!initEditor.get() && stackedPane.getActiveControlKey() == EDIT_VIEW)
    {
      return chartEditor.getViewer().getSelection();
    }
    // if chart view need to provide selection info via properties view, change empty selection to
    // represent object of chart view selection.
    return new StructuredSelection();// empty selection
  }

  @Override
  public void
      removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionListeners.remove(listener);

  }

  @Override
  public void setSelection(ISelection selection)
  {
    if (!initEditor.get())
    {
      chartEditor.getViewer().setSelection(selection);
    }

  }

  /**
   * View Selection provider where it proxy between selected view
   */
  protected void fireSelectionChnaged()
  {
    final ISelection selection = getSelection();
    for (ISelectionChangedListener listener : new ArrayList<>(
        selectionListeners))
    {
      listener.selectionChanged(new SelectionChangedEvent(this, selection));
    }
  }

}
