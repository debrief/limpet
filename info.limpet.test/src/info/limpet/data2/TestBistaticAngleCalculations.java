package info.limpet.data2;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.MockContext;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.spatial.msa.BistaticAngleOperation;
import info.limpet.persistence.CsvParser;

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
    List<IStoreItem> items = parser.parse(file.getAbsolutePath());
    StoreGroup tx1 = new StoreGroup("tx1");
    tx1.addAll(items);
    store.add(tx1);

    file = TestCsvParser.getDataFile("multistatics/rx1_stat.csv");
    assertTrue(file.isFile());
    items = parser.parse(file.getAbsolutePath());
    StoreGroup rx1 = new StoreGroup("rx1");
    rx1.addAll(items);
    store.add(rx1);

    file = TestCsvParser.getDataFile("multistatics/ssn_stat.csv");
    assertTrue(file.isFile());
    items = parser.parse(file.getAbsolutePath());
    StoreGroup ssn = new StoreGroup("ssn");
    ssn.addAll(items);
    store.add(ssn);
    
    // check data loaded
    assertEquals("have all 3 tracks", 3, store.size());

    // ok, generate the command
    BistaticAngleOperation generator = new BistaticAngleOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tx1);
    selection.add(ssn);
    List<ICommand> actions = generator.actionsFor(selection, store, context);
    assertEquals("correct actions", 1, actions.size());
    
    
  }

}
