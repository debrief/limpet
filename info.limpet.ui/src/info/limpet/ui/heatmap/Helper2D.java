package info.limpet.ui.heatmap;

import java.util.List;

import org.eclipse.january.MetadataException;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class Helper2D
{

  public static class HContainer
  {
    public double[] rowTitles;
    public double[] colTitles;
    public double[][] values;
  }

  public static HContainer convert(DoubleDataset dataset)
  {
    HContainer res = new HContainer();

    // ok, start by changing the table columns to the correct size
    // sort out the axes
    List<AxesMetadata> amList;
    try
    {
      amList = dataset.getMetadata(AxesMetadata.class);
      AxesMetadata am = amList.get(0);
      ILazyDataset[] axes = am.getAxes();
      if (axes.length != 2)
      {
        return null;
      }
      DoubleDataset aOne = (DoubleDataset) axes[0];
      DoubleDataset aTwo = (DoubleDataset) axes[1];

      double[] aIndices = aOne.getData();
      res.rowTitles = aIndices;
      double[] bIndices = aTwo.getData();
      res.colTitles = bIndices;

      res.values = new double[aIndices.length][bIndices.length];
      for (int i = 0; i < aIndices.length; i++)
      {
        for (int j = 0; j < bIndices.length; j++)
        {
          res.values[i][j] = dataset.get(i, j);
        }
      }

    }
    catch (MetadataException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return res;
  }
}
