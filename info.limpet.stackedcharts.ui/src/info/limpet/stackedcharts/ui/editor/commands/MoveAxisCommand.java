package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.impl.ChartImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

public class MoveAxisCommand extends Command
{
  private final DependentAxis axis;
  private final EList<DependentAxis> destination;
  private final EList<DependentAxis> source;
  private int index = -1;

  private int redoIndex = -1;

  public MoveAxisCommand(EList<DependentAxis> destination, DependentAxis axis)
  {
    this.axis = axis;
    this.destination = destination;
    source = getHostListFor(axis);
  }

  public MoveAxisCommand(EList<DependentAxis> destination, DependentAxis axis,
      int index)
  {
    this.axis = axis;
    this.destination = destination;
    this.index = index;
    source = getHostListFor(axis);
  }

  @Override
  public void execute()
  {
    redoIndex = source.indexOf(axis);
    source.remove(axis);

    if (index > -1)
    {
      destination.add(index, axis);
    }
    else
    {
      destination.add(axis);
    }
  }

  @Override
  public void undo()
  {

    destination.remove(axis);

    if (redoIndex != -1)
    {
      source.add(redoIndex, axis);
    }
    else
    {
      source.add(axis);
    }
  }

  /**
   * convenience class to find the relevant list (min/max axes) for the supplied axis
   * 
   * @param axis
   * @return
   */
  public static EList<DependentAxis> getHostListFor(DependentAxis axis)
  {
    ChartImpl chart = (ChartImpl) axis.eContainer();
    // ok, find out which item this is in
    final EList<DependentAxis> minAxes = chart.getMinAxes();
    final EList<DependentAxis> maxAxes = chart.getMaxAxes();

    // check if max has it
    if (maxAxes.contains(axis))
    {
      return maxAxes;
    }

    return minAxes;
  }

}
