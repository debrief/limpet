package info.limpet.stackedcharts.ui.view;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.stackedcharts.ui.editor.StackedchartsEditControl;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
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
import org.eclipse.nebula.effects.stw.ImageTransitionable;
import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

public class StackedChartsView extends ViewPart implements
    ITabbedPropertySheetPageContributor, ISelectionProvider, DisposeListener
{

  public static final String STACKED_CHARTS_CONFIG = "StackedCharts";

  /**
   * interface for external objects that are able to supply a date and resond to a new date
   * 
   * @author ian
   * 
   */
  public static interface ControllableDate
  {
    /**
     * control the date
     * 
     * @param time
     */
    void setDate(Date date);

    /**
     * retrieve the date
     * 
     * @return current date
     */
    Date getDate();
  }

  public static final int CHART_VIEW = 1;
  public static final int EDIT_VIEW = 2;

  public static final String ID = "info.limpet.StackedChartsView";

  private StackedPane stackedPane;

  // effects
  protected TransitionManager transitionManager = null;
  private Composite chartHolder;
  private Composite editorHolder;
  private ChartSet charts;
  private final AtomicBoolean initEditor = new AtomicBoolean(true);

  private StackedchartsEditControl chartEditor;

  private final List<ISelectionChangedListener> selectionListeners =
      new ArrayList<ISelectionChangedListener>();
  private Date _currentTime;
  private ChartComposite _chartComposite;
  private ArrayList<Runnable> _closeCallbacks;
  private ControllableDate _controllableDate = null;

  @Override
  public void addSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    selectionListeners.add(listener);

  }

  protected void connectFileDropSupport(final Control compoent)
  {
    final DropTarget target =
        new DropTarget(compoent, DND.DROP_MOVE | DND.DROP_COPY
            | DND.DROP_DEFAULT);
    final FileTransfer fileTransfer = FileTransfer.getInstance();
    target.setTransfer(new Transfer[]
    {fileTransfer});
    target.addDropListener(new DropTargetListener()
    {
      @Override
      public void dragEnter(final DropTargetEvent event)
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

      @Override
      public void dragLeave(final DropTargetEvent event)
      {
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent event)
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
      public void dragOver(final DropTargetEvent event)
      {
      }

      @Override
      public void drop(final DropTargetEvent event)
      {
        if (fileTransfer.isSupportedType(event.currentDataType))
        {
          final String[] files = (String[]) event.data;

          // *.stackedcharts
          if (files.length == 1 && files[0].endsWith("stackedcharts"))
          {
            final File file = new File(files[0]);
            final Resource resource =
                new ResourceSetImpl().createResource(URI.createURI(file.toURI()
                    .toString()));
            try
            {
              resource.load(new HashMap<>());
              final ChartSet chartsSet =
                  (ChartSet) resource.getContents().get(0);
              setModel(chartsSet);
            }
            catch (final IOException e)
            {
              e.printStackTrace();
              MessageDialog.openError(Display.getCurrent().getActiveShell(),
                  "Error", e.getMessage());
            }
          }
        }
      }

      @Override
      public void dropAccept(final DropTargetEvent event)
      {
      }
    });
  }

  protected void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected Control createChartView()
  {
    // defer creation of the actual chart until we receive
    // some model data. So, just have an empty panel
    // to start with
    chartHolder = new Composite(stackedPane, SWT.NONE);
    chartHolder.setLayout(new FillLayout());
    return chartHolder;
  }

  protected Control createEditView()
  {
    editorHolder = new Composite(stackedPane, SWT.NONE);
    editorHolder.setLayout(new FillLayout());
    // create gef base editor
    chartEditor = new StackedchartsEditControl(editorHolder);
    // proxy editor selection to view site
    chartEditor.getViewer().addSelectionChangedListener(
        new ISelectionChangedListener()
        {
          @Override
          public void selectionChanged(final SelectionChangedEvent event)
          {
            fireSelectionChnaged();

          }
        });
    return editorHolder;
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    getViewSite().setSelectionProvider(this);// setup proxy selection provider
    stackedPane = new StackedPane(parent);

    stackedPane.add(CHART_VIEW, createChartView());
    stackedPane.add(EDIT_VIEW, createEditView());

    selectView(CHART_VIEW);
    contributeToActionBars();
    chartEditor.init(this);

    // Drop Support for *.stackedcharts
    connectFileDropSupport(stackedPane);
    final boolean IS_LINUX_OS =
        System.getProperty("os.name").toLowerCase().indexOf("nux") >= 0;
    final Image[] compImage = new Image[2]; // stackedPane comp count
    transitionManager = new TransitionManager(new ImageTransitionable()
    {

      public Image getControlImage(int index)
      {
        // Linux has problems to get the control image using
        // <code>org.eclipse.swt.widgets.Control.print(GC)</code>,
        // so we return the image directly from this
        // image transitionable object.
        if (IS_LINUX_OS)
        {
          return compImage[index - 1];
        }
        else
        {
          return null;
        }
      }

      public void updateControlImage(Image image, int index)
      {
        if (IS_LINUX_OS)
        {
          compImage[index - 1] = image;
        }
      }

      @Override
      public void addSelectionListener(final SelectionListener listener)
      {
        stackedPane.addSelectionListener(listener);
      }

      @Override
      public Composite getComposite()
      {
        return stackedPane;
      }

      @Override
      public Control getControl(final int index)
      {
        return stackedPane.getControl(index);
      }

      @Override
      public double getDirection(final int toIndex, final int fromIndex)
      {
        return toIndex == CHART_VIEW ? Transition.DIR_RIGHT
            : Transition.DIR_LEFT;
      }

      @Override
      public int getSelection()
      {
        return stackedPane.getActiveControlKey();
      }

      @Override
      public void setSelection(final int index)
      {
        stackedPane.showPane(index, false);
      }
    });
    transitionManager.addTransitionListener(new TransitionListener()
    {

      @Override
      public void transitionFinished(TransitionManager arg0)
      {
        stackedPane.completeSelection();

      }
    });
    // new SlideTransition(_tm)
    transitionManager.setTransition(new CubicRotationTransition(
        transitionManager));

    // listen out for closing
    parent.addDisposeListener(this);

    // and remember to detach ourselves
    final DisposeListener meL = this;
    final Runnable dropMe = new Runnable()
    {
      @Override
      public void run()
      {
        parent.removeDisposeListener(meL);
      }
    };
    addRunOnCloseCallback(dropMe);
  }

  protected void fillLocalPullDown(final IMenuManager manager)
  {
  }

  protected void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(new Action("Edit", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        if (stackedPane.getActiveControlKey() == CHART_VIEW)
        {
          selectView(EDIT_VIEW);
          setText("View");
          manager.update(true);
        }
        else
        {
          // recreate the model
          // TODO: let's not re-create the model each time we revert
          // to the view mode. let's create listeners, so the
          // chart has discrete updates in response to
          // model changes

          // double check we have a charts model
          if (charts != null)
          {
            setModel(charts);
          }

          selectView(CHART_VIEW);
          setText("Edit");
          manager.update(true);
        }
      }
    });

    final Action showTime = new Action("Show time marker", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        // ok, trigger graph redraw
        JFreeChart combined = _chartComposite.getChart();
        TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
        plot._showLine = isChecked();

        // ok, trigger ui update
        refreshPlot();
      }
    };
    showTime.setChecked(true);
    showTime.setImageDescriptor(Activator.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/clock.png"));
    manager.add(showTime);

    final Action showMarker = new Action("Show marker value", SWT.TOGGLE)
    {
      @Override
      public void run()
      {
        // ok, trigger graph redraw
        JFreeChart combined = _chartComposite.getChart();
        TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
        plot._showLabels = isChecked();

        // ok, trigger ui update
        refreshPlot();
      }
    };
    showMarker.setChecked(true);
    showMarker.setImageDescriptor(Activator.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/labels.png"));
    manager.add(showMarker);

    final Action sizeUp = new Action("+", SWT.PUSH)
    {
      @Override
      public void run()
      {
        if (this.isChecked())
        {
          changeFontSize(true);
        }
      }
    };
    sizeUp.setDescription("Increase size of font for value marker labels");
    manager.add(sizeUp);
    final Action sizeDown = new Action("-", SWT.PUSH)
    {
      @Override
      public void run()
      {
        if (this.isChecked())
        {
          changeFontSize(false);
        }
      }
    };
    sizeUp.setDescription("Decrease size of font for value marker labels");
    manager.add(sizeDown);

  }

  /**
   * convenience method to make the value marker labels larger or smaller
   * 
   * @param up
   */
  private void changeFontSize(final boolean up)
  {
    final int change;
    if (up)
    {
      change = 2;
    }
    else
    {
      change = -2;
    }

    int curSize =
        ConfigurationScope.INSTANCE.getNode(STACKED_CHARTS_CONFIG).getInt(
            TimeBarPlot.MARKER_SIZE_NODE, 12);

    // trim to reasonable size
    curSize = Math.max(10, curSize);

    final int newVal = curSize + change;
    ConfigurationScope.INSTANCE.getNode(STACKED_CHARTS_CONFIG).putInt(
        TimeBarPlot.MARKER_SIZE_NODE, newVal);

    // do we know the time?
    if (_currentTime != null)
    {
      // try to get the time aware plot
      JFreeChart combined = _chartComposite.getChart();
      TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
      plot.setTime(_currentTime);

      // ok, trigger ui update
      refreshPlot();
    }

  }

  /**
   * View Selection provider where it proxy between selected view
   */
  protected void fireSelectionChnaged()
  {
    final ISelection selection = getSelection();
    for (final ISelectionChangedListener listener : new ArrayList<>(
        selectionListeners))
    {
      listener.selectionChanged(new SelectionChangedEvent(this, selection));
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class type)
  {
    if (type == CommandStack.class)
    {
      return chartEditor.getViewer().getEditDomain().getCommandStack();
    }
    if (type == IPropertySheetPage.class)
    {
      return chartEditor.getPropertySheetPage();
    }
    return super.getAdapter(type);
  }

  @Override
  public String getContributorId()
  {
    return getViewSite().getId();
  }

  @Override
  public ISelection getSelection()
  {
    if (!initEditor.get() && stackedPane.getActiveControlKey() == EDIT_VIEW)
    {
      return chartEditor.getSelection();
    }
    // if chart view need to provide selection info via properties view, change empty selection to
    // represent object of chart view selection.
    return new StructuredSelection();// empty selection
  }

  private void initEditorViewModel()
  {
    if (initEditor.getAndSet(false))
    {
      chartEditor.setModel(charts);
    }
    editorHolder.pack(true);
    editorHolder.getParent().layout();
  }

  @Override
  public void removeSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    selectionListeners.remove(listener);
  }

  public void selectView(final int view)
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      // if switch to edit mode make sure to init editor model
      if (view == EDIT_VIEW)
      {
        initEditorViewModel();
      }
      stackedPane.showPane(view);
      // fire selection change to refresh properties view
      fireSelectionChnaged();
    }
  }

  @Override
  public void setFocus()
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      stackedPane.forceFocus();
    }
  }

  public void setDateSupport(ControllableDate controllableDate)
  {
    _controllableDate = controllableDate;
  }

  public void setModel(final ChartSet charts)
  {
    this.charts = charts;
    // mark editor to recreate
    initEditor.set(true);

    // remove any existing base items on view holder
    if (chartHolder != null)
    {
      for (final Control control : chartHolder.getChildren())
      {
        control.dispose();
      }
    }

    // and now repopulate
    final JFreeChart chart = ChartBuilder.build(charts, _controllableDate);
    _chartComposite =
        new ChartComposite(chartHolder, SWT.NONE, chart, 400, 600, 300, 200,
            1800, 1800, true, false, true, true, true, true)
        {

          @Override
          public void mouseUp(final MouseEvent event)
          {
            super.mouseUp(event);
            final JFreeChart c = getChart();
            if (c != null)
            {
              c.setNotify(true); // force redraw
            }

            if (event.count == 2)
            {
              handleDoubleClick(event.x, event.y);
            }
          }
        };
    chart.setAntiAlias(false);

    // try the double-click handler
    _chartComposite.addMouseListener(new MouseListener()
    {

      @Override
      public void mouseDoubleClick(MouseEvent e)
      {
        System.out.println("double-click at:" + e);
      }

      @Override
      public void mouseDown(MouseEvent e)
      {
        System.out.println("down at:" + e);
      }

      @Override
      public void mouseUp(MouseEvent e)
      {
        System.out.println("up at:" + e);
      }
    });

    chartHolder.pack(true);
    chartHolder.getParent().layout();
    selectView(CHART_VIEW);
  }

  protected void handleDoubleClick(int x, int y)
  {
    // retrieve the data location
    Rectangle dataArea = _chartComposite.getScreenDataArea();
    Rectangle2D d2 =
        new Rectangle2D.Double(dataArea.x, dataArea.y, dataArea.width,
            dataArea.height);
    TimeBarPlot plot = (TimeBarPlot) _chartComposite.getChart().getPlot();
    double chartX =
        plot.getDomainAxis().java2DToValue((double) x, d2,
            plot.getDomainAxisEdge());

    // do we have a date to control?
    if (_controllableDate != null)
    {
      // ok, update it
      _controllableDate.setDate(new Date((long) chartX));
    }
  }

  @Override
  public void setSelection(final ISelection selection)
  {
    if (!initEditor.get())
    {
      chartEditor.getViewer().setSelection(selection);
    }
  }

  /**
   * update (or clear) the displayed time marker
   * 
   * @param newTime
   */
  public void updateTime(final Date newTime)
  {
    Date oldTime = _currentTime;
    _currentTime = newTime;

    if (newTime != null && !newTime.equals(oldTime) || newTime != oldTime)
    {
      // try to get the time aware plot
      JFreeChart combined = _chartComposite.getChart();
      TimeBarPlot plot = (TimeBarPlot) combined.getPlot();
      plot.setTime(newTime);

      // ok, trigger ui update
      refreshPlot();
    }
  }

  private void refreshPlot()
  {
    Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        if (_chartComposite != null && !_chartComposite.isDisposed())
        {
          JFreeChart c = _chartComposite.getChart();
          if (c != null)
          {
            c.setNotify(true);
          }
        }
      }
    };
    if (Display.getCurrent() != null)
    {
      runnable.run();
    }
    else
    {
      Display.getDefault().syncExec(runnable);
    }
  }

  /**
   * let classes pass callbacks to be run when we are closing
   * 
   * @param runnable
   */
  public void addRunOnCloseCallback(Runnable runnable)
  {
    if (_closeCallbacks == null)
    {
      _closeCallbacks = new ArrayList<Runnable>();
    }
    _closeCallbacks.add(runnable);
  }

  @Override
  public void widgetDisposed(DisposeEvent e)
  {
    if (_closeCallbacks != null)
    {
      for (Runnable callback : _closeCallbacks)
      {
        callback.run();
      }
    }

    // and remove ourselves from our parent

  }

  /**
   * accessor, to be used in exporting the image
   * 
   * @return
   */
  public ChartComposite getChartComposite()
  {
    return _chartComposite;
  }

}
