package info.limpet.impl;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.metadata.AxesMetadata;

abstract public class Document implements IDocument
{

  // TODO: long-term, find a better place for this
  public static enum InterpMethod
  {
    Linear, Nearest, Before, After
  };

  /**
   * _dataset isn't final, sincew we replace it when the document is re-calculated
   */
  protected IDataset dataset;
  final protected ICommand predecessor;
  private List<IChangeListener> changeListeners =
      new ArrayList<IChangeListener>();
  private IStoreGroup parent;
  final private UUID uuid;
  private List<ICommand> dependents = new ArrayList<ICommand>();

  private Unit<?> indexUnits;

  public Document(IDataset dataset, ICommand predecessor)
  {
    this.dataset = dataset;
    this.predecessor = predecessor;
    uuid = UUID.randomUUID();
  }

  @Override
  @UIProperty(name = "IndexUnits", category = "Label")
  public Unit<?> getIndexUnits()
  {
    if (!isIndexed())
    {
      throw new IllegalArgumentException(
          "Index not present, cannot retrieve index units");
    }
    return indexUnits;
  }

  /**
   * set the units for the index data
   * 
   * @param units
   */
  public void setIndexUnits(Unit<?> units)
  {
    if (!isIndexed())
    {
      throw new IllegalArgumentException(
          "Index not present, cannot set index units");
    }
    indexUnits = units;
  }

  public IDataset getDataset()
  {
    return dataset;
  }

  public void setDataset(IDataset dataset)
  {
    this.dataset = dataset;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#beingDeleted()
   */
  @Override
  public void beingDeleted()
  {
    if (changeListeners != null)
    {
      // tell any standard listeners
      for (IChangeListener thisL : changeListeners)
      {
        thisL.collectionDeleted(this);
      }
    }

    // now tell the dependents
    Iterator<ICommand> iter = dependents.iterator();
    while (iter.hasNext())
    {
      ICommand iC = (ICommand) iter.next();
      iC.collectionDeleted(this);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#getName()
   */
  @Override
  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  public String getName()
  {
    return dataset.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#setName(java.lang.String)
   */
  @Override
  public void setName(String name)
  {
    dataset.setName(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#getParent()
   */
  @Override
  public IStoreGroup getParent()
  {
    return parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#setParent(info.limpet.IStoreGroup)
   */
  @Override
  public void setParent(IStoreGroup parent)
  {
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#addChangeListener(info.limpet.IChangeListener)
   */
  @Override
  public void addChangeListener(IChangeListener listener)
  {
    changeListeners.add(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#removeChangeListener(info.limpet.IChangeListener)
   */
  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    changeListeners.remove(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#fireDataChanged()
   */
  @Override
  public void fireDataChanged()
  {
    for (final IChangeListener thisL : changeListeners)
    {
      thisL.dataChanged(this);
    }
    for (final ICommand thisL : dependents)
    {
      thisL.dataChanged(this);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#getUUID()
   */
  @Override
  public UUID getUUID()
  {
    return uuid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#size()
   */
  @Override
  @UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
  public int size()
  {
    return dataset.getSize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#isIndexed()
   */
  @Override
  @UIProperty(name = "Indexed", category = UIProperty.CATEGORY_LABEL)
  public boolean isIndexed()
  {
    // is there an axis?
    final AxesMetadata am = dataset.getFirstMetadata(AxesMetadata.class);

    // is it a time axis?
    return am != null;
  }

  private static class DoubleIterator implements Iterator<Double>
  {
    private double[] _data;
    private int _ctr;

    private DoubleIterator(double[] data)
    {
      _data = data;
      _ctr = 0;
    }

    @Override
    public boolean hasNext()
    {
      return _ctr < _data.length;
    }

    @Override
    public Double next()
    {
      return _data[_ctr++];
    }

    @Override
    public void remove()
    {
      throw new RuntimeException(
          "Remove operation not provided for this iterator");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#getIndices()
   */
  @Override
  public Iterator<Double> getIndex()
  {
    DoubleIterator res = null;

    if (isIndexed())
    {
      final AxesMetadata am = dataset.getFirstMetadata(AxesMetadata.class);

      ILazyDataset ds = am.getAxes()[0];
      try
      {
        DoubleDataset dd =
            (DoubleDataset) DatasetUtils.sliceAndConvertLazyDataset(ds);
        double[] items = dd.getData();
        res = new DoubleIterator(items);
      }
      catch (DatasetException e)
      {
        throw new RuntimeException(e);
      }
    }

    return res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#isQuantity()
   */
  @Override
  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity()
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#getPrecedent()
   */
  @Override
  public ICommand getPrecedent()
  {
    return predecessor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#addDependent(info.limpet.ICommand)
   */
  @Override
  public void addDependent(ICommand command)
  {
    dependents.add(command);
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#getDependents()
   */
  @Override
  public List<ICommand> getDependents()
  {
    return dependents;
  }

  /**
   * temporarily use this - until we're confident about replacing child Dataset objects
   * 
   */
  @Override
  public void clearQuiet()
  {
    dataset = null;
  }

  @Override
  public String toString()
  {
    return getName();
  }

}
