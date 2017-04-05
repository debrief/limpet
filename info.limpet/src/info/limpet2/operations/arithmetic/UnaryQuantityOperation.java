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
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;
import info.limpet2.NumberDocument;
import info.limpet2.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public abstract class UnaryQuantityOperation implements IOperation
{
  private final String _opName;

  public UnaryQuantityOperation(String opName)
  {
    _opName = opName;
  }

  public String getName()
  {
    return _opName;
  }

  protected final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public Collection<ICommand> actionsFor(List<Document> selection,
      IStoreGroup destination, IContext context)
  {
    Collection<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      ICommand newC =
          new UnaryQuantityCommand("Math - " + _opName, "description here",
              destination, selection, context);
      
      res.add(newC);
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
   * determine the units of the product
   * 
   * @param first
   * @param second
   * @return
   */
  abstract protected Unit<?> getUnaryOutputUnit(Unit<?> first);

  /**
   * provide the name for the product dataset
   * 
   * @param name
   * @param name2
   * @return
   */
  abstract protected String getUnaryNameFor(String name);

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * perform the operation on the subject dataset
   * 
   * @param input
   * @return
   */
  abstract public Dataset calculate(Dataset input);

  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public class UnaryQuantityCommand extends CoreQuantityCommand
  {

    public UnaryQuantityCommand(String title, String description,
        IStoreGroup store, List<Document> inputs, IContext context)
    {
      super(title, description, store, true, true, inputs, context);
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
      final IDataset in1 = getInputs().get(0).getDataset();
      Dataset in1d;
      try
      {
        in1d = DatasetUtils.sliceAndConvertLazyDataset(in1);
      }
      catch (DatasetException e)
      {
        throw new IllegalArgumentException("Unable to load subject dataset:"
            + in1.getName());
      }

      final Dataset res = calculate(in1d);

      // look for axes metadata
      final AxesMetadata axis1 = in1.getFirstMetadata(AxesMetadata.class);

      // if there are indices, store them
      if (axis1 != null)
      {
        AxesMetadata am = new AxesMetadataImpl();
        // keep track of the indices to use in the output
        final ILazyDataset outputIndicesLazy = axis1.getAxes()[0];
        Dataset outputIndices = null;
        try
        {
          outputIndices =
              DatasetUtils.sliceAndConvertLazyDataset(outputIndicesLazy);
        }
        catch (DatasetException e)
        {
          throw new IllegalArgumentException("Unable to load axis for dataset:"
              + in1.getName());
        }
        am.initialize(1);
        am.setAxis(0, outputIndices);
        res.addMetadata(am);
      }

      // and fire out the update
      for (Document output : getOutputs())
      {
        output.fireDataChanged();
      }

      // done
      return res;
    }

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

  }

}
