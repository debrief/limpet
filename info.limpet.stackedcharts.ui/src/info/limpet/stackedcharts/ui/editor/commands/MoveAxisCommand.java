package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

public class MoveAxisCommand extends Command
{
  private final DependentAxis[] axes;
  private final EList<DependentAxis> destination;
  
  /** keep track of where axes came from, so 
   * operation can be undone
   */   
  private EList<DependentAxis>[] sources;

  public MoveAxisCommand(EList<DependentAxis> destination,
      DependentAxis... axes)
  {
    this.axes = axes;
    this.destination = destination;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute()
  {
    boolean sourcesAssigned = false;
    int ctr = 0;

    if (!sourcesAssigned)
    {
      sources = new EList[axes.length];
    }

    for (DependentAxis ds : axes)
    {
      EList<DependentAxis> source = null;
      if (sourcesAssigned)
      {
        source = sources[ctr++];
      }
      else
      {
        // find the current parent axis
        final Chart parent = (Chart) ds.eContainer();
        EList<DependentAxis> minAxes = parent.getMinAxes();
        EList<DependentAxis> maxAxes = parent.getMaxAxes();

        for (DependentAxis da : minAxes)
        {
          if (da.equals(ds))
          {
            source = minAxes;
            break;
          }
        }
        for (DependentAxis da : maxAxes)
        {
          if (da.equals(ds))
          {
            source = maxAxes;
            break;
          }
        }

        sources[ctr++] = source;
      }

      // now add it to the new host
      destination.add(ds);
    }

    sourcesAssigned = true;
  }

  @Override
  public void undo()
  {
    int ctr = 0;
    for (DependentAxis ds : axes)
    {
      // and add it to its host
      EList<DependentAxis> newHost = sources[ctr++];
      newHost.add(ds);
    }
  }
}
