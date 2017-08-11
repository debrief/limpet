package info.limpet.ui.heatmap;

import java.util.List;

import org.eclipse.swt.widgets.Text;

import info.limpet.IStoreItem;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.core_view.CoreAnalysisView;

abstract public class CommonGridView extends CoreAnalysisView
{
  protected final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  protected Text titleLbl;

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    final boolean allNonQuantity = tests.allNonQuantity(res);
    final boolean allCollections = tests.allCollections(res);
    final boolean allQuantity = tests.allQuantity(res);
    final boolean suitableIndex =
        tests.allEqualIndexed(res) || tests.allNonIndexed(res);
    return allCollections && suitableIndex && (allQuantity || allNonQuantity);
  }

  public CommonGridView(String myId, String myTitle)
  {
    super(myId, myTitle);
  }

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
      if (aTests.allTwoDim(res) && res.size() == 1)
      {
        // ok, it's a single two-dim dataset
        show(res.get(0));
      }
      else
      {
        // nope, clear it
        clearChart();
      }
    }
  }

  abstract protected void show(IStoreItem iStoreItem);

  abstract void clearChart();
}
