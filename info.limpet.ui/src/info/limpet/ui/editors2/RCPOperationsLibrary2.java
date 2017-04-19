package info.limpet.ui.editors2;

import info.limpet2.IOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RCPOperationsLibrary2
{
  
  /** protected constructor, to prevent accidental initialisation
   * 
   */
  protected RCPOperationsLibrary2()
  {
    
  }
  
  public static HashMap<String, List<IOperation>> getOperations()
  {
    HashMap<String, List<IOperation>> res =
        new HashMap<String, List<IOperation>>();

    res.put("Analysis", getAnalysis());

    return res;

  }

  private static List<IOperation> getAnalysis()
  {
    List<IOperation> analysis = new ArrayList<IOperation>();
//    analysis.add(new ShowInNamedView("Show in XY Plot View", XyPlotView.ID)
//    {
//      protected boolean appliesTo(List<IStoreItem> selection)
//      {
//        return getTests().nonEmpty(selection)
//            && (getTests().allQuantity(selection) || getTests().allLocation(
//                selection));
//      }
//    });
//    analysis.add(new ShowInNamedView("Show in Time Frequency View",
//        TimeFrequencyView.ID));
//    analysis.add(new ShowInNamedView("Show in Data Frequency View",
//        DataFrequencyView.ID));
//    analysis.add(new ShowInNamedView("Show in Analysis View", AnalysisView.ID));
//    analysis
//        .add(new ShowInNamedView("Show in Range Slider", RangeSliderView.ID));
//    analysis.add(new ShowInTacticalOverview("Show in Tactical Overview"));
//    analysis.add(new ShowInStackedChartsOverview("Show in Stacked Charts"));
// 
    // TODO: create Limpet2 versions of the above actions
    return analysis;
  }
}
