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
import info.limpet2.Document;
import info.limpet2.Document.InterpMethod;
import info.limpet2.ICommand;
import info.limpet2.IContext;
import info.limpet2.IndexedNumberDocumentBuilder;
import info.limpet2.MockContext;
import info.limpet2.NumberDocument;
import info.limpet2.StoreGroup;
import info.limpet2.operations.arithmetic.AddQuantityOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.quantity.Velocity;

import junit.framework.TestCase;

import org.eclipse.january.dataset.DatasetFactory;
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
    Document strDoc = new Document(str, null);

    // check it got stored
    assertEquals("correct number of samples", 10, strDoc.size());
  }

  public void testTemporalQuantityInterp()
  {
    IndexedNumberDocumentBuilder speeds =
        new IndexedNumberDocumentBuilder("Speeds", METRE.divide(SECOND).asType(
            Velocity.class), null);

    speeds.add(100, 10);
    speeds.add(200, 20);
    speeds.add(300, 30);
    speeds.add(400, 40);

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
    IndexedNumberDocumentBuilder tqb1 =
        new IndexedNumberDocumentBuilder("Some data1", METRE.divide(SECOND)
            .asType(Velocity.class), null);
    tqb1.add(100, 10);
    tqb1.add(230, 23);
    tqb1.add(270, 27);
    tqb1.add(300, 30);
    tqb1.add(320, 32);
    tqb1.add(400, 40);

    IndexedNumberDocumentBuilder tqb2 =
        new IndexedNumberDocumentBuilder("Some data2", METRE.divide(SECOND)
            .asType(Velocity.class), null);
    tqb2.add(220, 22);
    tqb2.add(340, 34);
    tqb2.add(440, 44);

    NumberDocument tq1 = tqb1.toDocument();
    NumberDocument tq2 = tqb2.toDocument();

    List<Document> selection = new ArrayList<Document>();
    selection.add(tq1);
    selection.add(tq2);

    StoreGroup store = new StoreGroup("data store");
    Collection<ICommand> commands =
        new AddQuantityOperation().actionsFor(selection, store, context);
    ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    Document series = (Document) store.get("Sum of Some data1 + Some data2");
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
   
    //
    // @SuppressWarnings(
    // {"unchecked", "rawtypes"})
    // public void testMathOperators()
    // {
    // StockTypes.Temporal.SpeedMSec tq1 =
    // new StockTypes.Temporal.SpeedMSec("Some data1", null);
    // tq1.add(100, 10);
    // tq1.add(200, -20);
    // tq1.add(300, 30);
    // tq1.add(400, -20);
    //
    // ITemporalQuantityCollection<?> tq2 =
    // new StockTypes.Temporal.SpeedMSec("Some data2", null);
    // tq2.add(220, -11);
    // tq2.add(340, -17);
    // tq2.add(440, -22);
    //
    // StockTypes.NonTemporal.SpeedMSec nq1 =
    // new StockTypes.NonTemporal.SpeedMSec("Some data1", null);
    // nq1.add(10);
    // nq1.add(-20);
    // nq1.add(30);
    // nq1.add(-20);
    //
    // List<ICollection> selection = new ArrayList<ICollection>();
    // selection.add(tq1);
    // selection.add((IQuantityCollection<Quantity>) tq2);
    //
    // StoreGroup store = new StoreGroup();
    // UnitaryMathOperation absOp = new UnitaryMathOperation("Abs")
    // {
    // @Override
    // public double calcFor(double val)
    // {
    // return Math.abs(val);
    // }
    // };
    // Collection<ICommand<ICollection>> commands =
    // absOp.actionsFor(selection, store, context);
    // ICommand<ICollection> firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 2, store.size());
    // assertEquals("corrent num of outputs", 2, firstC.getOutputs().size());
    //
    // // get the first one.
    // ITemporalQuantityCollection<?> series =
    // (ITemporalQuantityCollection<?>) firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 4, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // // check some values
    // assertEquals("value correct", 10d, series.getValues().get(0).doubleValue(
    // (Unit) series.getUnits()));
    // assertEquals("value correct", 20d, series.getValues().get(1).doubleValue(
    // (Unit) series.getUnits()));
    // assertEquals("value correct", 30d, series.getValues().get(2).doubleValue(
    // (Unit) series.getUnits()));
    // assertEquals("value correct", 20d, series.getValues().get(3).doubleValue(
    // (Unit) series.getUnits()));
    //
    // series = (ITemporalQuantityCollection<?>) firstC.getOutputs().get(1);
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 3, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // // check some values
    // assertEquals("value correct", 11d, series.getValues().get(0).doubleValue(
    // (Unit) series.getUnits()));
    // assertEquals("value correct", 17d, series.getValues().get(1).doubleValue(
    // (Unit) series.getUnits()));
    // assertEquals("value correct", 22d, series.getValues().get(2).doubleValue(
    // (Unit) series.getUnits()));
    //
    // // try to clear the units
    // UnitaryMathOperation clearU = new UnitaryMathOperation("Clear units")
    // {
    // public double calcFor(double val)
    // {
    // return val;
    // }
    //
    // protected Unit getUnits(IQuantityCollection input)
    // {
    // return Dimensionless.UNIT;
    // }
    // };
    //
    // assertEquals("previous type:", "[L]/[T]", tq1.getUnits().getDimension()
    // .toString());
    //
    // selection.clear();
    // selection.add(tq1);
    // store.clear();
    //
    // Collection<ICommand<ICollection>> ops =
    // clearU.actionsFor(selection, store, context);
    // ICommand<ICollection> command = ops.iterator().next();
    // command.execute();
    //
    // ITemporalQuantityCollection<Quantity> output =
    // (ITemporalQuantityCollection<Quantity>) command.getOutputs().iterator()
    // .next();
    //
    // assertEquals("new type:", "", output.getUnits().getDimension().toString());
    // assertEquals("same size", output.getValuesCount(), tq1.getValuesCount());
    // assertEquals("first item same value", output.getValues().iterator().next()
    // .doubleValue(output.getUnits()), tq1.getValues().iterator().next()
    // .doubleValue(tq1.getUnits()));
    // assertEquals("same num times", output.getTimes().size(), tq1.getTimes()
    // .size());
    //
    // // try again with a non temporal collection
    // selection.clear();
    // selection.add(nq1);
    // store.clear();
    //
    // assertEquals("previous type:", "[L]/[T]", nq1.getUnits().getDimension()
    // .toString());
    //
    // ops = clearU.actionsFor(selection, store, context);
    // command = ops.iterator().next();
    // command.execute();
    //
    // IQuantityCollection<Quantity> output2 =
    // (IQuantityCollection<Quantity>) command.getOutputs().iterator().next();
    //
    // assertEquals("new type:", "", output2.getUnits().getDimension().toString());
    // assertEquals("same size", output2.getValuesCount(), nq1.getValuesCount());
    // assertEquals("first item same value", output2.getValues().iterator().next()
    // .doubleValue(output2.getUnits()), nq1.getValues().iterator().next()
    // .doubleValue(nq1.getUnits()));
    //
    // }
    //
    // @SuppressWarnings(
    // {"unchecked"})
    // public void testMultiplyQuantitySingleton()
    // {
    // ITemporalQuantityCollection<Velocity> tq1 =
    // new StockTypes.Temporal.SpeedMSec("Some data1", null);
    // tq1.add(100, 10);
    // tq1.add(200, 20);
    // tq1.add(300, 30);
    // tq1.add(400, 40);
    //
    // IQuantityCollection<Velocity> tq2 =
    // new StockTypes.NonTemporal.SpeedMSec("Some data2", null);
    // tq2.add(11);
    //
    // List<IStoreItem> selection = new ArrayList<IStoreItem>();
    // selection.add((IQuantityCollection<Velocity>) tq1);
    // selection.add((IQuantityCollection<Velocity>) tq2);
    //
    // StoreGroup store = new StoreGroup();
    // Collection<ICommand<IStoreItem>> commands =
    // new MultiplyQuantityOperation().actionsFor(selection, store, context);
    // ICommand<IStoreItem> firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 1, store.size());
    //
    // IQuantityCollection<Velocity> series =
    // (IQuantityCollection<Velocity>) firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 4, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    // assertEquals("correct value", 110d, series.getValues().get(0).doubleValue(
    // series.getUnits()));
    //
    // tq2.add(11);
    // commands =
    // new MultiplyQuantityOperation().actionsFor(selection, store, context);
    // assertEquals("no commands returned", 0, commands.size());
    //
    // }
    //
    // @SuppressWarnings(
    // {"unchecked", "rawtypes"})
    // public void testMultiplyQuantityTemporalInterp()
    // {
    // ITemporalQuantityCollection<?> tq1 =
    // new StockTypes.Temporal.SpeedMSec("Some data1", null);
    // tq1.add(100, 10);
    // tq1.add(200, 20);
    // tq1.add(300, 30);
    // tq1.add(400, 40);
    //
    // ITemporalQuantityCollection<?> tq2 =
    // new StockTypes.Temporal.SpeedMSec("Some data2", null);
    // tq2.add(220, 11);
    // tq2.add(340, 17);
    // tq2.add(440, 22);
    //
    // List<IStoreItem> selection = new ArrayList<IStoreItem>();
    // selection.add((IQuantityCollection<Quantity>) tq1);
    // selection.add((IQuantityCollection<Quantity>) tq2);
    //
    // StoreGroup store = new StoreGroup();
    // Collection<ICommand<IStoreItem>> commands =
    // new MultiplyQuantityOperation().actionsFor(selection, store, context);
    // ICommand<IStoreItem> firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 1, store.size());
    //
    // ICollection series = (ICollection) firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 4, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // ITemporalQuantityCollection<?> tq = (ITemporalQuantityCollection<?>) series;
    //
    // assertEquals("returned correct value", 10d, tq.interpolateValue(100,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 20d, tq.interpolateValue(200,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 450d, tq.interpolateValue(300,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 800d, tq.interpolateValue(400,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    //
    // // ok, mangle the second array a bit more
    // tq2 = new StockTypes.Temporal.SpeedMSec("Some data2", null);
    // tq2.add(20, 11);
    // tq2.add(340, 17);
    // tq2.add(440, 22);
    //
    // selection = new ArrayList<IStoreItem>();
    // selection.add((IQuantityCollection<Quantity>) tq1);
    // selection.add((IQuantityCollection<Quantity>) tq2);
    //
    // store = new StoreGroup();
    // commands =
    // new MultiplyQuantityOperation().actionsFor(selection, store, context);
    // firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 1, store.size());
    //
    // series = (ICollection) firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 4, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // tq = (ITemporalQuantityCollection<?>) series;
    //
    // assertEquals("returned correct value", 125d, tq.interpolateValue(100,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 287.5d, tq.interpolateValue(200,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 487.5d, tq.interpolateValue(300,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 800d, tq.interpolateValue(400,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    //
    // // ok, make the second array longer
    // tq2 = new StockTypes.Temporal.SpeedMSec("Some data2", null);
    // tq2.add(200, 11);
    // tq2.add(250, 13);
    // tq2.add(330, 17);
    // tq2.add(360, 19);
    // tq2.add(440, 22);
    //
    // selection = new ArrayList<IStoreItem>();
    // selection.add((IQuantityCollection<Quantity>) tq1);
    // selection.add((IQuantityCollection<Quantity>) tq2);
    //
    // store = new StoreGroup();
    // commands =
    // new MultiplyQuantityOperation().actionsFor(selection, store, context);
    // firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 1, store.size());
    //
    // series = (ICollection) firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 5, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // tq = (ITemporalQuantityCollection<?>) series;
    //
    // assertEquals("returned correct value", null, tq.interpolateValue(100,
    // InterpMethod.Linear));
    // assertEquals("returned correct value", 220d, tq.interpolateValue(200,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 472.5d, tq.interpolateValue(300,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 353d, tq.interpolateValue(400,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 187.5d, tq.interpolateValue(420,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 22d, tq.interpolateValue(440,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    //
    // }
    //
    // public void testSampleData()
    // {
    // IStore data = new SampleData().getData(10);
    // @SuppressWarnings("unchecked")
    // IQuantityCollection<Quantity> ranged =
    // (IQuantityCollection<Quantity>) data
    // .get(SampleData.RANGED_SPEED_SINGLETON);
    // assertNotNull("found series", ranged);
    //
    // QuantityRange<Quantity> range = ranged.getRange();
    // assertNotNull("found range", range);
    //
    // // check the range has values
    // assertEquals("correct values", 940d, range.getMinimum().doubleValue(
    // ranged.getUnits()), 0.1);
    // assertEquals("correct values", 1050d, range.getMaximum().doubleValue(
    // ranged.getUnits()), 0.1);
    // }
    //
    // public void testCreateTemporalObject()
    // {
    // // the target collection
    // TemporalObjectCollection<String> stringCollection =
    // new TemporalObjectCollection<String>("strings");
    //
    // for (int i = 1; i <= 12; i++)
    // {
    // // store the measurement
    // stringCollection.add(i, i + "aaa");
    // }
    //
    // // check it didn't get stored
    // assertEquals("correct number of samples", 12, stringCollection
    // .getValuesCount());
    //
    // IBaseTemporalCollection it = stringCollection;
    // assertEquals("correct start", 1, it.start());
    // assertEquals("correct finish", 12, it.finish());
    // assertEquals("correct duration", 11, it.duration());
    // assertEquals("correct start", 1.1d, it.rate(), 0.1);
    //
    // // ok, now check the iterator
    // long runningValueSum = 0;
    // long runningTimeSum = 0;
    // Iterator<Doublet<String>> iter = stringCollection.iterator();
    // while (iter.hasNext())
    // {
    // Doublet<String> doublet = iter.next();
    // runningValueSum += doublet.getObservation().length();
    // runningTimeSum += doublet.getTime();
    // }
    // assertEquals("values adds up", 51, runningValueSum);
    // assertEquals("times adds up", 78, runningTimeSum);
    //
    // boolean eThrown = false;
    // try
    // {
    // stringCollection.add("done");
    // }
    // catch (UnsupportedOperationException er)
    // {
    // eThrown = true;
    // }
    //
    // assertTrue("exception thrown for invalid add operation", eThrown);
    //
    // }
    //
    // public void testUnitlessQuantity()
    // {
    // // the units for this measurement
    // Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
    //
    // // the target collection
    // SpeedMSec speedCollection =
    // new StockTypes.NonTemporal.SpeedMSec("Speed", null);
    //
    // for (int i = 1; i <= 10; i++)
    // {
    // // create a measurement
    // double thisSpeed = i * 2;
    // Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);
    //
    // // store the measurement
    // speedCollection.add(speedVal);
    // }
    //
    // assertEquals("correct num of items", 10, speedCollection.getValuesCount());
    //
    // speedCollection.add(12);
    //
    // assertEquals("correct num of items", 11, speedCollection.getValuesCount());
    // }
    //
    // public void testCreateQuantity()
    // {
    // // the units for this measurement
    // Unit<Velocity> ms = METRE.divide(SECOND).asType(Velocity.class);
    //
    // // the target collection
    // SpeedMSec speedCollection =
    // new StockTypes.NonTemporal.SpeedMSec("Speed", null);
    //
    // for (int i = 1; i <= 10; i++)
    // {
    // // create a measurement
    // double thisSpeed = i * 2;
    // Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, ms);
    //
    // // store the measurement
    // speedCollection.add(speedVal);
    // }
    //
    // // check it didn't get stored
    // assertEquals("correct number of samples", 10, speedCollection
    // .getValuesCount());
    // assertEquals("correct name", "Speed", speedCollection.getName());
    //
    // assertEquals("correct min", 2d, speedCollection.min().doubleValue(
    // speedCollection.getUnits()));
    // assertEquals("correct max", 20d, speedCollection.max().doubleValue(
    // speedCollection.getUnits()));
    // assertEquals("correct mean", 11d, speedCollection.mean().doubleValue(
    // speedCollection.getUnits()));
    // assertEquals("correct variance", 33, speedCollection.variance()
    // .doubleValue(speedCollection.getUnits()), 0.1);
    // assertEquals("correct sd", 5.744, speedCollection.sd().doubleValue(
    // speedCollection.getUnits()), 0.001);
    // }
    //
    // public void testTemporalQuantityAddition()
    // {
    // // the units for this measurement
    // Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
    // Unit<Velocity> mSec = METRES_PER_SECOND;
    //
    // // the target collection
    // TemporalQuantityCollection<Velocity> sc =
    // new TemporalQuantityCollection<Velocity>("Speed", null, kmh);
    //
    // // create a measurement
    // Measurable<Velocity> speedVal = Measure.valueOf(5, kmh);
    //
    // // store the measurement
    // sc.add(12, speedVal);
    //
    // // check it got stored
    // assertEquals("correct number of samples", 1, sc.getValuesCount());
    //
    // long time = sc.getTimes().iterator().next();
    // Measurable<Velocity> theS = sc.getValues().iterator().next();
    //
    // assertEquals("correct time", 12, time);
    // assertEquals("correct speed value", 5d, theS.doubleValue(sc.getUnits()));
    // assertEquals("correct speed units", kmh, sc.getUnits());
    //
    // // ok, now add another
    // speedVal = Measure.valueOf(25, mSec);
    //
    // // store the measurement
    // Exception errorThrown = null;
    // try
    // {
    // sc.add(14, speedVal);
    // }
    // catch (Exception e)
    // {
    // errorThrown = e;
    // }
    //
    // // check the error got thrown
    // assertNotNull("runtime got thrown", errorThrown);
    //
    // // check it didn't get stored
    // assertEquals("correct number of samples", 1, sc.getValuesCount());
    //
    // // ok, now add another
    // speedVal = Measure.valueOf(12, kmh);
    //
    // // store the measurement
    // sc.add(14, speedVal);
    //
    // // check it got get stored
    // assertEquals("correct number of samples", 2, sc.getValuesCount());
    //
    // boolean eThrown = false;
    // try
    // {
    // sc.add(12);
    // }
    // catch (UnsupportedOperationException er)
    // {
    // eThrown = true;
    // }
    //
    // assertTrue("exception thrown for invalid add operation", eThrown);
    //
    // }
    //
    // public void testTimeQuantityCollectionIterator()
    // {
    // // the units for this measurement
    // Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
    //
    // // the target collection
    // TemporalQuantityCollection<Velocity> speedCollection =
    // new TemporalQuantityCollection<Velocity>("Speed", null, kmh);
    //
    // for (int i = 1; i <= 10; i++)
    // {
    // // create a measurement
    // double thisSpeed = i * 2;
    // Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);
    //
    // // store the measurement
    // speedCollection.add(i, speedVal);
    // }
    //
    // // check it didn't get stored
    // assertEquals("correct number of samples", 10, speedCollection
    // .getValuesCount());
    //
    // IBaseTemporalCollection it = speedCollection;
    // assertEquals("correct start", 1, it.start());
    // assertEquals("correct finish", 10, it.finish());
    // assertEquals("correct duration", 9, it.duration());
    // assertEquals("correct start", 1.1d, it.rate(), 0.1);
    //
    // // ok, now check the iterator
    // double runningValueSum = 0;
    // double runningTimeSum = 0;
    // Iterator<Doublet<Measurable<Velocity>>> iter = speedCollection.iterator();
    // while (iter.hasNext())
    // {
    // Doublet<Measurable<Velocity>> doublet = iter.next();
    // runningValueSum +=
    // doublet.getObservation().doubleValue(speedCollection.getUnits());
    // runningTimeSum += doublet.getTime();
    // }
    // assertEquals("values adds up", 110d, runningValueSum);
    // assertEquals("times adds up", 55d, runningTimeSum);
    //
    // assertEquals("correct mean", 11d, speedCollection.mean().doubleValue(
    // speedCollection.getUnits()));
    // assertEquals("correct variance", 33, speedCollection.variance()
    // .doubleValue(speedCollection.getUnits()), 0.1);
    // assertEquals("correct sd", 5.744, speedCollection.sd().doubleValue(
    // speedCollection.getUnits()), 0.001);
    //
    // }
    //
    // public void testQuantityCollectionIterator()
    // {
    // // the units for this measurement
    // Unit<Velocity> kmh = KILO(METRE).divide(HOUR).asType(Velocity.class);
    //
    // // the target collection
    // QuantityCollection<Velocity> speedCollection =
    // new QuantityCollection<Velocity>("Speed", null, kmh);
    //
    // for (int i = 1; i <= 10; i++)
    // {
    // // create a measurement
    // double thisSpeed = i * 2;
    // Measurable<Velocity> speedVal = Measure.valueOf(thisSpeed, kmh);
    //
    // // store the measurement
    // speedCollection.add(speedVal);
    // }
    //
    // // check it didn't get stored
    // assertEquals("correct number of samples", 10, speedCollection
    // .getValuesCount());
    //
    // // ok, now check the iterator
    // double runningValueSum = 0;
    // Iterator<Measurable<Velocity>> vIter =
    // speedCollection.getValues().iterator();
    // while (vIter.hasNext())
    // {
    // Measurable<Velocity> value = vIter.next();
    // runningValueSum += value.doubleValue(speedCollection.getUnits());
    // }
    // assertEquals("values adds up", 110d, runningValueSum);
    //
    // assertEquals("correct mean", 11d, speedCollection.mean().doubleValue(
    // speedCollection.getUnits()));
    // assertEquals("correct variance", 33, speedCollection.variance()
    // .doubleValue(speedCollection.getUnits()), 0.1);
    // assertEquals("correct sd", 5.744, speedCollection.sd().doubleValue(
    // speedCollection.getUnits()), 0.001);
    //
    // }
    //
    // @SuppressWarnings(
    // {"unchecked", "rawtypes"})
    // public void testSubtractQuantityTemporalInterp()
    // {
    // ITemporalQuantityCollection<?> tq1 =
    // new StockTypes.Temporal.SpeedMSec("Some data1", null);
    // tq1.add(100, 10);
    // tq1.add(200, 20);
    // tq1.add(300, 30);
    // tq1.add(400, 40);
    //
    // ITemporalQuantityCollection<?> tq2 =
    // new StockTypes.Temporal.SpeedMSec("Some data2", null);
    // tq2.add(220, 11);
    // tq2.add(340, 17);
    // tq2.add(440, 22);
    //
    // List<IQuantityCollection<Quantity>> selection =
    // new ArrayList<IQuantityCollection<Quantity>>();
    // selection.add((IQuantityCollection<Quantity>) tq1);
    // selection.add((IQuantityCollection<Quantity>) tq2);
    //
    // StoreGroup store = new StoreGroup();
    // Collection<ICommand<IQuantityCollection<Quantity>>> commands =
    // new SubtractQuantityOperation<>().actionsFor(selection, store, context);
    // ICommand<IQuantityCollection<Quantity>> firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 1, store.size());
    //
    // ICollection series = firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 4, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // ITemporalQuantityCollection<?> tq = (ITemporalQuantityCollection<?>) series;
    //
    // assertEquals("returned correct value", 10d, tq.interpolateValue(100,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 20d, tq.interpolateValue(200,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 15d, tq.interpolateValue(300,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 20d, tq.interpolateValue(400,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    //
    // // ok, mangle the second array a bit more
    // tq2 = new StockTypes.Temporal.SpeedMSec("Some data2", null);
    // tq2.add(20, 11);
    // tq2.add(340, 17);
    // tq2.add(440, 22);
    //
    // selection = new ArrayList<IQuantityCollection<Quantity>>();
    // selection.add((IQuantityCollection<Quantity>) tq1);
    // selection.add((IQuantityCollection<Quantity>) tq2);
    //
    // store = new StoreGroup();
    // commands =
    // new SubtractQuantityOperation<>().actionsFor(selection, store, context);
    // firstC = commands.iterator().next();
    //
    // assertEquals("store empty", 0, store.size());
    //
    // firstC.execute();
    //
    // assertEquals("new collection created", 1, store.size());
    //
    // series = firstC.getOutputs().iterator().next();
    // assertTrue("non empty", series.getValuesCount() > 0);
    // assertEquals("corrent length results", 4, series.getValuesCount());
    // assertTrue("temporal", series.isTemporal());
    // assertTrue("quantity", series.isQuantity());
    //
    // tq = (ITemporalQuantityCollection<?>) series;
    //
    // assertEquals("returned correct value", -2.5d, tq.interpolateValue(100,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 5.625d, tq.interpolateValue(200,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 13.75d, tq.interpolateValue(300,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));
    // assertEquals("returned correct value", 20d, tq.interpolateValue(400,
    // InterpMethod.Linear).doubleValue((Unit) tq.getUnits()));

}