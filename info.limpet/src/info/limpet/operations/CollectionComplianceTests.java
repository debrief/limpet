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
package info.limpet.operations;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.Document;
import info.limpet.ILocations;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.NumberDocument;
import info.limpet.Document.InterpMethod;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Dimension;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class CollectionComplianceTests
{

  /**
   * check if the specific number of arguments are supplied
   * 
   * @param selection
   * @param num
   * @return
   */
  public boolean exactNumber(final List<IStoreItem> selection, final int num)
  {
    return selection.size() == num;
  }

  /**
   * check if the series are all non locations
   * 
   * @param selection
   * @return true/false
   */
  public boolean allNonLocation(List<IStoreItem> selection)
  {
    // are they all non location?
    boolean allValid = true;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        Class<?> theClass = thisC.storedClass();
        if (Point2D.class.equals(theClass))
        {
          allValid = false;
          break;
        }
      }
      else
      {
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * check if the series are all non locations
   * 
   * @param selection
   * @return true/false
   */
  public boolean allLocation(List<IStoreItem> selection)
  {
    // are they all non location?
    boolean allValid = true;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof ILocations)
      {
      }
      else
      {
        allValid = false;
        break;
      }

    }
    return allValid;
  }

  /**
   * check if the series are all quantity datasets
   * 
   * @param selection
   * @return true/false
   */
  public boolean allNonIndexed(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = true;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (thisC.isIndexed())
        {
          // oops, no
          allValid = false;
          break;
        }
      }
      else
      {
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * check if the series are all quantity datasets
   * 
   * @param selection
   * @return true/false
   */
  public boolean allNonQuantity(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = true;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (thisC.isQuantity())
        {
          // oops, no
          allValid = false;
          break;
        }
      }
      else
      {
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * determine if these datasets are suited to a temporal operation - where we interpolate time
   * values
   * 
   * @param selection
   * @return
   */
  public boolean suitableForIndexedInterpolation(List<IStoreItem> selection)
  {
    // are suitable
    boolean suitable = selection.size() >= 2;

    Long startT = null;
    Long endT = null;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (thisC.isQuantity() || thisC instanceof ILocations)
        {
          if (thisC.isIndexed())
          {
            // NumberDocument nd = (NumberDocument) thisC;
            AxesMetadata axes =
                thisC.getDataset().getFirstMetadata(AxesMetadata.class);
            ILazyDataset axesDatasetLazy = axes.getAxes()[0];
            Dataset axesDataset;
            try
            {
              axesDataset =
                  DatasetUtils.sliceAndConvertLazyDataset(axesDatasetLazy);
              long thisStart = axesDataset.getLong(0);
              long thisEnd = axesDataset.getLong(axesDataset.getSize() - 1);

              if (startT == null)
              {
                startT = thisStart;
                endT = thisEnd;
              }
              else
              {
                // check the overlap
                if (thisStart > endT || thisEnd < startT)
                {
                  suitable = false;
                  break;
                }
              }
            }
            catch (DatasetException e)
            {
              throw new RuntimeException(e);
            }
          }
        }
        else
        {
          // oops, no
          suitable = false;
          break;
        }
      }
      else
      {
        suitable = false;
        break;
      }

    }
    return suitable && startT != null;
  }

  /**
   * check if the series are all quantity datasets
   * 
   * @param selection
   * @return true/false
   */
  public boolean allQuantity(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = selection.size() > 0;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (!thisC.isQuantity())
        {
          // oops, no
          allValid = false;
          break;
        }
      }
      else
      {
        allValid = false;
        break;
      }

    }
    return allValid;
  }

  /**
   * check if the series are all quantity datasets
   * 
   * @param selection
   * @return true/false
   */
  public boolean nonEmpty(List<IStoreItem> selection)
  {
    return selection.size() > 0;
  }

  /**
   * check if the series are all have equal dimensions
   * 
   * @param selection
   * @return true/false
   */
  public boolean allEqualDimensions(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = false;
    Dimension theD = null;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof NumberDocument)
      {
        NumberDocument thisC = (NumberDocument) thisI;
        if (thisC.getUnits() != null)
        {
          Dimension thisD = thisC.getUnits().getDimension();
          if (theD == null)
          {
            theD = thisD;
          }
          else
          {
            if (thisD.equals(theD))
            {
              // all fine.
              allValid = true;
            }
            else
            {
              allValid = false;
              break;
            }
          }
        }
        else
        {
          allValid = false;
          break;
        }
      }
      else
      {
        // oops, no
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * check if the series all have equal units
   * 
   * @param selection
   * @return true/false
   */
  public boolean allEqualUnits(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = true;
    Unit<?> theD = null;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof NumberDocument)
      {
        NumberDocument thisC = (NumberDocument) thisI;
        Unit<?> thisD = thisC.getUnits();
        if (theD == null)
        {
          theD = thisD;
        }
        else if (!thisD.equals(theD))
        {
          allValid = false;
          break;
        }
      }
      else
      {
        allValid = false;
        break;
      }
    }

    return allValid;
  }

  /**
   * check if the list has at least on indexed dataset
   * 
   * @param selection
   * @return true/false
   */
  public boolean hasIndexed(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = false;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (thisC.isIndexed())
        {
          allValid = true;
          break;
        }
      }
    }
    return allValid;
  }

  /**
   * check if the series all have indixes
   * 
   * @param selection
   * @return true/false
   */
  public boolean allIndexed(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = true;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (!thisC.isIndexed())
        {
          // oops, no
          allValid = false;
          break;
        }
      }
      else
      {
        // oops, no
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * check if the series are at least one temporal dataset, plus one or more singletons
   * 
   * @param selection
   * @return true/false
   */
  public boolean allIndexedOrSingleton(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = false;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (thisC.isIndexed())
        {
          allValid = true;
        }
        else
        {
          // check if it's not a singleton
          if (thisC.size() != 1)
          {
            // oops, no
            allValid = false;
            break;
          }
        }
      }
      else
      {
        // oops, no
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * check if the series are all of equal length, or singletons
   * 
   * @param selection
   * @return true/false
   */
  public boolean allEqualLengthOrSingleton(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = true;
    int size = -1;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisC = selection.get(i);

      if (thisC instanceof Document)
      {
        Document thisD = (Document) thisC;
        int thisSize = thisD.size();

        // valid, check the size
        if (size == -1)
        {
          // ok, is this a singleton?
          if (thisSize != 1)
          {
            // nope, it's a real array store it.
            size = thisSize;
          }
        }
        else
        {
          if (thisSize != size && thisSize != 1)
          {
            // oops, no
            allValid = false;
            break;
          }

        }
      }
    }

    return allValid;
  }

  /**
   * check if the series are all of equal length
   * 
   * @param selection
   * @return true/false
   */
  public boolean allEqualLength(List<IStoreItem> selection)
  {
    // are they all temporal?
    boolean allValid = true;
    int size = -1;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;

        // valid, check the size
        if (size == -1)
        {
          size = thisC.size();
        }
        else
        {
          if (size != thisC.size())
          {
            // oops, no
            allValid = false;
            break;
          }
        }
      }
      else
      {
        allValid = false;
        break;
      }
    }

    return allValid;
  }

  public boolean allCollections(List<IStoreItem> selection)
  {
    boolean res = true;
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (!(storeItem instanceof Document))
      {
        res = false;
        break;
      }
    }
    return res;
  }

  public boolean minNumberOfGroups(List<IStoreItem> selection, final int count)
  {
    int res = 0;
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (storeItem instanceof IStoreGroup)
      {
        res++;
      }
    }
    return res > count;
  }

  public int getNumberOfGroups(List<IStoreItem> selection)
  {
    int res = 0;
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (storeItem instanceof IStoreGroup)
      {
        res++;
      }
    }
    return res;
  }

  public boolean allGroups(List<IStoreItem> selection)
  {
    boolean res = true;
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (!(storeItem instanceof IStoreGroup))
      {
        res = false;
        break;
      }
    }
    return res;
  }

  /**
   * convenience test to verify if children of the supplied item can all be treated as tracks
   * 
   * @param selection
   *          one or more group objects
   * @return yes/no
   */
  public int getNumberOfTracks(List<IStoreItem> selection)
  {
    int count = 0;
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      boolean valid = true;
      if (storeItem instanceof IStoreGroup)
      {
        // ok, check the contents
        IStoreGroup group = (IStoreGroup) storeItem;

        valid = isATrack(group);

        // special case: we can miss out course & speed if there's just a single
        // stationery location, and there's a single location
        if (!valid && hasSingletonLocation(group))
        {
          valid = true;
        }
      }
      else if (storeItem instanceof ILocations)
      {
        valid = true;
      }
      else
      {
        valid = false;
      }
      if (valid)
      {
        count++;
      }

    }
    return count;
  }

  private boolean hasSingletonLocation(IStoreGroup group)
  {
    boolean res = false;

    final Iterator<IStoreItem> iter = group.iterator();
    while (iter.hasNext())
    {
      final IStoreItem item = iter.next();
      if (item instanceof Document)
      {
        final Document doc = (Document) item;
        if (doc instanceof ILocations && doc.size() == 1)
        {
          res = true;
          break;
        }
      }
    }

    return res;
  }

  /**
   * test for if a group contains enough data for us to treat it as a track
   * 
   * @param group
   *          the group of tracks
   * @return yes/no
   */
  public boolean isATrack(IStoreGroup group)
  {
    boolean res = true;

    // ok, keep looping through, to check we have the right types
    if (!isPresent(group, METRE.divide(SECOND).getDimension()))
    {
      return false;
    }

    // ok, keep looping through, to check we have the right types
    if (!isPresent(group, SI.RADIAN.getDimension()))
    {
      return false;
    }

    if (!hasLocation(group))
    {
      return false;
    }

    return res;
  }

  /**
   * convenience test to verify if children of the supplied item can all be treated as tracks
   * 
   * @param selection
   *          one or more group objects
   * @return yes/no
   */
  public boolean allChildrenAreTracks(List<IStoreItem> selection)
  {
    boolean res = true;
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (storeItem instanceof IStoreGroup)
      {
        // ok, check the contents
        IStoreGroup group = (IStoreGroup) storeItem;

        res = isATrack(group);

      }
    }
    return res;
  }

  /**
   * get any layers that we contain location data
   * 
   * @param selection
   *          one or more group objects
   * @return yes/no
   */
  public ArrayList<IStoreGroup> getChildTrackGroups(List<IStoreItem> selection)
  {
    ArrayList<IStoreGroup> res = new ArrayList<IStoreGroup>();
    Iterator<IStoreItem> iter = selection.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = iter.next();
      if (storeItem instanceof IStoreGroup)
      {
        // ok, check the contents
        IStoreGroup group = (IStoreGroup) storeItem;
        Collection<IStoreItem> kids = group;

        Iterator<IStoreItem> kIter = kids.iterator();
        while (kIter.hasNext())
        {
          IStoreItem storeItem2 = (IStoreItem) kIter.next();
          if (storeItem2 instanceof ILocations)
          {
            res.add(group);
          }
        }

      }
    }
    return res;
  }

  /**
   * see if all the collections have the specified dimension
   * 
   * @param items
   *          to check
   * @param dimension
   *          we're looking for
   * @return yes/no
   */
  public boolean allHaveDimension(List<IStoreItem> kids, Dimension dim)
  {
    boolean res = true;

    for (IStoreItem sItem : kids)
    {
      if (sItem instanceof Document)
      {
        Document item = (Document) sItem;

        if (item.isQuantity())
        {
          NumberDocument coll = (NumberDocument) item;
          if (!coll.getUnits().getDimension().equals(dim))
          {
            res = false;
            break;
          }
        }
      }
    }

    return res;
  }

  /**
   * see if a collection of the specified dimension is present
   * 
   * @param items
   *          to check
   * @param dimension
   *          we're looking for
   * @return yes/no
   */
  private boolean isPresent(Collection<IStoreItem> kids, Dimension dim)
  {
    boolean res = false;

    Iterator<IStoreItem> iter = kids.iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof NumberDocument)
      {
        NumberDocument coll = (NumberDocument) item;
        if (coll.getUnits().getDimension().equals(dim))
        {
          res = true;
          break;
        }
      }
    }

    return res;
  }

  /**
   * see if a collection of the specified dimension is present
   * 
   * @param items
   *          to check
   * @param dimension
   *          we're looking for
   * @return yes/no
   */
  private boolean hasLocation(Collection<IStoreItem> kids)
  {
    boolean res = false;

    Iterator<IStoreItem> iter = kids.iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof ILocations)
      {
        res = true;
        break;
      }
    }

    return res;
  }

  /**
   * find the item in the list with the specified dimension
   * 
   * @param kids
   *          items to examine
   * @param dimension
   *          dimension we need to be present
   * @return yes/no
   */
  public NumberDocument findCollectionWith(Collection<IStoreItem> kids,
      Dimension dimension, final boolean walkTree)
  {
    NumberDocument res = null;

    Iterator<IStoreItem> iter = kids.iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof IStoreGroup && walkTree)
      {
        IStoreGroup group = (IStoreGroup) item;
        res = findCollectionWith(group, dimension, walkTree);
        if (res != null)
        {
          break;
        }

      }
      else if (item instanceof NumberDocument)
      {
        NumberDocument coll = (NumberDocument) item;
        if (coll.getUnits().getDimension().equals(dimension))
        {
          res = coll;
          break;
        }
      }
    }

    return res;
  }

  /**
   * check the list has a location collection
   * 
   * @param kids
   *          items to examine
   * @param dimension
   *          dimension we need to be present
   * @return yes/no
   */
  public Document someHaveLocation(Collection<IStoreItem> kids)
  {
    Document res = null;

    Iterator<IStoreItem> iter = kids.iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) item;
        res = someHaveLocation(group);
        if (res != null)
        {
          break;
        }
      }
      else if (item instanceof ILocations)
      {
        res = (Document) item;
        break;
      }
    }

    return res;
  }

  public int getLongestCollectionLength(List<IStoreItem> selection)
  {
    // find the longest time series.
    Iterator<IStoreItem> iter = selection.iterator();
    int longest = -1;

    while (iter.hasNext())
    {
      Document thisC = (Document) iter.next();
      longest = Math.max(longest, thisC.size());
    }
    return longest;
  }

  public Document getLongestIndexedCollection(List<IStoreItem> selection)
  {
    // find the longest time series.
    Iterator<IStoreItem> iter = selection.iterator();
    Document longest = null;

    while (iter.hasNext())
    {
      IStoreItem thisC = (IStoreItem) iter.next();
      if (thisC instanceof Document)
      {
        Document thisD = (Document) thisC;
        if (thisD.isIndexed()
            && (thisD.isQuantity() || thisC instanceof ILocations))
        {

          // check it has some data
          if (thisD.size() > 0)
          {
            if (longest == null)
            {
              longest = thisD;
            }
            else
            {
              // store the longest one
              longest = longest.size() > thisD.size() ? longest : thisD;
            }
          }
        }
      }
    }
    return longest;
  }

  public boolean allHaveData(List<IStoreItem> selection)
  {
    // are they all non location?
    boolean allValid = true;

    for (int i = 0; i < selection.size(); i++)
    {
      IStoreItem thisI = selection.get(i);
      if (thisI instanceof Document)
      {
        Document thisC = (Document) thisI;
        if (thisC.size() == 0)
        {
          allValid = false;
          break;
        }
      }
      else
      {
        allValid = false;
        break;
      }
    }
    return allValid;
  }

  /**
   * find the indexed range for overlapping data
   * 
   * @param items
   * @return
   */
  public TimePeriod getBoundingRange(final Collection<IStoreItem> items)
  {
    TimePeriod res = null;

    Iterator<IStoreItem> iter = items.iterator();
    while (iter.hasNext())
    {
      Document iCollection = (Document) iter.next();

      // allow for empty value. sometimes our logic allows null objects for some
      // data types
      if (iCollection != null && iCollection.isIndexed())
      {
        Iterator<Double> lIter = iCollection.getIndices();
        Double start = null;
        Double end = null;
        while (lIter.hasNext())
        {
          double thisT = lIter.next();
          if (start == null)
          {
            start = thisT;
          }

          // remember the last item
          end = thisT;
        }

        if (res == null)
        {
          res = new TimePeriod(start, end);
        }
        else
        {
          res.setStartTime(Math.max(res.getStartTime(), start));
          res.setEndTime(Math.min(res.getEndTime(), end));
        }
      }
    }

    return res;
  }

  /**
   * find the best collection to use as a interpolation-base. Which collection has the most values
   * within the specified range?
   * 
   * @param period
   *          (optional) period in which we count valid times
   * @param items
   *          list of datasets we're examining
   * @return most suited collection
   */
  public Document getOptimalTimes(TimePeriod period,
      Collection<IStoreItem> items)
  {
    Document res = null;
    long resScore = 0;

    Iterator<IStoreItem> iter = items.iterator();
    while (iter.hasNext())
    {
      Document iCollection = (Document) iter.next();

      // occasionally we may store a null dataset, since it is optional in some
      // circumstances
      if (iCollection != null && iCollection.isIndexed())
      {
        Iterator<Double> lIter = iCollection.getIndices();
        int score = 0;
        while (lIter.hasNext())
        {
          double long1 = lIter.next();
          if (period == null || period.contains(long1))
          {
            score++;
          }
        }

        if (res == null || score > resScore)
        {
          res = iCollection;
          resScore = score;
        }
      }
    }

    return res;
  }

  /**
   * retrieve the value at the specified index (even if it's a non-temporal collection)
   * 
   * @param iCollection
   *          set of locations to use
   * @param thisTime
   *          time we're need a location for
   * @return
   */
  public double valueAt(Document iCollection, long thisTime,
      Unit<?> requiredUnits)
  {
    Double res = null;

    // just check it's not an empty set, since we return zero for empty dataset
    if (iCollection == null)
    {
      return 0;
    }
    else if (iCollection.isQuantity())
    {
      NumberDocument iQ = (NumberDocument) iCollection;

      // just check it's not empty (which can happen during edits)
      if (iQ.size() == 0)
      {
        return 0;
      }

      if (iCollection.isIndexed())
      {
        NumberDocument tQ = (NumberDocument) iCollection;
        res = tQ.interpolateValue(thisTime, InterpMethod.Linear);
      }
      else
      {
        NumberDocument qC = (NumberDocument) iCollection;
        res = qC.getIterator().next();
      }

      if (res != null)
      {
        UnitConverter converter = iQ.getUnits().getConverterTo(requiredUnits);
        double doubleValue = res;
        double result = converter.convert(doubleValue);
        return result;
      }
      else
      {
        return 0;
      }
    }
    else
    {
      throw new RuntimeException("Tried to get value of non quantity data type");
    }
  }

  // /**
  // * retrieve the location at the specified time (even if it's a non-temporal collection)
  // *
  // * @param iCollection
  // * set of locations to use
  // * @param thisTime
  // * time we're need a location for
  // * @return
  // */
  // public Point2D locationFor(Document iCollection, Long thisTime)
  // {
  // Point2D res = null;
  // if (iCollection.isIndexed())
  // {
  // LocationDocument tLoc = (LocationDocument) iCollection;
  // res = tLoc.interpolateValue(thisTime, Document.InterpMethod.Linear);
  // }
  // else
  // {
  // LocationDocument tLoc = (LocationDocument) iCollection;
  // if (tLoc.size() > 0)
  // {
  // res = tLoc.getLocationIterator().next();
  // }
  // }
  // return res;
  // }

  public static class TimePeriod
  {
    private double startTime;
    private double endTime;

    public TimePeriod(final double tStart, final double tEnd)
    {
      setStartTime(tStart);
      setEndTime(tEnd);
    }

    public boolean invalid()
    {
      return getEndTime() < getStartTime();
    }

    public boolean contains(double time)
    {
      return getStartTime() <= time && getEndTime() >= time;
    }

    public double getStartTime()
    {
      return startTime;
    }

    public void setStartTime(double startTime)
    {
      this.startTime = startTime;
    }

    public double getEndTime()
    {
      return endTime;
    }

    public void setEndTime(double endTime)
    {
      this.endTime = endTime;
    }
  }

  public List<Document> getDocumentsIn(Collection<IStoreItem> selection)
  {
    List<Document> res = new ArrayList<Document>();

    for (IStoreItem sel : selection)
    {
      if (sel instanceof Document)
      {
        res.add((Document) sel);
      }
      else
      {
        processThis(res, (IStoreGroup) sel);
      }
    }

    return res;
  }

  private void processThis(List<Document> target, IStoreGroup selection)
  {
    for (IStoreItem sel : selection)
    {
      if (sel instanceof Document)
      {
        target.add((Document) sel);
      }
      else
      {
        processThis(target, (IStoreGroup) sel);
      }
    }
  }
}
