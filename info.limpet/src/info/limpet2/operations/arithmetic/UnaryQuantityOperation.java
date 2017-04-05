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
package info.limpet2.operations.arithmetic;

import info.limpet2.Document;
import info.limpet2.ICommand;
import info.limpet2.IContext;
import info.limpet2.IStoreGroup;
import info.limpet2.NumberDocument;
import info.limpet2.operations.CollectionComplianceTests;
import info.limpet2.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Comparisons;
import org.eclipse.january.dataset.Comparisons.Monotonicity;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public abstract class UnaryQuantityOperation 
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public Collection<ICommand> actionsFor(List<Document> selection,
      IStoreGroup destination, IContext context)
  {
    Collection<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {

      // so, do we do our indexed commands?
      if (getATests().allEqualLengthOrSingleton(selection))
      {
        addIndexedCommands(selection, destination, res, context);
      }

      // aah, what about temporal (interpolated) values?
      if (getATests().allIndexed(selection)
          && getATests().suitableForIndexedInterpolation(selection)
          || getATests().hasIndexed(selection)
          && getATests().allEqualLengthOrSingleton(selection))
      {
        addInterpolatedCommands(selection, destination, res, context);
      }
    }

    return res;
  }

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<Document> selection);

  /**
   * produce any new commands for this s election
   * 
   * @param selection
   *          current selection
   * @param destination
   *          where the results will end up
   * @param commands
   *          the list of commands
   */
  protected abstract void addIndexedCommands(List<Document> selection,
      IStoreGroup destination, Collection<ICommand> commands, IContext context);

  /**
   * add any commands that require temporal interpolation
   * 
   * @param selection
   * @param destination
   * @param res
   */
  protected abstract void addInterpolatedCommands(List<Document> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context);

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public abstract class UnaryQuantityCommand extends CoreQuantityCommand
  {

    @SuppressWarnings("unused")
    private final Document timeProvider;

    public UnaryQuantityCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<Document> inputs, IContext context)
    {
      this(title, description, store, canUndo, canRedo, inputs, null, context);
    }

    public UnaryQuantityCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<Document> inputs, Document timeProvider, IContext context)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);

      this.timeProvider = timeProvider;
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     *          the units to use
     * @param outputs
     *          the list of output series
     */
    protected IDataset performCalc()
    {
      final IDataset res;

      final IDataset in1 = getInputs().get(0).getDataset();
      final IDataset in2 = getInputs().get(1).getDataset();

      // look for axes metadata
      final AxesMetadata axis1 = in1.getFirstMetadata(AxesMetadata.class);

      // keep track of the indices to use in the output
      final Dataset outputIndices;

      AxesMetadata axis2 = null;

      final boolean doInterp;

      if (axis1 != null)
      {
        // ok, is that axis monotonic?
        final Monotonicity axis1Mono =
            Comparisons.findMonotonicity(axis1.getAxes()[0]);

        if (axis1Mono.equals(Monotonicity.NOT_ORDERED))
        {
          // ok, not ordered. we can't use it
          doInterp = false;
          outputIndices = null;
        }
        else
        {
          axis2 = in2.getFirstMetadata(AxesMetadata.class);

          final Monotonicity axis2Mono =
              Comparisons.findMonotonicity(axis2.getAxes()[0]);

          if (axis1.getAxes()[0].equals(axis2.getAxes()[0]))
          {
            // identical indexes, we don't need to intepolate
            doInterp = false;
            outputIndices = (Dataset) axis1.getAxes()[0];
          }
          else if (axis2Mono.equals(Monotonicity.NOT_ORDERED))
          {
            // ok, not ordered. we can't use it
            throw new IllegalArgumentException("Axes must be ordered");
          }
          else
          {
            // ok, are they in the same direction?
            if (axis1Mono.equals(axis2Mono))
            {
              // fake index
              outputIndices = (Dataset) axis1.getAxes()[0];

              // ok, do an interpolated add operation.
              doInterp = true;

            }
            else
            {
              // wrong directions, can't do
              doInterp = false;
              outputIndices = null;
            }

          }
        }
      }
      else
      {
        doInterp = false;
        outputIndices = null;
      }

      if (doInterp)
      {
        final InterpolatedMaths.IOperationPerformer doAdd = getOperation();

        Dataset ind1 = null;
        Dataset ind2 = null;
        // extract the datasets
        try
        {
          // load the datasets
          ind1 = DatasetUtils.sliceAndConvertLazyDataset(in1);
          ind2 = DatasetUtils.sliceAndConvertLazyDataset(in2);
        }
        catch (DatasetException e)
        {
          e.printStackTrace();
        }

        if (ind2 != null)
        {
          // apply our operation to the two datasets
          res =
              InterpolatedMaths.performWithInterpolation(ind1, ind2, null,
                  doAdd);
        }
        else
        {
          res = null;
        }

      }
      else if (getATests().allEqualLengthOrSingleton(getInputs()))
      {
        // ok, is one a singleton?

        // ok, can't interpolate. are they the same size?
        // ok, just do plain add
        res = Maths.add(in1, in2);

        // if there are indices, store them
        if (outputIndices != null)
        {
          AxesMetadata am = new AxesMetadataImpl();
          am.initialize(1);
          am.setAxis(0, outputIndices);
          res.addMetadata(am);
        }
      }
      else
      {
        res = null;
      }

      // and fire out the update
      for (Document output : getOutputs())
      {
        output.fireDataChanged();
      }

      // done
      return res;
    }

    /** provide class that can perform required operation
     * 
     * @return
     */
    abstract protected IOperationPerformer getOperation();

    protected Unit<?> getUnits()
    {
      // get the unit
      NumberDocument first = (NumberDocument) getInputs().get(0);
      
      return getUnaryOutputUnit(first.getUnits());
    }


    protected String generateName()
    {
      // get the unit
      NumberDocument first = (NumberDocument) getInputs().get(0);

      return getUnaryNameFor(first.getName());
    }

    /** determine the units of the product
     * 
     * @param first
     * @param second
     * @return
     */
    abstract protected Unit<?>
        getUnaryOutputUnit(Unit<?> first);
    
    /** provide the name for the product dataset
     * 
     * @param name
     * @param name2
     * @return
     */
    abstract protected String getUnaryNameFor(String name);
  }

}
