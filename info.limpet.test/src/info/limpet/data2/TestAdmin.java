package info.limpet.data2;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.admin.CreateNewIndexedDatafileOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;


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
  
  public void testNewIndexedDatafileValidSelection() throws IOException
  {
    StoreGroup store = new StoreGroup("data");
    CreateNewIndexedDatafileOperation oper = new CreateNewIndexedDatafileOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    
    StoreGroup sampleData = new SampleData().getData(10);
    
    selection.clear();    
    selection.add(sampleData.get(SampleData.SPEED_ONE));
    selection.add(sampleData.get(SampleData.SPEED_ONE_RAD));
    
    List<ICommand> ops = oper.actionsFor(selection, store, context);
    assertEquals("action created", 2, ops.size());
    
    assertEquals("store empty", 0, store.size());
    ops.get(0).execute();
    assertEquals("store populated", 1, store.size());

    NumberDocument output = (NumberDocument) ops.get(0).getOutputs().get(0);
    
    // check the units
    assertEquals("right x axis", SI.METRE.divide(SI.SECOND), output.getIndexUnits());
    assertEquals("right y axis", NonSI.DECIBEL, output.getUnits());

    // and now the other way around
    ops.get(1).execute();
    output = (NumberDocument) ops.get(1).getOutputs().get(0);
    assertEquals("store populated", 2, store.size());
    
    // check the units
    assertEquals("right x axis", NonSI.DECIBEL, output.getIndexUnits());
    assertEquals("right y axis", SI.METRE.divide(SI.SECOND), output.getUnits());
  }

}
