package info.limpet.data2;

import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.operations.spatial.GeoSupport;

import java.awt.geom.Point2D;

import junit.framework.TestCase;

import org.eclipse.january.metadata.AxesMetadata;

public class TestLocations extends TestCase
{

  private static final String DOC_NAME = "some input";

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
  }

  public void testIndexedData()
  {
    LocationDocumentBuilder builder =
        new LocationDocumentBuilder(DOC_NAME, null, SampleData.M_SEC);
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
  }

}
