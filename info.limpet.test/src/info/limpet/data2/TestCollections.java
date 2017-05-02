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

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.Range;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocument;
import info.limpet.impl.Document.InterpMethod;
import info.limpet.operations.arithmetic.UnaryQuantityOperation;
import info.limpet.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet.operations.arithmetic.simple.MultiplyQuantityOperation;
import info.limpet.operations.arithmetic.simple.SubtractQuantityOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import junit.framework.TestCase;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.dataset.StringDataset;

public class TestCollections extends TestCase
{
  private IContext context = new MockContext();

  public void testCreateObject()
  {
    // the target collection
    List<String> stringCollection = new ArrayList<String>();

    for (int i = 1; i <= 10; i++)
    {
      // store the measurement
      stringCollection.add(i + "aaa");
    }

    StringDataset str =
        (StringDataset) DatasetFactory.createFromObject(stringCollection);
    IDocument strDoc = new StringDocument(str, null);

    // check it got stored
    assertEquals("correct number of samples", 10, strDoc.size());
  }

  public void testTemporalQuantityInterp()
  {
    NumberDocumentBuilder speeds =
        new NumberDocumentBuilder("Speeds", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    speeds.add(100, 10d);
    speeds.add(200, 20d);
    speeds.add(300, 30d);
    speeds.add(400, 40d);

    NumberDocument tq = (NumberDocument) speeds.toDocument();

    assertEquals("returned null", null, tq.interpolateValue(90,
        InterpMethod.Linear));
    assertEquals("returned null", null, tq.interpolateValue(410,
        InterpMethod.Linear));

    assertEquals("returned correct value", 15d, tq.interpolateValue(150,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 28d, tq.interpolateValue(280,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 10d, tq.interpolateValue(100,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 20d, tq.interpolateValue(200,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 30d, tq.interpolateValue(300,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 40d, tq.interpolateValue(400,
        InterpMethod.Linear), 0.001);
  }

  public void testAddQuantityTemporalInterp()
  {
    NumberDocumentBuilder tqb1 =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tqb1.add(100, 10d);
    tqb1.add(230, 23d);
    tqb1.add(270, 27d);
    tqb1.add(300, 30d);
    tqb1.add(320, 32d);
    tqb1.add(400, 40d);

    NumberDocumentBuilder tqb2 =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tqb2.add(220, 22d);
    tqb2.add(340, 34d);
    tqb2.add(440, 44d);

    NumberDocument tq1 = tqb1.toDocument();
    NumberDocument tq2 = tqb2.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    StoreGroup store = new StoreGroup("data store");
    Collection<ICommand> commands =
        new AddQuantityOperation().actionsFor(selection, store, context);
    ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    IDocument series = (IDocument) store.get("Sum of Some data1 + Some data2");
    assertTrue("non empty", series.size() > 0);
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    NumberDocument tq = (NumberDocument) series;

    assertEquals("is correct length", 5, tq.size());
    assertNull("returned null for out of intersection", tq.interpolateValue(
        100, InterpMethod.Linear));
    assertEquals("returned correct value", 46d, tq.interpolateValue(230,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 60d, tq.interpolateValue(300,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 80d, tq.interpolateValue(400,
        InterpMethod.Linear), 0.001);
    assertNull("returned null for out of intersection", tq.interpolateValue(
        420, InterpMethod.Linear));
  }

  public void testMathOperators()
  {
    NumberDocumentBuilder tq1 =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1.add(100, 10d);
    tq1.add(200, -20d);
    tq1.add(300, 30d);
    tq1.add(400, -20d);

    NumberDocument tq1d = tq1.toDocument();

    NumberDocumentBuilder tq2 =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq2.add(220, -11d);
    tq2.add(340, -17d);
    tq2.add(440, -22d);

    NumberDocument tq2d = tq2.toDocument();

    NumberDocumentBuilder nq1 =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, null);
    nq1.add(10d);
    nq1.add(-20d);
    nq1.add(30d);
    nq1.add(-20d);

    NumberDocument nq1d = nq1.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1d);
    selection.add(tq2d);

    StoreGroup store = new StoreGroup("Store");
    UnaryQuantityOperation absOp = new UnaryQuantityOperation("Abs")
    {

      @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        // check it's numerical
        return true;
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      protected String getUnaryNameFor(String name)
      {
        return "Absolute value of " + name;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.abs(input);
      }
    };
    Collection<ICommand> commands = absOp.actionsFor(selection, store, context);

    assertEquals("have some commands", 1, commands.size());

    ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 2, store.size());
    assertEquals("corrent num of outputs", 2, firstC.getOutputs().size());

    // get the first one.
    NumberDocument series = (NumberDocument) firstC.getOutputs().get(0);
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 4, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    // check some values
    assertEquals("value correct", 10d, series.getValueAt(0), 0.001);
    assertEquals("value correct", 20d, series.getValueAt(1), 0.001);
    assertEquals("value correct", 30d, series.getValueAt(2), 0.001);
    assertEquals("value correct", 20d, series.getValueAt(3), 0.001);

    series = (NumberDocument) firstC.getOutputs().get(1);
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 3, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    // check some values
    assertEquals("value correct", 11d, series.getValueAt(0));
    assertEquals("value correct", 17d, series.getValueAt(1));
    assertEquals("value correct", 22d, series.getValueAt(2));

    // try to clear the units
    UnaryQuantityOperation clearU = new UnaryQuantityOperation("Clear units")
    {

      @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return true;
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return Dimensionless.UNIT;
      }

      @Override
      protected String getUnaryNameFor(String name)
      {
        return "Dimensionless " + name;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return input;
      }
    };

    assertEquals("previous type:", "[L]/[T]", tq1d.getUnits().getDimension()
        .toString());

    selection.clear();
    selection.add(tq1d);
    store.clear();

    Collection<ICommand> ops = clearU.actionsFor(selection, store, context);
    ICommand command = ops.iterator().next();
    command.execute();

    NumberDocument output =
        (NumberDocument) command.getOutputs().iterator().next();

    assertEquals("new type:", "", output.getUnits().getDimension().toString());
    assertEquals("same size", output.size(), tq1d.size());
    assertEquals("first item same value", output.getValueAt(0), tq1d.getValueAt(0));
    // assertEquals("same num times", output.getTimes().size(), tq1.getTimes()
    // .size());

    // try again with a non temporal collection
    selection.clear();
    selection.add(nq1d);
    store.clear();

    assertEquals("previous type:", "[L]/[T]", nq1d.getUnits().getDimension()
        .toString());

    ops = clearU.actionsFor(selection, store, context);
    command = ops.iterator().next();
    command.execute();

    NumberDocument output2 =
        (NumberDocument) command.getOutputs().iterator().next();

    assertEquals("new type:", "", output2.getUnits().getDimension().toString());
    assertEquals("same size", output2.size(), nq1d.size());
    assertEquals("first item same value", output2.getValueAt(0),
        nq1d.getValueAt(0), 0.001);
  }

  public void testMultiplyQuantitySingleton()
  {

    NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    NumberDocument tq1 = tq1b.toDocument();

    NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, null);
    tq2b.add(11d);

    NumberDocument tq2 = tq2b.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    StoreGroup store = new StoreGroup("Store");
    Collection<ICommand> commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    NumberDocument series =
        (NumberDocument) firstC.getOutputs().iterator().next();
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 4, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());
    assertEquals("correct value", 110d, series.getValueAt(0));

    tq2b.add(11d);

    NumberDocument tq3 = tq2b.toDocument();

    selection.remove(tq2);
    selection.add(tq3);

    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("no commands returned", 0, commands.size());

  }

  public void testMultiplyQuantityTemporalInterp()
  {
    NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    tq2b.add(220, 11d);
    tq2b.add(340, 17d);
    tq2b.add(440, 22d);

    NumberDocument tq1 = tq1b.toDocument();
    NumberDocument tq2 = tq2b.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    StoreGroup store = new StoreGroup("Store");
    Collection<ICommand> commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    NumberDocument series = (NumberDocument) firstC.getOutputs().get(0);
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 2, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    try
    {
      Dataset oDataset =
          DatasetUtils.sliceAndConvertLazyDataset(series.getDataset());
      System.out.println(oDataset.toString(true));
    }
    catch (DatasetException e)
    {
      throw new RuntimeException(e);
    }

    // ITemporalQuantityCollection<?> tq = (ITemporalQuantityCollection<?>) series;

    assertEquals("returned correct value", null, series.interpolateValue(100,
        InterpMethod.Linear));
    assertEquals("returned correct value", 242d, series.interpolateValue(220,
        InterpMethod.Linear));
    assertEquals("returned correct value", 578d, series.interpolateValue(340,
        InterpMethod.Linear));
    assertEquals("returned correct value", null, series.interpolateValue(400,
        InterpMethod.Linear));

    // ok, mangle the second array a bit more
    tq2b.clear();
    tq2b.add(20, 11d);
    tq2b.add(340, 17d);
    tq2b.add(440, 22d);

    tq2 = tq2b.toDocument();

    selection.clear();
    selection.add(tq1);
    selection.add(tq2);

    store = new StoreGroup("Out");
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    series = (NumberDocument) firstC.getOutputs().get(0);
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 4, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    assertEquals("returned correct value", 125d, series.interpolateValue(100,
        InterpMethod.Linear));
    assertEquals("returned correct value", 287.5d, series.interpolateValue(200,
        InterpMethod.Linear));
    assertEquals("returned correct value", 487.5d, series.interpolateValue(300,
        InterpMethod.Linear));
    assertEquals("returned correct value", 800d, series.interpolateValue(400,
        InterpMethod.Linear));

    // ok, make the second array longer
    tq2b.clear();
    tq2b.add(200, 11d);
    tq2b.add(250, 13d);
    tq2b.add(330, 17d);
    tq2b.add(360, 19d);
    tq2b.add(440, 22d);

    tq2 = tq2b.toDocument();

    selection.clear();
    selection.add(tq1);
    selection.add(tq2);

    store = new StoreGroup("output");
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    series = (NumberDocument) firstC.getOutputs().iterator().next();
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 4, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    assertEquals("returned correct value", null, series.interpolateValue(100,
        InterpMethod.Linear));
    assertEquals("returned correct value", 220d, series.interpolateValue(200,
        InterpMethod.Linear));
    assertEquals("returned correct value", 472.5d, series.interpolateValue(300,
        InterpMethod.Linear));
    assertEquals("returned correct value", null, series.interpolateValue(400,
        InterpMethod.Linear));
    assertEquals("returned correct value", null, series.interpolateValue(420,
        InterpMethod.Linear));
    assertEquals("returned correct value", null, series.interpolateValue(440,
        InterpMethod.Linear));

  }

  public void testSampleData()
  {
    StoreGroup data = new SampleData().getData(10);
    NumberDocument ranged =
        (NumberDocument) data.get(SampleData.RANGED_SPEED_SINGLETON);
    assertNotNull("found series", ranged);

    Range range = ranged.getRange();
    assertNotNull("found range", range);

    // check the range has values
    assertEquals("correct values", 940d, (Double) range.getMinimum(), 0.1);
    assertEquals("correct values", 1050d, (Double) range.getMaximum(), 0.1);
  }

  public void testCreateQuantity()
  {
    NumberDocumentBuilder speedCollectionB =
        new NumberDocumentBuilder("Speed", METRE.divide(SECOND).asType(
            Velocity.class), null, null);

    for (int i = 1; i <= 10; i++)
    {
      // create a measurement
      double thisSpeed = i * 2;

      // store the measurement
      speedCollectionB.add(thisSpeed);
    }

    NumberDocument speedCollection = speedCollectionB.toDocument();

    // check it didn't get stored
    assertEquals("correct number of samples", 10, speedCollection.size());
    assertEquals("correct name", "Speed", speedCollection.getName());

    assertEquals("correct min", 2d, speedCollection.stats().min(), 0.001);
    assertEquals("correct max", 20d, speedCollection.stats().max(), 0.001);
    assertEquals("correct mean", 11d, speedCollection.stats().mean(), 0.001);
    assertEquals("correct variance", 33, speedCollection.stats().variance(),
        0.1);
    assertEquals("correct sd", 5.744, speedCollection.stats().sd(), 0.001);
  }

  public void testSubtractQuantityTemporalInterp()
  {
    NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    NumberDocument tq1 = tq1b.toDocument();

    NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq2b.add(220, 11d);
    tq2b.add(340, 17d);
    tq2b.add(440, 22d);

    NumberDocument tq2 = tq2b.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    StoreGroup store = new StoreGroup("Output");
    Collection<ICommand> commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    NumberDocument series =
        (NumberDocument) firstC.getOutputs().iterator().next();
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 2, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    assertEquals("returned correct value", null, series.interpolateValue(100,
        InterpMethod.Linear));
    assertEquals("returned correct value", null, series.interpolateValue(200,
        InterpMethod.Linear));
    assertEquals("returned correct value", 11d, series.interpolateValue(220,
        InterpMethod.Linear));
    assertEquals("returned correct value", 15d, series.interpolateValue(300,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", 17d, series.interpolateValue(340,
        InterpMethod.Linear), 0.001);
    assertEquals("returned correct value", null, series.interpolateValue(400,
        InterpMethod.Linear));

    // ok, mangle the second array a bit more
    tq2b.clear();
    tq2b.add(20, 11d);
    tq2b.add(340, 17d);
    tq2b.add(440, 22d);

    tq2 = tq2b.toDocument();

    selection.clear();
    selection.add(tq1);
    selection.add(tq2);

    store.clear();
    commands =
        new SubtractQuantityOperation().actionsFor(selection, store, context);
    firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    series = (NumberDocument) firstC.getOutputs().iterator().next();
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 4, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    assertEquals("returned correct value", -2.5d, series.interpolateValue(100,
        InterpMethod.Linear));
    assertEquals("returned correct value", 5.625d, series.interpolateValue(200,
        InterpMethod.Linear));
    assertEquals("returned correct value", 13.75d, series.interpolateValue(300,
        InterpMethod.Linear));
    assertEquals("returned correct value", 20d, series.interpolateValue(400,
        InterpMethod.Linear));
  }
}
