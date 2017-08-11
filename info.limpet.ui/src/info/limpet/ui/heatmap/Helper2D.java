package info.limpet.ui.heatmap;

import info.limpet.IStoreItem;
import info.limpet.impl.DoubleListDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.january.MetadataException;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class Helper2D
{

  public static class HContainer
  {
    public double[] rowTitles;
    public double[] colTitles;
    public double[][] values;
  }

  private static interface ListConverterHelper
  {

    HContainer convertMe(IStoreItem item);

  }

  public static boolean appliesToMe(final List<IStoreItem> res,
      final CollectionComplianceTests tests)
  {
    final boolean allNonQuantity = tests.allNonQuantity(res);
    final boolean allCollections = tests.allCollections(res);
    final boolean allQuantity = tests.allQuantity(res);
    final boolean suitableIndex =
        tests.allEqualIndexed(res) || tests.allNonIndexed(res);

    // if there are multiple datasets, we need to check they have equal binning
    final boolean equalBins;
    Class<?> firstClass = null;
    if (allCollections && suitableIndex)
    {
      if (res.size() > 0)
      {
        int[] shape = null;
        for (final IStoreItem item : res)
        {
          // check they're of constent type
          final Class<?> thisClass = item.getClass();
          if (firstClass == null)
          {
            firstClass = item.getClass();
          }
          else
          {
            if (!thisClass.equals(firstClass))
            {
              // inconsistent types
              return false;
            }
          }

          if (item instanceof NumberDocument)
          {
            final NumberDocument doc = (NumberDocument) item;
            final DoubleDataset ds = (DoubleDataset) doc.getDataset();
            final int[] thisShape = ds.getShape();
            if (shape == null)
            {
              shape = thisShape;
            }
            else
            {
              if (!Arrays.equals(shape, thisShape))
              {
                // fail, they're not equal
                return false;
              }
            }
          }
          else if (item instanceof DoubleListDocument)
          {
            final DoubleListDocument doc = (DoubleListDocument) item;
            final ObjectDataset ds = (ObjectDataset) doc.getDataset();
            final int[] thisShape = ds.getShape();
            if (shape == null)
            {
              shape = thisShape;
            }
            else
            {
              if (!Arrays.equals(shape, thisShape))
              {
                // fail, they're not equal
                return false;
              }
            }
          }
        }
        // if we've reached here, then we haven't failed
        equalBins = true;
      }
      else
      {
        equalBins = true;
      }
    }
    else
    {
      equalBins = false;
    }

    return allCollections && suitableIndex && (allQuantity || allNonQuantity)
        && equalBins;
  }

  public static HContainer convert(final DoubleDataset dataset)
  {
    final HContainer res = new HContainer();

    // ok, start by changing the table columns to the correct size
    // sort out the axes
    List<AxesMetadata> amList;
    try
    {
      amList = dataset.getMetadata(AxesMetadata.class);
      final AxesMetadata am = amList.get(0);
      final ILazyDataset[] axes = am.getAxes();
      if (axes.length != 2)
      {
        return null;
      }
      final DoubleDataset aOne = (DoubleDataset) axes[0];
      final DoubleDataset aTwo = (DoubleDataset) axes[1];

      final double[] aIndices = aOne.getData();
      res.rowTitles = aIndices;
      final double[] bIndices = aTwo.getData();
      res.colTitles = bIndices;

      res.values = new double[aIndices.length][bIndices.length];
      for (int i = 0; i < aIndices.length; i++)
      {
        for (int j = 0; j < bIndices.length; j++)
        {
          res.values[i][j] = dataset.get(i, j);
        }
      }

    }
    catch (final MetadataException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return res;
  }

  public static HContainer convertToMean(final List<IStoreItem> items)
  {
    // sort out the helper
    final ListConverterHelper helper;
    final IStoreItem first = items.get(0);
    if (first instanceof NumberDocument)
    {
      helper = new ListConverterHelper()
      {

        @Override
        public HContainer convertMe(final IStoreItem item)
        {
          final NumberDocument nd = (NumberDocument) item;
          final DoubleDataset ds = (DoubleDataset) nd.getDataset();
          return convert(ds);
        }
      };
    }
    else if (first instanceof DoubleListDocument)
    {
      helper = new ListConverterHelper()
      {

        @Override
        public HContainer convertMe(final IStoreItem item)
        {
          final DoubleListDocument nd = (DoubleListDocument) item;
          final ObjectDataset ds = (ObjectDataset) nd.getDataset();
          return convert(ds, false);
        }
      };
    }
    else
    {
      throw new IllegalArgumentException("Unexpected data type");
    }

    return convert(items, helper, false);
  }

  public static HContainer convert(final List<IStoreItem> items,
      final ListConverterHelper helper, final boolean count)
  {
    final List<HContainer> containers = new ArrayList<HContainer>();
    for (final IStoreItem item : items)
    {
      final HContainer cont = helper.convertMe(item);
      containers.add(cont);
    }

    // ok, now we need to collate them
    final HContainer res = new HContainer();

    // get the first container, to get the indices
    final HContainer first = containers.get(0);
    final int rows = first.rowTitles.length;
    final int cols = first.colTitles.length;
    res.values = new double[rows][cols];

    res.rowTitles = first.rowTitles;
    res.colTitles = first.colTitles;

    for (int i = 0; i < rows; i++)
    {
      for (int j = 0; j < cols; j++)
      {
        final double value;
        if(count)
        {
          double counter = 0;

          // build up the values at this cell
          for (final HContainer cont : containers)
          {
            final double thisValue = cont.values[i][j];
            if (!Double.isNaN(thisValue))
            {
              counter+= thisValue;
            }
          }

          // and the sum
          value = counter > 0 ? counter : Double.NaN;
        }
        else
        {
          double runningTotal = 0;
          double counter = 0;

          // build up the values at this cell
          for (final HContainer cont : containers)
          {
            final double thisValue = cont.values[i][j];
            if (!Double.isNaN(thisValue))
            {
              runningTotal += thisValue;
              counter++;
            }
          }

          // and the sum
          value = runningTotal / counter;
        }
        res.values[i][j] = value;
      }
    }

    return res;
  }

  public static HContainer convert(final ObjectDataset dataset,
      final boolean count)
  {
    final HContainer res = new HContainer();

    // ok, start by changing the table columns to the correct size
    // sort out the axes
    List<AxesMetadata> amList;
    try
    {
      amList = dataset.getMetadata(AxesMetadata.class);
      final AxesMetadata am = amList.get(0);
      final ILazyDataset[] axes = am.getAxes();
      if (axes.length != 2)
      {
        return null;
      }
      final DoubleDataset aOne = (DoubleDataset) axes[0];
      final DoubleDataset aTwo = (DoubleDataset) axes[1];

      final double[] aIndices = aOne.getData();
      res.rowTitles = aIndices;
      final double[] bIndices = aTwo.getData();
      res.colTitles = bIndices;

      res.values = new double[aIndices.length][bIndices.length];
      for (int i = 0; i < aIndices.length; i++)
      {
        for (int j = 0; j < bIndices.length; j++)
        {
          @SuppressWarnings("unchecked")
          final List<Double> items = (List<Double>) dataset.get(i, j);
          if (items != null)
          {
            final double value;
            if (count)
            {
              int ctr = 0;
              for (final Double t : items)
              {
                if (!Double.isNaN(t))
                {
                  ctr++;
                }
              }
              value = ctr >= 0 ? ctr : Double.NaN;
            }
            else
            {
              double total = 0;
              int ctr = 0;
              for (final Double t : items)
              {
                if (!Double.isNaN(t))
                {
                  total += t;
                  ctr++;
                }
              }
              value = total / ctr;
            }
            res.values[i][j] = value;

          }
          else
          {
            res.values[i][j] = Double.NaN;
          }
        }
      }

    }
    catch (final MetadataException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return res;
  }

  public static String indexUnitsFor(final List<IStoreItem> items)
  {
    final IStoreItem first = items.get(0);
    final String res;
    if (first instanceof NumberDocument)
    {
      final NumberDocument nd = (NumberDocument) first;
      res = nd.getIndexUnits().toString();
    }
    else if (first instanceof DoubleListDocument)
    {
      final DoubleListDocument nd = (DoubleListDocument) first;
      res = nd.getIndexUnits().toString();
    }
    else
    {
      throw new IllegalArgumentException("Wrong input type");
    }

    return res;
  }

  public static String titleFor(final List<IStoreItem> items)
  {
    String seriesName = "";
    for (final IStoreItem item : items)
    {
      if (!"".equals(seriesName))
      {
        seriesName += ", ";
      }

      if (item instanceof NumberDocument)
      {
        final NumberDocument thisQ = (NumberDocument) item;
        seriesName += item.getName() + " (" + thisQ.getUnits().toString() + ")";
      }
      else if (item instanceof DoubleListDocument)
      {
        final DoubleListDocument thisQ = (DoubleListDocument) item;
        seriesName += item.getName() + " (" + thisQ.getUnits().toString() + ")";
      }

    }
    return seriesName;
  }

  public static HContainer convertToCount(List<IStoreItem> items)
  {
    // sort out the helper
    final ListConverterHelper helper;
    final IStoreItem first = items.get(0);
    if (first instanceof NumberDocument)
    {
      helper = new ListConverterHelper()
      {

        @Override
        public HContainer convertMe(final IStoreItem item)
        {
          final NumberDocument nd = (NumberDocument) item;
          final DoubleDataset ds = (DoubleDataset) nd.getDataset();
          return convert(ds);
        }
      };
    }
    else if (first instanceof DoubleListDocument)
    {
      helper = new ListConverterHelper()
      {

        @Override
        public HContainer convertMe(final IStoreItem item)
        {
          final DoubleListDocument nd = (DoubleListDocument) item;
          final ObjectDataset ds = (ObjectDataset) nd.getDataset();
          return convert(ds, true);
        }
      };
    }
    else
    {
      throw new IllegalArgumentException("Unexpected data type");
    }

    return convert(items, helper, true);
  };

}
