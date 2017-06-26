package info.limpet.ui.xy_plot;

import org.eclipse.january.dataset.DoubleDataset;

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
    return new HContainer();
  }
}
