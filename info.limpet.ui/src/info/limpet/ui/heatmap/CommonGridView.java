package info.limpet.ui.heatmap;

import info.limpet.IStoreItem;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.core_view.CoreAnalysisView;
import info.limpet.ui.heatmap.Helper2D.HContainer;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

abstract public class CommonGridView extends CoreAnalysisView
{
  protected final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  protected Text titleLbl;
  protected Action showCount;
  private List<IStoreItem> _myData;

  public CommonGridView(final String myId, final String myTitle)
  {
    super(myId, myTitle);
    
    
    // also create an extra item
    showCount = new Action("Show Count", SWT.TOGGLE){

      @Override
      public void run()
      {
        // 
        super.run();
        
        // ok, redraw
        display(_myData);
      }
      
    };
    showCount.setToolTipText("Show count of items");
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    return Helper2D.appliesToMe(res, tests);
  }

  abstract protected void clearChart();

  @Override
  protected void doDisplay(List<IStoreItem> res)
  {
    // anything selected?
    if (res.size() == 0)
    {
      clearChart();
    }
    else
    {
      // store the data
      _myData = res;
      
      // check they're all one dim
      if (aTests.allTwoDim(res) && res.size() >= 1)
      {
        // ok, they're two-dim dataset
        show(res);
      }
      else
      {
        // nope, clear it
        clearChart();
      }
    }
  }

  protected void show(final List<IStoreItem> items)
  {
    final String seriesName = Helper2D.titleFor(items);

    titleLbl.setText(seriesName);
    titleLbl.pack();

    // get the data
    final HContainer hData;
    if(showCount.isChecked())
    {
      hData = Helper2D.convertToCount(items);
    }
    else
    {
      hData = Helper2D.convertToMean(items);
    }

    final String indexUnits = Helper2D.indexUnitsFor(items);

    // do the actual showing
    showGrid(hData, indexUnits);
  }

  /** show this grid of data
   * 
   * @param data grid of data, with axis labels
   * @param indexUnits units used for the data
   */
  abstract protected void showGrid(HContainer data, String indexUnits);
}
