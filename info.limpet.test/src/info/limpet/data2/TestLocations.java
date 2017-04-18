package info.limpet.data2;

import info.limpet2.LocationDocument;
import info.limpet2.LocationDocumentBuilder;

import java.awt.geom.Point2D;

import org.eclipse.january.metadata.AxesMetadata;

import junit.framework.TestCase;

public class TestLocations extends TestCase
{

  private static final String DOC_NAME = "some input";

  public void testNonIndexedData()
  {
    LocationDocumentBuilder builder = new LocationDocumentBuilder(DOC_NAME, null);
    for(int i=0;i<10;i++)
    {
      Point2D newP = new Point2D.Double(i,  i*2);
      builder.add(newP);
    }
    
    LocationDocument res = builder.toDocument();
    assertNotNull("document produced", res);
    assertEquals("correct name",  DOC_NAME, res.getName());
    assertEquals("has points", 10, res.getDataset().getSize());
    assertNull("doesn't have indices", res.getDataset().getFirstMetadata(AxesMetadata.class));
  }
  
  public void testIndexedData()
  {
    LocationDocumentBuilder builder = new LocationDocumentBuilder(DOC_NAME, null);
    for(int i=0;i<10;i++)
    {
      Point2D newP = new Point2D.Double(i,  i*2);
      builder.add(newP, (long)(i * 100000));
    }
    
    LocationDocument res = builder.toDocument();
    assertNotNull("document produced", res);
    assertEquals("correct name",  DOC_NAME, res.getName());
    assertEquals("has points", 10, res.getDataset().getSize());
    assertNotNull("has indices", res.getDataset().getFirstMetadata(AxesMetadata.class));
  }

}
