package info.limpet.rcp.editors;

import info.limpet.ICollection;
import info.limpet.IOperation;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.DeleteCollectionOperation;
import info.limpet.data.operations.DivideQuantityOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SubtractQuantityOperation;
import info.limpet.rcp.analysis_view.AnalysisView;
import info.limpet.rcp.data_frequency.DataFrequencyView;
import info.limpet.rcp.operations.ShowInNamedView;
import info.limpet.rcp.xy_plot.XyPlotView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OperationsLibrary
{
	public static HashMap<String, List<IOperation<?>>> getOperations()
	{
		HashMap<String, List<IOperation<?>>> res = new HashMap<String, List<IOperation<?>>>();

		List<IOperation<?>> ops = new ArrayList<IOperation<?>>();
		ops.add(new MultiplyQuantityOperation());
		ops.add(new AddQuantityOperation<>());
		ops.add(new SubtractQuantityOperation<>());
		ops.add(new DivideQuantityOperation());

		List<IOperation<?>> admin = new ArrayList<IOperation<?>>();
		admin.add(new DeleteCollectionOperation());

		List<IOperation<?>> analysis = new ArrayList<IOperation<?>>();

		analysis.add(new ShowInNamedView("Show in XY Plot View", XyPlotView.ID)
		{
			protected boolean appliesTo(List<ICollection> selection)
			{
				return getTests().allQuantity(selection);
			}
		});
		analysis.add(new ShowInNamedView("Show in Data Frequency View",
				DataFrequencyView.ID));
		analysis.add(new ShowInNamedView("Show in Analysis View", AnalysisView.ID));

		res.put("Arithmetic", ops);
		res.put("Administration", admin);
		res.put("Analysis", analysis);
		return res;

	}
}
