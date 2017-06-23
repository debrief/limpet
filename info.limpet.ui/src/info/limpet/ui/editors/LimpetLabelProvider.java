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
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.DoubleListDocument;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.StringDocument;
import info.limpet.ui.Activator;
import info.limpet.ui.data_provider.data.DataModel;
import info.limpet.ui.data_provider.data.LimpetWrapper;
import info.limpet.ui.data_provider.data.NamedList;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Dimension;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LimpetLabelProvider extends LabelProvider
{

  public ImageDescriptor getImageDescriptor(Object obj2)
  {

    ImageDescriptor res = null;

    IStoreItem item = null;

    if (obj2 instanceof LimpetWrapper)
    {
      LimpetWrapper wrapper = (LimpetWrapper) obj2;
      Object obj = wrapper.getSubject();
      if (obj instanceof IStoreItem)
      {
        item = (IStoreItem) obj;
      }
      else if (obj instanceof NamedList)
      {
        // is it just one, or multiple?
        res = Activator.getImageDescriptor("icons/folder.png");
      }
    }
    else if (obj2 instanceof IStoreItem)
    {
      item = (IStoreItem) obj2;
    }

    if (item != null)
    {

      if (item instanceof IStoreGroup)
      {
        // is it just one, or multiple?
        res = Activator.getImageDescriptor("icons/folder.png");
      }
      if (item instanceof IDocument)
      {
        // is it just one, or multiple?
        IDocument<?> coll = (IDocument<?>) item;
        if (coll.isQuantity())
        {
          NumberDocument q = (NumberDocument) coll;
          Dimension dim = q.getUnits().getDimension();
          if (dim.equals(Dimension.LENGTH))
          {
            res = Activator.getImageDescriptor("icons/measure.png");
          }
          else if (dim.equals(Angle.UNIT.getDimension()))
          {
            res = Activator.getImageDescriptor("icons/angle.png");
          }
          else if (dim.equals(Dimension.MASS))
          {
            res = Activator.getImageDescriptor("icons/weight.png");
          }
          else if (dim.equals(NonSI.DECIBEL))
          {
            res = Activator.getImageDescriptor("icons/volume.png");
          }
          else if (dim.equals(Dimension.LENGTH.times(Dimension.LENGTH).times(
              Dimension.LENGTH).divide(Dimension.MASS)))
          {
            res = Activator.getImageDescriptor("icons/density.png");
          }
          else if (dim.equals(Dimension.TIME))
          {
            res = Activator.getImageDescriptor("icons/time.png");
          }
          else if (dim.equals(Dimensionless.UNIT.getDimension()))
          {
            res = Activator.getImageDescriptor("icons/numbers.png");
          }
          else if (dim.equals(SI.HERTZ.getDimension()))
          {
            res = Activator.getImageDescriptor("icons/frequency.png");
          }
          else if (dim.equals(Dimension.LENGTH.divide(Dimension.TIME)))
          {
            res = Activator.getImageDescriptor("icons/speed.png");
          }
          else
          {
            // default image type
            res = Activator.getImageDescriptor("icons/frequency.png");
          }
        }
        else if (coll instanceof LocationDocument)
        {
          res = Activator.getImageDescriptor("icons/location.png");
        }
        else if (coll instanceof StringDocument)
        {
          res = Activator.getImageDescriptor("icons/string.png");
        }
        else if (coll instanceof DoubleListDocument)
        {
          res = Activator.getImageDescriptor("icons/string.png");
        }
      }
      else if (item instanceof ICommand)
      {
        res = Activator.getImageDescriptor("icons/interpolate.png");
      }
      else if (obj2 instanceof NamedList)
      {
        NamedList nl = (NamedList) obj2;
        String name = nl.toString();

        if (name.equals(DataModel.PRECEDENTS))
        {
          res = Activator.getImageDescriptor("icons/l_arrow.png");

        }
        else if (name.equals(DataModel.DEPENDENTS))
        {
          res = Activator.getImageDescriptor("icons/r_arrow.png");
        }
      }

      if (res == null)
      {
        System.err.println("no icon for:" + item);
      }
    }

    return res;
  }

  @Override
  public Image getImage(Object obj)
  {
    Image res = null;

    ImageDescriptor desc = getImageDescriptor(obj);

    if (desc != null)
    {
      res = Activator.getImageFromRegistry(desc);
    }

    return res;
  }
}
