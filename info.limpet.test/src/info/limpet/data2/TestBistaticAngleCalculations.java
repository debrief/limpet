package info.limpet.data2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.arithmetic.simple.AddLogQuantityOperation;
import info.limpet.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet.operations.arithmetic.simple.SubtractLogQuantityOperation;
import info.limpet.operations.arithmetic.simple.SubtractQuantityOperation;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;
import info.limpet.operations.spatial.ProplossBetweenTwoTracksOperation;
import info.limpet.operations.spatial.msa.BistaticAngleOperation;
import info.limpet.persistence.CsvParser;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.SI;

import org.junit.Test;

public class TestBistaticAngleCalculations
{
  final private IContext context = new MockContext();

  @Test
  public void testLoadTracks() throws IOException
  {
    File file = TestCsvParser.getDataFile("multistatics/tx1_stat.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 3, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(0);
    assertEquals("correct num rows", 541, firstColl.size());
  }
  
  @Test
  public void testAddLogData() throws IOException
  {
    IStoreGroup store = new StoreGroup("data");

    // now get our tracks
    File file = TestCsvParser.getDataFile("multistatics/tx1_stat.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> tx1 = parser.parse(file.getAbsolutePath());
    store.addAll(tx1);

    file = TestCsvParser.getDataFile("multistatics/rx1_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> rx1 = parser.parse(file.getAbsolutePath());
    store.addAll(rx1);

    file = TestCsvParser.getDataFile("multistatics/ssn_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> ssn = parser.parse(file.getAbsolutePath());
    store.addAll(ssn);
    
    IOperation pDiff = new ProplossBetweenTwoTracksOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tx1.get(0));
    selection.add(ssn.get(0));
    List<ICommand> actions = pDiff.actionsFor(selection , store, context);
    
    assertNotNull("found actions");
    assertEquals("got actions", 2, actions.size());
    assertEquals("store has original data", 3, store.size());
    
    // run it
    actions.get(0).execute();
    assertEquals("has new data", 4, store.size());
    
    Document<?> txProp = actions.get(0).getOutputs().get(0);
    txProp.setName("txProp");
    
    // now the other proploss file
    selection.clear();
    selection.add(rx1.get(0));
    selection.add(ssn.get(0));

    actions = pDiff.actionsFor(selection , store, context);
    
    assertNotNull("found actions");
    assertEquals("got actions", 2, actions.size());
    assertEquals("store has original data", 4, store.size());
    
    // run it
    actions.get(0).execute();
    assertEquals("has new data", 5, store.size());
    
    Document<?> rxProp = actions.get(0).getOutputs().get(0);
    rxProp.setName("rxProp");
    
    // ok, now we can try to add them
    IOperation addL = new AddLogQuantityOperation();
    IOperation add = new AddQuantityOperation();
    selection.clear();
    selection.add(txProp);
    selection.add(rxProp);
    
    // check the normal adder drops out
    actions = add.actionsFor(selection, store, context);
    assertEquals("no actions returned", 0, actions.size());
    
    // now the log adder
    actions = addL.actionsFor(selection, store, context);
    assertEquals("actions returned", 2, actions.size());
    
    // ok, run the first action
    actions.get(0).execute();
    assertEquals("has new data", 6, store.size());
    
    // check the outputs
    NumberDocument propSum = (NumberDocument) actions.get(0).getOutputs().get(0);
    propSum.setName("propSum");
    
    // ok, change the selection so we can do the reverse of the add
    selection.clear();
    selection.add(propSum);
    selection.add(rxProp);
    
    // hmm, go for the subtract
    IOperation sub = new SubtractQuantityOperation();
    IOperation subL = new SubtractLogQuantityOperation();
    
    actions = sub.actionsFor(selection, store, context);
    assertEquals("none returned", 0, actions.size());
    
    actions = subL.actionsFor(selection, store, context);
    assertEquals("actions returned", 4, actions.size());
    
    // ok, run it
    actions.get(0).execute();
    assertEquals("has new data", 7, store.size());
    
    // check the results
    NumberDocument propDiff = (NumberDocument) actions.get(0).getOutputs().get(0);
    propDiff.setName("propDiff");  
    
  }

  @Test
  public void testCreateActions() throws IOException
  {
    IStoreGroup store = new StoreGroup("data");

    // now get our tracks
    File file = TestCsvParser.getDataFile("multistatics/tx1_stat.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> tx1 = parser.parse(file.getAbsolutePath());
    store.addAll(tx1);

    file = TestCsvParser.getDataFile("multistatics/rx1_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> rx1 = parser.parse(file.getAbsolutePath());
    store.addAll(rx1);

    file = TestCsvParser.getDataFile("multistatics/ssn_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> ssn = parser.parse(file.getAbsolutePath());
    store.addAll(ssn);

    // check data loaded
    assertEquals("have all 3 tracks", 3, store.size());

    // ok, generate the command
    BistaticAngleOperation generator = new BistaticAngleOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // just one track
    selection.add(store.iterator().next());

    List<ICommand> actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 0, actions.size());

    // and two tracks
    selection.clear();
    selection.add(tx1.get(0));
    selection.add(ssn.get(0));
    selection.add(rx1.get(0));

    actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 3, actions.size());

    // check the store contents
    assertEquals("correct datasets", 3, store.size());

    // remove the course from one track, check it doesn't get offered
    IStoreGroup txGroup = (IStoreGroup) store.iterator().next();
    Iterator<IStoreItem> t1Iter = txGroup.iterator();
    IStoreItem toDelete = null;
    while (t1Iter.hasNext())
    {
      IStoreItem thisDoc = (IStoreItem) t1Iter.next();
      if (thisDoc instanceof NumberDocument)
      {
        NumberDocument nd = (NumberDocument) thisDoc;
        if (nd.isQuantity() && nd.getUnits().equals(SampleData.DEGREE_ANGLE))
        {
          toDelete = nd;
        }
      }
    }
    assertNotNull("found course data", toDelete);
    txGroup.remove(toDelete);

    actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 2, actions.size());

    // ok, have a go at running it.
    actions.get(0).execute();

    // check the store contents
    assertEquals("correct datasets", 5, store.size());

  }

  @Test
  public void testCalculation()
  {
    NumberDocumentBuilder bi =
        new NumberDocumentBuilder("Bi angle", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder biA =
        new NumberDocumentBuilder("Bi Aspect angle", SampleData.DEGREE_ANGLE,
            null, SI.SECOND);

    double heading = 15;
    Point2D tx = new Point2D.Double(2, 1);
    Point2D tgt = new Point2D.Double(1, 1);
    Point2D rx = new Point2D.Double(1.1, 0);
    final double time = 1000d;

    final IGeoCalculator calc = GeoSupport.getCalculator();
    BistaticAngleOperation.calcAndStore(calc, tx, tgt, rx, heading, time, bi,
        biA);

    // look at hte results
    assertEquals("correct bi angle", 85, bi.getValues().get(0), 1);
    assertEquals("correct bi A angle", 117, biA.getValues().get(0), 1);

    bi.clear();
    biA.clear();

    // try another permutation
    rx = new Point2D.Double(2, 1.5);

    BistaticAngleOperation.calcAndStore(calc, tx, tgt, rx, heading, time, bi,
        biA);

    // look at the results
    assertEquals("correct bi angle", 26, bi.getValues().get(0), 1);
    assertEquals("correct bi A angle", 61, biA.getValues().get(0), 1);

    heading = 326;
    bi.clear();
    biA.clear();

    BistaticAngleOperation.calcAndStore(calc, tx, tgt, rx, heading, time, bi,
        biA);

    // look at the results
    assertEquals("correct bi angle", 26, bi.getValues().get(0), 1);
    assertEquals("correct bi A angle", 110, biA.getValues().get(0), 1);

    tx.setLocation(1.4, 1.1);
    rx.setLocation(1.3, 1.3);
    tgt.setLocation(1, 1);
    heading = 0;

    bi.clear();
    biA.clear();

    BistaticAngleOperation.calcAndStore(calc, tx, tgt, rx, heading, time, bi,
        biA);

    // look at the results
    assertEquals("correct bi angle", 30, bi.getValues().get(0), 1);
    assertEquals("correct bi A angle", 60, biA.getValues().get(0), 1);

    tx.setLocation(1.2, 1.05);
    rx.setLocation(1.1, 1.17);
    tgt.setLocation(1, 1);
    heading = 356;

    bi.clear();
    biA.clear();

    BistaticAngleOperation.calcAndStore(calc, tx, tgt, rx, heading, time, bi,
        biA);

    // look at the results
    assertEquals("correct bi angle", 45, bi.getValues().get(0), 1);
    assertEquals("correct bi A angle", 57, biA.getValues().get(0), 1);

  }
}