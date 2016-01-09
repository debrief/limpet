package info.limpet.data.operations.spatial;

public class GeoSupport
{
  private static final IGeoCalculator CALC = new GeotoolsCalculator(); 
  
  /**
   * protected constructor, to prevent inadvertent instantiation
   * 
   */
  protected GeoSupport()
  {
  }

  public static void setCalculator(IGeoCalculator calculator)
  {

  }

  public static IGeoCalculator getCalculator()
  {
    return CALC;
  }
}
