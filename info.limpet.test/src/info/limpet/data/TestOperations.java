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
package info.limpet.data;

import static javax.measure.unit.NonSI.HOUR;
import static javax.measure.unit.NonSI.KILOMETERS_PER_HOUR;
import static javax.measure.unit.NonSI.KILOMETRES_PER_HOUR;
import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.SI.KILO;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.MockContext;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.samples.SampleData;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.AngleDegrees;
import info.limpet.data.impl.samples.StockTypes.Temporal.SpeedKts;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.UnitConversionOperation;
import info.limpet.data.operations.admin.OperationsLibrary;
import info.limpet.data.operations.arithmetic.AddQuantityOperation;
import info.limpet.data.operations.arithmetic.DivideQuantityOperation;
import info.limpet.data.operations.arithmetic.MultiplyQuantityOperation;
import info.limpet.data.operations.arithmetic.SimpleMovingAverageOperation;
import info.limpet.data.operations.arithmetic.SubtractQuantityOperation;
import info.limpet.data.operations.arithmetic.UnitaryMathOperation;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import junit.framework.TestCase;

public class TestOperations extends TestCase
{

  private IContext context = new MockContext();

  @SuppressWarnings(
  { "rawtypes", "unchecked" })
  public void testInterpolateTests()
  {
    // place to store results data
    InMemoryStore store = new SampleData().getData(10);

    // ok, let's try one that works
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // ///////////////
    // TEST INVALID PERMUTATIONS
    // ///////////////
    ICollection speedGood1 = (ICollection) store.get(SampleData.SPEED_ONE);
    ICollection speedGood2 = (ICollection) store.get(SampleData.SPEED_TWO);
    ICollection speedLonger =
        (ICollection) store.get(SampleData.SPEED_THREE_LONGER);

    selection.add(speedGood1);
    selection.add(speedLonger);

    Collection<ICommand<ICollection>> actions =
        new AddQuantityOperation().actionsFor(selection, store, context);

    assertEquals("correct number of actions returned", 1, actions.size());

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);

    actions = new AddQuantityOperation().actionsFor(selection, store, context);

    assertEquals("correct number of actions returned", 2, actions.size());

    ICommand<?> addAction = actions.iterator().next();

    assertNotNull("found action", addAction);

  }

  public void testTrig()
  {
    // prepare some data
    SpeedKts speedData = new StockTypes.Temporal.SpeedKts("speed", null);
    speedData.add(100, 23);
    speedData.add(200, 44);

    AngleDegrees angleData =
        new StockTypes.NonTemporal.AngleDegrees("degs", null);
    angleData.add(200d);
    angleData.add(123d);

    StockTypes.Temporal.AngleDegrees temporalAngleData =
        new StockTypes.Temporal.AngleDegrees("degs", null);
    temporalAngleData.add(1000, 200d);
    temporalAngleData.add(3000, 123d);
    temporalAngleData.add(4000, 13d);

    List<ICollection> selection = new ArrayList<ICollection>();
    InMemoryStore store = new InMemoryStore();

    HashMap<String, List<IOperation<?>>> ops =
        OperationsLibrary.getOperations();
    List<IOperation<?>> arith = ops.get(OperationsLibrary.ARITHMETIC);
    // ok, now find the trig op
    Iterator<IOperation<?>> iter = arith.iterator();
    IOperation<ICollection> sinOp = null;
    IOperation<ICollection> cosOp = null;
    while (iter.hasNext())
    {
      IOperation<?> thisO = (IOperation<?>) iter.next();
      if (thisO instanceof UnitaryMathOperation)
      {
        UnitaryMathOperation umo = (UnitaryMathOperation) thisO;
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
    Collection<ICommand<ICollection>> validOps =
        sinOp.actionsFor(selection, null, null);
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

    ICommand<ICollection> theOp = validOps.iterator().next();
    theOp.execute();

    assertEquals("has new dataset", 1, store.size());
    ICollection output = theOp.getOutputs().iterator().next();

    // check the size
    assertEquals("correct size", 2, output.size());

    // check data type
    assertTrue("isn't temporal", !output.isTemporal());
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
    assertTrue("isn't temporal", output.isTemporal());

  }

  public void testAppliesTo()
  {
    // the units for this measurement
    Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
    Unit<Velocity> kmm = KILO(METRE).divide(MINUTE).asType(Velocity.class);
    Unit<Length> m = METRE.asType(Length.class);

    // the target collection
    QuantityCollection<Velocity> speedGood1 =
        new QuantityCollection<Velocity>("Speed 1", null, kmh);
    QuantityCollection<Velocity> speedGood2 =
        new QuantityCollection<Velocity>("Speed 2", null, kmh);
    QuantityCollection<Velocity> speedLonger =
        new QuantityCollection<Velocity>("Speed 3", null, kmh);
    QuantityCollection<Velocity> speedDiffUnits =
        new QuantityCollection<Velocity>("Speed 4", null, kmm);
    QuantityCollection<Length> len1 =
        new QuantityCollection<Length>("Length 1", null, m);
    TemporalQuantityCollection<Velocity> temporalSpeed1 =
        new TemporalQuantityCollection<Velocity>("Speed 5", null, kmh);
    TemporalQuantityCollection<Velocity> temporalSpeed2 =
        new TemporalQuantityCollection<Velocity>("Speed 6", null, kmh);
    ObjectCollection<String> string1 = new ObjectCollection<>("strings 1");
    ObjectCollection<String> string2 = new ObjectCollection<>("strings 2");

    for (int i = 1; i <= 10; i++)
    {
      // create a measurement
      double thisSpeed = i * 2;
      Measurable<Velocity> speedVal1 = Measure.valueOf(thisSpeed, kmh);
      Measurable<Velocity> speedVal2 = Measure.valueOf(thisSpeed * 2, kmh);
      Measurable<Velocity> speedVal3 = Measure.valueOf(thisSpeed / 2, kmh);
      Measurable<Velocity> speedVal4 = Measure.valueOf(thisSpeed / 2, kmm);
      Measurable<Length> lenVal1 = Measure.valueOf(thisSpeed / 2, m);

      // store the measurements
      speedGood1.add(speedVal1);
      speedGood2.add(speedVal2);
      speedLonger.add(speedVal3);
      speedDiffUnits.add(speedVal4);
      temporalSpeed1.add(i, speedVal2);
      temporalSpeed2.add(i, speedVal3);
      len1.add(lenVal1);

      string1.add(i + " ");
      string2.add(i + "a ");
    }

    Measurable<Velocity> speedVal3a = Measure.valueOf(2, kmh);
    speedLonger.add(speedVal3a);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    CollectionComplianceTests testOp = new CollectionComplianceTests();

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertTrue("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allTemporal(selection));

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    selection.add(speedDiffUnits);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allTemporal(selection));

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    selection.add(len1);

    assertFalse("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allTemporal(selection));

    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);
    selection.add(speedLonger);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertTrue("all same units", testOp.allEqualUnits(selection));
    assertFalse("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allTemporal(selection));

    selection.clear();
    selection.add(temporalSpeed1);
    selection.add(temporalSpeed2);

    assertTrue("all same dim", testOp.allEqualDimensions(selection));
    assertTrue("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all quantities", testOp.allQuantity(selection));
    assertTrue("all temporal", testOp.allTemporal(selection));

    selection.clear();
    selection.add(temporalSpeed1);
    selection.add(string1);

    assertFalse("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertFalse("all quantities", testOp.allQuantity(selection));
    assertFalse("all temporal", testOp.allTemporal(selection));

    selection.clear();
    selection.add(string1);
    selection.add(string1);

    assertFalse("all same dim", testOp.allEqualDimensions(selection));
    assertFalse("all same units", testOp.allEqualUnits(selection));
    assertTrue("all same length", testOp.allEqualLength(selection));
    assertTrue("all non quantities", testOp.allNonQuantity(selection));
    assertFalse("all temporal", testOp.allTemporal(selection));

    // ok, let's try one that works
    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood2);

    InMemoryStore store = new InMemoryStore();
    assertEquals("store empty", 0, store.size());

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    Collection<ICommand<ICollection>> actions =
        new AddQuantityOperation().actionsFor(selection, store, context);

    assertEquals("correct number of actions returned", 1, actions.size());

    ICommand<?> addAction = actions.iterator().next();
    addAction.execute();

    assertEquals("new collection added to store", 1, store.size());

    ICollection firstItem = (ICollection) store.iterator().next();
    ICommand<?> precedent = firstItem.getPrecedent();
    assertNotNull("has precedent", precedent);
    assertEquals("Correct name",
        "Add numeric values in provided series (indexed)", precedent.getName());

    List<? extends IStoreItem> inputs = precedent.getInputs();
    assertEquals("Has both precedents", 2, inputs.size());

    Iterator<? extends IStoreItem> iIter = inputs.iterator();
    while (iIter.hasNext())
    {
      ICollection thisC = (ICollection) iIter.next();
      List<ICommand<?>> deps = thisC.getDependents();
      assertEquals("has a depedent", 1, deps.size());
      Iterator<ICommand<?>> dIter = deps.iterator();
      while (dIter.hasNext())
      {
        ICommand<?> iCommand = dIter.next();
        assertEquals("Correct dependent", precedent, iCommand);
      }
    }

    List<? extends IStoreItem> outputs = precedent.getOutputs();
    assertEquals("Has both dependents", 1, outputs.size());

    Iterator<? extends IStoreItem> oIter = outputs.iterator();
    while (oIter.hasNext())
    {
      ICollection thisC = (ICollection) oIter.next();
      ICommand<?> dep = thisC.getPrecedent();
      assertNotNull("has a depedent", dep);
      assertEquals("Correct dependent", precedent, dep);
    }
  }

  public void testDimensionlessMultiply()
  {
    // place to store results data
    InMemoryStore store = new SampleData().getData(10);

    // ok, let's try one that works
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // ///////////////
    // TEST INVALID PERMUTATIONS
    // ///////////////
    ICollection speedGood1 = (ICollection) store.get(SampleData.SPEED_ONE);
    ICollection speedGood2 = (ICollection) store.get(SampleData.SPEED_TWO);
    ICollection speedIrregular =
        (ICollection) store.get(SampleData.SPEED_IRREGULAR2);
    ICollection string1 = (ICollection) store.get(SampleData.STRING_ONE);
    ICollection len1 = (ICollection) store.get(SampleData.LENGTH_ONE);
    ICollection factor =
        (ICollection) store.get(SampleData.FLOATING_POINT_FACTOR);

    selection.clear();
    selection.add(speedGood1);
    selection.add(string1);
    Collection<ICommand<IStoreItem>> commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not both quantities", 0,
        commands.size());

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

    ICommand<IStoreItem> command = commands.iterator().next();

    // test actions has single item: "Multiply series by constant"
    assertEquals("correct name", "Multiply series", command.getName());

    // apply action
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", 1, store.size());

    ICollection newS =
        (ICollection) store
            .get("Product of Speed One Time, Floating point factor");

    // test results is same length as thisSpeed
    assertEquals("correct size", 10, newS.size());

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
    ICollection output = (ICollection) command.getOutputs().iterator().next();
    assertTrue(output.isTemporal());
    assertTrue(output.isQuantity());
    assertEquals("Correct len",
        Math.max(speedGood1.size(), speedIrregular.size()), output.size());
  }

  @SuppressWarnings("unchecked")
  public void testUnitConversion()
  {
    // place to store results data
    IStore store = new SampleData().getData(10);

    List<ICollection> selection = new ArrayList<ICollection>(3);
    // speed one defined in m/s
    ICollection speedGood1 = (ICollection) store.get(SampleData.SPEED_ONE);
    selection.add(speedGood1);

    // test incompatible target unit
    Collection<ICommand<ICollection>> commands =
        new UnitConversionOperation(METRE)
            .actionsFor(selection, store, context);
    assertEquals("target unit not same dimension as input", 0, commands.size());

    // test valid target unit
    commands =
        new UnitConversionOperation(KILOMETRES_PER_HOUR).actionsFor(selection,
            store, context);
    assertEquals("valid unit dimensions", 1, commands.size());

    ICommand<ICollection> command = commands.iterator().next();

    // apply action
    command.execute();

    ICollection newS =
        (ICollection) store.get("Speed One Time converted to km/h");
    assertNotNull(newS);

    // test results is same length as thisSpeed
    assertEquals("correct size", 10, newS.size());
    assertTrue("is temporal", newS.isTemporal());

    // check that operation isn't offered if the dataset is already in
    // that type
    commands =
        new UnitConversionOperation(METRES_PER_SECOND).actionsFor(selection,
            store, context);
    assertEquals("already in destination units", 0, commands.size());

    IQuantityCollection<?> inputSpeed = (IQuantityCollection<?>) speedGood1;

    Measurable<Velocity> firstInputSpeed =
        (Measurable<Velocity>) inputSpeed.getValues().get(0);

    IQuantityCollection<?> outputSpeed = (IQuantityCollection<?>) newS;

    Measurable<Velocity> outputMEas =
        (Measurable<Velocity>) outputSpeed.getValues().get(0);
    double firstOutputSpeed =
        outputMEas.doubleValue((Unit<Velocity>) outputSpeed.getUnits());

    UnitConverter oc =
        inputSpeed.getUnits().getConverterTo(KILOMETERS_PER_HOUR);

    assertEquals(oc.convert(firstInputSpeed
        .doubleValue((Unit<Velocity>) inputSpeed.getUnits())), firstOutputSpeed);

  }

  public void testSimpleMovingAverage()
  {
    // place to store results data
    IStore store = new SampleData().getData(10);

    List<ICollection> selection = new ArrayList<>();

    @SuppressWarnings("unchecked")
    IQuantityCollection<Velocity> speedGood1 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    selection.add(speedGood1);

    int windowSize = 3;

    Collection<ICommand<ICollection>> commands =
        new SimpleMovingAverageOperation(windowSize).actionsFor(selection,
            store, context);
    assertEquals(1, commands.size());

    ICommand<ICollection> command = commands.iterator().next();

    // apply action
    command.execute();

    @SuppressWarnings("unchecked")
    IQuantityCollection<Velocity> newS =
        (IQuantityCollection<Velocity>) store
            .get("Moving average of Speed One Time");
    assertNotNull(newS);

    // test results is same length as thisSpeed
    assertEquals("correct size", 10, newS.size());

    // calculate sum of input values [0..windowSize-1]
    double sum = 0;
    for (int i = 0; i < windowSize; i++)
    {
      Measurable<Velocity> inputQuantity = speedGood1.getValues().get(i);
      sum += inputQuantity.doubleValue(speedGood1.getUnits());
    }
    double average = sum / windowSize;

    // compare to output value [windowSize-1]
    Measurable<Velocity> simpleMovingAverage =
        newS.getValues().get(windowSize - 1);

    assertEquals(average, simpleMovingAverage.doubleValue(newS.getUnits()), 0);

  }

  @SuppressWarnings(
  { "unchecked" })
  public void testAddition()
  {
    InMemoryStore store = new SampleData().getData(10);

    // test invalid dimensions
    ITemporalQuantityCollection<Velocity> speedGood1 =
        (ITemporalQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    IQuantityCollection<Velocity> speedGood2 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_TWO);

    IQuantityCollection<Velocity> newS =
        (IQuantityCollection<Velocity>) store
            .get("Sum of Speed One Time, Speed Two Time");

    assertNotNull(newS);
    assertEquals("correct size", 10, newS.size());

    // assert same unit
    assertEquals(newS.getUnits(), speedGood1.getUnits());

    double firstDifference =
        newS.getValues().get(0).doubleValue(newS.getUnits());
    double speed1firstValue =
        speedGood1.getValues().get(0).doubleValue(speedGood1.getUnits());
    double speed2firstValue =
        speedGood2.getValues().get(0).doubleValue(speedGood2.getUnits());

    assertEquals(firstDifference, speed1firstValue + speed2firstValue);

    // test that original series have dependents
    assertEquals("first series has dependents", 2, speedGood1.getDependents()
        .size());
    assertEquals("second series has dependents", 1, speedGood2
        .getDependents().size());

    // test that new series has predecessors
    assertNotNull("new series has precedent", newS.getPrecedent());

  }

  @SuppressWarnings(
  { "rawtypes", "unchecked" })
  public void testSubtractionSingleton()
  {
    InMemoryStore store = new SampleData().getData(10);
    List<ICollection> selection = new ArrayList<ICollection>(3);

    // test invalid dimensions
    IQuantityCollection<Velocity> speedGood1 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    IQuantityCollection<Velocity> speedSingle =
        new StockTypes.NonTemporal.SpeedMSec("singleton", null);

    speedSingle.add(2d);

    selection.add(speedGood1);
    selection.add(speedSingle);
    Collection<ICommand<ICollection>> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("got two commands", 4, commands.size());

    // have a look
    ICommand<ICollection> first = commands.iterator().next();
    first.execute();
    ICollection output = first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertEquals("correct size", speedGood1.size(), output.size());

    assertEquals("correct value", 2.3767, speedGood1.getValues().get(0)
        .doubleValue(Velocity.UNIT) * 2, 0.001);
  }

  @SuppressWarnings(
  { "rawtypes", "unchecked" })
  public void testAddSingleton()
  {
    InMemoryStore store = new SampleData().getData(10);
    List<ICollection> selection = new ArrayList<ICollection>(3);

    // test invalid dimensions
    IQuantityCollection<Velocity> speedGood1 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    IQuantityCollection<Velocity> speedSingle =
        new StockTypes.NonTemporal.SpeedMSec("singleton", null);

    speedSingle.add(2d);

    selection.add(speedGood1);
    selection.add(speedSingle);
    Collection<ICommand<ICollection>> commands =
        new AddQuantityOperation().actionsFor(selection, store, context);
    assertEquals("got two commands", 2, commands.size());

    // have a look
    Iterator<ICommand<ICollection>> iter = commands.iterator();
    iter.next();
    ICommand<ICollection> first = iter.next();
    first.execute();
    IQuantityCollection<Velocity> output =
        (IQuantityCollection) first.getOutputs().iterator().next();
    assertNotNull("produced output", output);
    assertTrue("output is temporal", output.isTemporal());
    assertEquals("correct size", speedGood1.size(), output.size());

    assertEquals("correct value",
        output.getValues().get(0).doubleValue(Velocity.UNIT), speedGood1
            .getValues().get(0).doubleValue(Velocity.UNIT) + 2, 0.001);
  }

  @SuppressWarnings(
  { "rawtypes", "unchecked" })
  public void testSubtraction()
  {
    InMemoryStore store = new SampleData().getData(10);
    int storeSize = store.size();
    List<ICollection> selection = new ArrayList<ICollection>(3);

    // test invalid dimensions
    IQuantityCollection<Velocity> speedGood1 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    IQuantityCollection<Angle> angle1 =
        (IQuantityCollection<Angle>) store.get(SampleData.ANGLE_ONE);
    selection.add(speedGood1);
    selection.add(angle1);
    Collection<ICommand<ICollection>> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not same dimensions", 0,
        commands.size());

    selection.clear();

    // test not all quantities
    ICollection string1 = (ICollection) store.get(SampleData.STRING_ONE);
    selection.add(speedGood1);
    selection.add(string1);
    commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid collections - not all quantities", 0, commands.size());

    selection.clear();

    // test valid command
    IQuantityCollection<Velocity> speedGood2 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_TWO);
    selection.add(speedGood1);
    selection.add(speedGood2);

    commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid command", 4, commands.size());

    ICommand<ICollection> command = commands.iterator().next();
    command.execute();

    // test store has a new item in it
    assertEquals("store not empty", storeSize + 1, store.size());

    IQuantityCollection<Velocity> newS =
        (IQuantityCollection<Velocity>) store.get(speedGood2.getName()
            + " from " + speedGood1.getName());

    assertNotNull(newS);
    assertEquals("correct size", 10, newS.size());

    // assert same unit
    assertEquals(newS.getUnits(), speedGood1.getUnits());

    double firstDifference =
        newS.getValues().get(0).doubleValue(newS.getUnits());
    double speed1firstValue =
        speedGood1.getValues().get(0).doubleValue(speedGood1.getUnits());
    double speed2firstValue =
        speedGood2.getValues().get(0).doubleValue(speedGood2.getUnits());

    assertEquals(firstDifference, speed1firstValue - speed2firstValue);
  }

  @SuppressWarnings("unchecked")
  public void testDivision()
  {
    // place to store results data
    InMemoryStore store = new SampleData().getData(10);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    IQuantityCollection<Velocity> speedGood1 =
        (IQuantityCollection<Velocity>) store.get(SampleData.SPEED_ONE);
    ICollection speedGood2 = (ICollection) store.get(SampleData.SPEED_TWO);
    IQuantityCollection<Length> length1 =
        (IQuantityCollection<Length>) store.get(SampleData.LENGTH_ONE);
    ICollection string1 = (ICollection) store.get(SampleData.STRING_ONE);
    IQuantityCollection<Dimensionless> factor =
        (IQuantityCollection<Dimensionless>) store
            .get(SampleData.FLOATING_POINT_FACTOR);
    ICollection speedGood1Bigger =
        (ICollection) new SampleData().getData(20).get(SampleData.SPEED_ONE);

    // /
    // / TEST NOT APPLICABLE INPUT
    // /

    // test invalid number of inputs
    selection.add(speedGood1);
    selection.add(speedGood2);
    selection.add(length1);
    Collection<ICommand<IStoreItem>> commands =
        new DivideQuantityOperation().actionsFor(selection, store, context);
    assertEquals("invalid number of inputs", 0, commands.size());

    // test not all quantities
    selection.clear();
    selection.add(speedGood1);
    selection.add(string1);
    commands =
        new DivideQuantityOperation().actionsFor(selection, store, context);
    assertEquals("not all quantities", 0, commands.size());

    // test different size
    selection.clear();
    selection.add(speedGood1);
    selection.add(speedGood1Bigger);
    commands =
        new DivideQuantityOperation().actionsFor(selection, store, context);
    assertEquals("collection not same size", 2, commands.size());

    // /
    // / TEST APPLICABLE INPUT
    // /

    // test length over speed
    selection.clear();
    selection.add(length1);
    selection.add(speedGood1);
    commands =
        new DivideQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid input", 2, commands.size());

    ICommand<IStoreItem> command = commands.iterator().next();
    command.execute();

    IStoreItem output = command.getOutputs().iterator().next();

    IQuantityCollection<Quantity> iQ = (IQuantityCollection<Quantity>) output;

    assertEquals("correct units", "[T]", iQ.getUnits().getDimension()
        .toString());

    store.clear();
    command.execute();

    assertEquals(1, store.size());
    IQuantityCollection<Duration> duration =
        (IQuantityCollection<Duration>) store.iterator().next();
    assertEquals(speedGood1.size(), duration.size());

    double firstDuration =
        duration.getValues().get(0).doubleValue(duration.getUnits());
    double firstLength =
        length1.getValues().get(0).doubleValue(length1.getUnits());
    double firstSpeed =
        speedGood1.getValues().get(0).doubleValue(speedGood1.getUnits());

    assertEquals(firstLength / firstSpeed, firstDuration);

    // test length over factor
    selection.clear();
    selection.add(length1);
    selection.add(factor);
    commands =
        new DivideQuantityOperation().actionsFor(selection, store, context);
    assertEquals("valid input", 2, commands.size());

    Iterator<ICommand<IStoreItem>> iterator = commands.iterator();
    command = iterator.next();

    store.clear();
    command.execute();

    assertEquals(1, store.size());
    IQuantityCollection<Length> resultLength =
        (IQuantityCollection<Length>) store.iterator().next();
    assertEquals(length1.size(), resultLength.size());

    double firstResultLength =
        resultLength.getValues().get(0).doubleValue(resultLength.getUnits());
    double factorValue =
        factor.getValues().get(0).doubleValue(factor.getUnits());
    assertEquals(firstLength / factorValue, firstResultLength);

    // test command #2: factor over length
    command = iterator.next();
    store.clear();
    command.execute();
    IQuantityCollection<Quantity> resultQuantity =
        (IQuantityCollection<Quantity>) store.iterator().next();
    // assert expected unit (1/m)
    assertEquals("1/" + length1.getUnits().toString(), resultQuantity
        .getUnits().toString());
    assertEquals(length1.size(), resultQuantity.size());

    double firstResultQuantity =
        resultQuantity.getValues().get(0)
            .doubleValue(resultQuantity.getUnits());
    assertEquals(factorValue / firstLength, firstResultQuantity);
  }
}
