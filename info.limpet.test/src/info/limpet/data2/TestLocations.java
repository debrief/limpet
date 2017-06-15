package info.limpet.data2;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.ProplossBetweenTwoTracksOperation;
import info.limpet.persistence.CsvParser;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.january.metadata.AxesMetadata;
import org.junit.Test;

public class TestLocations extends TestCase
{
  final private IContext context = new MockContext();
  
  private static final String DOC_NAME = "some input";

  @Test
  public void testSingleton() throws IOException
  {
    IStoreGroup store = new StoreGroup("data");

    // now get our tracks
    File file = TestCsvParser.getDataFile("multistatics/tx1_stat.csv");
    assertTrue(file.isFile());
    CsvParser parser = new CsvParser();
    List<IStoreItem> tx1 = parser.parse(file.getAbsolutePath());
    store.addAll(tx1);

    file = TestCsvParser.getDataFile("multistatics/rx1_stat_single.csv");
    assertTrue(file.isFile());
    List<IStoreItem> rx1 = parser.parse(file.getAbsolutePath());
    store.addAll(rx1);

    file = TestCsvParser.getDataFile("multistatics/ssn_stat.csv");
    assertTrue(file.isFile());
    List<IStoreItem> ssn = parser.parse(file.getAbsolutePath());
    store.addAll(ssn);
    
    // check they're all in
    assertEquals("Loaded all files", 3, store.size());
    
    IOperation pDiff = new ProplossBetweenTwoTracksOperation();
    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(rx1.get(0));
    selection.add(ssn.get(0));
    List<ICommand> actions = pDiff.actionsFor(selection , store, context);  
    
    assertEquals("got actions", 2, actions.size());
    
    // ok, run it
    actions.get(0).execute();
    
    assertEquals("Got new data", 4, store.size());
    
    NumberDocument res = (NumberDocument) actions.get(0).getOutputs().get(0);
    System.out.println(res.toListing());
    
  }
  
  public void testNonIndexedData()
  {
    LocationDocumentBuilder builder =
        new LocationDocumentBuilder(DOC_NAME, null, null);
    for (int i = 0; i < 10; i++)
    {
      Point2D newP = GeoSupport.getCalculator().createPoint(i, i * 2);
      builder.add(newP);
    }

    LocationDocument res = builder.toDocument();
    assertNotNull("document produced", res);
    assertEquals("correct name", DOC_NAME, res.getName());
    assertEquals("has points", 10, res.getDataset().getSize());
    assertNull("doesn't have indices", res.getDataset().getFirstMetadata(
        AxesMetadata.class));
    
    String listing = res.toListing();
    assertNotNull(listing);
  }

  public void testIndexedData()
  {
    LocationDocumentBuilder builder =
        new LocationDocumentBuilder(DOC_NAME, null, SampleData.MILLIS);
    for (int i = 0; i < 10; i++)
    {
      Point2D newP = GeoSupport.getCalculator().createPoint(i, i * 2);
      builder.add((long) (i * 100000), newP);
    }

    LocationDocument res = builder.toDocument();
    assertNotNull("document produced", res);
    assertEquals("correct name", DOC_NAME, res.getName());
    assertEquals("has points", 10, res.getDataset().getSize());
    assertNotNull("has indices", res.getDataset().getFirstMetadata(
        AxesMetadata.class));
    
    String listing = res.toListing();
    assertNotNull(listing);
  }

}
