package info.limpet.data2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.spatial.msa.BistaticAngleOperation;
import info.limpet.persistence.CsvParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class TestBistaticAngleCalculations
{
  final private IContext context = new MockContext();

  @Test
  public void testLoadTracks() throws IOException
  {
    File file = TestCsvParser.getDataFile("multistatics/tx1_stat.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    assertEquals("correct group", 1, items.size());
    StoreGroup group = (StoreGroup) items.get(0);
    assertEquals("correct num collections", 3, group.size());
    IDocument<?> firstColl = (IDocument<?>) group.get(0);
    assertEquals("correct num rows", 541, firstColl.size());
  }

  @Test
  public void testCreateActions() throws IOException
  {
    IStoreGroup store = new StoreGroup("data");

    // now get our tracks
    File file = TestCsvParser.getDataFile("multistatics/tx1_stat.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> tx1 = parser.parse(file.getAbsolutePath());
    store.addAll(tx1);

    file = TestCsvParser.getDataFile("multistatics/rx1_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> rx1 = parser.parse(file.getAbsolutePath());
    store.addAll(rx1);

    file = TestCsvParser.getDataFile("multistatics/ssn_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> ssn = parser.parse(file.getAbsolutePath());
    store.addAll(ssn);

    // check data loaded
    assertEquals("have all 3 tracks", 3, store.size());

    // ok, generate the command
    BistaticAngleOperation generator = new BistaticAngleOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();

    // just one track
    selection.add(store.iterator().next());

    List<ICommand> actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 0, actions.size());

    // and two tracks
    selection.clear();
    selection.add(tx1.get(0));
    selection.add(ssn.get(0));
    selection.add(rx1.get(0));

    actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 3, actions.size());

    // check the store contents
    assertEquals("correct datasets", 3, store.size());

    // remove the course from one track, check it doesn't get offered
    IStoreGroup txGroup = (IStoreGroup) store.iterator().next();
    Iterator<IStoreItem> t1Iter = txGroup.iterator();
    IStoreItem toDelete = null;
    while (t1Iter.hasNext())
    {
      IStoreItem thisDoc = (IStoreItem) t1Iter.next();
      if (thisDoc instanceof NumberDocument)
      {
        NumberDocument nd = (NumberDocument) thisDoc;
        if (nd.isQuantity())
        {
          if (nd.getUnits().equals(SampleData.DEGREE_ANGLE))
          {
            toDelete = nd;
          }
        }
      }
    }
    assertNotNull("found course data", toDelete);
    txGroup.remove(toDelete);

    actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 2, actions.size());

    // ok, have a go at running it.
    actions.get(0).execute();

    // check the store contents
    assertEquals("correct datasets", 4, store.size());

  }
}
