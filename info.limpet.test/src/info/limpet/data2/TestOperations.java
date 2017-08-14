/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data2;

import static javax.measure.unit.NonSI.HOUR;
import static javax.measure.unit.NonSI.KILOMETRES_PER_HOUR;
import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.SI.KILO;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import static javax.measure.unit.SI.SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.Range;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocument;
import info.limpet.impl.StringDocumentBuilder;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.OperationsLibrary;
import info.limpet.operations.admin.AddLayerOperation;
import info.limpet.operations.admin.CopyCsvToClipboardAction;
import info.limpet.operations.admin.CreateLocationAction;
import info.limpet.operations.admin.CreateSingletonGenerator;
import info.limpet.operations.admin.DeleteCollectionOperation;
import info.limpet.operations.admin.ExportCsvToFileAction;
import info.limpet.operations.admin.GenerateDummyDataOperation;
import info.limpet.operations.arithmetic.UnaryQuantityOperation;
import info.limpet.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet.operations.arithmetic.simple.MultiplyQuantityOperation;
import info.limpet.operations.arithmetic.simple.SubtractQuantityOperation;
import info.limpet.operations.arithmetic.simple.UnitConversionOperation;
import info.limpet.operations.spatial.BearingBetweenTracksOperation;
import info.limpet.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.operations.spatial.IGeoCalculator;
import info.limpet.persistence.csv.CsvParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.easymock.EasyMock;
import org.eclipse.january.dataset.DoubleDataset;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestOperations
{

  private IContext context = new MockContext();
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testSingletonGenerator()
  {
    CreateSingletonGenerator op =
        new CreateSingletonGenerator("Some data", METRE.asType(Length.class));
    List<IStoreItem> sel = new ArrayList<IStoreItem>();
    IStoreGroup target = new StoreGroup("data");
    IContext context = new MockContext()
    {

      @Override
      public String getInput(String title, String description,
          String defaultText)
      {
        if (description.equals("Enter name for variable"))
        {
          return "output variable name";
        }
        else
        {
          return "1000";
        }
      }
    };
    List<ICommand> ops = op.actionsFor(sel, target, context);
    assertEquals("operation created", 1, ops.size());
    ICommand firstOp = ops.get(0);
    firstOp.execute();
    NumberDocument output = (NumberDocument) firstOp.getOutputs().get(0);
    assertNotNull("output created", output);
    assertEquals("single value", 1, output.size());
    assertEquals("correct units", METRE.asType(Length.class), output.getUnits());
  }

  @Test
  public void testInterpolateTests()
  {
    // place to store results data
    StoreGroup store = new SampleData().getData(10);

    // ok, let's try one that works
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // ///////////////
    // TEST INVALID PERMUTATIONS
    // ///////////////
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument speedGood2 =
        (NumberDocument) store.get(SampleData.SPEED_TWO);
    NumberDocument speedLonger =
        (NumberDocument) store.get(SampleData.SPEED_THREE_LONGER);

    selection.add(speedGood1);
    selection.add(speedLonger);

    Collection<ICommand> actions =
        new AddQuantityOperation().actionsFor(selection, store, context);

    assertEquals("correct number of actions returned", 1, actions.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    actions = new AddQuantityOperation().actionsFor(selection, store, context);

    assertEquals("correct number of actions returned", 1, actions.size());

    ICommand addAction = actions.iterator().next();

    assertNotNull("found action", addAction);

  }

  @Test
  public void testTrig()
  {
    // prepare some data
    NumberDocumentBuilder speedDatab =
        new NumberDocumentBuilder("speed", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    speedDatab.add(100, 23d);
    speedDatab.add(200, 44d);
    NumberDocument speedData = speedDatab.toDocument();

    NumberDocumentBuilder angledataB =
        new NumberDocumentBuilder("degs", Dimensionless.UNIT, null, null);
    angledataB.add(200d);
    angledataB.add(123d);

    NumberDocument angleData = angledataB.toDocument();

    NumberDocumentBuilder temporalAngleDataB =
        new NumberDocumentBuilder("degs", SampleData.DEGREE_ANGLE
            .asType(Angle.class), null, SampleData.MILLIS);
    temporalAngleDataB.add(1000, 200d);
    temporalAngleDataB.add(3000, 123d);
    temporalAngleDataB.add(4000, 13d);

    NumberDocument temporalAngleData = temporalAngleDataB.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup store = new StoreGroup("Store");

    HashMap<String, List<IOperation>> ops = OperationsLibrary.getOperations();
    List<IOperation> arith = ops.get(OperationsLibrary.ARITHMETIC);
    // ok, now find the trig op
    Iterator<IOperation> iter = arith.iterator();
    IOperation sinOp = null;
    IOperation cosOp = null;
    while (iter.hasNext())
    {
      IOperation thisO = (IOperation) iter.next();
      if (thisO instanceof UnaryQuantityOperation)
      {
        UnaryQuantityOperation umo = (UnaryQuantityOperation) thisO;
        if (umo.getName().equals("Sin"))
        {
          sinOp = umo;
        }
        if (umo.getName().equals("Cos"))
        {
          cosOp = umo;
        }
      }
    }

    assertNotNull("check we found it", sinOp);

    // ok, try it with empty data
    Collection<ICommand> validOps = sinOp.actionsFor(selection, null, null);
    assertEquals("null for empty selection", 0, validOps.size());

    // add some speed data
    selection.add(speedData);
    // ok, try it with empty data
    validOps = sinOp.actionsFor(selection, store, null);
    assertEquals("empty for invalid selection", 0, validOps.size());

    // add some valid data
    selection.add(angleData);

    // ok, try it with empty data
    validOps = sinOp.actionsFor(selection, store, null);
    assertEquals("empty for invalid selection", 0, validOps.size());

    // ok, try it with empty data
    validOps = cosOp.actionsFor(selection, store, null);
    assertEquals(" cos also empty for invalid selection", 0, validOps.size());

    // and remove the speed data
    selection.remove(speedData);

    // ok, try it with empty data
    validOps = sinOp.actionsFor(selection, store, context);
    assertEquals("non-empty for valid selection", 1, validOps.size());

    ICommand theOp = validOps.iterator().next();
    theOp.execute();

    assertEquals("has new dataset", 1, store.size());
    IDocument<?> output = theOp.getOutputs().iterator().next();

    // check the size
    assertEquals("correct size", 2, output.size());

    // check data type
    assertTrue("isn't temporal", !output.isIndexed());
    assertTrue("is quantity", output.isQuantity());

    // ok, try it temporal data
    selection.remove(angleData);
    selection.add(temporalAngleData);

    validOps = sinOp.actionsFor(selection, store, context);
    assertEquals("non-empty for valid selection", 1, validOps.size());

    theOp = validOps.iterator().next();
    theOp.execute();

    assertEquals("has new dataset", 2, store.size());
    output = theOp.getOutputs().iterator().next();

    // check the size
    assertEquals("correct size", 3, output.size());

    // check data type
    assertTrue("isn't temporal", output.isIndexed());

  }

  @Test
  public void testIndexUnits()
  {
    CollectionComplianceTests testOp = new CollectionComplianceTests();

    NumberDocumentBuilder sec1 =
        new NumberDocumentBuilder("sec1", null, null, SECOND);
    NumberDocumentBuilder sec2 =
        new NumberDocumentBuilder("sec2", null, null, SECOND);
    NumberDocumentBuilder len1 =
        new NumberDocumentBuilder("len1", null, null, METRE);
    NumberDocumentBuilder len2 =
        new NumberDocumentBuilder("len2", null, null, METRE);

    sec1.add(1000, 12d);
    sec1.add(2000, 13d);
    sec2.add(1000, 12d);
    sec2.add(2000, 13d);
    len1.add(1000, 13d);
    len1.add(2000, 13d);
    len2.add(1000, 13d);
    len2.add(2000, 13d);
    len2.add(3000, 13d);

    List<IStoreItem> sel = new ArrayList<IStoreItem>();
    sel.add(sec1.toDocument());
    sel.add(sec2.toDocument());

    assertTrue("all equal", testOp.allEqualIndexed(sel));

    AddQuantityOperation adder = new AddQuantityOperation();
    IStoreGroup destination = new StoreGroup("data");
    List<ICommand> ops = adder.actionsFor(sel, destination, context);
    assertEquals("one found", 1, ops.size());

    sel.clear();
    sel.add(len1.toDocument());
    sel.add(len2.toDocument());

    assertTrue("all equal", testOp.allEqualIndexed(sel));
    ops = adder.actionsFor(sel, destination, context);
    assertEquals("one found", 1, ops.size());

    sel.clear();
    sel.add(sec1.toDocument());
    sel.add(len2.toDocument());

    assertFalse("all not equal", testOp.allEqualIndexed(sel));
    ops = adder.actionsFor(sel, destination, context);
    assertEquals("none found", 0, ops.size());

  }

  @Test
  public void testLocationComplianceTest()
  {
    LocationDocumentBuilder lda1 =
        new LocationDocumentBuilder("relative", null, SECOND);
    LocationDocumentBuilder lda2 =
        new LocationDocumentBuilder("relative", null, SECOND);
    LocationDocumentBuilder ldb =
        new LocationDocumentBuilder("relative", null, SECOND, SI.METER);

    IGeoCalculator calcA = lda1.getCalculator();
    IGeoCalculator calcB = ldb.getCalculator();

    lda1.add(1000, calcA.createPoint(2, 4));
    lda1.add(2000, calcA.createPoint(3, 5));

    lda2.add(1000, calcA.createPoint(1, 2));
    lda2.add(2000, calcA.createPoint(4, 1));

    ldb.add(1000, calcB.createPoint(2, 4));
    ldb.add(2000, calcB.createPoint(3, 5));

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(lda1.toDocument());
    selection.add(ldb.toDocument());
    final StoreGroup store = new StoreGroup("Results");

    List<ICommand> ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("empty collection - wrong distance units", 0, ops.size());

    // try with wroking permutation
    selection.clear();
    selection.add(lda1.toDocument());
    selection.add(lda2.toDocument());

    ops =
        new DistanceBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("received command", 1, ops.size());

  }

  @Test
  public void testAppliesTo()
  {
    // the units for this measurement
    Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
    Unit<Velocity> kmm = KILO(METRE).divide(MINUTE).asType(Velocity.class);
    Unit<Length> m = METRE.asType(Length.class);

    // the target collection
    NumberDocumentBuilder speedGood1b =
        new NumberDocumentBuilder("Speed 1", kmh, null, null);
    NumberDocumentBuilder speedGood2b =
        new NumberDocumentBuilder("Speed 2", kmh, null, null);
    NumberDocumentBuilder speedLongerb =
        new NumberDocumentBuilder("Speed 3", kmh, null, null);
    NumberDocumentBuilder speedDiffUnitsb =
        new NumberDocumentBuilder("Speed 4", kmm, null, null);
    NumberDocumentBuilder len1b =
        new NumberDocumentBuilder("Length 1", m, null, null);
    NumberDocumentBuilder temporalSpeed1b =
        new NumberDocumentBuilder("Speed 5", kmh, null, SampleData.MILLIS);
    NumberDocumentBuilder temporalSpeed2b =
        new NumberDocumentBuilder("Speed 6", kmh, null, SampleData.MILLIS);
    StringDocumentBuilder string1 =
        new StringDocumentBuilder("strings 1", null, null);
    StringDocumentBuilder string2 =
        new StringDocumentBuilder("strings 2", null, null);

    for (int i = 1; i <= 10; i++)
    {
      // create a measurement
      double thisSpeed = i * 2;

      // store the measurements
      speedGood1b.add(thisSpeed);
      speedGood2b.add(thisSpeed * 2);
      speedLongerb.add(thisSpeed / 2);
      speedDiffUnitsb.add(thisSpeed / 2);
      temporalSpeed1b.add(i, thisSpeed * 2);
      temporalSpeed2b.add(i, thisSpeed / 2);
      len1b.add(thisSpeed / 2);
      string1.add(i + " ");
      string2.add(i + "a ");
    }

    speedLongerb.add(2d);

    NumberDocument temporalSpeed1 = temporalSpeed1b.toDocument();
    NumberDocument temporalSpeed2 = temporalSpeed2b.toDocument();

    double max = temporalSpeed1.stats().max();
    System.out.println(max);

    double min = temporalSpeed1.stats().min();
    System.out.println(min);

    Range range = temporalSpeed1.getRange();
    System.out.println(range);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    CollectionComplianceTests testOp = new CollectionComplianceTests();

    IStoreItem speedGood1 = speedGood1b.toDocument();
    IStoreItem speedGood2 = speedGood2b.toDocument();

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertTrue("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allEqualIndexed(selection));
    assertFalse("all groups", testOp.allGroups(selection));

    assertFalse("all Temporal or singleton", testOp
        .allIndexedOrSingleton(selection));

    assertTrue("Longest collection lenght", testOp
        .getLongestCollectionLength(selection) > 0);

    StoreGroup track1 = new StoreGroup("Track 1");
    selection.add(track1);

    assertFalse("all childrens are tracks", testOp
        .allChildrenAreTracks(selection));

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    IStoreItem speedDiffUnits = speedDiffUnitsb.toDocument();
    selection.add(speedDiffUnits);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allEqualIndexed(selection));

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    IStoreItem len1 = len1b.toDocument();
    selection.add(len1);

    assertFalse("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allEqualIndexed(selection));

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    IStoreItem speedLonger = speedLongerb.toDocument();
    selection.add(speedLonger);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertTrue("all same units", testOp.allEqualUnits(selection));
    assertFalse("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allEqualIndexed(selection));

    selection.clear();
    selection.add(temporalSpeed1);
    selection.add(temporalSpeed2);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertTrue("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertTrue("all temporal", testOp.allEqualIndexed(selection));

    selection.clear();
    selection.add(temporalSpeed1);
    final StringDocument string1d = string1.toDocument();
    selection.add(string1d);

    assertFalse("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertFalse("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allEqualIndexed(selection));

    selection.clear();
    selection.add(string1d);
    selection.add(string1d);

    assertFalse("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all non quantities", testOp.allNonQuantity(selection));
    assertFalse("all temporal", testOp.allEqualIndexed(selection));

    // ok, let's try one that works
    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);

    StoreGroup store = new StoreGroup("Store");
    assertEquals("store empty", 0, store.size());

    @SuppressWarnings(
    {})
    Collection<ICommand> actions =
        new AddQuantityOperation().actionsFor(selection, store, context);

    assertEquals("correct number of actions returned", 1, actions.size());

    ICommand addAction = actions.iterator().next();
    addAction.execute();

    assertEquals("new collection added to store", 1, store.size());

    IDocument<?> firstItem = (IDocument<?>) store.iterator().next();
    ICommand precedent = firstItem.getPrecedent();
    assertNotNull("has precedent", precedent);
    assertEquals("Correct name",
        "Add numeric values in provided series (indexed)", precedent.getName());

    List<? extends IStoreItem> inputs = precedent.getInputs();
    assertEquals("Has both precedents", 2, inputs.size());

    Iterator<? extends IStoreItem> iIter = inputs.iterator();
    while (iIter.hasNext())
    {
      IDocument<?> thisC = (IDocument<?>) iIter.next();
      List<ICommand> deps = thisC.getDependents();
      assertEquals("has a depedent", 1, deps.size());
      Iterator<ICommand> dIter = deps.iterator();
      while (dIter.hasNext())
      {
        ICommand iCommand = dIter.next();
        assertEquals("Correct dependent", precedent, iCommand);
      }
    }

    List<Document<?>> outputs = precedent.getOutputs();
    assertEquals("Has both dependents", 1, outputs.size());

    Iterator<Document<?>> oIter = outputs.iterator();
    while (oIter.hasNext())
    {
      IDocument<?> thisC = (IDocument<?>) oIter.next();
      ICommand dep = thisC.getPrecedent();
      assertNotNull("has a depedent", dep);
      assertEquals("Correct dependent", precedent, dep);
    }
  }

  @Test
  public void testDimensionlessMultiply()
  {
    // place to store results data
    StoreGroup store = new SampleData().getData(30);

    // ok, let's try one that works
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // ///////////////
    // TEST INVALID PERMUTATIONS
    // ///////////////
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument speedGood2 =
        (NumberDocument) store.get(SampleData.SPEED_TWO);
    NumberDocument speedIrregular =
        (NumberDocument) store.get(SampleData.SPEED_IRREGULAR2);
    IDocument<?> string1 = (IDocument<?>) store.get(SampleData.STRING_ONE);
    NumberDocument len1 = (NumberDocument) store.get(SampleData.LENGTH_ONE);
    NumberDocument factor =
        (NumberDocument) store.get(SampleData.FLOATING_POINT_FACTOR);

    selection.clear();
    selection.add(speedGood1);
    selection.add(string1);
    Collection<ICommand> commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not both quantities", 0, commands
        .size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(len1);

    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - both quantities", 1, commands.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    store.clear();
    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - both speeds", 1, commands.size());

    // //////////////////////////
    // now test valid collections
    // /////////////////////////

    selection.clear();
    selection.add(speedGood1);
    selection.add(factor);

    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - one is singleton", 1, commands.size());

    ICommand command = commands.iterator().next();

    // test actions has single item: "Multiply series by constant"
    assertEquals("correct name",
        "Multiply numeric values in provided series (interpolated)", command
            .getName());

    // apply action
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", 1, store.size());

    NumberDocument newS = (NumberDocument) command.getOutputs().get(0);

    // test results is same length as thisSpeed
    assertEquals("correct size", 30, newS.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(factor);
    store.clear();
    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - one is singleton", 1, commands.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedIrregular);
    store.clear();
    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - one is singleton", 1, commands.size());
    command = commands.iterator().next();
    command.execute();
    NumberDocument output =
        (NumberDocument) command.getOutputs().iterator().next();
    assertTrue(output.isIndexed());
    assertTrue(output.isQuantity());
  }

  @Test
  public void testUnitConversion()
  {
    // place to store results data
    IStoreGroup store = new SampleData().getData(10);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    // speed one defined in m/s
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    selection.add(speedGood1);

    // test incompatible target unit
    Collection<ICommand> commands =
        new UnitConversionOperation(METRE)
            .actionsFor(selection, store, context);
    assertEquals("target unit not same dimension as input", 0, commands.size());

    // test valid target unit
    commands =
        new UnitConversionOperation(KILOMETRES_PER_HOUR).actionsFor(selection,
            store, context);
    assertEquals("valid unit dimensions", 1, commands.size());

    ICommand command = commands.iterator().next();

    // apply action
    command.execute();

    NumberDocument newS =
        (NumberDocument) store.get("Speed One Time converted to:km/h");
    assertNotNull(newS);
    command.dataChanged(newS);

    // test results is same length as thisSpeed
    assertEquals("correct size", 10, newS.size());
    assertTrue("is temporal", newS.isIndexed());

    // check that operation isn't offered if the dataset is already in
    // that type
    commands =
        new UnitConversionOperation(METRES_PER_SECOND).actionsFor(selection,
            store, context);
    assertEquals("already in destination units", 0, commands.size());

    NumberDocument inputSpeed = speedGood1;

    @SuppressWarnings("unused")
    double firstInputSpeed = inputSpeed.getValueAt(0);
  }

  // TODO: reinstate simple moving average
  // @Test
  // public void testTemporalMovingAverage()
  // {
  // // place to store results data
  // IStoreGroup store = new SampleData().getData(10);
  //
  // List<Document> selection = new ArrayList<Document>();
  //
  // @SuppressWarnings("unchecked")
  // NumberDocument speedGood1 =
  // (NumberDocument) store.get(SampleData.SPEED_ONE);
  // selection.add(speedGood1);
  //
  // int windowSize = 3;
  //
  // Collection<ICommand> commands =
  // new SimpleMovingAverageOperation(windowSize).actionsFor(selection,
  // store, context);
  // assertEquals(1, commands.size());
  //
  // ICommand command = commands.iterator().next();
  //
  // // apply action
  // command.execute();
  //
  // @SuppressWarnings("unchecked")
  // NumberDocument newS =
  // (NumberDocument) store.get("Moving average of Speed One Time");
  // assertNotNull(newS);
  //
  // // test results is same length as thisSpeed
  // assertEquals("correct size", 10, newS.size());
  //
  // // calculate sum of input values [0..windowSize-1]
  // double sum = 0;
  // for (int i = 0; i < windowSize; i++)
  // {
  // double inputQuantity = speedGood1.getValue(i);
  // sum += inputQuantity;
  // }
  // double average = sum / windowSize;
  //
  // // compare to output value [windowSize-1]
  // double simpleMovingAverage =
  // newS.getValue(windowSize - 1);
  //
  // assertEquals(average, 0, simpleMovingAverage);
  //
  // }
  //
  // @Test
  // public void testSimpleMovingAverage()
  // {
  // // place to store results data
  // IStore store = new SampleData().getData(10);
  //
  // List<ICollection> selection = new ArrayList<>();
  //
  // @SuppressWarnings("unchecked")
  // QuantityCollection<Length> lengthOne =
  // (QuantityCollection<Length>) store.get(SampleData.LENGTH_ONE);
  // selection.add(lengthOne);
  //
  // int windowSize=3;
  //
  // Collection<ICommand<ICollection>> commands =
  // new SimpleMovingAverageOperation(windowSize).actionsFor(selection,
  // store, context);
  // assertEquals(1, commands.size());
  //
  // ICommand<ICollection> command = commands.iterator().next();
  //
  // // apply action
  // command.execute();
  //
  // @SuppressWarnings("unchecked")
  // IQuantityCollection<Velocity> newS =
  // (IQuantityCollection<Velocity>) store
  // .get("Moving average of Length One non-Time");
  // assertNotNull(newS);
  //
  // // test results is same length as thisSpeed
  // assertEquals("correct size", 10, newS.getValuesCount());
  //
  // // calculate sum of input values [0..windowSize-1]
  // double sum = 0;
  // for (int i = 0; i < windowSize; i++)
  // {
  // Measurable<Length> inputQuantity = lengthOne.getValues().get(i);
  // sum += inputQuantity.doubleValue(lengthOne.getUnits());
  // }
  // double average = sum / windowSize;
  //
  // // compare to output value [windowSize-1]
  // Measurable<Velocity> simpleMovingAverage =
  // newS.getValues().get(windowSize - 1);
  //
  // assertEquals(average, simpleMovingAverage.doubleValue(newS.getUnits()), 0);
  //
  // }

  @Test
  public void testAddition()
  {
    StoreGroup store = new SampleData().getData(10);

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument speedGood2 =
        (NumberDocument) store.get(SampleData.SPEED_TWO);

    NumberDocument newS =
        (NumberDocument) store.get("Speed One Time + Speed Two Time");

    assertNotNull(newS);
    assertEquals("correct size", 10, newS.size());

    // assert same unit
    assertEquals(newS.getUnits(), speedGood1.getUnits());

    double firstDifference = newS.getValueAt(0);
    double speed1firstValue = speedGood1.getValueAt(0);
    double speed2firstValue = speedGood2.getValueAt(0);

    assertEquals(firstDifference, speed1firstValue + speed2firstValue, 0);

    // test that original series have dependents
    assertEquals("first series has dependents", 2, speedGood1.getDependents()
        .size());
    assertEquals("second series has dependents", 1, speedGood2.getDependents()
        .size());

    // test that new series has predecessors
    assertNotNull("new series has precedent", newS.getPrecedent());
    assertEquals("Have correct precedent",
        "Add numeric values in provided series (interpolated)", newS
            .getPrecedent().getName());

  }

  @Test
  public void testSubtractionSingleton()
  {
    StoreGroup store = new SampleData().getData(10);
    StoreGroup target = new StoreGroup("data");
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    CollectionComplianceTests testOp = new CollectionComplianceTests();

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument speedSingle =
        (NumberDocument) store.get(SampleData.RANGED_SPEED_SINGLETON);

    // TODO: subtract should offer operations that go both ways.

    selection.add(speedGood1);
    selection.add(speedSingle);

    // does the equal dimensions operator work for this?
    assertTrue("equal dimensions", testOp.allEqualDimensions(selection));

    List<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);

    assertEquals("got four commands", 2, commands.size());

    // have a look
    ICommand first = commands.get(0);
    first.execute();
    NumberDocument output =
        (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedGood1.getValueAt(0)
        - speedSingle.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // ok, try the reverse operation
    target.clear();

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);
    first = commands.get(1);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedSingle.getValueAt(0)
        - speedGood1.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // ok, try the forward indexed operation
    target.clear();

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);
    assertEquals("correct number of actions", 2, commands.size());
    first = commands.get(0);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedGood1.getValueAt(0)
        - speedSingle.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // ok, try the reverse indexed operation
    target.clear();

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);
    first = commands.get(1);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedSingle.getValueAt(0)
        - speedGood1.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // swap them around - so the singleton is first
    selection.clear();
    selection.add(speedSingle);
    selection.add(speedGood1);

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);

    assertEquals("got four commands", 2, commands.size());

    // have a look
    first = commands.get(1);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedGood1.getValueAt(0)
        - speedSingle.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // ok, try the reverse operation
    target.clear();

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);
    first = commands.get(0);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedSingle.getValueAt(0)
        - speedGood1.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // ok, try the forward indexed operation
    target.clear();

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);
    first = commands.get(1);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedGood1.getValueAt(0)
        - speedSingle.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());

    // ok, try the reverse indexed operation
    target.clear();

    commands =
        new SubtractQuantityOperation().actionsFor(selection, target, context);
    first = commands.get(0);
    first.execute();
    output = (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());
    assertEquals("correct value", speedSingle.getValueAt(0)
        - speedGood1.getValueAt(0), output.getValueAt(0), 0.001);
    assertTrue(output.isIndexed());
  }

  @Test
  public void testAddSingleton()
  {
    StoreGroup store = new SampleData().getData(10);
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocumentBuilder speedSingleb =
        new NumberDocumentBuilder("singleton", METRE.divide(SECOND).asType(
            Velocity.class), null, null);

    speedSingleb.add(2d);

    NumberDocument speedSingle = speedSingleb.toDocument();

    selection.add(speedGood1);
    selection.add(speedSingle);
    Collection<ICommand> commands =
        new AddQuantityOperation().actionsFor(selection, store, context);
    assertEquals("got two commands", 1, commands.size());

    // have a look
    Iterator<ICommand> iter = commands.iterator();
    ICommand first = iter.next();
    first.execute();
    NumberDocument output =
        (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertTrue("output is temporal", output.isIndexed());
    assertEquals("correct size", speedGood1.size(), output.size());

    assertEquals("correct value", output.getValueAt(0), speedGood1
        .getValueAt(0) + 2, 0.001);
  }

  @Test
  public void testSubtractionNonOverlapping()
  {
    final CollectionComplianceTests testOp = new CollectionComplianceTests();

    final StoreGroup store = new SampleData().getData(10);
    final List<IStoreItem> selection = new ArrayList<IStoreItem>();

    final NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    final NumberDocument speedGood2 =
        (NumberDocument) store.get(SampleData.SPEED_EARLY);
    selection.add(speedGood1);
    selection.add(speedGood2);

    // check they are the same length (since indexing relies on that)
    assertEquals("both same length", speedGood1.size(), speedGood2.size());

    // check they don't overlap
    assertFalse("don't overlap", testOp
        .suitableForIndexedInterpolation(selection));

    // suitable for indexed
    assertTrue("suitable for indexing", testOp
        .allEqualLengthOrSingleton(selection));

    List<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("Offered indexed operations", 2, commands.size());
    // get the first one
    ICommand first = commands.get(0);
    assertTrue("is indexed", first.getName().contains("(indexed)"));
  }

  @Test
  public void testSubtraction()
  {
    StoreGroup store = new SampleData().getData(10);
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument angle1 = (NumberDocument) store.get(SampleData.ANGLE_ONE);
    selection.add(speedGood1);
    selection.add(angle1);
    List<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not same dimensions", 0, commands
        .size());

    selection.clear();

    // test not all quantities
    IDocument<?> string1 = (IDocument<?>) store.get(SampleData.STRING_ONE);
    selection.add(speedGood1);
    selection.add(string1);
    commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not all quantities", 0, commands.size());

    selection.clear();

    // test valid command
    NumberDocument speedGood2 =
        (NumberDocument) store.get(SampleData.SPEED_TWO);
    selection.add(speedGood1);
    selection.add(speedGood2);

    commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid command", 2, commands.size());

    int storeSize = store.size();

    ICommand command = commands.get(0);
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", storeSize + 1, store.size());

    NumberDocument newS =
        (NumberDocument)command.getOutputs().get(0);

    assertNotNull("document produced", newS);
    assertEquals("correct size", 10, newS.size());

    // assert same unit
    assertEquals(newS.getUnits(), speedGood1.getUnits());

    double firstDifference = newS.getValueAt(0);
    double speed1firstValue = speedGood1.getValueAt(0);
    double speed2firstValue = speedGood2.getValueAt(0);

    assertEquals(firstDifference, speed1firstValue - speed2firstValue, 0);

    // and try the other operation
    command = commands.get(1);
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", storeSize + 2, store.size());

    newS =
        (NumberDocument) command.getOutputs().get(0);

    assertNotNull("document produced", newS);
    assertEquals("correct size", 10, newS.size());

    // assert same unit
    assertEquals(newS.getUnits(), speedGood1.getUnits());

    firstDifference = newS.getValueAt(0);
    speed1firstValue = speedGood1.getValueAt(0);
    speed2firstValue = speedGood2.getValueAt(0);
    assertEquals(firstDifference, speed2firstValue - speed1firstValue, 0);
  }

  @Test
  public void testSubtractSingleton() throws RuntimeException
  {
    StoreGroup store = new SampleData().getData(10);
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);

    // test singleton
    NumberDocument speedSingleton =
        (NumberDocument) store.get(SampleData.RANGED_SPEED_SINGLETON);
    assertEquals("it's a singleton", 1, speedSingleton.getSize());

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedSingleton);

    List<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid command", 2, commands.size());

    int storeSize = store.size();

    ICommand command = commands.get(0);
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", storeSize + 1, store.size());

    String outName =
        speedGood1.getName() + " subtracted from " + speedSingleton.getName();
    NumberDocument newS =  (NumberDocument) command.getOutputs().get(0);

    assertNotNull("document produced", newS);
    assertEquals("correct size", 10, newS.size());

    store.remove(outName);

    // ok, do the reverse
    command = commands.get(1);
    command.execute();

    newS = (NumberDocument) command.getOutputs().get(0);

    assertNotNull("document produced", newS);
    assertEquals("correct size", 10, newS.size());

  }

  @Test
  public void testAddLayerOperation() throws RuntimeException
  {
    IContext context = EasyMock.createMock(MockContext.class);
    // place to store results data
    StoreGroup store = new SampleData().getData(10);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    StoreGroup track1 = new StoreGroup("Track 1");
    selection.add(track1);

    Collection<ICommand> commands =
        new AddLayerOperation().actionsFor(selection, store, context);
    assertEquals("Valid number of commands", 1, commands.size());
    commands.contains(track1);
    Iterator<ICommand> iterator = commands.iterator();
    ICommand firstItem = iterator.next();

    EasyMock.expect(
        context.getInput("Add layer", "Provide name for new folder", ""))
        .andReturn("").times(2);
    EasyMock.expect(
        context.getInput("Add layer", "Provide name for new folder", ""))
        .andReturn(null).times(1);
    EasyMock.expect(
        context.getInput("Add layer", "Provide name for new folder", ""))
        .andReturn("").times(1);

    EasyMock.replay(context);

    firstItem.execute();

    // Coverage purpose for Equals method.
    boolean equals = firstItem.equals(track1);
    assertEquals("Two objects are not equal", false, equals);

    // Coverage purpose for Hash code method
    long hashCode = firstItem.hashCode();
    final int prime = 31;
    int result = 1;
    result = prime * result + firstItem.getUUID().hashCode();
    assertEquals(result, hashCode);

    assertEquals("Parent not defined", null, firstItem.getParent());

    firstItem.setParent(track1);
    assertEquals("Parent defined as a Track1", track1, firstItem.getParent());

    assertNotNull("UUID is generated randomly", firstItem.getUUID());

    StoreGroup dummyStoreTrack = new StoreGroup("Dummy Store Track");
    firstItem.metadataChanged(dummyStoreTrack);

    assertTrue("First Item is dynamic", firstItem.getDynamic());

    assertEquals("Store Item Description", "Add a new layer", firstItem
        .getDescription());
    firstItem.execute();
    firstItem.collectionDeleted(dummyStoreTrack);

    try
    {
      firstItem.undo();
    }
    catch (Throwable throwable)
    {
      assertEquals(true, throwable instanceof UnsupportedOperationException);
    }
    try
    {
      firstItem.redo();
    }
    catch (Throwable throwable)
    {
      assertEquals(true, throwable instanceof UnsupportedOperationException);
    }
    assertEquals("CanUndo operation", false, firstItem.canRedo());
    assertEquals("CanRedo operation", false, firstItem.canUndo());

    // boolean hasChildren = firstItem.hasChildren();
    // assertEquals("Parent have children", true, hasChildren);

    firstItem.execute();

    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    selection = new ArrayList<>();
    selection.add(speedGood1);

    commands = new AddLayerOperation().actionsFor(selection, store, context);
    assertEquals("invalid number of inputs", 1, commands.size());
    for (ICommand iCommand : commands)
    {
      iCommand.execute();
      iCommand.dataChanged(speedGood1);
    }
  }

  @Test
  public void testCreateSingletonGenerator()
  {
    StoreGroup store = new SampleData().getData(10);
    CreateSingletonGenerator generator =
        new CreateSingletonGenerator("dimensionless", Dimensionless.UNIT);
    assertNotNull("Create Single Generator is not NULL", generator);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup storeGroup = new StoreGroup("Track 1");
    selection.add(storeGroup);

    IContext mockContext = EasyMock.createMock(MockContext.class);

    Collection<ICommand> singleGeneratorActionFor =
        generator.actionsFor(selection, store, mockContext);
    assertEquals("Create location collection size", 1, singleGeneratorActionFor
        .size());
    ICommand singleGenCommand = singleGeneratorActionFor.iterator().next();

    final String outName = "new_name";
    EasyMock.expect(
        mockContext.getInput("New variable", "Enter name for variable", "new dimensionless"))
        .andReturn(outName).times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter initial value for variable", "100")).andReturn("1234.56")
        .times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter the range for variable (or cancel to leave un-ranged)", "0:100")).andReturn("5:100")
        .times(1);
    EasyMock.replay(mockContext);

    singleGenCommand.execute();

    NumberDocument output = (NumberDocument) singleGenCommand.getOutputs().get(0);
    assertNotNull(output);
    assertEquals("correct name", outName, output.getName());
    assertEquals("correct size", 1, output.size());
    assertEquals("correct value", 1234.56, output.getValueAt(0), 0.01);
    assertEquals("correct range", new Range(5d,100d), output.getRange());
    assertEquals("correct units", Dimensionless.UNIT, output.getUnits());
    assertEquals("no index units", null, output.getIndexUnits());
    assertFalse("not indexed", output.isIndexed());
    assertTrue("is quantity", output.isQuantity());
  }


  @Test
  public void testCreateSingletonDecibelGenerator()
  {
    StoreGroup store = new SampleData().getData(10);
    CreateSingletonGenerator generator =
        new CreateSingletonGenerator("Decibel", NonSI.DECIBEL);
    assertNotNull("Create Single Generator is not NULL", generator);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup storeGroup = new StoreGroup("Track 1");
    selection.add(storeGroup);

    IContext mockContext = EasyMock.createMock(MockContext.class);

    Collection<ICommand> singleGeneratorActionFor =
        generator.actionsFor(selection, store, mockContext);
    assertEquals("Create location collection size", 1, singleGeneratorActionFor
        .size());
    ICommand singleGenCommand = singleGeneratorActionFor.iterator().next();

    final String outName = "new_name";
    EasyMock.expect(
        mockContext.getInput("New variable", "Enter name for variable", "new Decibel"))
        .andReturn(outName).times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter initial value for variable", "100")).andReturn("1234.56")
        .times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter the range for variable (or cancel to leave un-ranged)", "0:100")).andReturn("5:100")
        .times(1);
    EasyMock.replay(mockContext);

    singleGenCommand.execute();

    NumberDocument output = (NumberDocument) singleGenCommand.getOutputs().get(0);
    assertNotNull(output);
    assertEquals("correct name", outName, output.getName());
    assertEquals("correct size", 1, output.size());
    assertEquals("correct value", 1234.56, output.getValueAt(0), 0.01);
    assertEquals("correct range", new Range(5d,100d), output.getRange());
    assertEquals("correct units", NonSI.DECIBEL, output.getUnits());
    assertEquals("no index units", null, output.getIndexUnits());
    assertFalse("not indexed", output.isIndexed());
    assertTrue("is quantity", output.isQuantity());
  }
  
  @Test
  public void testCreateSingletonDecibelGeneratorNoRange1()
  {
    StoreGroup store = new SampleData().getData(10);
    CreateSingletonGenerator generator =
        new CreateSingletonGenerator("Decibel", NonSI.DECIBEL);
    assertNotNull("Create Single Generator is not NULL", generator);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup storeGroup = new StoreGroup("Track 1");
    selection.add(storeGroup);

    IContext mockContext = EasyMock.createMock(MockContext.class);

    Collection<ICommand> singleGeneratorActionFor =
        generator.actionsFor(selection, store, mockContext);
    assertEquals("Create location collection size", 1, singleGeneratorActionFor
        .size());
    ICommand singleGenCommand = singleGeneratorActionFor.iterator().next();

    final String outName = "new_name";
    EasyMock.expect(
        mockContext.getInput("New variable", "Enter name for variable", "new Decibel"))
        .andReturn(outName).times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter initial value for variable", "100")).andReturn("1234.56")
        .times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter the range for variable (or cancel to leave un-ranged)", "0:100")).andReturn("")
        .times(1);
    EasyMock.replay(mockContext);

    singleGenCommand.execute();

    NumberDocument output = (NumberDocument) singleGenCommand.getOutputs().get(0);
    assertNotNull(output);
    assertEquals("correct name", outName, output.getName());
    assertEquals("correct size", 1, output.size());
    assertEquals("correct value", 1234.56, output.getValueAt(0), 0.01);
    assertEquals("correct range", null, output.getRange());
    assertEquals("correct units", NonSI.DECIBEL, output.getUnits());
    assertEquals("no index units", null, output.getIndexUnits());
    assertFalse("not indexed", output.isIndexed());
    assertTrue("is quantity", output.isQuantity());
  }
  
  
  @Test
  public void testCreateSingletonDecibelGeneratorNoRange2()
  {
    StoreGroup store = new SampleData().getData(10);
    CreateSingletonGenerator generator =
        new CreateSingletonGenerator("Decibel", NonSI.DECIBEL);
    assertNotNull("Create Single Generator is not NULL", generator);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup storeGroup = new StoreGroup("Track 1");
    selection.add(storeGroup);

    IContext mockContext = EasyMock.createMock(MockContext.class);

    Collection<ICommand> singleGeneratorActionFor =
        generator.actionsFor(selection, store, mockContext);
    assertEquals("Create location collection size", 1, singleGeneratorActionFor
        .size());
    ICommand singleGenCommand = singleGeneratorActionFor.iterator().next();

    final String outName = "new_name";
    EasyMock.expect(
        mockContext.getInput("New variable", "Enter name for variable", "new Decibel"))
        .andReturn(outName).times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter initial value for variable", "100")).andReturn("1234.56")
        .times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter the range for variable (or cancel to leave un-ranged)", "0:100")).andReturn("0-100")
        .times(1);
    EasyMock.replay(mockContext);

    singleGenCommand.execute();

    NumberDocument output = (NumberDocument) singleGenCommand.getOutputs().get(0);
    assertNotNull(output);
    assertEquals("correct name", outName, output.getName());
    assertEquals("correct size", 1, output.size());
    assertEquals("correct value", 1234.56, output.getValueAt(0), 0.01);
    assertEquals("correct range", null, output.getRange());
    assertEquals("correct units", NonSI.DECIBEL, output.getUnits());
    assertEquals("no index units", null, output.getIndexUnits());
    assertFalse("not indexed", output.isIndexed());
    assertTrue("is quantity", output.isQuantity());
  }
  
  
  @Test
  public void testCreateLocationAction()
  {
    StoreGroup store = new SampleData().getData(10);
    CreateLocationAction createLocationAction = new CreateLocationAction();
    assertNotNull("Create Location action is not NULL", createLocationAction);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup storeGroup = new StoreGroup("Track 1");
    selection.add(storeGroup);

    IContext mockContext = EasyMock.createMock(MockContext.class);

    Collection<ICommand> actionsFor =
        createLocationAction.actionsFor(selection, store, mockContext);
    assertEquals("Create location collection size", 1, actionsFor.size());
    Iterator<ICommand> creationLocIterator = actionsFor.iterator();
    ICommand command = creationLocIterator.next();

    EasyMock.expect(
        mockContext.getInput("New fixed location", "Enter name for location",
            "")).andReturn("seriesName").times(1);
    EasyMock.expect(
        mockContext.getInput("New location",
            "Enter initial value for latitude", "")).andReturn("123.23").times(
        1);
    EasyMock.expect(
        mockContext.getInput("New location",
            "Enter initial value for longitude", "")).andReturn("3456.78")
        .times(1);
    EasyMock.replay(mockContext);

    command.execute();
  }

  @Test
  public void testExportCsvToFileAction()
  {
    StoreGroup store = new SampleData().getData(10);
    ExportCsvToFileAction exportCSVFileAction = new ExportCsvToFileAction();
    assertNotNull(exportCSVFileAction);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    selection.add(speedGood1);

    IContext mockContext = EasyMock.createMock(MockContext.class);

    Collection<ICommand> exportActionfor =
        exportCSVFileAction.actionsFor(selection, store, mockContext);
    assertEquals("Export CSV file collection size", 1, exportActionfor.size());
    Iterator<ICommand> iterator = exportActionfor.iterator();
    ICommand command = iterator.next();

    EasyMock.expect(mockContext.getCsvFilename()).andReturn("ExportCSV.csv")
        .times(1);
    EasyMock.expect(
        mockContext.openQuestion("Overwrite '" + "ExportCSV.csv" + "'?",
            "Are you sure you want to overwrite '" + "ExportCSV.csv" + "'?"))
        .andReturn(true).times(1);
    EasyMock.replay(mockContext);

    command.execute();
  }

  @Test
  public void testCopyCsvToClipboardAction()
  {

    StoreGroup store = new SampleData().getData(10);
    CopyCsvToClipboardAction copyCSVToClipAction =
        new CopyCsvToClipboardAction();
    assertNotNull(copyCSVToClipAction);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    selection.add(speedGood1);

    IContext mockContext = EasyMock.createMock(MockContext.class);
    Collection<ICommand> copyCSVActionfor =
        copyCSVToClipAction.actionsFor(selection, store, mockContext);
    assertEquals("Copy CSV file collection size", 1, copyCSVActionfor.size());

    Iterator<ICommand> copyrIterator = copyCSVActionfor.iterator();
    ICommand copyCommand = copyrIterator.next();
    copyCommand.execute();
  }

  @Test
  public void testOperations()
  {
    // place to store results data
    HashMap<String, List<IOperation>> ops = OperationsLibrary.getOperations();

    List<IOperation> create = ops.get(OperationsLibrary.CREATE);
    assertEquals("Creation size", 7, create.size());
    // Administrator Operations.

    List<IOperation> adminOperations =
        ops.get(OperationsLibrary.ADMINISTRATION);
    assertEquals("Creation size", 8, adminOperations.size());

    List<IOperation> topLevel = OperationsLibrary.getTopLevel();
    assertNotNull(topLevel);
  }

  //
  // @Test
  // @SuppressWarnings("unchecked")
  // public void testDivision()
  // {
  // // place to store results data
  // StoreGroup store = new SampleData().getData(10);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  //
  // IQuantityCollection<Velocity> speedGood1 =
  // (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
  // ICollection speedGood2 = (ICollection) store.get(SampleData.SPEED_TWO);
  // IQuantityCollection<Length> length1 =
  // (IQuantityCollection<Length>) store.get(SampleData.LENGTH_ONE);
  // ICollection string1 = (ICollection) store.get(SampleData.STRING_ONE);
  // IQuantityCollection<Dimensionless> factor =
  // (IQuantityCollection<Dimensionless>) store
  // .get(SampleData.FLOATING_POINT_FACTOR);
  // ICollection speedGood1Bigger =
  // (ICollection) new SampleData().getData(20).get(SampleData.SPEED_ONE);
  //
  // // /
  // // / TEST NOT APPLICABLE INPUT
  // // /
  //
  // // test invalid number of inputs
  // selection.add(speedGood1);
  // selection.add(speedGood2);
  // selection.add(length1);
  // Collection<ICommand<IStoreItem>> commands =
  // new DivideQuantityOperation().actionsFor(selection, store, context);
  // assertEquals("invalid number of inputs", 0, commands.size());
  //
  // // test not all quantities
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(string1);
  // commands =
  // new DivideQuantityOperation().actionsFor(selection, store, context);
  // assertEquals("not all quantities", 0, commands.size());
  //
  // // test different size
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(speedGood1Bigger);
  // commands =
  // new DivideQuantityOperation().actionsFor(selection, store, context);
  // assertEquals("collection not same size", 2, commands.size());
  //
  // // /
  // // / TEST APPLICABLE INPUT
  // // /
  //
  // // test length over speed
  // selection.clear();
  // selection.add(length1);
  // selection.add(speedGood1);
  // commands =
  // new DivideQuantityOperation().actionsFor(selection, store, context);
  // assertEquals("valid input", 2, commands.size());
  //
  // ICommand<IStoreItem> command = commands.iterator().next();
  // command.execute();
  //
  // IStoreItem output = command.getOutputs().iterator().next();
  //
  // IQuantityCollection<Quantity> iQ = (IQuantityCollection<Quantity>) output;
  //
  // assertEquals("correct units", "[T]", iQ.getUnits().getDimension()
  // .toString());
  //
  // store.clear();
  // command.execute();
  // assertEquals(1, store.size());
  // IQuantityCollection<Duration> duration =
  // (IQuantityCollection<Duration>) store.iterator().next();
  // assertEquals(speedGood1.getValuesCount(), duration.getValuesCount());
  //
  // double firstDuration =
  // duration.getValues().get(0).doubleValue(duration.getUnits());
  // double firstLength =
  // length1.getValues().get(0).doubleValue(length1.getUnits());
  // double firstSpeed =
  // speedGood1.getValues().get(0).doubleValue(speedGood1.getUnits());
  //
  // assertEquals(firstLength / firstSpeed, firstDuration,0);
  //
  // // test length over factor
  // selection.clear();
  // selection.add(length1);
  // selection.add(factor);
  // commands =
  // new DivideQuantityOperation().actionsFor(selection, store, context);
  // assertEquals("valid input", 2, commands.size());
  //
  // Iterator<ICommand<IStoreItem>> iterator = commands.iterator();
  // command = iterator.next();
  //
  // store.clear();
  // command.execute();
  //
  // assertEquals(1, store.size());
  // IQuantityCollection<Length> resultLength =
  // (IQuantityCollection<Length>) store.iterator().next();
  // assertEquals(length1.getValuesCount(), resultLength.getValuesCount());
  //
  // double firstResultLength =
  // resultLength.getValues().get(0).doubleValue(resultLength.getUnits());
  // double factorValue =
  // factor.getValues().get(0).doubleValue(factor.getUnits());
  // assertEquals(firstLength / factorValue, firstResultLength,0);
  //
  // // test command #2: factor over length
  // command = iterator.next();
  // store.clear();
  // command.execute();
  // IQuantityCollection<Quantity> resultQuantity =
  // (IQuantityCollection<Quantity>) store.iterator().next();
  // // assert expected unit (1/m)
  // assertEquals("1/" + length1.getUnits().toString(), resultQuantity
  // .getUnits().toString());
  // assertEquals(length1.getValuesCount(), resultQuantity.getValuesCount());
  //
  // double firstResultQuantity =
  // resultQuantity.getValues().get(0)
  // .doubleValue(resultQuantity.getUnits());
  // assertEquals(factorValue / firstLength, firstResultQuantity,0);
  // }

  @Test
  public void testGenerateDummyDataOperation()
  {
    StoreGroup store = new SampleData().getData(10);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    Collection<ICommand> commands =
        new GenerateDummyDataOperation("Generate Dummy Data Test", 10)
            .actionsFor(selection, store, context);
    assertEquals("Valid number of commands", 1, commands.size());

    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    selection = new ArrayList<>();

    for (ICommand iCommand : commands)
    {
      iCommand.execute();
      iCommand.dataChanged(speedGood1);
    }
  }

  @Test
  public void testDeleteCollectionOperation()
  {
    StoreGroup store = new SampleData().getData(10);
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    IDocument<?> speedGood1 = (IDocument<?>) store.get(SampleData.SPEED_ONE);
    IDocument<?> string1 = (IDocument<?>) store.get(SampleData.TIME_STAMPS_1);
    IDocument<?> len1 = (IDocument<?>) store.get(SampleData.LENGTH_ONE);

    selection.add(speedGood1);
    selection.add(string1);
    selection.add(len1);

    int storeLen = store.size();
    IStoreGroup speedParent = speedGood1.getParent();
    int factorLen = speedParent.size();

    DeleteCollectionOperation deleteCollectionOperation =
        new DeleteCollectionOperation();
    Collection<ICommand> commands =
        deleteCollectionOperation.actionsFor(selection, store, context);
    assertEquals("Delete collection operation", 1, commands.size());
    ICommand command = commands.iterator().next();
    command.execute();

    // how many do we expect to lose? This will be the number of documents deleted,
    // plus any output documents that depend on them
    int expectedToDelete = 4;

    assertEquals("store smaller", storeLen - expectedToDelete, store.size());
    assertEquals("speeds smaller", factorLen - 1, speedParent.size());
  }

  @Test
  public void testBearingBetweenTracks2D()
  {
    StoreGroup store = new SampleData().getData(10);
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    LocationDocumentBuilder lda =
        new LocationDocumentBuilder("one", null, SampleData.MILLIS, SI.METER);
    LocationDocumentBuilder ldb =
        new LocationDocumentBuilder("one", null, SampleData.MILLIS, SI.METER);

    IGeoCalculator calc = lda.getCalculator();
    lda.add(1000, calc.createPoint(1, 1));
    lda.add(2000, calc.createPoint(1, 1));

    ldb.add(1000, calc.createPoint(2, 2));
    ldb.add(2000, calc.createPoint(0, 0));

    selection.add(lda.toDocument());
    selection.add(ldb.toDocument());

    List<ICommand> commands =
        new BearingBetweenTracksOperation().actionsFor(selection, store,
            context);
    ICommand oper = commands.get(0);
    oper.execute();
    NumberDocument output = (NumberDocument) oper.getOutputs().get(0);
    DoubleDataset ds = (DoubleDataset) output.getDataset();
    assertEquals("correct ang", 45d, ds.get(0), 0.0001);
    assertEquals("correct ang", 225d, ds.get(1), 0.0001);
  }

  @Test
  public void testBearingBetweenTracksOperation() throws IOException
  {
    StoreGroup store = new SampleData().getData(10);
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    File file = TestCsvParser.getDataFile("americas_cup/usa.csv");
    assertTrue(file.isFile());
    File file2 = TestCsvParser.getDataFile("americas_cup/nzl.csv");
    assertTrue(file2.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 3, group.size());
    Document<?> firstColl = (Document<?>) group.get(2);
    assertEquals("correct num rows", 1708, firstColl.size());

    List<IStoreItem> items2 = parser.parse(file2.getAbsolutePath());
    assertEquals("correct group", 1, items2.size());
    StoreGroup group2 = (StoreGroup) items2.get(0);
    assertEquals("correct num collections", 3, group2.size());
    Document<?> secondColl = (Document<?>) group2.get(2);
    assertEquals("correct num rows", 1708, secondColl.size());

    LocationDocument track1 = (LocationDocument) firstColl;
    LocationDocument track2 = (LocationDocument) secondColl;
    selection.add(track1);
    selection.add(track2);

    List<ICommand> commands =
        new BearingBetweenTracksOperation().actionsFor(selection, store,
            context);
    assertEquals("Bearing Between Tracks operation", 1, commands.size());
    Iterator<ICommand> iterator = commands.iterator();
    ICommand command = iterator.next();
    command.execute();

    boolean numeric = CsvParser.isNumeric("123");
    assertTrue(numeric);
    numeric = CsvParser.isNumeric("NAN");
    assertFalse(numeric);
  }

  @Test
  public void testCreateSingletonGeneratorInvalid()
  {
    StoreGroup store = new SampleData().getData(10);
    CreateSingletonGenerator generator =
        new CreateSingletonGenerator("dimensionless", Dimensionless.UNIT);
    assertNotNull("Create Single Generator is not NULL", generator);
  
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    StoreGroup storeGroup = new StoreGroup("Track 1");
    selection.add(storeGroup);
  
    IContext mockContext = EasyMock.createMock(MockContext.class);
  
    Collection<ICommand> singleGeneratorActionFor =
        generator.actionsFor(selection, store, mockContext);
    assertEquals("Create location collection size", 1, singleGeneratorActionFor
        .size());
    ICommand singleGenCommand = singleGeneratorActionFor.iterator().next();
  
    final String outName = "new_name";
    EasyMock.expect(
        mockContext.getInput("New variable", "Enter name for variable", "new dimensionless"))
        .andReturn(outName).times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter initial value for variable", "100")).andReturn("1234.56")
        .times(1);
    EasyMock.expect(
        mockContext.getInput("New variable",
            "Enter the range for variable (or cancel to leave un-ranged)", "0:100")).andReturn("")
        .times(1);
    EasyMock.replay(mockContext);
  
    singleGenCommand.execute();
  
    NumberDocument output = (NumberDocument) singleGenCommand.getOutputs().get(0);
    assertNotNull(output);    
    assertEquals("correct name", outName, output.getName());
    assertEquals("correct size", 1, output.size());
    assertEquals("correct value", 1234.56, output.getValueAt(0), 0.01);
    assertEquals("correct range", null, output.getRange());
  }

}
