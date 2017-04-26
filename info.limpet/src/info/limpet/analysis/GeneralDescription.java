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
package info.limpet.analysis;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GeneralDescription extends CoreAnalysis
{

  public GeneralDescription()
  {
    super("General Description");
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  @Override
  public void analyse(List<IStoreItem> selection)
  {
    List<String> titles = new ArrayList<String>();
    List<String> values = new ArrayList<String>();

    // check compatibility
    if (appliesTo(selection) && selection.size() == 1)
    {
      // ok, let's go for it.
      for (Iterator<IStoreItem> iter = selection.iterator(); iter.hasNext();)
      {
        IDocument thisC = (IDocument) iter.next();

        titles.add("Collection");
        values.add(thisC.getName());
        titles.add("Size");
        values.add("" + thisC.size());
        titles.add("Quantity");
        values.add("" + thisC.isQuantity());
        titles.add("Indexed");
        values.add("" + thisC.isIndexed());
        if(thisC.isIndexed())
        {
          titles.add("Index units");
          values.add("" + thisC.getIndexUnits().toString());
        }
      }
    }

    if (titles.size() > 0)
    {
      presentResults(titles, values);
    }

  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    return aTests.allCollections(selection);
  }

  protected abstract void presentResults(List<String> titles,
      List<String> values);
}
