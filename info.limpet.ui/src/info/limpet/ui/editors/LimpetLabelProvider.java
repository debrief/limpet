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
import javax.measure.unit.TransformedUnit;
import javax.measure.unit.Unit;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class LimpetLabelProvider extends LabelProvider
{

  public ImageDescriptor getImageDescriptor(Object obj2)
  {

    ImageDescriptor res = null;

    final IStoreItem item;

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
        item = null;
        // is it just one, or multiple?
        res = Activator.getImageDescriptor("icons/folder.png");
      }
      else
      {
        item = null;
      }
    }
    else if (obj2 instanceof IStoreItem)
    {
      item = (IStoreItem) obj2;
    }
    else
    {
      item = null;
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
          Unit<?> unit = q.getUnits();
          Dimension dim = q.getUnits().getDimension();

          // ok - workaround to tidy the decibels data
          checkDecibels(q, unit);

          if (dim.equals(Dimension.LENGTH))
          {
            res = Activator.getImageDescriptor("icons/measure.png");
          }
          if (Angle.UNIT.equals(unit.getStandardUnit()))
          {
            res = Activator.getImageDescriptor("icons/angle.png");
          }
          else if (dim.equals(Dimension.MASS))
          {
            res = Activator.getImageDescriptor("icons/weight.png");
          }
          else if (dim.equals(Dimension.MASS.divide(Dimension.LENGTH.times(
              Dimension.LENGTH).times(Dimension.LENGTH))))
          {
            res = Activator.getImageDescriptor("icons/density.png");
          }
          else if (dim.equals(Dimension.TIME))
          {
            res = Activator.getImageDescriptor("icons/time.png");
          }
          else if (dim.equals(SI.HERTZ.getDimension()))
          {
            res = Activator.getImageDescriptor("icons/frequency.png");
          }
          else if (dim.equals(Dimension.LENGTH.divide(Dimension.TIME)))
          {
            res = Activator.getImageDescriptor("icons/speed.png");
          }
          else if (unit.equals(NonSI.DECIBEL))
          {
            // TODO: this test relies on the decibel data being corrected
            // in the checkDecibels call
            res = Activator.getImageDescriptor("icons/volume.png");
          }
          else if (dim.equals(Dimensionless.UNIT.getDimension()))
          {
            res = Activator.getImageDescriptor("icons/numbers.png");
          }
          else
          {
            // default image type
            res = Activator.getImageDescriptor("icons/numbers.png");
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

  /**
   * special case. Decibel units are losing their type once restored. Do a check for if they should
   * be decibels, and override the units, if we have to
   * 
   * @param document
   * @param currentUnits
   */
  private void checkDecibels(final NumberDocument document,
      final Unit<?> currentUnits)
  {
    if (!currentUnits.equals(NonSI.DECIBEL) && isDecibels(currentUnits))
    {
      // ok, replace the units with decibels
      document.setUnits(NonSI.DECIBEL);
    }
  }

  /**
   * special case. Once a decibels unit has been restored from file, the equals comparison no longer
   * works. So, we've copied the below content from the original sources. The original sources also
   * includes a comparison of db.converter, but we've omitted that, since it's not visible. If we
   * start getting false positives, we'll have to consider using the derived converter object.
   * 
   * @param that
   *          object we're considering
   * @return if it's the units of decibels
   */
  private boolean isDecibels(Object that)
  {
    final TransformedUnit<Dimensionless> db =
        (TransformedUnit<Dimensionless>) NonSI.DECIBEL;
    if (this == that)
      return true;
    if (!(that instanceof TransformedUnit))
      return false;
    TransformedUnit<?> thatUnit = (TransformedUnit<?>) that;
    return db.getParentUnit().equals(thatUnit.getParentUnit());
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
