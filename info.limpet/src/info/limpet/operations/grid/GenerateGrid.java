package info.limpet.operations.grid;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.analysis.QuantityFrequencyBins;
import info.limpet.analysis.QuantityFrequencyBins.Bin;
import info.limpet.analysis.QuantityFrequencyBins.BinnedData;
import info.limpet.impl.Document;
import info.limpet.impl.DoubleListDocument;
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

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class GenerateGrid implements IOperation
{

  private static interface DataProcessor
  {
    Document<?> getOutputDocument(final ICommand predecessor,
        final Unit<?> units);

    String outName();

    Dataset processGrid(final List<Double>[][] grid, final double[] oneBins,
        final double[] twoBins);
  }

  public static class GenerateGridCommand extends AbstractCommand
  {
    final private Triplet triplet;
    final private double[] bins;
    final private DataProcessor helper;

    public GenerateGridCommand(final String title, final String description,
        final IStoreGroup store, final List<IStoreItem> inputs,
        final IContext context, final Triplet triplet, final double[] bins,
        final DataProcessor helper)
    {
      super(title, description, store, true, true, inputs, context);
      this.triplet = triplet;
      this.bins = bins;
      this.helper = helper;
    }

    public int binFor(final double[] bins, final double vOne)
    {
      // find the bin for this value
      for (int i = 0; i < bins.length; i++)
      {
        final double thisLimit = bins[i];

        if (thisLimit >= vOne)
        {
          return i;
        }
      }

      return bins.length-1;
    }

    public static double[] binsFor(final NumberDocument axis)
    {
      final double[] res;

      // are these degrees?
      if (axis.getUnits().equals(SampleData.DEGREE_ANGLE))
      {
        res = new double[]
        {45, 90, 135, 180, 225, 270, 315, 360};
      }
      else
      {
        // collate the values into an array
        DoubleDataset dd = (DoubleDataset) axis.getDataset();
        double[] data = dd.getData();

        // ok, now bin the data
        BinnedData binnedData = QuantityFrequencyBins.binTheseValues(data);

        // convert to array
        res = new double[binnedData.size()];
        int ctr = 0;
        for (Bin d : binnedData)
        {
          res[ctr++] = d.getLowerVal();
        }
      }

      return res;
    }

    @Override
    public void execute()
    {
      // create the output document
      final Document<?> nd =
          helper.getOutputDocument(this, triplet.measurements.getUnits());

      super.getOutputs().add(nd);

      performCalc();

      // set the name
      nd.setName(helper.outName() + " of " + triplet.measurements.getName());

      // also set the index units
      nd.setIndexUnits(triplet.axisOne.getUnits());

      super.getStore().add(nd);

      super.execute(); // listens to the inputs
    }

    public void performCalc()
    {
      // ok, generate the bins.
      final double[] oneBins;
      final double[] twoBins;
      if (bins != null)
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
      final List<Double>[][] grid = new List[oneBins.length][twoBins.length];

      // ok, loop through the data
      final Iterator<Double> oneIter = triplet.axisOne.getIterator();
      final Iterator<Double> twoIter = triplet.axisTwo.getIterator();
      final Iterator<Double> valIter = triplet.measurements.getIterator();

      while (oneIter.hasNext())
      {
        final double vOne = oneIter.next();
        final double vTwo = twoIter.next();
        final double vVal = valIter.next();

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

      final Dataset processed = helper.processGrid(grid, oneBins, twoBins);

      // insert the metadata
      final AxesMetadata am = new AxesMetadataImpl();
      am.initialize(2);
      final Dataset xAxis = DatasetFactory.createFromObject(oneBins);
      xAxis.setName(triplet.axisOne.getName());
      final Dataset yAxis = DatasetFactory.createFromObject(twoBins);
      yAxis.setName(triplet.axisTwo.getName());
      am.setAxis(0, xAxis);
      am.setAxis(1, yAxis);
      processed.addMetadata(am);

      // get the output doc
      final Document<?> nd = (Document<?>) super.getOutputs().get(0);
      
      // store the results object in it
      nd.setDataset(processed);
      
      // restore the name
      nd.setName(helper.outName() + " of " + triplet.measurements.getName());
    }

    @Override
    protected void recalculate(final IStoreItem subject)
    {
      performCalc();
    }

    @Override
    public void redo()
    {
      // TODO Auto-generated method stub
      super.redo();
    }

    @Override
    public void undo()
    {
      // TODO Auto-generated method stub
      super.undo();
    }

  }

  private static class Triplet
  {
    private NumberDocument axisOne;
    private NumberDocument axisTwo;
    private NumberDocument measurements;
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private class SampleHelper implements DataProcessor
  {
    @Override
    public Document<?> getOutputDocument(final ICommand predecessor,
        final Unit<?> units)
    {
      return new DoubleListDocument(null, predecessor, units);
    }

    @Override
    public Dataset processGrid(final List<Double>[][] grid,
        final double[] oneBins, final double[] twoBins)
    {
      return doSampleGrid(grid, oneBins, twoBins);
    }

    @Override
    public String outName()
    {
      return "Collated samples of";
    }
  }
  
  private class MeanHelper implements DataProcessor 
  {

    @Override
    public Document<?> getOutputDocument(final ICommand predecessor,
        final Unit<?> units)
    {
      return new NumberDocument(null, predecessor, units);
    }

    @Override
    public Dataset processGrid(final List<Double>[][] grid,
        final double[] oneBins, final double[] twoBins)
    {
      return doMeanGrid(grid, oneBins, twoBins);
    }

    @Override
    public String outName()
    {
      return "Calculated mean of";
    }

  };
  
  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();

    // check the data
    final List<Triplet> perms = findPermutations(selection, aTests);
    if (perms != null && perms.size() > 0)
    {
      final DataProcessor meanHelper = new MeanHelper();
      final DataProcessor sampleHelper = new SampleHelper();

      // ok, put them into actions
      for (final Triplet thisP : perms)
      {
        // special case. check the units of the axes
        final Unit<?> aUnits = thisP.axisOne.getUnits();
        if (aUnits.equals(SampleData.DEGREE_ANGLE))
        {
          // ok, we'll do the fancy degree grid
          final String sampleTitle =
              "Collate 360 deg grid of samples of  " + thisP.measurements;
          final String meanTitle =
              "Collate 360 deg grid of calculated means of "
                  + thisP.measurements;

          // produce the description
          final String description =
              "Collate 360 degree grid of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;

          final double[] bins = new double[]
          {45, 90, 135, 180, 225, 270, 315, 360};

          res.add(new GenerateGridCommand(meanTitle, description, destination,
              selection, context, thisP, bins, meanHelper));

          res.add(new GenerateGridCommand(sampleTitle, description,
              destination, selection, context, thisP, bins, sampleHelper));
        }
        else
        {
          // produce the title
          final String sampleTitle =
              "Collate grid of samples of  " + thisP.measurements;
          final String meanTitle =
              "Collate grid of calculated means of " + thisP.measurements;

          // produce the description
          final String description =
              "Collate grid of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;

          res.add(new GenerateGridCommand(meanTitle, description, destination,
              selection, context, thisP, null, meanHelper));
          res.add(new GenerateGridCommand(sampleTitle, description,
              destination, selection, context, thisP, null, sampleHelper));
        }
      }
    }
    return res;
  }

  protected Dataset doMeanGrid(final List<Double>[][] grid,
      final double[] oneBins, final double[] twoBins)
  {
    final double[][] means = new double[oneBins.length][twoBins.length];

    // ok, populate it
    for (int i = 0; i < oneBins.length; i++)
    {
      for (int j = 0; j < twoBins.length; j++)
      {
        final List<Double> list = grid[i][j];
        final double thisV;
        if (list != null)
        {
          double total = 0;
          for (final Double d : list)
          {
            total += d;
          }
          thisV = total / list.size();
        }
        else
        {
          thisV = Double.NaN;
        }
        means[i][j] = thisV;
      }
    }
    // now put the grid into a dataset
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(means);

    return ds;
  }

  protected Dataset doSampleGrid(final List<Double>[][] grid,
      final double[] oneBins, final double[] twoBins)
  {
    final ObjectDataset ds =
        DatasetFactory.zeros(ObjectDataset.class, new int[]
        {8, 8});

    // ok, populate it
    for (int i = 0; i < oneBins.length; i++)
    {
      for (int j = 0; j < twoBins.length; j++)
      {
        final List<Double> list = grid[i][j];
        ds.set(list, i, j);
      }
    }

    return ds;

  }

  public static List<Triplet> findPermutations(
      final List<IStoreItem> selection, CollectionComplianceTests aTests)
  {
    final List<Triplet> res = new ArrayList<Triplet>();

    if (aTests.allEqualLength(selection) && selection.size() == 3)
    {
      final Map<Unit<?>, ArrayList<NumberDocument>> matches =
          new HashMap<Unit<?>, ArrayList<NumberDocument>>();

      Unit<?> unitA = null;

      // do the binning
      for (final IStoreItem item : selection)
      {
        if (item instanceof NumberDocument)
        {
          final NumberDocument doc = (NumberDocument) item;

          // check the index units
          final Unit<?> index = doc.getIndexUnits();
          if (unitA == null)
          {
            unitA = index;
          }
          else
          {
            if (!index.equals(unitA))
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
        final ArrayList<NumberDocument> list =
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
        final Triplet thisT = new Triplet();

        // ok, it's obvious. find the singleton
        for (final ArrayList<NumberDocument> thisL : matches.values())
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

  private static Triplet tripletFor(final ArrayList<NumberDocument> list)
  {
    final Triplet res = new Triplet();
    res.axisOne = list.get(0);
    res.axisTwo = list.get(1);
    res.measurements = list.get(2);

    return res;
  }
}
