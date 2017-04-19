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
package info.limpet2.operations.spatial;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IContext;
import info.limpet2.Document;
import info.limpet2.ICommand;
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.LocationDocument;
import info.limpet2.NumberDocument;
import info.limpet2.NumberDocumentBuilder;
import info.limpet2.SampleData;
import info.limpet2.operations.AbstractCommand;
import info.limpet2.operations.CollectionComplianceTests;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class GenerateCourseAndSpeedOperation implements IOperation
{

  protected abstract static class DistanceOperation extends AbstractCommand
  {

    public DistanceOperation(List<IStoreItem> selection, IStoreGroup store,
        String title, String description, IContext context)
    {
      super(title, description, store, false, false, selection, context);
    }

    /** produce a name for the output
     * 
     * @return
     */
    abstract protected String getOutputName();

    @Override
    public void execute()
    {
      // get the unit
      List<Document> outputs = new ArrayList<Document>();

      String prefix = getOutputName();

      if (prefix == null)
      {
        return;
      }

      // ok, generate the new series
      for (int i = 0; i < getInputs().size(); i++)
      {
        LocationDocument thisInput = (LocationDocument) getInputs().get(i);
        String name = getOutputName();
        final Unit<?> units = getUnits();

        DoubleDataset res = (DoubleDataset) performCalc(thisInput, name, units);

        NumberDocument doc = new NumberDocument(res, this, units);

        outputs.add(doc);
        // store the output
        super.addOutput(doc);
      }

      // tell each series that we're a dependent
      Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        Document iCollection = (Document) iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      getStore().addAll(outputs);
    }

    protected abstract Unit<?> getUnits();

    /**
     * for unitary operations we only act on a single input. We may be acting on an number of
     * datasets, so find the relevant one, and re-calculate it
     */
    protected void recalculate(IStoreItem subject)
    {
      // TODO: change logic, we should only re-generate the
      // single output

      // workaround: we don't know which output derives
      // from this input. So, we will have to regenerate
      // all outputs

      Iterator<Document> oIter = getOutputs().iterator();

      // we may be acting separately on multiple inputs.
      // so, loop through them
      for (final IStoreItem input : getInputs())
      {
        final LocationDocument inputDoc = (LocationDocument) input;
        final NumberDocument outputDoc = (NumberDocument) oIter.next();

        // start adding values.
        IDataset dataset =
            performCalc(inputDoc, outputDoc.getName(), outputDoc.getUnits());

        // store the data
        outputDoc.setDataset(dataset);

        // and fire out the update
        outputDoc.fireDataChanged();
      }
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     * @param outputs
     */
    private IDataset performCalc(LocationDocument thisTrack, String name,
        Unit<?> units)
    {
      // get a calculator to use
      final IGeoCalculator calc = GeoSupport.getCalculator();

      final NumberDocumentBuilder builder =
          new NumberDocumentBuilder(name, units, this);

      // get the objects
      ObjectDataset od = (ObjectDataset) thisTrack.getDataset();

      // get the time indices
      AxesMetadata am = od.getFirstMetadata(AxesMetadata.class);
      if (am == null)
      {
        throw new IllegalArgumentException(
            "Index metadata missing for this dataset");
      }
      ILazyDataset amdl = am.getAxis(0)[0];
      Dataset amd = null;
      try
      {
        amd = DatasetUtils.sliceAndConvertLazyDataset(amdl);
      }
      catch (DatasetException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (amd == null)
      {
        // ok, failed - drop out
        throw new RuntimeException("Failed to slice lazy datset");
      }

      // remember the last value
      long lastTime = 0;
      Point2D lastLocation = null;

      for (int i = 0; i < od.getSize(); i++)
      {
        Point2D thisP = (Point2D) od.getObject(i);
        long thisT = amd.getLong(i);

        if (lastLocation != null)
        {
          calcAndStore(builder, calc, lastTime, lastLocation, thisT, thisP);
        }

        // and remember the values
        lastLocation = thisP;
        lastTime = thisT;

      }

      return builder.toDocument().getDataset();

    }

    protected abstract void calcAndStore(NumberDocumentBuilder thisOut,
        final IGeoCalculator calc, final long timeA, final Point2D locA,
        final long timeB, final Point2D locB);
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean nonEmpty = aTests.nonEmpty(selection);
    boolean allTemporal = aTests.allIndexed(selection);

    return nonEmpty && allTemporal && aTests.allNonQuantity(selection)
        && aTests.allLocation(selection);
  }

  public Collection<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    Collection<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {

      int len = selection.size();
      final String title;
      if (len > 1)
      {
        title = "Generate course for track";
      }
      else
      {
        title = "Generate course for tracks";
      }

      ICommand genCourse =
          new DistanceOperation(selection, destination,
              "Generate calculated course", title, context)
          {
            protected Unit<?> getUnits()
            {
              return SampleData.DEGREE_ANGLE;
            }

            @Override
            protected String getOutputName()
            {
              return getContext().getInput("Generate course",
                  "Please provide a dataset prefix", "Generated course for ");
            }

            protected void calcAndStore(NumberDocumentBuilder target,
                final IGeoCalculator calc, long lastTime, final Point2D locA,
                long thisTime, final Point2D locB)
            {

              // now find the bearing between them
              double angleDegs = calc.getAngleBetween(locA, locB);
              if (angleDegs < 0)
              {
                angleDegs += 360;
              }

              target.add(thisTime, angleDegs);
            }
          };
      ICommand genSpeed =
          new DistanceOperation(selection, destination,
              "Generate calculated speed", title, context)
          {
            protected Unit<?> getUnits()
            {
              return METRE.divide(SECOND).asType(Velocity.class);
            }

            protected String getOutputName()
            {
              return getContext().getInput("Generate speed",
                  "Please provide a dataset prefix", "Generated speed for ");
            }

            protected void calcAndStore(NumberDocumentBuilder target,
                final IGeoCalculator calc, long lastTime, final Point2D locA,
                long thisTime, final Point2D locB)
            {
              // now find the range between them
              double thisDist = calc.getDistanceBetween(locA, locB);
              double calcTime = thisTime - lastTime;
              double thisSpeed = thisDist / (calcTime / 1000d);
              target.add(thisTime, thisSpeed);
            }
          };

      res.add(genCourse);
      res.add(genSpeed);
    }

    return res;
  }
}
