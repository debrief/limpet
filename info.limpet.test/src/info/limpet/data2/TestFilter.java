package info.limpet.data2;

import java.util.ArrayList;
import java.util.List;

import info.limpet.ICommand;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.filter.MaxFilterOperation;

import javax.measure.unit.SI;

import org.junit.Test;

import junit.framework.TestCase;

public class TestFilter extends TestCase
{
 
  @Test
  public void testFilterIndexed()
  {
    ArrayList<IStoreItem> selection = new ArrayList<IStoreItem>();
    MockContext context = new MockContext();
    StoreGroup store = new StoreGroup("data");

    NumberDocumentBuilder builder = new NumberDocumentBuilder("doc 1", SI.METER, null, SI.SECOND);
    builder.add(10d, 4d);
    builder.add(20d, 5d);
    builder.add(30d, 6d);
    builder.add(40d, 7d);
    builder.add(50d, 8d);
    builder.add(60d, 7d);
    builder.add(70d, 6d);
    
    NumberDocument doc = builder.toDocument();
    selection.add(doc);
    
    List<ICommand> oper = new MaxFilterOperation().actionsFor(selection, store, context);
    assertNotNull("operations present", oper);
    assertEquals("correct operations", 0, oper.size());
    
    // ok, give it a singleton
    NumberDocumentBuilder sBuilder = new NumberDocumentBuilder("singleton", SI.CELSIUS, null, null);
    sBuilder.add(6d);
    NumberDocument singleton = sBuilder.toDocument();
    
    selection.add(singleton);
    
    oper = new MaxFilterOperation().actionsFor(selection, store, context);
    assertNotNull("operations present", oper);
    assertEquals("correct operations", 2, oper.size());
        
    // ok, run it
    oper.get(0).execute();
    
    NumberDocument nd = (NumberDocument) oper.get(0).getOutputs().get(0);
    assertNotNull("have output", nd);
    assertEquals("correct length", 4, nd.size());
    assertEquals("correct units", SI.METER, nd.getUnits());
    assertEquals("correct index units", SI.SECOND, nd.getIndexUnits());
    
    // have a got at applying the minimum filter
    singleton.setValue(5d);
    
    // ok, run it
    oper.get(1).execute();
    
    nd = (NumberDocument) oper.get(1).getOutputs().get(0);
    assertNotNull("have output", nd);
    assertEquals("correct length", 6, nd.size());
    assertEquals("correct units", SI.METER, nd.getUnits());
    assertEquals("correct index units", SI.SECOND, nd.getIndexUnits());
  }
}
