package info.limpet.data2;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;

import java.util.ArrayList;
import java.util.List;

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.operations.CollectionComplianceTests;

import javax.measure.quantity.Velocity;

import junit.framework.TestCase;

public class TestCollectionCompliance extends TestCase
{
  
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  
  public void testBoundingRange()
  {
    NumberDocumentBuilder speed1B =
        new NumberDocumentBuilder("Speeds", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    speed1B.add(100, 10d);
    speed1B.add(200, 20d);
    speed1B.add(300, 30d);
    speed1B.add(400, 40d);

    NumberDocument speed1 = (NumberDocument) speed1B.toDocument();
    
    NumberDocumentBuilder speed2B =
        new NumberDocumentBuilder("Speeds", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    speed2B.add(450, 10d);
    speed2B.add(500, 20d);
    speed2B.add(700, 30d);
    speed2B.add(900, 40d);

    NumberDocument speed2 = (NumberDocument) speed2B.toDocument();
    

    NumberDocumentBuilder speed3B =
        new NumberDocumentBuilder("Speeds", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    speed3B.add(120, 10d);
    speed3B.add(130, 20d);
    speed3B.add(140, 30d);
    speed3B.add(150, 40d);

    NumberDocument speed3 = (NumberDocument) speed3B.toDocument();
    
    List<IStoreItem> sel = new ArrayList<IStoreItem>();
    
    sel.add(speed1);
    sel.add(speed2);
    
    assertNull("no overlap", aTests.getBoundingRange(sel));
    
    sel.clear();
    
    sel.add(speed1);
    sel.add(speed3);
    
    assertNotNull("no overlap", aTests.getBoundingRange(sel));
    
    sel.clear();
    
    sel.add(speed1);
    sel.add(speed2);
    sel.add(speed3);
    
    assertNull("no overlap", aTests.getBoundingRange(sel));
    
    
    
    
  }
}
