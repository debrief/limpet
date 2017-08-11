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
  public static HContainer convert(ObjectDataset dataset)
  {
    HContainer res = new HContainer();

    // ok, start by changing the table columns to the correct size
    // sort out the axes
    List<AxesMetadata> amList;
    try
    {
      amList = dataset.getMetadata(AxesMetadata.class);
      AxesMetadata am = amList.get(0);
      ILazyDataset[] axes = am.getAxes();
      if (axes.length != 2)
      {
        return null;
      }
      DoubleDataset aOne = (DoubleDataset) axes[0];
      DoubleDataset aTwo = (DoubleDataset) axes[1];

      double[] aIndices = aOne.getData();
      res.rowTitles = aIndices;
      double[] bIndices = aTwo.getData();
      res.colTitles = bIndices;

      res.values = new double[aIndices.length][bIndices.length];
      for (int i = 0; i < aIndices.length; i++)
      {
        for (int j = 0; j < bIndices.length; j++)
        {
          @SuppressWarnings("unchecked")
          List<Double> items = (List<Double>) dataset.get(i, j);
          if(items != null)
          {
            double total = 0;
            int ctr = 0;
            for(Double t: items)
            {
              if(!Double.isNaN(t))
              {
                total += t;
                ctr++;
              }
            }
            res.values[i][j] = total / ctr; 
          }
          else
          {
            res.values[i][j] = Double.NaN; 
          }
        }
      }

    }
    catch (MetadataException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return res;
  }

  public static HContainer convert(DoubleDataset dataset)
  {
    HContainer res = new HContainer();

    // ok, start by changing the table columns to the correct size
    // sort out the axes
    List<AxesMetadata> amList;
    try
    {
      amList = dataset.getMetadata(AxesMetadata.class);
      AxesMetadata am = amList.get(0);
      ILazyDataset[] axes = am.getAxes();
      if (axes.length != 2)
      {
        return null;
      }
      DoubleDataset aOne = (DoubleDataset) axes[0];
      DoubleDataset aTwo = (DoubleDataset) axes[1];

      double[] aIndices = aOne.getData();
      res.rowTitles = aIndices;
      double[] bIndices = aTwo.getData();
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
    catch (MetadataException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return res;
  }

  private static interface ListConverterHelper
  {

    HContainer convertMe(IStoreItem item);
    
  }
  
  
  public static HContainer convert(List<IStoreItem> items)
  {
    // sort out the helper
    final ListConverterHelper helper;
    final IStoreItem first = items.get(0);
    if(first instanceof NumberDocument)
    {
      helper = new ListConverterHelper(){

        @Override
        public HContainer convertMe(IStoreItem item)
        {
          NumberDocument nd = (NumberDocument) item;
          DoubleDataset ds = (DoubleDataset) nd.getDataset();
          return convert(ds);
        }};
    }else if(first instanceof DoubleListDocument)
    {
      helper = new ListConverterHelper(){

        @Override
        public HContainer convertMe(IStoreItem item)
        {
          DoubleListDocument nd = (DoubleListDocument) item;
          ObjectDataset ds = (ObjectDataset) nd.getDataset();
          return convert(ds);
        }};
    }
    else
    {
      throw new IllegalArgumentException("Unexpected data type");
    }
    
    return convert(items, helper);
  }
  
  public static HContainer convert(List<IStoreItem> items, final ListConverterHelper helper)
  {
    List<HContainer> containers = new ArrayList<HContainer>();
    for (IStoreItem item : items)
    {
      HContainer cont = helper.convertMe(item);
      containers.add(cont);
    }

    // ok, now we need to collate them
    HContainer res = new HContainer();

    // get the first container, to get the indices
    HContainer first = containers.get(0);
    final int rows = first.rowTitles.length;
    final int cols = first.colTitles.length;
    res.values = new double[rows][cols];

    res.rowTitles = first.rowTitles;
    res.colTitles = first.colTitles;

    for (int i = 0; i < rows; i++)
    {
      for (int j = 0; j < cols; j++)
      {
        double runningTotal = 0;
        double counter = 0;

        // build up the values at this cell
        for (HContainer cont : containers)
        {
          final double thisValue = cont.values[i][j];
          if (!Double.isNaN(thisValue))
          {
            runningTotal += thisValue;
            counter++;
          }
        }

        // and the sum
        double mean = runningTotal / counter;

        res.values[i][j] = mean;
      }
    }

    return res;
  }

  public static boolean appliesToMe(List<IStoreItem> res,
      CollectionComplianceTests tests)
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
        for (IStoreItem item : res)
        {
          // check they're of constent type
          Class<?> thisClass = item.getClass();
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
            NumberDocument doc = (NumberDocument) item;
            DoubleDataset ds = (DoubleDataset) doc.getDataset();
            int[] thisShape = ds.getShape();
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
            DoubleListDocument doc = (DoubleListDocument) item;
            ObjectDataset ds = (ObjectDataset) doc.getDataset();
            int[] thisShape = ds.getShape();
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
  
  public static String titleFor(final List<IStoreItem> items)
  {
    String seriesName = "";
    for(IStoreItem item: items)
    {
      if(!seriesName.equals(""))
      {
        seriesName += ", ";
      }
      
      if(item instanceof NumberDocument)
      {
        final NumberDocument thisQ = (NumberDocument) item;
        seriesName += item.getName() + " (" + thisQ.getUnits().toString() + ")";
      }
      else if(item instanceof DoubleListDocument)
      {
        final DoubleListDocument thisQ = (DoubleListDocument) item;
        seriesName += item.getName() + " (" + thisQ.getUnits().toString() + ")";
      }
      
    }
    return seriesName;
  }

  public static String indexUnitsFor(List<IStoreItem> items)
  {
    IStoreItem first = items.get(0);
    final String res;
    if(first instanceof NumberDocument)
    {
      NumberDocument nd = (NumberDocument) first;
      res  = nd.getIndexUnits().toString();
    }else if(first instanceof DoubleListDocument)
    {
      DoubleListDocument nd = (DoubleListDocument) first;
      res  = nd.getIndexUnits().toString();      
    }
    else
    {
      throw new IllegalArgumentException("Wrong input type");
    }
    
    return res;
  };

}
