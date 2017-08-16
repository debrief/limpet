package info.limpet.data2;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.arithmetic.simple.AddQuantityOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;

import junit.framework.TestCase;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.Slice;

public class TestNewStrategy extends TestCase
{
  private static final String D_NAME = "d-name";

  public void testGeneral()
  {
    double[] items = new double[]
    {12d, 13d, 14d, 15d};
    DoubleDataset ds = (DoubleDataset) DatasetFactory.createFromObject(items);
    ds.setName(D_NAME);

    assertNotNull("failed to create", ds);
    NumberDocument nd =
        new NumberDocument(ds, null, METRE.asType(Length.class));

    assertEquals("correct name", D_NAME, nd.getName());
    assertEquals("correct units", "m", nd.getUnits().toString());
    assertEquals("correct dimension", "[L]", nd.getUnits().getDimension()
        .toString());

    Iterator<Double> iter = nd.getIterator();
    while (iter.hasNext())
    {
      Double double1 = (Double) iter.next();
      System.out.println("value:" + double1);
    }

  }

  public void testSingletonDocument()
  {
    NumberDocumentBuilder builder =
        new NumberDocumentBuilder("some data", null, null, null);
    builder.add(1d);
    NumberDocument doc = builder.toDocument();
    double val = doc.getValueAt(0);
    assertEquals("correct value", 1d, val);
  }

  public void testAddingDocuments()
  {
    StoreGroup data = new SampleData().getData(15);

    IStoreItem doc1 = data.get(SampleData.SPEED_ONE);
    IStoreItem doc2 = data.get(SampleData.SPEED_TWO);

    assertNotNull("found sample data", doc1);

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add((IDocument<?>) doc1);
    selection.add((IDocument<?>) doc2);

    IOperation adder = new AddQuantityOperation();
    IStoreGroup target = new StoreGroup("Destination");
    IContext context = new MockContext();
    Collection<ICommand> actions = adder.actionsFor(selection, target, context);

    assertEquals("target empty", 0, target.size());
    assertEquals("correct num actions", 1, actions.size());

    ICommand addAction = actions.iterator().next();
    addAction.execute();

    assertEquals("target has data", 1, target.size());

    IDocument<?> output = (IDocument<?>) target.iterator().next();

    System.out.println(output);

    assertEquals("correct name", "Speed One Time + Speed Two Time", output
        .getName());
    assertEquals("correct parent", "Destination", output.getParent().getName());

  }

  public void testGenerateSampleData()
  {
    StoreGroup data = new SampleData().getData(10);
    assertEquals("top level items created", 25, data.size());
  }

  public void testIterators()
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

    NumberDocument doc = tqb1.toDocument();
    Iterator<Double> indIter = doc.getIndexIterator();
    double sum = 0;
    while (indIter.hasNext())
    {
      sum += indIter.next();
    }

    assertEquals("correct total", 1620d, sum, 0.001);

    // hmm, check we correcly step through sliced dataset
    DoubleDataset ds = (DoubleDataset) doc.getDataset();

    // do a slice
    DoubleDataset sliced = (DoubleDataset) ds.getSliceView(new Slice(2, 4));
    NumberDocument slicedDoc = new NumberDocument(sliced, null, null);

    indIter = slicedDoc.getIndexIterator();
    sum = 0;
    while (indIter.hasNext())
    {
      sum += indIter.next();
    }

    assertEquals("correct total", 570d, sum, 0.001);
  }
}
