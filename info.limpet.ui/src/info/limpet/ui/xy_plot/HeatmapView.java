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
package info.limpet.ui.xy_plot;

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.ui.core_view.CoreAnalysisView;
import info.limpet.ui.xy_plot.Helper2D.HContainer;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class HeatmapView extends CoreAnalysisView
{

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.HeatMapView";
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private IntensityGraphFigure intensityGraph;
  private Canvas parentCanvas;
  private Text title;

  public HeatmapView()
  {
    super(ID, "Heatmap view");
  }

  @Override
  protected boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    final boolean allNonQuantity = tests.allNonQuantity(res);
    final boolean allCollections = tests.allCollections(res);
    final boolean allQuantity = tests.allQuantity(res);
    final boolean suitableIndex =
        tests.allIndexed(res) || tests.allNonIndexed(res);
    return allCollections && suitableIndex && (allQuantity || allNonQuantity);
  }

  private void clearChart()
  {
    intensityGraph.setDataArray(new double[]
    {});
  }

  @Override
  protected void contributeToActionBars()
  {
    super.contributeToActionBars();
    @SuppressWarnings("unused")
    final IActionBars bars = getViewSite().getActionBars();
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent)
  {
    // Create the composite to hold the controls
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    // Create the combo to hold the team names
    title = new Text(composite, SWT.NONE);
    title.setText(" ");

    // insert the holder for the heatmap
    parentCanvas = new Canvas(composite, SWT.NULL);
    parentCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));

    // Create Intensity Graph
    intensityGraph = new IntensityGraphFigure();

    // use LightweightSystem to create the bridge between SWT and draw2D
    final LightweightSystem lws = new LightweightSystem(parentCanvas);
    lws.setContents(intensityGraph);

    // ok, layout the panel
    parentCanvas.pack();

    makeActions();
    contributeToActionBars();

    // register as selection listener
    setupListener();
  }

  @Override
  protected void datasetDataChanged(final IStoreItem subject)
  {
    showTwoDim(subject);
  }

  @Override
  public void display(final List<IStoreItem> res)
  {
    // anything selected?
    if (res.size() == 0)
    {
      clearChart();
    }
    else
    {
      // check they're all one dim
      if (aTests.allTwoDim(res) && res.size() == 1)
      {
        // ok, it's a single two-dim dataset
        showTwoDim(res.get(0));
      }
      else
      {
        // nope, clear it
        clearChart();
      }
    }
  }

  @Override
  protected String getTextForClipboard()
  {
    return "Pending";
  }

  @Override
  protected void makeActions()
  {
    super.makeActions();
  }

  @Override
  public void setFocus()
  {
    parentCanvas.setFocus();
  }

  private void showTwoDim(final IStoreItem item)
  {
    final NumberDocument thisQ = (NumberDocument) item;

    clearChart();

    final String seriesName = thisQ.getName();
    title.setText(seriesName + " (" + thisQ.getUnits().toString() + ")");

    // get the data
    final HContainer hData =
        Helper2D.convert((DoubleDataset) thisQ.getDataset());

    final int rows = hData.rowTitles.length;
    final int cols = hData.colTitles.length;

    final int DataHeight = 1024;
    final int DataWidth = 1024;

    final double rowStep = DataHeight / rows;
    final double colStep = DataWidth / cols;

    final double[] data = new double[DataWidth * DataHeight];

    for (int i = 0; i < DataHeight; i++)
    {
      for (int j = 0; j < DataWidth; j++)
      {
        final int thisI = (int) (i / rowStep);
        final int thisJ = (int) (j / colStep);
        final double thisVal = hData.values[thisI][thisJ];
        data[i + j * DataHeight] = thisVal;
      }
    }

    final DescriptiveStatistics stats = new DescriptiveStatistics(data);

    if(Double.isNaN(stats.getMax()))
    {
      // ok, drop out, we haven't got any data
      return;
    }
    
    // Configure
    intensityGraph.setMax(stats.getMax());
    intensityGraph.setMin(stats.getMin());
    intensityGraph.setDataHeight(DataHeight);
    intensityGraph.setDataWidth(DataWidth);
    intensityGraph
        .setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
    intensityGraph.setDataArray(data);

    // parentCanvas.pack();
    title.pack();
  }

}
