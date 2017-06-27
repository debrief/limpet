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
import info.limpet.ui.xy_plot.Helper2D.HContainer;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * display analysis overview of selection
 * 
 * @author ian
 * 
 */
public class HeatmapView extends CommonGridView
{

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "info.limpet.ui.HeatMapView";

  private IntensityGraphFigure intensityGraph;
  private Canvas parentCanvas;

  public HeatmapView()
  {
    super(ID, "Heatmap view");
  }

  protected void clearChart()
  {
    intensityGraph.setDataArray(new double[]
    {});
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
    titleLbl = new Text(composite, SWT.NONE);
    titleLbl.setText(" ");

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
  protected String getTextForClipboard()
  {
    return "Pending";
  }

  @Override
  public void setFocus()
  {
    parentCanvas.setFocus();
  }

  protected void show(final IStoreItem item)
  {
    final NumberDocument thisQ = (NumberDocument) item;

    clearChart();

    final String seriesName = thisQ.getName();
    titleLbl.setText(seriesName + " (" + thisQ.getUnits().toString() + ")");

    // get the data
    final HContainer hData =
        Helper2D.convert((DoubleDataset) thisQ.getDataset());

    final int rows = hData.rowTitles.length;
    final int cols = hData.colTitles.length;

    final int DataHeight = rows;
    final int DataWidth = cols;

    final double[] data = new double[DataWidth * DataHeight];

    for (int i = 0; i < DataHeight; i++)
    {
      for (int j = 0; j < DataWidth; j++)
      {
        final double thisVal = hData.values[i][j];
        data[i + j * DataHeight] = thisVal;
      }
    }

    final DescriptiveStatistics stats = new DescriptiveStatistics(data);

    if (Double.isNaN(stats.getMax()))
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

    // sort out the axis ranges
    intensityGraph.getXAxis().setRange(
        new Range(hData.rowTitles[0],
            hData.rowTitles[hData.rowTitles.length - 1]));
    intensityGraph.getYAxis().setRange(
        new Range(hData.colTitles[0],
            hData.colTitles[hData.colTitles.length - 1]));

    // parentCanvas.pack();
    titleLbl.pack();
  }

}
