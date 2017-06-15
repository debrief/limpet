package info.limpet.operations.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.unit.Unit;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

public class GenerateGrid implements IOperation
{

  private class GenerateGridCommand extends AbstractCommand
  {

    private Triplet triplet;

    public GenerateGridCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<IStoreItem> inputs, IContext context, Triplet triplet)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);
      this.triplet = triplet;
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      System.out.println("gridding on " + triplet.measurements);
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
        // produce the title
        String title = "Collate grid of " + thisP.measurements;

        // produce the description
        String description =
            "Collate grid of " + thisP.measurements + " based on "
                + thisP.axisOne + " and " + thisP.axisTwo;

        res.add(new GenerateGridCommand(title, description, destination, true,
            true, selection, context, thisP));
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
          if(commonUnit == null)
          {
            commonUnit = index;
          }
          else
          {
            if(!index.equals(commonUnit))
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
        ArrayList<NumberDocument> list = matches.get(matches.keySet().iterator().next());
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
