package info.limpet.rcp.product;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import info.limpet.rcp.analysis_view.AnalysisView;
import info.limpet.rcp.data_frequency.DataFrequencyView;
import info.limpet.rcp.xy_plot.XyPlotView;

public class Perspective implements IPerspectiveFactory
{
	public static final String ID = "info.limpet.product.perspective";

	@SuppressWarnings("deprecation")
	public void createInitialLayout(IPageLayout layout)
	{
		final String editorArea = layout.getEditorArea();

		final IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.25f, editorArea);

		topLeft.addView(IPageLayout.ID_RES_NAV);
		final IFolderLayout bottom = layout
				.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
		
		bottom.addView(AnalysisView.ID);
		bottom.addView(DataFrequencyView.ID);
		bottom.addView(IPageLayout.ID_PROP_SHEET);

		final IFolderLayout bottom2 = layout
				.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
		bottom2.addView(XyPlotView.ID);

		
		// and our view shortcuts
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(AnalysisView.ID);
		layout.addShowViewShortcut(DataFrequencyView.ID);
		layout.addShowViewShortcut(XyPlotView.ID);
	}
}
