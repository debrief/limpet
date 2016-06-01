package info.limpet.ui.editors;

import info.limpet.IOperation;
import info.limpet.IStoreItem;
import info.limpet.ui.analysis_view.AnalysisView;
import info.limpet.ui.data_frequency.DataFrequencyView;
import info.limpet.ui.operations.ShowInNamedView;
import info.limpet.ui.operations.ShowInStackedChartsOverview;
import info.limpet.ui.operations.ShowInTacticalOverview;
import info.limpet.ui.range_slider.RangeSliderView;
import info.limpet.ui.time_frequency.TimeFrequencyView;
import info.limpet.ui.xy_plot.XyPlotView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RCPOperationsLibrary
{
  
  /** protected constructor, to prevent accidental initialisation
   * 
   */
  protected RCPOperationsLibrary()
  {
    
  }
  
  public static HashMap<String, List<IOperation<?>>> getOperations()
  {
    HashMap<String, List<IOperation<?>>> res =
        new HashMap<String, List<IOperation<?>>>();

    res.put("Analysis", getAnalysis());

    return res;

  }

  private static List<IOperation<?>> getAnalysis()
  {
    List<IOperation<?>> analysis = new ArrayList<IOperation<?>>();
    analysis.add(new ShowInNamedView("Show in XY Plot View", XyPlotView.ID)
    {
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getTests().nonEmpty(selection)
            && (getTests().allQuantity(selection) || getTests().allLocation(
                selection));
      }
    });
    analysis.add(new ShowInNamedView("Show in Time Frequency View",
        TimeFrequencyView.ID));
    analysis.add(new ShowInNamedView("Show in Data Frequency View",
        DataFrequencyView.ID));
    analysis.add(new ShowInNamedView("Show in Analysis View", AnalysisView.ID));
    analysis
        .add(new ShowInNamedView("Show in Range Slider", RangeSliderView.ID));
    analysis.add(new ShowInTacticalOverview("Show in Tactical Overview"));
    analysis.add(new ShowInStackedChartsOverview("Show in Stacked Charts"));
    
    return analysis;
  }
}
