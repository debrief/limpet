package info.limpet.operations.grid;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;

public class GenerateGrid implements IOperation
{

  public static class GenerateGridCommand extends AbstractCommand
  {

    final private Triplet triplet;
    final private double[] bins;

    public GenerateGridCommand(String title, String description,
        IStoreGroup store, List<IStoreItem> inputs, IContext context,
        Triplet triplet, double[] bins)
    {
      super(title, description, store, true, true, inputs, context);
      this.triplet = triplet;
      this.bins = bins;
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void execute()
    {
      // ok, generate the bins.
      final double[] oneBins;
      final double[] twoBins;
      if(bins != null)
      {
        oneBins = bins;
        twoBins = bins;        
      }
      else
      {
        oneBins = binsFor(triplet.axisOne);
        twoBins = binsFor(triplet.axisTwo);        
      }

      // output dataset
      @SuppressWarnings("unchecked")
      List<Double>[][] grid = new List[oneBins.length][twoBins.length];

      // ok, loop through the data
      Iterator<Double> oneIter = triplet.axisOne.getIterator();
      Iterator<Double> twoIter = triplet.axisTwo.getIterator();
      Iterator<Double> valIter = triplet.measurements.getIterator();

      while (oneIter.hasNext())
      {
        double vOne = oneIter.next();
        double vTwo = twoIter.next();
        double vVal = valIter.next();

        // work out the x axis
        final int i = binFor(oneBins, vOne);

        // work out the y axis
        final int j = binFor(twoBins, vTwo);

        // store the variable
        if (grid[i][j] == null)
        {
          grid[i][j] = new ArrayList<Double>();
        }
        grid[i][j].add(vVal);
      }

      // ok, now we do the stats
      double[][] means = new double[oneBins.length][twoBins.length];

      // ok, populate it
      for (int i = 0; i < oneBins.length; i++)
      {
        for (int j = 0; j < twoBins.length; j++)
        {
          List<Double> list = grid[i][j];
          final double thisV;
          if (list != null)
          {
            double total = 0;
            for (Double d : list)
            {
              total += d;
            }
            thisV = total / list.size();
            
            System.out.println("mean of " + list.size() + " items is:" + thisV);
            
          }
          else
          {
            thisV = Double.NaN;
          }
          means[i][j] = thisV;
        }
      }

      // now put the grid into a dataset
      DoubleDataset ds = (DoubleDataset) DatasetFactory.createFromObject(means);

      NumberDocument nd =
          new NumberDocument(ds, this, triplet.measurements.getUnits());

      super.getOutputs().add(nd);
      super.getStore().add(nd);

      super.execute();
    }

    public int binFor(double[] bins, double vOne)
    {
      // find the bin for this value
      for (int i = 0; i < bins.length; i++)
      {
        double thisLimit = bins[i];

        if (thisLimit >= vOne)
        {
          return i;
        }
      }

      return -1;
    }

    @Override
    public void undo()
    {
      // TODO Auto-generated method stub
      super.undo();
    }

    @Override
    public void redo()
    {
      // TODO Auto-generated method stub
      super.redo();
    }

    public double[] binsFor(final NumberDocument axis)
    {
      // collate the values into an array
      // DoubleDataset dd = (DoubleDataset) axis.getDataset();;
      // double[] data = dd.getData();
      //
      // // Get a DescriptiveStatistics instance
      // DescriptiveStatistics stats = new DescriptiveStatistics(data);

      final double[] res;

      // are these degrees?
      if (axis.getUnits().equals(SampleData.DEGREE_ANGLE))
      {
        res = new double[]
        {45, 90, 135, 180, 225, 270, 315, 360};
      }
      else
      {
        res = null;
      }

      return res;
    }

  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  @Override
  public List<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();

    // check the data
    List<Triplet> perms = findPermutations(selection);
    if (perms != null && perms.size() > 0)
    {
      // ok, put them into actions
      for (Triplet thisP : perms)
      {
        // special case. check the units of the axes
        final Unit<?> aUnits = thisP.axisOne.getUnits();
        if(aUnits.equals(SampleData.DEGREE_ANGLE))
        {
          // ok, we'll do the fancy degree grid
          String title = "Collate 360 deg grid of " + thisP.measurements;

          // produce the description
          String description =
              "Collate grid of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;

          double[] bins = new double[]
              {45, 90, 135, 180, 225, 270, 315, 360};
          
          res.add(new GenerateGridCommand(title, description, destination,
              selection, context, thisP, bins));
        }
        else
        {
          // produce the title
          String title = "Collate grid of " + thisP.measurements;

          // produce the description
          String description =
              "Collate grid of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;

          res.add(new GenerateGridCommand(title, description, destination,
              selection, context, thisP, null));
        }
      }
    }
    return res;
  }

  private static class Triplet
  {
    NumberDocument axisOne;
    NumberDocument axisTwo;
    NumberDocument measurements;
  }

  private List<Triplet> findPermutations(List<IStoreItem> selection)
  {
    List<Triplet> res = new ArrayList<Triplet>();

    if (aTests.allEqualLength(selection) && selection.size() == 3)
    {
      Map<Unit<?>, ArrayList<NumberDocument>> matches =
          new HashMap<Unit<?>, ArrayList<NumberDocument>>();

      Unit<?> commonUnit = null;

      // do the binning
      for (final IStoreItem item : selection)
      {
        if (item instanceof NumberDocument)
        {
          final NumberDocument doc = (NumberDocument) item;

          // check the index units
          Unit<?> index = doc.getIndexUnits();
          if (commonUnit == null)
          {
            commonUnit = index;
          }
          else
          {
            if (!index.equals(commonUnit))
            {
              return null;
            }
          }

          final Unit<?> units = doc.getUnits();

          ArrayList<NumberDocument> list = matches.get(units);
          if (list == null)
          {
            list = new ArrayList<NumberDocument>();
            matches.put(units, list);
          }

          list.add(doc);
        }
      }

      // ok, do we have enough items
      if (matches.size() == 1)
      {
        // ok, we need to offer all three as the measurement
        ArrayList<NumberDocument> list =
            matches.get(matches.keySet().iterator().next());
        res.add(tripletFor(list));

        // ok, push the first item to the end
        list.add(list.remove(0));
        res.add(tripletFor(list));

        // ok, push the next item to the end
        list.add(list.remove(0));
        res.add(tripletFor(list));

      }
      else if (matches.size() == 2)
      {
        Triplet thisT = new Triplet();

        // ok, it's obvious. find the singleton
        for (ArrayList<NumberDocument> thisL : matches.values())
        {
          if (thisL.size() == 1)
          {
            // here is it
            thisT.measurements = thisL.get(0);
          }
          else if (thisL.size() == 2)
          {
            // here they are
            thisT.axisOne = thisL.get(0);
            thisT.axisTwo = thisL.get(1);
          }
          else
          {
            throw new IllegalArgumentException(
                "Unable to organise inputs for gridding operation");
          }
        }

        res.add(thisT);
      }

    }
    // see if

    return res;
  }

  private Triplet tripletFor(ArrayList<NumberDocument> list)
  {
    Triplet res = new Triplet();
    res.axisOne = list.get(0);
    res.axisTwo = list.get(1);
    res.measurements = list.get(2);

    return res;
  }
}
