package info.limpet.data2;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.grid.GenerateGrid;
import info.limpet.operations.grid.GenerateGrid.GenerateGridCommand;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.junit.Test;

public class TestGrids extends TestCase
{
  private IContext context = new MockContext();

  @Test
  public void testBinning()
  {
    double[] bins = new double[]
    {20, 40, 60, 80, 100};
    GenerateGridCommand gen =
        new GenerateGrid.GenerateGridCommand("title", "desc", null, null,
            context, null, bins, null);
    assertEquals("correct bin", 1, gen.binFor(bins, 25));
    assertEquals("correct bin", 0, gen.binFor(bins, 15));
    assertEquals("correct bin", 2, gen.binFor(bins, 60));
    assertEquals("correct bin", -1, gen.binFor(bins, 105));
  }

  @Test
  public void testOperations()
  {
    GenerateGrid gen = new GenerateGrid();
    StoreGroup store = new StoreGroup("Store");
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    List<ICommand> ops = gen.actionsFor(selection, store, context);
    assertEquals("empty ops", 0, ops.size());

    // ok, create some real docs
    NumberDocumentBuilder ang1 =
        new NumberDocumentBuilder("ang_1", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder ang2 =
        new NumberDocumentBuilder("ang_2", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder other1 =
        new NumberDocumentBuilder("other1", SI.METER, null, SI.SECOND);

    // put some data into them
    for (int i = 0; i < 25; i++)
    {
      ang1.add(i * 10000, i * 13d);
      ang2.add(i * 10000, i * 12d);
      other1.add(i * 10000, 100 * Math.sin(i));
    }

    selection.clear();
    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());
    selection.add(other1.toDocument());

    ops = gen.actionsFor(selection, store, context);
    assertEquals("Perm created", 2, ops.size());

    // ok, now execute it
    final GenerateGridCommand thisOp = (GenerateGridCommand) ops.get(0);
    thisOp.execute();

    double[] bins = thisOp.binsFor(ang1.toDocument());
    assertEquals("correct num bins", 8, bins.length);

    assertEquals("output produced", 1, thisOp.getOutputs().size());
    Document<?> output = thisOp.getOutputs().get(0);
    assertNotNull("Output produced", output);
    System.out.println(output.toListing());
    
    store.clear();
    final GenerateGridCommand thisOp2 = (GenerateGridCommand) ops.get(1);
    thisOp2.execute();
    assertEquals("output produced", 1, thisOp2.getOutputs().size());
    output = thisOp2.getOutputs().get(0);
    assertNotNull("Output produced", output);
    System.out.println(output.toListing());
  }

  @Test
  public void testGenActionsSameDims()
  {
    GenerateGrid gen = new GenerateGrid();
    StoreGroup store = new StoreGroup("Store");
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    List<ICommand> ops = gen.actionsFor(selection, store, context);
    assertEquals("empty ops", 0, ops.size());

    // ok, create some real docs
    NumberDocumentBuilder ang1 =
        new NumberDocumentBuilder("ang_1", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder ang2 =
        new NumberDocumentBuilder("ang_2", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder ang3 =
        new NumberDocumentBuilder("ang_3", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);

    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());
    selection.add(ang3.toDocument());

    assertEquals("still empty", 0, gen.actionsFor(selection, store, context)
        .size());

    // put some data into them

    for (int i = 0; i < 5; i++)
    {
      ang1.add(i * 10000, i * 3d);
      ang2.add(i * 10000, i * 2d);

      if (i < 4)
      {
        ang3.add(i * 10000, Math.sin(i));
      }
    }

    selection.clear();
    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());

    assertEquals("still empty", 0, gen.actionsFor(selection, store, context)
        .size());

    // and add the third dataset
    selection.add(ang3.toDocument());

    assertEquals("still empty", 0, gen.actionsFor(selection, store, context)
        .size());

    // make the third dataset the correct length
    ang3.add(4 * 10000, Math.sin(4));

    selection.clear();
    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());
    selection.add(ang3.toDocument());

    ops = gen.actionsFor(selection, store, context);
    assertEquals("3 perms created", 6, ops.size());

  }

  @Test
  public void testGenActionsDiffDims()
  {
    GenerateGrid gen = new GenerateGrid();
    StoreGroup store = new StoreGroup("Store");
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    List<ICommand> ops = gen.actionsFor(selection, store, context);
    assertEquals("empty ops", 0, ops.size());

    // ok, create some real docs
    NumberDocumentBuilder ang1 =
        new NumberDocumentBuilder("ang_1", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder ang2 =
        new NumberDocumentBuilder("ang_2", SampleData.DEGREE_ANGLE, null,
            SI.SECOND);
    NumberDocumentBuilder other1 =
        new NumberDocumentBuilder("other1", SI.METER, null, SI.SECOND);

    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());
    selection.add(other1.toDocument());

    assertEquals("still empty", 0, gen.actionsFor(selection, store, context)
        .size());

    // put some data into them

    for (int i = 0; i < 5; i++)
    {
      ang1.add(i * 10000, i * 3d);
      ang2.add(i * 10000, i * 2d);

      if (i < 4)
      {
        other1.add(i * 10000, Math.sin(i));
      }
    }

    selection.clear();
    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());

    assertEquals("still empty", 0, gen.actionsFor(selection, store, context)
        .size());

    // and add the third dataset
    selection.add(other1.toDocument());

    assertEquals("still empty", 0, gen.actionsFor(selection, store, context)
        .size());

    // make the third dataset the correct length
    other1.add(4 * 10000, Math.sin(4));

    selection.clear();
    selection.add(ang1.toDocument());
    selection.add(ang2.toDocument());
    selection.add(other1.toDocument());

    ops = gen.actionsFor(selection, store, context);
    assertEquals("Perm created", 2, ops.size());
  }

}
