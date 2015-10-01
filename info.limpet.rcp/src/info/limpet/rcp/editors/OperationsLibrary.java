package info.limpet.rcp.editors;

import info.limpet.ICollection;
import info.limpet.IOperation;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.DeleteCollectionOperation;
import info.limpet.data.operations.DivideQuantityOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SubtractQuantityOperation;
import info.limpet.data.operations.UnitConversionOperation;
import info.limpet.rcp.analysis_view.AnalysisView;
import info.limpet.rcp.data_frequency.DataFrequencyView;
import info.limpet.rcp.operations.ShowInNamedView;
import info.limpet.rcp.xy_plot.XyPlotView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tec.units.ri.unit.Units;

public class OperationsLibrary
{
	public static HashMap<String, List<IOperation<?>>> getOperations()
	{
		HashMap<String, List<IOperation<?>>> res = new HashMap<String, List<IOperation<?>>>();

		List<IOperation<?>> arithmetic = new ArrayList<IOperation<?>>();
		arithmetic.add(new MultiplyQuantityOperation());
		arithmetic.add(new AddQuantityOperation<>());
		arithmetic.add(new SubtractQuantityOperation<>());
		arithmetic.add(new DivideQuantityOperation());

		List<IOperation<?>> conversions = new ArrayList<IOperation<?>>();
		conversions.add(new UnitConversionOperation(Units.METRE));
		conversions.add(new UnitConversionOperation(Units.SECOND));
		conversions.add(new UnitConversionOperation(Units.MINUTE));
		conversions.add(new UnitConversionOperation(Units.METRES_PER_SECOND));
		conversions
				.add(new UnitConversionOperation(Units.METRES_PER_SQUARE_SECOND));
		conversions.add(new UnitConversionOperation(Units.CELSIUS));
		conversions.add(new UnitConversionOperation(Units.RADIAN));
		conversions.add(new UnitConversionOperation(StockTypes.DEGREE_ANGLE));

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

		res.put("Arithmetic", arithmetic);
		res.put("Conversions", conversions);
		res.put("Administration", admin);
		res.put("Analysis", analysis);
		return res;

	}
}
