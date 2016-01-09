package info.limpet.data.operations.spatial;

import java.awt.geom.Point2D;

public interface GeoCalculator
{
  Point2D createPoint(double dLong, double dLat);

  double getDistanceBetween(Point2D locA, Point2D locB);

  Point2D calculatePoint(Point2D pos1, double radians, double distance);

  double getAngleBetween(Point2D txLoc, Point2D rxLoc);
}
