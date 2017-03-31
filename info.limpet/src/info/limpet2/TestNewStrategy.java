package info.limpet2;

import static javax.measure.unit.SI.METRE;

import info.limpet2.operations.arithmetic.AddQuantityOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.quantity.Length;

import junit.framework.TestCase;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;

public class TestNewStrategy extends TestCase
{
  private static final String D_NAME = "d-name";

  public void testGeneral()
  {
    double[] items = new double[]{12d, 13d, 14d, 15d}; 
    DoubleDataset ds = (DoubleDataset) DatasetFactory.createFromObject(items);
    ds.setName(D_NAME);
    
    assertNotNull("failed to create", ds);
    NumberDocument nd = new NumberDocument(ds, null, METRE.asType(Length.class));
    
    assertEquals("correct name", D_NAME, nd.getName());
    
    assertEquals("correct units", "m", nd.getUnits().toString());
    assertEquals("correct dimension", "[L]", nd.getUnits().getDimension().toString());
    
  }
  
  public void testAddingDocuments()
  {
    StoreGroup data = new SampleData().getData(12);
    
    IStoreItem doc1 = data.get(SampleData.SPEED_ONE);
    IStoreItem doc2 = data.get(SampleData.SPEED_TWO);
    
    assertNotNull("found sample data", doc1);
    
    List<Document> selection = new ArrayList<Document>();
    selection.add((Document) doc1);
    selection.add((Document) doc2);
    
    IOperation adder = new AddQuantityOperation();
    IStoreGroup target = new StoreGroup("Destination");
    IContext context = new MockContext();
    Collection<ICommand> actions = adder.actionsFor(selection, target, context );
    
    assertEquals("target empty", 0, target.size());    
    assertEquals("correct num actions",  1, actions.size());
    
    ICommand addAction = actions.iterator().next();
    addAction.execute();
    
    assertEquals("target has data", 1, target.size());
    
    Document output = (Document) target.iterator().next();
    
    System.out.println(output);
    
  }
  
  public void testSampleData()
  {
    @SuppressWarnings("unused")
    StoreGroup data = new SampleData().getData(10);
    
  }
}
