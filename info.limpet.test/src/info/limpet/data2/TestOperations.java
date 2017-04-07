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

import static javax.measure.unit.NonSI.KILOMETRES_PER_HOUR;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import static javax.measure.unit.SI.SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet2.Document;
import info.limpet2.ICommand;
import info.limpet2.IContext;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.MockContext;
import info.limpet2.NumberDocument;
import info.limpet2.NumberDocumentBuilder;
import info.limpet2.SampleData;
import info.limpet2.StoreGroup;
import info.limpet2.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet2.operations.arithmetic.simple.MultiplyQuantityOperation;
import info.limpet2.operations.arithmetic.simple.SubtractQuantityOperation;
import info.limpet2.operations.arithmetic.simple.UnitConversionOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Velocity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestOperations
{
  private IContext context = new MockContext();
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testInterpolateTests()
  {
    // place to store results data
    StoreGroup store = new SampleData().getData(10);

    // ok, let's try one that works
    List<Document> selection = new ArrayList<Document>();

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

    assertEquals("correct number of actions returned", 2, actions.size());

    ICommand addAction = actions.iterator().next();

    assertNotNull("found action", addAction);

  }

  // @Test
  // public void testTrig()
  // {
  // // prepare some data
  // SpeedKts speedData = new StockTypes.Temporal.SpeedKts("speed", null);
  // speedData.add(100, 23);
  // speedData.add(200, 44);
  //
  // AngleDegrees angleData =
  // new StockTypes.NonTemporal.AngleDegrees("degs", null);
  // angleData.add(200d);
  // angleData.add(123d);
  //
  // StockTypes.Temporal.AngleDegrees temporalAngleData =
  // new StockTypes.Temporal.AngleDegrees("degs", null);
  // temporalAngleData.add(1000, 200d);
  // temporalAngleData.add(3000, 123d);
  // temporalAngleData.add(4000, 13d);
  //
  // List<ICollection> selection = new ArrayList<ICollection>();
  // StoreGroup store = new StoreGroup();
  //
  // HashMap<String, List<IOperation<?>>> ops =
  // OperationsLibrary.getOperations();
  // List<IOperation<?>> arith = ops.get(OperationsLibrary.ARITHMETIC);
  // // ok, now find the trig op
  // Iterator<IOperation<?>> iter = arith.iterator();
  // IOperation<ICollection> sinOp = null;
  // IOperation<ICollection> cosOp = null;
  // while (iter.hasNext())
  // {
  // IOperation<?> thisO = (IOperation<?>) iter.next();
  // if (thisO instanceof UnitaryMathOperation)
  // {
  // UnitaryMathOperation umo = (UnitaryMathOperation) thisO;
  // if (umo.getName().equals("Sin"))
  // {
  // sinOp = umo;
  // }
  // if (umo.getName().equals("Cos"))
  // {
  // cosOp = umo;
  // }
  // }
  // }
  //
  // assertNotNull("check we found it", sinOp);
  //
  // // ok, try it with empty data
  // Collection<ICommand<ICollection>> validOps =
  // sinOp.actionsFor(selection, null, null);
  // assertEquals("null for empty selection", 0, validOps.size());
  //
  // // add some speed data
  // selection.add(speedData);
  // // ok, try it with empty data
  // validOps = sinOp.actionsFor(selection, store, null);
  // assertEquals("empty for invalid selection", 0, validOps.size());
  //
  // // add some valid data
  // selection.add(angleData);
  //
  // // ok, try it with empty data
  // validOps = sinOp.actionsFor(selection, store, null);
  // assertEquals("empty for invalid selection", 0, validOps.size());
  //
  // // ok, try it with empty data
  // validOps = cosOp.actionsFor(selection, store, null);
  // assertEquals(" cos also empty for invalid selection", 0, validOps.size());
  //
  // // and remove the speed data
  // selection.remove(speedData);
  //
  // // ok, try it with empty data
  // validOps = sinOp.actionsFor(selection, store, context);
  // assertEquals("non-empty for valid selection", 1, validOps.size());
  //
  // ICommand<ICollection> theOp = validOps.iterator().next();
  // theOp.execute();
  //
  // assertEquals("has new dataset", 1, store.size());
  // ICollection output = theOp.getOutputs().iterator().next();
  //
  // // check the size
  // assertEquals("correct size", 2, output.getValuesCount());
  //
  // // check data type
  // assertTrue("isn't temporal", !output.isTemporal());
  // assertTrue("is quantity", output.isQuantity());
  //
  // // ok, try it temporal data
  // selection.remove(angleData);
  // selection.add(temporalAngleData);
  //
  // validOps = sinOp.actionsFor(selection, store, context);
  // assertEquals("non-empty for valid selection", 1, validOps.size());
  //
  // theOp = validOps.iterator().next();
  // theOp.execute();
  //
  // assertEquals("has new dataset", 2, store.size());
  // output = theOp.getOutputs().iterator().next();
  //
  // // check the size
  // assertEquals("correct size", 3, output.getValuesCount());
  //
  // // check data type
  // assertTrue("isn't temporal", output.isTemporal());
  //
  // }
  // @Test
  // public void testAppliesTo()
  // {
  // // the units for this measurement
  // Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
  // Unit<Velocity> kmm = KILO(METRE).divide(MINUTE).asType(Velocity.class);
  // Unit<Length> m = METRE.asType(Length.class);
  //
  // // the target collection
  // QuantityCollection<Velocity> speedGood1 =
  // new QuantityCollection<Velocity>("Speed 1", null, kmh);
  // QuantityCollection<Velocity> speedGood2 =
  // new QuantityCollection<Velocity>("Speed 2", null, kmh);
  // QuantityCollection<Velocity> speedLonger =
  // new QuantityCollection<Velocity>("Speed 3", null, kmh);
  // QuantityCollection<Velocity> speedDiffUnits =
  // new QuantityCollection<Velocity>("Speed 4", null, kmm);
  // QuantityCollection<Length> len1 =
  // new QuantityCollection<Length>("Length 1", null, m);
  // TemporalQuantityCollection<Velocity> temporalSpeed1 =
  // new TemporalQuantityCollection<Velocity>("Speed 5", null, kmh);
  // TemporalQuantityCollection<Velocity> temporalSpeed2 =
  // new TemporalQuantityCollection<Velocity>("Speed 6", null, kmh);
  // ObjectCollection<String> string1 = new ObjectCollection<>("strings 1");
  // ObjectCollection<String> string2 = new ObjectCollection<>("strings 2");
  //
  // for (int i = 1; i <= 10; i++)
  // {
  // // create a measurement
  // double thisSpeed = i * 2;
  // Measurable<Velocity> speedVal1 = Measure.valueOf(thisSpeed, kmh);
  // Measurable<Velocity> speedVal2 = Measure.valueOf(thisSpeed * 2, kmh);
  // Measurable<Velocity> speedVal3 = Measure.valueOf(thisSpeed / 2, kmh);
  // Measurable<Velocity> speedVal4 = Measure.valueOf(thisSpeed / 2, kmm);
  // Measurable<Length> lenVal1 = Measure.valueOf(thisSpeed / 2, m);
  //
  // // store the measurements
  // speedGood1.add(speedVal1);
  // speedGood2.add(speedVal2);
  // speedLonger.add(speedVal3);
  // speedDiffUnits.add(speedVal4);
  // temporalSpeed1.add(i, speedVal2);
  // temporalSpeed2.add(i, speedVal3);
  // len1.add(lenVal1);
  //
  // string1.add(i + " ");
  // string2.add(i + "a ");
  // }
  //
  // Measurable<Velocity> max = temporalSpeed1.max();
  // System.out.println(max);
  //
  // Measurable<Velocity> min = temporalSpeed1.min();
  // System.out.println(min);
  //
  // QuantityRange<Velocity> range = temporalSpeed1.getRange();
  // System.out.println(range);
  //
  //
  // Measurable<Velocity> speedVal3a = Measure.valueOf(2, kmh);
  // speedLonger.add(speedVal3a);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  // CollectionComplianceTests testOp = new CollectionComplianceTests();
  //
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(speedGood2);
  //
  // assertTrue("all same dim", testOp.allEqualDimensions(selection));
  // assertTrue("all same units", testOp.allEqualUnits(selection));
  // assertTrue("all same length", testOp.allEqualLength(selection));
  // assertTrue("all quantities", testOp.allQuantity(selection));
  // assertFalse("all temporal", testOp.allTemporal(selection));
  // assertFalse("all groups",testOp.allGroups(selection));
  //
  // assertFalse("all Temporal or singleton",testOp.allTemporalOrSingleton(selection));
  //
  // assertTrue("Longest collection lenght", testOp.getLongestCollectionLength(selection) > 0);
  //
  // StoreGroup track1 = new StoreGroup("Track 1");
  // selection.add(track1);
  //
  // assertFalse("all childrens are tracks",testOp.allChildrenAreTracks(selection));
  //
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(speedGood2);
  // selection.add(speedDiffUnits);
  //
  // assertTrue("all same dim", testOp.allEqualDimensions(selection));
  // assertFalse("all same units", testOp.allEqualUnits(selection));
  // assertTrue("all same length", testOp.allEqualLength(selection));
  // assertTrue("all quantities", testOp.allQuantity(selection));
  // assertFalse("all temporal", testOp.allTemporal(selection));
  //
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(speedGood2);
  // selection.add(len1);
  //
  // assertFalse("all same dim", testOp.allEqualDimensions(selection));
  // assertFalse("all same units", testOp.allEqualUnits(selection));
  // assertTrue("all same length", testOp.allEqualLength(selection));
  // assertTrue("all quantities", testOp.allQuantity(selection));
  // assertFalse("all temporal", testOp.allTemporal(selection));
  //
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(speedGood2);
  // selection.add(speedLonger);
  //
  // assertTrue("all same dim", testOp.allEqualDimensions(selection));
  // assertTrue("all same units", testOp.allEqualUnits(selection));
  // assertFalse("all same length", testOp.allEqualLength(selection));
  // assertTrue("all quantities", testOp.allQuantity(selection));
  // assertFalse("all temporal", testOp.allTemporal(selection));
  //
  // selection.clear();
  // selection.add(temporalSpeed1);
  // selection.add(temporalSpeed2);
  //
  // assertTrue("all same dim", testOp.allEqualDimensions(selection));
  // assertTrue("all same units", testOp.allEqualUnits(selection));
  // assertTrue("all same length", testOp.allEqualLength(selection));
  // assertTrue("all quantities", testOp.allQuantity(selection));
  // assertTrue("all temporal", testOp.allTemporal(selection));
  //
  // selection.clear();
  // selection.add(temporalSpeed1);
  // selection.add(string1);
  //
  // assertFalse("all same dim", testOp.allEqualDimensions(selection));
  // assertFalse("all same units", testOp.allEqualUnits(selection));
  // assertTrue("all same length", testOp.allEqualLength(selection));
  // assertFalse("all quantities", testOp.allQuantity(selection));
  // assertFalse("all temporal", testOp.allTemporal(selection));
  //
  // selection.clear();
  // selection.add(string1);
  // selection.add(string1);
  //
  // assertFalse("all same dim", testOp.allEqualDimensions(selection));
  // assertFalse("all same units", testOp.allEqualUnits(selection));
  // assertTrue("all same length", testOp.allEqualLength(selection));
  // assertTrue("all non quantities", testOp.allNonQuantity(selection));
  // assertFalse("all temporal", testOp.allTemporal(selection));
  //
  // // ok, let's try one that works
  // selection.clear();
  // selection.add(speedGood1);
  // selection.add(speedGood2);
  //
  // StoreGroup store = new StoreGroup();
  // assertEquals("store empty", 0, store.size());
  //
  // @SuppressWarnings(
  // {"unchecked", "rawtypes"})
  // Collection<ICommand<ICollection>> actions =
  // new AddQuantityOperation().actionsFor(selection, store, context);
  //
  // assertEquals("correct number of actions returned", 1, actions.size());
  //
  // ICommand<?> addAction = actions.iterator().next();
  // addAction.execute();
  //
  // assertEquals("new collection added to store", 1, store.size());
  //
  // ICollection firstItem = (ICollection) store.iterator().next();
  // ICommand<?> precedent = firstItem.getPrecedent();
  // assertNotNull("has precedent", precedent);
  // assertEquals("Correct name",
  // "Add numeric values in provided series (indexed)", precedent.getName());
  //
  // List<? extends IStoreItem> inputs = precedent.getInputs();
  // assertEquals("Has both precedents", 2, inputs.size());
  //
  // Iterator<? extends IStoreItem> iIter = inputs.iterator();
  // while (iIter.hasNext())
  // {
  // ICollection thisC = (ICollection) iIter.next();
  // List<ICommand<?>> deps = thisC.getDependents();
  // assertEquals("has a depedent", 1, deps.size());
  // Iterator<ICommand<?>> dIter = deps.iterator();
  // while (dIter.hasNext())
  // {
  // ICommand<?> iCommand = dIter.next();
  // assertEquals("Correct dependent", precedent, iCommand);
  // }
  // }
  //
  // List<? extends IStoreItem> outputs = precedent.getOutputs();
  // assertEquals("Has both dependents", 1, outputs.size());
  //
  // Iterator<? extends IStoreItem> oIter = outputs.iterator();
  // while (oIter.hasNext())
  // {
  // ICollection thisC = (ICollection) oIter.next();
  // ICommand<?> dep = thisC.getPrecedent();
  // assertNotNull("has a depedent", dep);
  // assertEquals("Correct dependent", precedent, dep);
  // }
  // }

  @Test
  public void testDimensionlessMultiply()
  {
    // place to store results data
    StoreGroup store = new SampleData().getData(30);

    // ok, let's try one that works
    List<Document> selection = new ArrayList<Document>();

    // ///////////////
    // TEST INVALID PERMUTATIONS
    // ///////////////
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument speedGood2 =
        (NumberDocument) store.get(SampleData.SPEED_TWO);
    NumberDocument speedIrregular =
        (NumberDocument) store.get(SampleData.SPEED_IRREGULAR2);
    Document string1 = (Document) store.get(SampleData.STRING_ONE);
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
    assertEquals("valid collections - both quantities", 2, commands.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    store.clear();
    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - both speeds", 2, commands.size());

    // //////////////////////////
    // now test valid collections
    // /////////////////////////

    selection.clear();
    selection.add(speedGood1);
    selection.add(factor);

    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - one is singleton", 2, commands.size());

    ICommand command = commands.iterator().next();

    // test actions has single item: "Multiply series by constant"
    assertEquals("correct name",
        "Multiply numeric values in provided series (indexed)", command
            .getName());

    // apply action
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", 1, store.size());

    NumberDocument newS =
        (NumberDocument) store
            .get("Product of Speed One Time + Floating point factor");

    // test results is same length as thisSpeed
    assertEquals("correct size", 30, newS.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(factor);
    store.clear();
    assertEquals("store empty", 0, store.size());
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid collections - one is singleton", 2, commands.size());

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

    List<Document> selection = new ArrayList<Document>();
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

    double firstInputSpeed = inputSpeed.getValue(0);
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
        (NumberDocument) store.get("Sum of Speed One Time + Speed Two Time");

    assertNotNull(newS);
    assertEquals("correct size", 10, newS.size());

    // assert same unit
    assertEquals(newS.getUnits(), speedGood1.getUnits());

    double firstDifference = newS.getValue(0);
    double speed1firstValue = speedGood1.getValue(0);
    double speed2firstValue = speedGood2.getValue(0);

    assertEquals(firstDifference, speed1firstValue + speed2firstValue, 0);

    // test that original series have dependents
    assertEquals("first series has dependents", 2, speedGood1.getDependents()
        .size());
    assertEquals("second series has dependents", 1, speedGood2.getDependents()
        .size());

    // test that new series has predecessors
    assertNotNull("new series has precedent", newS.getPrecedent());
    assertEquals("Have correct precedent",
        "Add numeric values in provided series (indexed)", newS.getPrecedent()
            .getName());

  }

  @Test
  public void testSubtractionSingleton()
  {
    StoreGroup store = new SampleData().getData(10);
    List<Document> selection = new ArrayList<Document>();

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocumentBuilder speedSingleb =
        new NumberDocumentBuilder("singleton", null, null);

    speedSingleb.add(2d);
    NumberDocument speedSingle = speedSingleb.toDocument();

    // TODO: subtract should offer operations that go both ways.

    selection.add(speedGood1);
    selection.add(speedSingle);
    Collection<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);

    // TODO: reinstate these tests once two-way subtract done
    // assertEquals("got two commands", 4, commands.size());
    //
    // // have a look
    // ICommand first = commands.iterator().next();
    // first.execute();
    // NumberDocument output =
    // (NumberDocument) first.getOutputs().iterator().next();
    // assertNotNull("produced output", output);
    // assertEquals("correct size", speedGood1.size(), output.size());
    //
    // assertEquals("correct value", 2.3767, speedGood1.getValue(0) * 2, 0.001);
  }

  @Test
  public void testAddSingleton()
  {
    StoreGroup store = new SampleData().getData(10);
    List<Document> selection = new ArrayList<Document>();

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocumentBuilder speedSingleb =
        new NumberDocumentBuilder("singleton", METRE.divide(SECOND).asType(
            Velocity.class), null);

    speedSingleb.add(2d);

    NumberDocument speedSingle = speedSingleb.toDocument();

    selection.add(speedGood1);
    selection.add(speedSingle);
    Collection<ICommand> commands =
        new AddQuantityOperation().actionsFor(selection, store, context);
    assertEquals("got two commands", 2, commands.size());

    // have a look
    Iterator<ICommand> iter = commands.iterator();
    iter.next();
    ICommand first = iter.next();
    first.execute();
    NumberDocument output =
        (NumberDocument) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertTrue("output is temporal", output.isIndexed());
    assertEquals("correct size", speedGood1.size(), output.size());

    assertEquals("correct value", output.getValue(0),
        speedGood1.getValue(0) + 2, 0.001);
  }

  @Test
  public void testSubtraction()
  {
    StoreGroup store = new SampleData().getData(10);
    int storeSize = store.size();
    List<Document> selection = new ArrayList<>();

    // test invalid dimensions
    NumberDocument speedGood1 =
        (NumberDocument) store.get(SampleData.SPEED_ONE);
    NumberDocument angle1 = (NumberDocument) store.get(SampleData.ANGLE_ONE);
    selection.add(speedGood1);
    selection.add(angle1);
    Collection<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not same dimensions", 0, commands
        .size());

    selection.clear();

    // test not all quantities
    Document string1 = (Document) store.get(SampleData.STRING_ONE);
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

    // TODO: reinstate this test once subtract provides both-ways commands
    // commands =
    // new SubtractQuantityOperation().actionsFor(selection, store, context);
    // assertEquals("valid command", 4, commands.size());
    //
    // ICommand command = commands.iterator().next();
    // command.execute();
    //
    // // test store has a new item in it
    // assertEquals("store not empty", storeSize + 1, store.size());
    //
    // NumberDocument newS =
    // (NumberDocument) store.get(speedGood2.getName() + " from "
    // + speedGood1.getName());
    //
    // assertNotNull(newS);
    // assertEquals("correct size", 10, newS.size());
    //
    // // assert same unit
    // assertEquals(newS.getUnits(), speedGood1.getUnits());
    //
    // double firstDifference = newS.getValue(0);
    // double speed1firstValue = speedGood1.getValue(0);
    // double speed2firstValue = speedGood2.getValue(0);
    //
    // assertEquals(firstDifference, speed1firstValue - speed2firstValue, 0);
    // context.logError(IContext.Status.ERROR, "Error", null);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAddLayerOperation() throws RuntimeException
  {
    IContext context = new MockContext();
    // place to store results data
    StoreGroup store = new SampleData().getData(10);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    StoreGroup track1 = new StoreGroup("Track 1");
    selection.add(track1);

    Collection<ICommand> commands =
        new AddLayerOperation().actionsFor(selection, store, context);
    assertEquals("Valid number of commands", 1, commands.size());
    commands.contains(track1);
    Iterator<ICommand<IStoreItem>> iterator = commands.iterator();
    ICommand<IStoreItem> firstItem = iterator.next();

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
      Assert.assertEquals(true,
          throwable instanceof UnsupportedOperationException);
    }
    try
    {
      firstItem.redo();
    }
    catch (Throwable throwable)
    {
      Assert.assertEquals(true,
          throwable instanceof UnsupportedOperationException);
    }
    assertEquals("CanUndo operation", false, firstItem.canRedo());
    assertEquals("CanRedo operation", false, firstItem.canUndo());

    boolean hasChildren = firstItem.hasChildren();
    assertEquals("Parent have children", true, hasChildren);

    firstItem.execute();

    IQuantityCollection<Velocity> speedGood1 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    selection = new ArrayList<>();
    selection.add(speedGood1);

    commands = new AddLayerOperation().actionsFor(selection, store, context);
    assertEquals("invalid number of inputs", 1, commands.size());
    for (ICommand<IStoreItem> iCommand : commands)
    {
      iCommand.execute();
      iCommand.dataChanged(speedGood1);
    }

  }
  //
  // @Test
  // public void testCreateSingletonGenerator(){
  // StoreGroup store = new SampleData().getData(10);
  // CreateSingletonGenerator generator= new CreateSingletonGenerator("dimensionless") {
  // @Override
  // protected QuantityCollection<?> generate(String name, ICommand<?> precedent) {
  // return new StockTypes.NonTemporal.DimensionlessDouble(name, precedent);
  // }
  // };
  // assertNotNull("Create Single Generator is not NULL", generator);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  // StoreGroup storeGroup = new StoreGroup("Track 1");
  // selection.add(storeGroup);
  //
  // IContext mockContext=EasyMock.createMock(MockContext.class);
  //
  // Collection<ICommand<IStoreItem>> singleGeneratorActionFor = generator.actionsFor(selection,
  // store, mockContext);
  // assertEquals("Create location collection size", 1,singleGeneratorActionFor.size());
  // ICommand<IStoreItem> singleGenCommand = singleGeneratorActionFor.iterator().next();
  //
  // EasyMock.expect(mockContext.getInput("New variable", "Enter name for variable",
  // "")).andReturn("new dimensionless").times(1);
  // EasyMock.expect(mockContext.getInput("New variable", "Enter initial value for variable",
  // "")).andReturn("1234.56").times(1);
  // EasyMock.replay(mockContext);
  //
  // singleGenCommand.execute();
  //
  // }
  //
  // @Test
  // public void testCreateLocationAction(){
  // StoreGroup store = new SampleData().getData(10);
  // CreateLocationAction createLocationAction= new CreateLocationAction();
  // assertNotNull("Create Location action is not NULL", createLocationAction);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  // StoreGroup storeGroup = new StoreGroup("Track 1");
  // selection.add(storeGroup);
  //
  // IContext mockContext=EasyMock.createMock(MockContext.class);
  //
  // Collection<ICommand<IStoreItem>> actionsFor = createLocationAction.actionsFor(selection, store,
  // mockContext);
  // assertEquals("Create location collection size", 1,actionsFor.size());
  // Iterator<ICommand<IStoreItem>> creationLocIterator = actionsFor.iterator();
  // ICommand<IStoreItem> command= creationLocIterator.next();
  //
  // EasyMock.expect(mockContext.getInput("New fixed location", "Enter name for location",
  // "")).andReturn("seriesName").times(1);
  // EasyMock.expect(mockContext.getInput("New location","Enter initial value for latitude",
  // "")).andReturn("123.23").times(1);
  // EasyMock.expect(mockContext.getInput("New location","Enter initial value for longitude",
  // "")).andReturn("3456.78").times(1);
  // EasyMock.replay(mockContext);
  //
  // command.execute();
  // }
  //
  // @Test
  // public void testExportCsvToFileAction(){
  // StoreGroup store = new SampleData().getData(10);
  // ExportCsvToFileAction exportCSVFileAction=new ExportCsvToFileAction();
  // assertNotNull(exportCSVFileAction);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  // @SuppressWarnings("unchecked")
  // IQuantityCollection<Velocity> speedGood1 = (IQuantityCollection<Velocity>)
  // store.get(SampleData.SPEED_ONE);
  // selection.add(speedGood1);
  //
  // IContext mockContext=EasyMock.createMock(MockContext.class);
  //
  // Collection<ICommand<IStoreItem>> exportActionfor = exportCSVFileAction.actionsFor(selection,
  // store, mockContext);
  // assertEquals("Export CSV file collection size", 1,exportActionfor.size());
  // Iterator<ICommand<IStoreItem>> iterator=exportActionfor.iterator();
  // ICommand<IStoreItem> command = iterator.next();
  //
  // EasyMock.expect(mockContext.getCsvFilename()).andReturn("ExportCSV.csv").times(1);
  // EasyMock.expect(mockContext.openQuestion("Overwrite '" + "ExportCSV.csv" + "'?",
  // "Are you sure you want to overwrite '" + "ExportCSV.csv" + "'?")).andReturn(true).times(1);
  // EasyMock.replay(mockContext);
  //
  // command.execute();
  // }
  //
  // @Test
  // public void testCopyCsvToClipboardAction(){
  //
  // StoreGroup store = new SampleData().getData(10);
  // CopyCsvToClipboardAction copyCSVToClipAction=new CopyCsvToClipboardAction();
  // assertNotNull(copyCSVToClipAction);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  // @SuppressWarnings("unchecked")
  // IQuantityCollection<Velocity> speedGood1 = (IQuantityCollection<Velocity>)
  // store.get(SampleData.SPEED_ONE);
  // selection.add(speedGood1);
  //
  // IContext mockContext=EasyMock.createMock(MockContext.class);
  // Collection<ICommand<IStoreItem>> copyCSVActionfor = copyCSVToClipAction.actionsFor(selection,
  // store, mockContext);
  // assertEquals("Copy CSV file collection size", 1,copyCSVActionfor.size());
  //
  // Iterator<ICommand<IStoreItem>> copyrIterator=copyCSVActionfor.iterator();
  // ICommand<IStoreItem> copyCommand = copyrIterator.next();
  // copyCommand.execute();
  // }
  //
  // @Test
  // public void testUnitaryMathOperation(){
  // UnitaryMathOperation clearUnit=new UnitaryMathOperation("Clear units"){
  // public double calcFor(double val){
  // return val;
  // }
  //
  // protected Unit<?> getUnits(IQuantityCollection<?> input){
  // return Dimensionless.UNIT;
  // }
  // };
  // assertNotNull(clearUnit);
  // double calcFor = clearUnit.calcFor(123.45);
  // assertTrue("Calc for",123.45==calcFor);
  // }
  //
  // @Test
  // public void testOperations(){
  // // place to store results data
  // HashMap<String, List<IOperation<?>>> ops = OperationsLibrary.getOperations();
  //
  // List<IOperation<?>> create = ops.get(OperationsLibrary.CREATE);
  // assertEquals("Creation size",7, create.size());
  // //Administrator Operations.
  //
  // List<IOperation<?>> adminOperations = ops.get(OperationsLibrary.ADMINISTRATION);
  // assertEquals("Creation size",6, adminOperations.size());
  //
  // List<IOperation<?>> topLevel = OperationsLibrary.getTopLevel();
  // assertNotNull(topLevel);
  // }
  //
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
  //
  // @Test
  // @SuppressWarnings("unchecked")
  // public void testGenerateDummyDataOperation()
  // {
  // StoreGroup store = new SampleData().getData(10);
  //
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  //
  // Collection<ICommand<IStoreItem>> commands =
  // new GenerateDummyDataOperation("Generate Dummy Data Test",1).actionsFor(selection, store,
  // context);
  // assertEquals("Valid number of commands", 1, commands.size());
  //
  // IQuantityCollection<Velocity> speedGood1 =
  // (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
  // selection = new ArrayList<>();
  //
  // for (ICommand<IStoreItem> iCommand : commands)
  // {
  // iCommand.execute();
  // iCommand.dataChanged(speedGood1);
  // }
  // }
  //
  // @Test
  // public void testDeleteCollectionOperation(){
  // StoreGroup store = new SampleData().getData(10);
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  //
  // ICollection speedGood1 = (ICollection) store.get(SampleData.SPEED_ONE);
  // ICollection string1 = (ICollection) store.get(SampleData.STRING_ONE);
  // selection.add(speedGood1);
  // selection.add(string1);
  //
  // DeleteCollectionOperation deleteCollectionOperation=new DeleteCollectionOperation();
  // Collection<ICommand<IStoreItem>> commands =
  // deleteCollectionOperation.actionsFor(selection, store, context);
  // assertEquals("Delete collection operation", 1, commands.size());
  // ICommand<IStoreItem> command = commands.iterator().next();
  // command.execute();
  // }
  //
  // @Test
  // public void testBearingBetweenTracksOperation() throws IOException{
  // StoreGroup store = new SampleData().getData(10);
  // List<IStoreItem> selection = new ArrayList<IStoreItem>();
  //
  // File file = TestCsvParser.getDataFile("americas_cup/usa.csv");
  // assertTrue(file.isFile());
  // File file2 = TestCsvParser.getDataFile("americas_cup/nzl.csv");
  // assertTrue(file2.isFile());
  // CsvParser parser = new CsvParser();
  // List<IStoreItem> items = parser.parse(file.getAbsolutePath());
  // assertEquals("correct group", 1, items.size());
  // StoreGroup group = (StoreGroup) items.get(0);
  // assertEquals("correct num collections", 3, group.size());
  // ICollection firstColl = (ICollection) group.get(2);
  // assertEquals("correct num rows", 1708, firstColl.getValuesCount());
  //
  // List<IStoreItem> items2 = parser.parse(file2.getAbsolutePath());
  // assertEquals("correct group", 1, items2.size());
  // StoreGroup group2 = (StoreGroup) items2.get(0);
  // assertEquals("correct num collections", 3, group2.size());
  // ICollection secondColl = (ICollection) group2.get(2);
  // assertEquals("correct num rows", 1708, secondColl.getValuesCount());
  //
  // TemporalLocation track1 = (TemporalLocation) firstColl;
  // TemporalLocation track2 = (TemporalLocation) secondColl;
  // selection.add(track1);
  // selection.add(track2);
  //
  // Collection<ICommand<IStoreItem>> commands =
  // new BearingBetweenTracksOperation().actionsFor(selection, store, context);
  // assertEquals("Bearing Between Tracks operation", 2, commands.size());
  // Iterator<ICommand<IStoreItem>> iterator = commands.iterator();
  // ICommand<IStoreItem> command = iterator.next();
  // command.execute();
  //
  // command = iterator.next();
  // command.execute();
  //
  // boolean numeric = CsvParser.isNumeric("123");
  // assertTrue(numeric);
  // numeric = CsvParser.isNumeric("NAN");
  // assertTrue(!numeric);
  // }

}
