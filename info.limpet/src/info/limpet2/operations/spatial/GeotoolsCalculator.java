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
package info.limpet2.operations.spatial;

import java.awt.geom.Point2D;

import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;

public class GeotoolsCalculator implements IGeoCalculator
{
  /**
   * protected constructor - to prevent declaration of GeoSupport
   * 
   */
  public GeotoolsCalculator()
  {

  }

  @Override
  public Point2D createPoint(double dLong, double dLat)
  {
    return new Point2D.Double(dLong, dLat);
  }

  @Override
  public Point2D calculatePoint(Point2D pos1, double angle, double distance)
  {
    GeodeticCalculator calculator =
        new GeodeticCalculator(DefaultGeographicCRS.WGS84);
    calculator.setStartingGeographicPoint(pos1);
    calculator.setDirection(angle, distance);
    return calculator.getDestinationGeographicPoint();
  }

  @Override
  public double getDistanceBetween(Point2D locA, Point2D locB)
  {
    GeodeticCalculator calc =
        new GeodeticCalculator(DefaultGeographicCRS.WGS84);
    calc.setStartingGeographicPoint(locA);
    calc.setDestinationGeographicPoint(locB);
    return calc.getOrthodromicDistance();
  }

  @Override
  public double getAngleBetween(Point2D txLoc, Point2D rxLoc)
  {
    GeodeticCalculator calc =
        new GeodeticCalculator(DefaultGeographicCRS.WGS84);
    calc.setStartingGeographicPoint(txLoc);
    calc.setDestinationGeographicPoint(rxLoc);
    return calc.getAzimuth();
  }
}
