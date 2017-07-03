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
package info.limpet.ui.editors;

import info.limpet.ICommand;
import info.limpet.IDocument;
import info.limpet.impl.Document;
import info.limpet.ui.Activator;
import info.limpet.ui.data_provider.data.CommandWrapper;
import info.limpet.ui.data_provider.data.DocumentWrapper;
import info.limpet.ui.data_provider.data.GroupWrapper;

import javax.measure.unit.SI;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class LimpetDecorator implements ILightweightLabelDecorator
{

  private static final ImageDescriptor TIME;

  private static final ImageDescriptor LENGTH;

  private static final ImageDescriptor SINGLE;

  private static final ImageDescriptor TWO_DIM;

  private static final ImageDescriptor LEFT_ARROW;

  private static final ImageDescriptor RIGHT_ARROW;

  private static final ImageDescriptor TWO_WAY_ARROW;

  private static final ImageDescriptor DYNAMIC;

  static
  {
    LEFT_ARROW =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/left.png");
    RIGHT_ARROW =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/right.png");
    TWO_WAY_ARROW =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/left_right.png");
    TIME =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/clock.png");
    LENGTH =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/measure_8.png");
    SINGLE =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/singleton.png");
    TWO_DIM =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/2.png");

    // TODO: switch to better "dynamic/connected" icon.
    DYNAMIC =
        AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "icons/plus.png");
  }

  @Override
  public void addListener(ILabelProviderListener listener)
  {
  }

  @Override
  public void dispose()
  {
  }

  @Override
  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  @Override
  public void removeListener(ILabelProviderListener listener)
  {
  }

  @Override
  public void decorate(Object element, IDecoration decoration)
  {
    if (element instanceof DocumentWrapper)
    {
      decorateWrapper((DocumentWrapper) element, decoration);
    }
    if (element instanceof CommandWrapper)
    {
      decorateWrapper((CommandWrapper) element, decoration);
    }
    if (element instanceof GroupWrapper)
    {
      decorateWrapper((GroupWrapper) element, decoration);
    }
  }

  protected void decorateWrapper(GroupWrapper element, IDecoration decoration)
  {
    // we don't currently apply and decorations to groups
  }

  protected void
      decorateWrapper(CommandWrapper element, IDecoration decoration)
  {
    final ICommand coll = element.getCommand();

    boolean in = coll.getInputs() != null && coll.getInputs().size() > 0;
    boolean out = coll.getOutputs() != null && coll.getOutputs().size() > 0;
    decorateInOut(decoration, in, out, IDecoration.TOP_RIGHT);

    // also apply a decoration to indicate that the symbol is dynamically updating
    if (coll.getDynamic())
    {
      decoration.addOverlay(DYNAMIC, IDecoration.BOTTOM_RIGHT);
    }
  }

  private int dimensionsFor(IDocument<?> doc)
  {
    int res = 0;

    if (doc instanceof Document)
    {
      Document<?> thisC = (Document<?>) doc;
      Dataset d = (Dataset) thisC.getDataset();
      int[] dims = d.getShape();
      res = dims.length;
    }

    return res;
  }

  protected void
      decorateWrapper(DocumentWrapper element, IDecoration decoration)
  {
    final IDocument<?> coll = element.getDocument();
    boolean out = coll.getPrecedent() != null;
    boolean in =
        coll.getDependents() != null && coll.getDependents().size() > 0;
    decorateInOut(decoration, in, out, IDecoration.TOP_RIGHT);

    if (coll.isIndexed())
    {
      if (coll.getIndexUnits().getDimension().equals(SI.SECOND.getDimension()))
      {
        decoration.addOverlay(TIME, IDecoration.BOTTOM_RIGHT);
      }
      else if (coll.getIndexUnits().getDimension().equals(
          SI.METRE.getDimension()))
      {
        decoration.addOverlay(LENGTH, IDecoration.BOTTOM_RIGHT);
      }
    }
    if (coll.size() == 1)
    {
      decoration.addOverlay(SINGLE, IDecoration.BOTTOM_LEFT);
    }

    // see if multi-dimensional
    decorateDims(decoration, dimensionsFor(coll), IDecoration.TOP_LEFT);

  }

  /**
   * indicate if this is a multi-dimensional dataset
   * 
   * @param decoration
   * @param dims
   *          how many dims there are
   * @param quadrant
   *          where to put the icon
   */
  private void decorateDims(IDecoration decoration, int dims, int quadrant)
  {
    if (dims > 1)
    {
      decoration.addOverlay(TWO_DIM, quadrant);
    }
  }

  private void decorateInOut(IDecoration decoration, boolean in, boolean out,
      int quadrant)
  {
    if (in && out)
    {
      decoration.addOverlay(TWO_WAY_ARROW, quadrant);
    }
    else if (out)
    {
      decoration.addOverlay(LEFT_ARROW, quadrant);
    }
    else if (in)
    {
      decoration.addOverlay(RIGHT_ARROW, quadrant);
    }
  }
}
