/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.rcp.product;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import info.limpet.stackedcharts.ui.view.StackedChartsView;
import info.limpet.ui.analysis_view.AnalysisView;
import info.limpet.ui.data_frequency.DataFrequencyView;
import info.limpet.ui.heatmap.HeatmapView;
import info.limpet.ui.heatmap.TabularView;
import info.limpet.ui.range_slider.RangeSliderView;
import info.limpet.ui.time_frequency.TimeFrequencyView;
import info.limpet.ui.xy_plot.XyPlotView;

public class Perspective implements IPerspectiveFactory
{
  public static final String ID = "info.limpet.product.perspective";

  @SuppressWarnings("deprecation")
  public void createInitialLayout(IPageLayout layout)
  {
    final String editorArea = layout.getEditorArea();

    final IFolderLayout topLeft =
        layout.createFolder("topLeft", IPageLayout.LEFT, 0.3f, editorArea);
    topLeft.addView(IPageLayout.ID_RES_NAV);

    final IFolderLayout bottomLeft =
        layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.6f, "topLeft");
    bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

    final IFolderLayout topRight =
        layout.createFolder("topRight", IPageLayout.RIGHT, 0.6f, editorArea);
    topRight.addView(AnalysisView.ID);
    topRight.addPlaceholder("org.eclipse.pde.runtime.LogView");
    topRight.addPlaceholder(StackedChartsView.ID);

    final IFolderLayout underAnalysis =
        layout.createFolder("underAnalysis", IPageLayout.BOTTOM, 0.7f,
            "topRight");
    underAnalysis.addView(RangeSliderView.ID);

    final IFolderLayout bottom =
        layout.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
    bottom.addView(XyPlotView.ID);
    bottom.addPlaceholder(TimeFrequencyView.ID);
    bottom.addPlaceholder(TabularView.ID);
    bottom.addPlaceholder(HeatmapView.ID);
    bottom.addPlaceholder(DataFrequencyView.ID);

    // and our view shortcuts
    layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
    layout.addShowViewShortcut(AnalysisView.ID);
    layout.addShowViewShortcut(DataFrequencyView.ID);
    layout.addShowViewShortcut(XyPlotView.ID);
    layout.addShowViewShortcut(RangeSliderView.ID);
    layout.addShowViewShortcut(TabularView.ID);
    layout.addShowViewShortcut(HeatmapView.ID);
    
    
    // and shortcuts to create new Limpet files
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
    layout.addNewWizardShortcut("info.limpet.rcp.wizards.NewFileWizard");
    
  }
}
