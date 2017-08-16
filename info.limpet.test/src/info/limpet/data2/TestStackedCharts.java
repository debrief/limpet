package info.limpet.data2;

import info.limpet.ICommand;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.ui.operations.ShowInStackedChartsOverview;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class TestStackedCharts extends TestCase
{
 
  @Test
  public void testGenChartModel()
  {
    ArrayList<IStoreItem> selection = new ArrayList<IStoreItem>();
    MockContext context = new MockContext();
    StoreGroup data = new SampleData().getData(10);
    
    NumberDocument s_one = (NumberDocument) data.get(SampleData.SPEED_ONE);
    
    NumberDocument product = (NumberDocument) data.get("Speed One Time + Speed Two Time + Speed two irregular time");
    
    assertNotNull(product);
    
    System.out.println("name:" + product);
    
    selection.add(product);
    ChartSet model = ShowInStackedChartsOverview.createModelFor(selection);
    
    List<ICommand> actions = new ShowInStackedChartsOverview("Show in stacked charts").actionsFor(selection, data, context);
    
    System.out.println("actions:" + actions);
    
  }

}
