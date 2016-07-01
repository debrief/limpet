package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

public class AddScatterSetsToChartCommand extends Command
{
  private final ScatterSet[] scatterSets;
  private final Chart parent;

  public AddScatterSetsToChartCommand(Chart parent, ScatterSet... scatterSets)
  {
    this.scatterSets = scatterSets;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    for (ScatterSet ds : scatterSets)
    {
      // ok, we may have to add it to the chartset first
      ChartSet charts = parent.getParent();
      EList<SelectiveAnnotation> annots =
          charts.getSharedAxis().getAnnotations();
      SelectiveAnnotation host = null;
      for (SelectiveAnnotation annot : annots)
      {
        if (annot.getAnnotation().getName() != null
            && annot.getAnnotation().getName().equals(ds.getName()))
        {
          host = annot;
          break;
        }
      }

      if (host == null)
      {
        StackedchartsFactory factory = new StackedchartsFactoryImpl();
        host = factory.createSelectiveAnnotation();
        host.setAnnotation(ds);
        annots.add(host);
      }

      // check we're not already in that chart
      EList<Chart> appearsIn = host.getAppearsIn();
      if (!appearsIn.contains(parent))
      {
        appearsIn.add(parent);
      }
    }
  }

  @Override
  public void undo()
  {
    for (@SuppressWarnings("unused")
    ScatterSet ds : scatterSets)
    {
      // parent.getDatasets().remove(ds);
      System.err.println("UNDO ADDING SCATTERSET NOT IMPLEMENTED");
    }
  }
}
