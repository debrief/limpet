package info.limpet.ui.heatmap;

import info.limpet.IStoreItem;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.core_view.CoreAnalysisView;
import info.limpet.ui.heatmap.Helper2D.HContainer;

import java.util.List;

import org.eclipse.swt.widgets.Text;

abstract public class CommonGridView extends CoreAnalysisView
{
  protected final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  protected Text titleLbl;

  public CommonGridView(final String myId, final String myTitle)
  {
    super(myId, myTitle);
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    return Helper2D.appliesToMe(res, tests);
  }

  abstract void clearChart();

  @Override
  public void display(final List<IStoreItem> res)
  {
    // anything selected?
    if (res.size() == 0)
    {
      clearChart();
    }
    else
    {
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
    clearChart();

    final String seriesName = Helper2D.titleFor(items);

    titleLbl.setText(seriesName);
    titleLbl.pack();

    // get the data
    final HContainer hData = Helper2D.convert(items);

    final String indexUnits = Helper2D.indexUnitsFor(items);

    showGrid(hData, indexUnits);
  }

  abstract protected void showGrid(HContainer data, String indexUnits);
}
