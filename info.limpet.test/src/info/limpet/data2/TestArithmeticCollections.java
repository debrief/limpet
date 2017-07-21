package info.limpet.data2;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.Document.InterpMethod;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
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
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.Maths;

public class TestArithmeticCollections extends TestCase
{

  private final IContext context = new MockContext();

  public void testAddQuantityTemporalInterp()
  {
    final NumberDocumentBuilder tqb1 =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tqb1.add(100, 10d);
    tqb1.add(230, 23d);
    tqb1.add(270, 27d);
    tqb1.add(300, 30d);
    tqb1.add(320, 32d);
    tqb1.add(400, 40d);

    final NumberDocumentBuilder tqb2 =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tqb2.add(220, 22d);
    tqb2.add(340, 34d);
    tqb2.add(440, 44d);

    final NumberDocument tq1 = tqb1.toDocument();
    final NumberDocument tq2 = tqb2.toDocument();

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    final StoreGroup store = new StoreGroup("data store");
    final Collection<ICommand> commands =
        new AddQuantityOperation().actionsFor(selection, store, context);
    final ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    final IDocument<?> series =
        (IDocument<?>) store.get("Sum of Some data1 + Some data2");
    assertTrue("non empty", series.size() > 0);
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());

    final NumberDocument tq = (NumberDocument) series;

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

  public void testMathOperatorsUnIndexed()
  {
    // check the unary operations work on non-indexed datasets

    final NumberDocumentBuilder nq2 =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, null);
    nq2.add(-20d);
    nq2.add(20d);
    nq2.add(-40d);

    final NumberDocument nq2d = nq2.toDocument();

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(nq2d);

    final StoreGroup store = new StoreGroup("Store");
    final UnaryQuantityOperation absOp = new AbsoluteOperator();
    final Collection<ICommand> commands =
        absOp.actionsFor(selection, store, context);

    assertEquals("have some commands", 1, commands.size());

    final ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());
    assertEquals("corrent num of outputs", 1, firstC.getOutputs().size());

    // get the first one.
    NumberDocument series = (NumberDocument) firstC.getOutputs().get(0);
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 3, series.size());
    assertFalse("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());
    assertEquals("no index units", null, series.getIndexUnits());

    // check some values
    assertEquals("value correct", 20d, series.getValueAt(0), 0.001);
    assertEquals("value correct", 20d, series.getValueAt(1), 0.001);
    assertEquals("value correct", 40d, series.getValueAt(2), 0.001);
  }

  final private static class ClearOperator extends UnaryQuantityOperation
  {

    public ClearOperator()
    {
      super("Clear units");
    }

    @Override
    protected boolean appliesTo(final List<IStoreItem> selection)
    {
      return true;
    }

    @Override
    public Dataset calculate(final Dataset input)
    {
      return input;
    }

    @Override
    protected String getUnaryNameFor(final String name)
    {
      return "Dimensionless " + name;
    }

    @Override
    protected Unit<?> getUnaryOutputUnit(final Unit<?> first)
    {
      return Dimensionless.UNIT;
    }
  };


  
  final private static class AbsoluteOperator extends UnaryQuantityOperation
  {

    public AbsoluteOperator()
    {
      super("Abs");
    }

    @Override
    protected boolean appliesTo(final List<IStoreItem> selection)
    {
      // check it's numerical
      return true;
    }

    @Override
    public Dataset calculate(final Dataset input)
    {
      return Maths.abs(input);
    }

    @Override
    protected String getUnaryNameFor(final String name)
    {
      return "Absolute value of " + name;
    }

    @Override
    protected Unit<?> getUnaryOutputUnit(final Unit<?> first)
    {
      return first;
    }
  };

  public void testMathOperatorsIndexed()
  {
    final NumberDocumentBuilder tq1 =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1.add(100, 10d);
    tq1.add(200, -20d);
    tq1.add(300, 30d);
    tq1.add(400, -20d);

    final NumberDocument tq1d = tq1.toDocument();

    final NumberDocumentBuilder tq2 =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq2.add(220, -11d);
    tq2.add(340, -17d);
    tq2.add(440, -22d);

    final NumberDocument tq2d = tq2.toDocument();

    final NumberDocumentBuilder nq1 =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, null);
    nq1.add(10d);
    nq1.add(-20d);
    nq1.add(30d);
    nq1.add(-20d);

    final NumberDocument nq1d = nq1.toDocument();

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1d);
    selection.add(tq2d);

    final StoreGroup store = new StoreGroup("Store");
    final UnaryQuantityOperation absOp = new AbsoluteOperator();
    final Collection<ICommand> commands =
        absOp.actionsFor(selection, store, context);

    assertEquals("have some commands", 1, commands.size());

    final ICommand firstC = commands.iterator().next();

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
    assertEquals("correct index units", SampleData.MILLIS, series.getIndexUnits());

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
    final UnaryQuantityOperation clearU = new ClearOperator();
    assertEquals("previous type:", "[L]/[T]", tq1d.getUnits().getDimension()
        .toString());

    selection.clear();
    selection.add(tq1d);
    store.clear();

    Collection<ICommand> ops = clearU.actionsFor(selection, store, context);
    ICommand command = ops.iterator().next();
    command.execute();

    final NumberDocument output =
        (NumberDocument) command.getOutputs().iterator().next();

    assertEquals("new type:", "", output.getUnits().getDimension().toString());
    assertEquals("same size", output.size(), tq1d.size());
    assertEquals("first item same value", output.getValueAt(0), tq1d
        .getValueAt(0));
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

    final NumberDocument output2 =
        (NumberDocument) command.getOutputs().iterator().next();

    assertEquals("new type:", "", output2.getUnits().getDimension().toString());
    assertEquals("same size", output2.size(), nq1d.size());
    assertEquals("first item same value", output2.getValueAt(0), nq1d
        .getValueAt(0), 0.001);
  }

  public void testMultiplyQuantitySingleton()
  {

    final NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    final NumberDocument tq1 = tq1b.toDocument();

    final NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, null);
    tq2b.add(11d);

    final NumberDocument tq2 = tq2b.toDocument();

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    final StoreGroup store = new StoreGroup("Store");
    Collection<ICommand> commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    final ICommand firstC = commands.iterator().next();

    assertEquals("store empty", 0, store.size());

    firstC.execute();

    assertEquals("new collection created", 1, store.size());

    final NumberDocument series =
        (NumberDocument) firstC.getOutputs().iterator().next();
    assertTrue("non empty", series.size() > 0);
    assertEquals("corrent length results", 4, series.size());
    assertTrue("temporal", series.isIndexed());
    assertTrue("quantity", series.isQuantity());
    assertEquals("correct value", 110d, series.getValueAt(0));

    tq2b.add(11d);

    final NumberDocument tq3 = tq2b.toDocument();

    selection.remove(tq2);
    selection.add(tq3);

    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    assertEquals("no commands returned", 0, commands.size());

  }

  public void testMultiplyQuantityTemporalInterp()
  {
    final NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    final NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    tq2b.add(220, 11d);
    tq2b.add(340, 17d);
    tq2b.add(440, 22d);

    final NumberDocument tq1 = tq1b.toDocument();
    NumberDocument tq2 = tq2b.toDocument();

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
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
      final Dataset oDataset =
          DatasetUtils.sliceAndConvertLazyDataset(series.getDataset());
      System.out.println(oDataset.toString(true));
    }
    catch (final DatasetException e)
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

  public void testSubtractQuantityTemporalInterp()
  {
    final NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    final NumberDocument tq1 = tq1b.toDocument();

    final NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq2b.add(220, 11d);
    tq2b.add(340, 17d);
    tq2b.add(440, 22d);

    NumberDocument tq2 = tq2b.toDocument();

    final List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    final StoreGroup store = new StoreGroup("Output");
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

  public void testTemporalQuantityInterp()
  {
    final NumberDocumentBuilder speeds =
        new NumberDocumentBuilder("Speeds", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);

    speeds.add(100, 10d);
    speeds.add(200, 20d);
    speeds.add(300, 30d);
    speeds.add(400, 40d);

    final NumberDocument tq = speeds.toDocument();

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

}
