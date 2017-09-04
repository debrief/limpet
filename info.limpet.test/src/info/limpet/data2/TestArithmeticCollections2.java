package info.limpet.data2;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocument;
import info.limpet.impl.StringDocumentBuilder;
import info.limpet.operations.arithmetic.simple.RateOfChangeOperation;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SI;

import junit.framework.TestCase;

public class TestArithmeticCollections2 extends TestCase
{
  final private IContext context = new MockContext();

  public void testRateOfChangeAppliesTo()
  {
    final StoreGroup group = new StoreGroup("data");
    final List<IStoreItem> selection = new ArrayList<IStoreItem>();

    final RateOfChangeOperation op = new RateOfChangeOperation();
    final List<ICommand> res = op.actionsFor(selection, group, context);

    assertEquals("empty for empty list", 0, res.size());

    StringDocumentBuilder sdb =
        new StringDocumentBuilder("strings", null, null);
    sdb.add("aa");
    StringDocument sDoc = sdb.toDocument();
    selection.clear();
    selection.add(sDoc);

    assertEquals("empty for string doc", 0, op.actionsFor(selection, group,
        context).size());

    sdb = new StringDocumentBuilder("strings", null, SI.MILLIMETER);
    sdb.add(200, "aa");
    sDoc = sdb.toDocument();
    selection.clear();
    selection.add(sDoc);

    assertEquals("empty for indexed string doc", 0, op.actionsFor(selection,
        group, context).size());

    NumberDocumentBuilder ndb =
        new NumberDocumentBuilder("number", SI.METER, null, null);
    ndb.add(22d);
    selection.clear();
    selection.add(ndb.toDocument());

    assertEquals("empty for non indexed number doc", 0, op.actionsFor(
        selection, group, context).size());

    ndb = new NumberDocumentBuilder("number", SI.METER, null, SI.SECOND);
    ndb.add(200, 22d);
    selection.clear();
    selection.add(ndb.toDocument());

    assertEquals("empty for singleton doc", 0, op.actionsFor(selection, group,
        context).size());

    ndb.add(400, 3d);
    selection.clear();
    selection.add(ndb.toDocument());

    assertEquals("non-empty for doc with contents", 1, op.actionsFor(selection,
        group, context).size());

  }

  public void testRateOfChangeCalculation()
  {
    final StoreGroup group = new StoreGroup("data");
    final List<IStoreItem> selection = new ArrayList<IStoreItem>();

    final RateOfChangeOperation op = new RateOfChangeOperation();

    final NumberDocumentBuilder ndb =
        new NumberDocumentBuilder("number", SI.METER, null, SI.SECOND);
    ndb.add(1, 22d);
    ndb.add(2, 32d);
    ndb.add(3, 37d);
    ndb.add(4, 47d);
    selection.clear();
    final NumberDocument numberDoc = ndb.toDocument();
    selection.add(numberDoc);

    final List<ICommand> res = op.actionsFor(selection, group, context);
    assertEquals("non-empty for doc with contents", 1, res.size());

    res.get(0).execute();

    final NumberDocument output =
        (NumberDocument) res.get(0).getOutputs().get(0);
    assertNotNull("output produced", output);
    assertTrue("is indexed", output.isIndexed());
    assertTrue("is quantity", output.isQuantity());
    assertEquals("correct size", 3, output.size());
    assertEquals("correct index", SI.SECOND, output.getIndexUnits());

    assertEquals("correct rate", 10d, output.getValueAt(0), 0.001);
    assertEquals("correct rate", 5d, output.getValueAt(1), 0.001);
    assertEquals("correct rate", 10d, output.getValueAt(2), 0.001);
  }
}
