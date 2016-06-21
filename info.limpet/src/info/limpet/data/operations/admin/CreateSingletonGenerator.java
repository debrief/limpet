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
package info.limpet.data.operations.admin;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.store.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CreateSingletonGenerator implements IOperation<IStoreItem>
{
  private final CollectionComplianceTests aTests = new CollectionComplianceTests();
  private final String _name;

  public CreateSingletonGenerator(String name)
  {
    _name = name;
  }

  /**
   * encapsulate creating a location into a command
   * 
   * @author ian
   * 
   */
  public class CreateSingletonCommand extends AbstractCommand<IStoreItem>
  {
    private StoreGroup _targetGroup;

    public CreateSingletonCommand(String title, StoreGroup group, IStore store, IContext context)
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

      name = getContext().getInput("New variable", "Enter name for variable", "");
      if (name == null || name.isEmpty())
      {
        return;
      }

      String str = getContext().getInput("New variable", "Enter initial value for variable", "");
      if (str == null || str.isEmpty())
      {
        return;
      }
      try
      {
        // get the new collection
        QuantityCollection<?> newData = generate(name, this);

        // add the new value
        value = Double.parseDouble(str);
        newData.add(value);

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
          IStore store = getStore();
          if (store != null)
          {
            store.add(newData);
          }
        }

      }
      catch (NumberFormatException e)
      {
        getContext().logError(Status.WARNING, "Failed to parse initial value", e);
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Create new " + _name, NEW_DATASET_MESSAGE, "");
    }

  }

  public Collection<ICommand<IStoreItem>> actionsFor(List<IStoreItem> selection, IStore destination,
      IContext context)
  {
    Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
    if (appliesTo(selection))
    { 
      final String thisTitle = "Add single " + _name;
      // hmm, see if a group has been selected
      ICommand<IStoreItem> newC = null;
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

  protected AbstractCommand<IStoreItem> getCommand(IStore destination, IContext context,
      final String thisTitle, StoreGroup group)
  {
    return new CreateSingletonCommand(thisTitle, group, destination, context);
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    // we can apply this either to a group, or at the top level
    boolean singleGroupSelected = getATests().exactNumber(selection, 1) && getATests()
        .allGroups(selection);
    return getATests().exactNumber(selection, 0) || singleGroupSelected;
  }

  protected abstract QuantityCollection<?> generate(String name, ICommand<?> precedent);

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

}
