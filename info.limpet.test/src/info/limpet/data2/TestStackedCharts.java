package info.limpet.data2;

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.ui.operations.ShowInStackedChartsOverview;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

public class TestStackedCharts extends TestCase
{

  @Test
  public void testGenChartModel()
  {
    final ArrayList<IStoreItem> selection = new ArrayList<IStoreItem>();
    final StoreGroup data = new SampleData().getData(20);

    final NumberDocument product =
        (NumberDocument) data
            .get("Speed One Time + Speed Two Time + Speed two irregular time");
    assertEquals("17 values", 17, product.size());
    assertEquals("17 indices", 17, product.getIndexValues().getSize());

    assertNotNull(product);

    selection.add(product);
    final ChartSet model =
        ShowInStackedChartsOverview.createModelFor(selection);
    assertNotNull(model);
  }

}
