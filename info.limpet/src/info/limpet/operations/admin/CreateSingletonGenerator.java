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
package info.limpet.operations.admin;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

public class CreateSingletonGenerator implements IOperation
{
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  private final String _name;
  private final Unit<?> _unit;

  public CreateSingletonGenerator(String name, Unit<?> unit)
  {
    _name = name;
    _unit = unit;
  }

  /**
   * encapsulate creating a location into a command
   * 
   * @author ian
   * 
   */
  public class CreateSingletonCommand extends AbstractCommand
  {
    private StoreGroup _targetGroup;

    public CreateSingletonCommand(String title, StoreGroup group,
        IStoreGroup store, IContext context)
    {
      super(title, "Create single " + _name, store, false, false, null, context);
      _targetGroup = group;
    }

    @Override
    public void execute()
    {
      // get the name
      String name = "new " + _name;
      double value;

      name =
          getContext().getInput("New variable", "Enter name for variable", "");
      if (name == null || name.isEmpty())
      {
        return;
      }

      String str =
          getContext().getInput("New variable",
              "Enter initial value for variable", "");
      if (str == null || str.isEmpty())
      {
        return;
      }
      try
      {
        // get the new collection
        value = Double.parseDouble(str);
        NumberDocument newData = generate(name, value, this);

        // and remember it as an output
        super.addOutput(newData);

        // put the new collection in to the selected folder, or into root
        if (_targetGroup != null)
        {
          _targetGroup.add(newData);
        }
        else
        {
          // just store it at the top level
          IStoreGroup store = getStore();
          if (store != null)
          {
            store.add(newData);
          }
        }

      }
      catch (NumberFormatException e)
      {
        getContext().logError(IContext.Status.WARNING,
            "Failed to parse initial value", e);
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

    protected String getOutputName()
    {
      return getContext().getInput("Create new " + _name, NEW_DATASET_MESSAGE,
          "");
    }

  }

  public List<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      final String thisTitle = "Add single " + _name;
      // hmm, see if a group has been selected
      ICommand newC = null;
      if (selection.size() == 1)
      {
        IStoreItem first = selection.get(0);
        if (first instanceof StoreGroup)
        {
          StoreGroup group = (StoreGroup) first;
          newC = getCommand(destination, context, thisTitle, group);
        }
      }

      if (newC == null)
      {
        newC = getCommand(destination, context, thisTitle, null);
      }

      if (newC != null)
      {
        res.add(newC);
      }
    }

    return res;
  }

  protected AbstractCommand getCommand(IStoreGroup destination,
      IContext context, final String thisTitle, StoreGroup group)
  {
    return new CreateSingletonCommand(thisTitle, group, destination, context);
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    // we can apply this either to a group, or at the top level
    boolean singleGroupSelected =
        getATests().exactNumber(selection, 1)
            && getATests().allGroups(selection);
    return getATests().exactNumber(selection, 0) || singleGroupSelected;
  }

  protected NumberDocument generate(String name, double value,
      ICommand precedent)
  {
    NumberDocumentBuilder builder =
        new NumberDocumentBuilder(name, _unit, precedent, null);
    builder.add(value);
    return builder.toDocument();
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

}
