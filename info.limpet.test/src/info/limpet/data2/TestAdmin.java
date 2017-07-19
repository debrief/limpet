package info.limpet.data2;

import java.util.ArrayList;
import java.util.List;

import info.limpet.IContext;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.admin.CreateNewIndexedDatafileOperation;
import junit.framework.TestCase;

public class TestAdmin extends TestCase
{
  private final IContext context = new MockContext();

  public void testNewIndexedDatafileInvalidSelection()
  {
    StoreGroup store = new StoreGroup("data");
    CreateNewIndexedDatafileOperation oper = new CreateNewIndexedDatafileOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    
    StoreGroup sampleData = new SampleData().getData(10);
    
    selection.clear();    
    selection.add(sampleData.get(SampleData.ANGLE_ONE));
    selection.add(sampleData.get(SampleData.FREQ_ONE));
    
    assertEquals("no ops", 0, oper.actionsFor(selection, store, context).size());
    
    selection.clear();    
    selection.add(sampleData.get(SampleData.FREQ_ONE));    
    assertEquals("no ops", 0, oper.actionsFor(selection, store, context).size());
    
   }
}
