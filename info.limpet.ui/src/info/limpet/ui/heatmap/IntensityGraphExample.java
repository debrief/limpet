package info.limpet.ui.heatmap;

/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.IPrimaryArrayWrapper;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure.IROIListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A live updated Intensity Graph example
 * 
 * @author Xihui Chen
 * 
 */
public class IntensityGraphExample
{
  private static class NullColorMap extends ColorMap
  {
    public NullColorMap()
    {
      super(PredefinedColorMap.JET, true, true);
    }

    @Override
    public ImageData drawImage(final IPrimaryArrayWrapper dataArray,
        final int dataWidth, final int dataHeight, final double max,
        final double min, final ImageData imageDataIn, final boolean shrink)
    {
      final ImageData imageData =
          super.drawImage(dataArray, dataWidth, dataHeight, max, min,
              imageDataIn, shrink);
      if (shrink)
      {
        final int height = imageData.height;
        final int width = imageData.width;
        // EDIT: added +1 to account for an early rounding problem
        final int x_ratio = (dataWidth << 16) / width + 1;
        final int y_ratio = (dataHeight << 16) / height + 1;
        // int x_ratio = (int)((w1<<16)/w2) ;
        // int y_ratio = (int)((h1<<16)/h2) ;
        for (int i = 0; i < height; i++)
        {
          for (int j = 0; j < width; j++)
          {
            final int x2 = ((j * x_ratio) >> 16);
            final int y2 = ((i * y_ratio) >> 16);
            final double d = dataArray.get(y2 * dataWidth + x2);
            if (Double.isNaN(d))
            {
              imageData.setAlpha(j, i, 0);
            }
            else
            {
              imageData.setAlpha(j, i, 255);
            }
          }
        }

      }
      else
      {
        for (int y = 0; y < dataHeight; y++)
        {
          for (int x = 0; x < dataWidth; x++)
          {
            // the index of the value in the color table array
            final double d = dataArray.get(y * dataWidth + x);
            if (Double.isNaN(d))
            {
              imageData.setAlpha(x, y, 0);
            }
            else
            {
              imageData.setAlpha(x, y, 255);
            }
          }
        }
      }
      return imageData;
    }
  }

  private static int count = 0;
  private static final int DataHeight = 1024;

  private static final int DataWidth = 1280;

  public static void main(final String[] args)
  {
    final Shell shell = new Shell();
    shell.setSize(300, 250);
    shell.open();

    // use LightweightSystem to create the bridge between SWT and draw2D
    final LightweightSystem lws = new LightweightSystem(shell);

    // Create Intensity Graph
    final IntensityGraphFigure intensityGraph = new IntensityGraphFigure();

    // Create Simulation Data
    final double[] simuData = new double[DataWidth * DataHeight * 2];
    final double[] data = new double[DataWidth * DataHeight];
    final int seed = count++;
    for (int i = 0; i < DataHeight; i++)
    {
      for (int j = 0; j < DataWidth; j++)
      {
        final int x = j - DataWidth;
        final int y = i - DataHeight;
        final double thisValue;
        if (Math.abs(i - j) < 40)
        {
          thisValue = Double.NaN;
        }
        else
        {
          final int p = (int) Math.sqrt(x * x + y * y);
          thisValue =
              110 + (Math.sin(p * 2 * Math.PI / DataWidth + seed * Math.PI
                  / 100) * 100);
        }
        simuData[i * DataWidth + j] = thisValue;
      }
    }

    // Configure
    intensityGraph.setMax(100);
    intensityGraph.setMin(-100);
    intensityGraph.setDataHeight(DataHeight);
    intensityGraph.setDataWidth(DataWidth);
    intensityGraph.setColorMap(new NullColorMap());
    intensityGraph.addROI("ROI 1", new IROIListener()
    {

      @Override
      public void roiUpdated(final int xIndex, final int yIndex,
          final int width, final int height)
      {
        System.out.println("Region of Interest: (" + xIndex + ", " + yIndex
            + ", " + width + ", " + height + ")");
      }
    }, null);
    lws.setContents(intensityGraph);

    // Update the graph in another thread.
    final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1);
    final ScheduledFuture<?> future =
        scheduler.scheduleAtFixedRate(new Runnable()
        {

          @Override
          public void run()
          {
            System.arraycopy(simuData, count % DataWidth, data, 0, DataWidth
                * DataHeight);

            Display.getDefault().asyncExec(new Runnable()
            {

              @Override
              public void run()
              {
                count++;
                intensityGraph.setDataArray(simuData);
              }
            });
          }
        }, 100, 10, TimeUnit.MILLISECONDS);

    final Display display = Display.getDefault();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
        display.sleep();
    }
    future.cancel(true);
    scheduler.shutdown();

  };
}
