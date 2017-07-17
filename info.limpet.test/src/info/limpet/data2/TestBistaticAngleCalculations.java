package info.limpet.data2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
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
import info.limpet.persistence.csv.CsvParser;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    LocationDocument doc = (LocationDocument) items.get(0);
    assertEquals("singleton", 1, doc.size());
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
    List<ICommand> actions = pDiff.actionsFor(selection, store, context);

    assertNotNull("found actions");
    assertEquals("got actions", 1, actions.size());
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

    actions = pDiff.actionsFor(selection, store, context);

    assertNotNull("found actions");
    assertEquals("got actions", 1, actions.size());
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
    assertEquals("actions returned", 1, actions.size());

    // ok, run the first action
    actions.get(0).execute();
    assertEquals("has new data", 6, store.size());

    // check the outputs
    NumberDocument propSum =
        (NumberDocument) actions.get(0).getOutputs().get(0);
    propSum.setName("propSum");

    // ok, check the values
    double valA = txProp.getDataset().getDouble(0);
    double valB = rxProp.getDataset().getDouble(0);
    double addRes = propSum.getDataset().getDouble(0);

    double testSum = doLogAdd(valA, valB);
    assertEquals("correct log sum", testSum, addRes, 0.0001);

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
    assertEquals("actions returned", 2, actions.size());

    // ok, run it
    actions.get(0).execute();
    assertEquals("has new data", 7, store.size());

    // check the results
    NumberDocument propDiff =
        (NumberDocument) actions.get(0).getOutputs().get(0);
    propDiff.setName("propDiff");

    double subRes = propDiff.getDataset().getDouble(0);

    double testSubtract = doLogSubtract(addRes, valB);
    assertEquals("correct log sum", testSubtract, subRes, 0.0001);

  }

  private double doLogAdd(double valA, double valB)
  {
    double aN = Math.pow(10d, valA / 10d);
    double bN = Math.pow(10d, valB / 10d);
    double sum = aN + bN;
    double toLog = Math.log10(sum) * 10d;
    return toLog;
  }

  private double doLogSubtract(double valA, double valB)
  {
    double aN = Math.pow(10d, valA / 10d);
    double bN = Math.pow(10d, valB / 10d);
    double sum = aN - bN;
    double toLog = Math.log10(sum) * 10d;
    return toLog;
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
    assertEquals("correct actions", 1, actions.size());

    // check the store contents
    assertEquals("correct datasets", 3, store.size());
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

    final IGeoCalculator calc = GeoSupport.getCalculatorWGS84();
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
