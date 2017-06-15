package info.limpet.data2;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.grid.GenerateGrid;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.junit.Test;

public class TestGrids extends TestCase
{
  private IContext context = new MockContext();

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
    assertEquals("3 perms created", 3, ops.size());

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
        new NumberDocumentBuilder("other1", SI.METER, null,
            SI.SECOND);

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
    assertEquals("3 perms created", 1, ops.size());
  }  
  
}
