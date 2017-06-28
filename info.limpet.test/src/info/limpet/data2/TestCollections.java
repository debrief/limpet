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
package info.limpet.data2;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.MockContext;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.Range;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocument;
import info.limpet.operations.arithmetic.simple.MultiplyQuantityOperation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.dataset.StringDataset;

public class TestCollections extends TestCase
{
  private IContext context = new MockContext();

  public void testCreateObject()
  {
    // the target collection
    List<String> stringCollection = new ArrayList<String>();

    for (int i = 1; i <= 10; i++)
    {
      // store the measurement
      stringCollection.add(i + "aaa");
    }

    StringDataset str =
        (StringDataset) DatasetFactory.createFromObject(stringCollection);
    IDocument<?> strDoc = new StringDocument(str, null);

    // check it got stored
    assertEquals("correct number of samples", 10, strDoc.size());
  }
  
  public void testDelete()
  {
    NumberDocumentBuilder tq1b =
        new NumberDocumentBuilder("Some data1", METRE.divide(SECOND).asType(
            Velocity.class), null, SampleData.MILLIS);
    tq1b.add(100, 10d);
    tq1b.add(200, 20d);
    tq1b.add(300, 30d);
    tq1b.add(400, 40d);

    NumberDocument tq1 = tq1b.toDocument();

    NumberDocumentBuilder tq2b =
        new NumberDocumentBuilder("Some data2", METRE.divide(SECOND).asType(
            Velocity.class), null, null);
    tq2b.add(11d);

    NumberDocument tq2 = tq2b.toDocument();

    List<IStoreItem> selection = new ArrayList<IStoreItem>();
    selection.add(tq1);
    selection.add(tq2);

    StoreGroup store = new StoreGroup("Store");
    Collection<ICommand> commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    ICommand firstC = commands.iterator().next();

    firstC.execute();
    
    // check deleting the output document
    Document<?> out = firstC.getOutputs().get(0);
    assertNotNull("found output", out);
    
    // ok, check inputs have 
    assertEquals("has output", 1, firstC.getOutputs().size());
    
    // ok, now delete the output 
    store.remove(out);

    // ok, check inputs have 
    assertEquals("output removed", 0, firstC.getOutputs().size());

    // ok, we need to generate a new set of options
    commands =
        new MultiplyQuantityOperation().actionsFor(selection, store, context);
    firstC = commands.iterator().next();

    // try to re-run
    firstC.execute();

    // ok, check inputs have 
    assertEquals("has output", 1, firstC.getOutputs().size());

    // check the command is registered as dependent on the input
    assertTrue("command registered as dependent", tq1.getDependents().contains(firstC));
    
    // now try to remove an input
    store.remove(tq1);
    
    // check we're no longer a dependent of the input
    assertFalse("no longer a dependent", tq1.getDependents().contains(firstC));
    
    // what's happened to the command?
    assertFalse("input should be deleted",firstC.getInputs().contains(tq1));
    
    // check the 
  }

  public void testDocumentListeners()
  {
    LocationDocumentBuilder ldb =
        new LocationDocumentBuilder("name", null, SI.METER);
    ldb.add(12, new Point2D.Double(12, 13));
    ldb.add(14, new Point2D.Double(15, 23));

    LocationDocument locDoc = ldb.toDocument();

    final List<String> dataChangedMsgs = new ArrayList<String>();
    final List<String> metadataChangedMsgs = new ArrayList<String>();
    final List<String> deletedMsgs = new ArrayList<String>();

    IChangeListener listOne = new IChangeListener()
    {

      @Override
      public void metadataChanged(IStoreItem subject)
      {
        metadataChangedMsgs.add("listOne");
      }

      @Override
      public void dataChanged(IStoreItem subject)
      {
        dataChangedMsgs.add("listOne");
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
        deletedMsgs.add("listOne");
      }
    };
    ICommand command =
        new DummyCommand(metadataChangedMsgs, dataChangedMsgs, deletedMsgs);
    IChangeListener transientL = new IChangeListener()
    {

      @Override
      public void metadataChanged(IStoreItem subject)
      {
        metadataChangedMsgs.add("tList");
      }

      @Override
      public void dataChanged(IStoreItem subject)
      {
        dataChangedMsgs.add("listOne");
      }

      @Override
      public void collectionDeleted(IStoreItem subject)
      {
        deletedMsgs.add("listOne");
      }
    };
    locDoc.addDependent(command);
    locDoc.addTransientChangeListener(transientL);
    locDoc.addChangeListener(listOne);

    // ok, check the updates happen
    locDoc.fireDataChanged();

    assertEquals(3, dataChangedMsgs.size());
    assertEquals(0, deletedMsgs.size());
    assertEquals(0, metadataChangedMsgs.size());

    // and the metadata change - oops, we don't have an event for it.
    locDoc.fireMetadataChanged();
    assertEquals(3, dataChangedMsgs.size());
    assertEquals(0, deletedMsgs.size());
    assertEquals(3, metadataChangedMsgs.size());

    // lastly, check delete
    locDoc.beingDeleted();

    assertEquals(3, dataChangedMsgs.size());
    assertEquals(3, deletedMsgs.size());
    assertEquals(3, metadataChangedMsgs.size());

  }

  public void testStoreWrongNumberType()
  {
    LocationDocumentBuilder ldb =
        new LocationDocumentBuilder("name", null, SI.METER);
    ldb.add(12, new Point2D.Double(12, 13));
    ldb.add(14, new Point2D.Double(15, 23));

    LocationDocument locDoc = ldb.toDocument();
    IDataset oData = locDoc.getDataset();
    assertTrue("we're expecting an object dataset",
        oData instanceof ObjectDataset);

    // ok, now check an error is thrown if we put it into a nubmer document
    NumberDocument nd = new NumberDocument(null, null, null);
    boolean thrown = false;
    try
    {
      nd.setDataset(oData);
    }
    catch (IllegalArgumentException ee)
    {
      thrown = true;
    }
    assertTrue("the expected exception got caught", thrown);
  }

  public void testStoreWrongLocationType()
  {
    NumberDocumentBuilder ldb =
        new NumberDocumentBuilder("name", null, null, SI.METER);
    ldb.add(12, 213d);
    ldb.add(14, 413d);

    NumberDocument locDoc = ldb.toDocument();
    IDataset oData = locDoc.getDataset();
    assertTrue("we're expecting an object dataset",
        oData instanceof DoubleDataset);

    // ok, now check an error is thrown if we put it into a nubmer document
    LocationDocument nd = new LocationDocument(null, null);
    boolean thrown = false;
    try
    {
      nd.setDataset(oData);
    }
    catch (IllegalArgumentException ee)
    {
      thrown = true;
    }
    assertTrue("the expected exception got caught", thrown);
  }

  public void testStoreWrongStringType()
  {
    NumberDocumentBuilder ldb =
        new NumberDocumentBuilder("name", null, null, SI.METER);
    ldb.add(12, 213d);
    ldb.add(14, 413d);

    NumberDocument locDoc = ldb.toDocument();
    IDataset oData = locDoc.getDataset();
    assertTrue("we're expecting an object dataset",
        oData instanceof DoubleDataset);

    // ok, now check an error is thrown if we put it into a nubmer document
    StringDocument nd = new StringDocument(null, null);
    boolean thrown = false;
    try
    {
      nd.setDataset(oData);
    }
    catch (IllegalArgumentException ee)
    {
      thrown = true;
    }
    assertTrue("the expected exception got caught", thrown);
  }

  private static class DummyCommand implements ICommand
  {

    final private List<String> metadataChangedMsgs;
    final private List<String> dataChangedMsgs;
    final private List<String> deletedMsgs;

    public DummyCommand(List<String> meta, List<String> data,
        List<String> deleted)
    {
      metadataChangedMsgs = meta;
      dataChangedMsgs = data;
      deletedMsgs = deleted;
    }

    @Override
    public void setParent(IStoreGroup parent)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void removeTransientChangeListener(
        IChangeListener collectionChangeListener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void removeChangeListener(IChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public UUID getUUID()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public IStoreGroup getParent()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getName()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void fireDataChanged()
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addTransientChangeListener(IChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void addChangeListener(IChangeListener listener)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void metadataChanged(IStoreItem subject)
    {
      metadataChangedMsgs.add("command");
    }

    @Override
    public void dataChanged(IStoreItem subject)
    {
      dataChangedMsgs.add("command");
    }

    @Override
    public void collectionDeleted(IStoreItem subject)
    {
      deletedMsgs.add("command");
    }

    @Override
    public void undo()
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void setDynamic(boolean dynamic)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void redo()
    {
      // TODO Auto-generated method stub

    }

    @Override
    public List<Document<?>> getOutputs()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public List<IStoreItem> getInputs()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean getDynamic()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public String getDescription()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void execute()
    {
      // TODO Auto-generated method stub

    }

    @Override
    public boolean canUndo()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean canRedo()
    {
      // TODO Auto-generated method stub
      return false;
    }
  }

  public void testSampleData()
  {
    StoreGroup data = new SampleData().getData(10);
    NumberDocument ranged =
        (NumberDocument) data.get(SampleData.RANGED_SPEED_SINGLETON);
    assertNotNull("found series", ranged);

    Range range = ranged.getRange();
    assertNotNull("found range", range);

    // check the range has values
    assertEquals("correct values", 940d, (Double) range.getMinimum(), 0.1);
    assertEquals("correct values", 1050d, (Double) range.getMaximum(), 0.1);
  }

  public void testCreateQuantity()
  {
    NumberDocumentBuilder speedCollectionB =
        new NumberDocumentBuilder("Speed", METRE.divide(SECOND).asType(
            Velocity.class), null, null);

    for (int i = 1; i <= 10; i++)
    {
      // create a measurement
      double thisSpeed = i * 2;

      // store the measurement
      speedCollectionB.add(thisSpeed);
    }

    NumberDocument speedCollection = speedCollectionB.toDocument();

    // check it didn't get stored
    assertEquals("correct number of samples", 10, speedCollection.size());
    assertEquals("correct name", "Speed", speedCollection.getName());

    assertEquals("correct min", 2d, speedCollection.stats().min(), 0.001);
    assertEquals("correct max", 20d, speedCollection.stats().max(), 0.001);
    assertEquals("correct mean", 11d, speedCollection.stats().mean(), 0.001);
    assertEquals("correct variance", 33, speedCollection.stats().variance(),
        0.1);
    assertEquals("correct sd", 5.744, speedCollection.stats().sd(), 0.001);
  }
}
