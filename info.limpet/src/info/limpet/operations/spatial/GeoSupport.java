package info.limpet.operations.spatial;

public class GeoSupport
{
  private static IGeoCalculator calc;

  /**
   * protected constructor, to prevent inadvertent instantiation
   * 
   */
  protected GeoSupport()
  {
  }

  public static void setCalculator(IGeoCalculator calculator)
  {
    calc = calculator;
  }

  public static IGeoCalculator getCalculator()
  {
    if (calc == null)
    {
      calc = new GeotoolsCalculator();
      System.err.println("ERROR: geospatial calculator not assigned");
    }

    return calc;
  }
}
