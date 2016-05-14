package info.limpet.stackedcharts.core.view;

import info.limpet.stackedcharts.model.ChartSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.effects.stw.Transition;
import org.eclipse.nebula.effects.stw.TransitionManager;
import org.eclipse.nebula.effects.stw.Transitionable;
import org.eclipse.nebula.effects.stw.transitions.CubicRotationTransition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

public class StackedChartsView extends ViewPart
{

  public static final int CHART_VIEW = 1;
  public static final int EDIT_VIEW = 2;
  
  public static final String ID = "info.limpet.StackedChartsView";

  private StackedPane stackedPane;

  // effects
  protected TransitionManager transitionManager = null;
  private Composite chartHolder;

  @Override
  public void createPartControl(Composite parent)
  {

    stackedPane = new StackedPane(parent);

    stackedPane.add(CHART_VIEW, createChartView());
    stackedPane.add(EDIT_VIEW, createEditView());
    selectView(CHART_VIEW);
    contributeToActionBars();
    
    // see  https://github.com/debrief/limpet/issues/265
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
          stackedPane.showPane(index,false);
        }

        public double getDirection(int toIndex, int fromIndex)
        {
          return toIndex == CHART_VIEW ? Transition.DIR_RIGHT
              : Transition.DIR_LEFT;
        }
      });
      //new SlideTransition(_tm)
      transitionManager.setTransition(new CubicRotationTransition(transitionManager));
    }

  }

  public void selectView(int view)
  {
    if (stackedPane != null && !stackedPane.isDisposed())
    {
      stackedPane.showPane(view);
    }

  }
  
  public void setModel(ChartSet charts)
  {
    // remove any existing base items
    if(chartHolder != null)
    {
      for (Control control : chartHolder.getChildren()) 
      {
        control.dispose();
      }
    }
      
    // and now repopulate
    JFreeChart chart =  new ChartBuilder(charts).build();
    @SuppressWarnings("unused")
    ChartComposite  _chartComposite = new ChartComposite(chartHolder, SWT.NONE, chart, true)
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
  }

  protected Control createChartView()
  {

    chartHolder = new Composite(stackedPane, SWT.NONE);
    chartHolder.setLayout(new FillLayout());
   
//    JFreeChart chart = createChart();
//    @SuppressWarnings("unused")
//    ChartComposite  _chartComposite = new ChartComposite(chartHolder, SWT.NONE, chart, true)
//    {
//      @Override
//      public void mouseUp(MouseEvent event)
//      {
//        super.mouseUp(event);
//        JFreeChart c = getChart();
//        if (c != null)
//        {
//          c.setNotify(true); // force redraw
//        }
//      }
//    };
    
    return chartHolder;
  }

//  private JFreeChart createChart()
//  {
//    return new ChartBuilder(ChartBuilder.createDummyModel()).build();
//  }

  protected Control createEditView()
  {
    Composite base = new Composite(stackedPane, SWT.NONE);
    base.setLayout(new GridLayout());
    Label label = new Label(base, SWT.NONE);
    label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
    label.setText("TODO: Edit UI ");
    return base;
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

}
