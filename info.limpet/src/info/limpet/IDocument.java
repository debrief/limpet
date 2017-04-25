package info.limpet;

import info.limpet.impl.UIProperty;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

public interface IDocument extends IStoreItem
{

  /**
   * tell listeners that it's about to be deleted
   * 
   */
  public void beingDeleted();

  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  public String getName();

  public void setName(String name);

  public IStoreGroup getParent();

  public void setParent(IStoreGroup parent);

  public void addChangeListener(IChangeListener listener);

  public void removeChangeListener(IChangeListener listener);

  public void fireDataChanged();

  public UUID getUUID();

  @UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
  public int size();

  @UIProperty(name = "Indexed", category = UIProperty.CATEGORY_LABEL)
  public boolean isIndexed();

  public Iterator<Double> getIndex();
  
  @UIProperty(name = "IndexUnits", category = UIProperty.CATEGORY_LABEL)
  public Unit<?> getIndexUnits();

  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity();

  public ICommand getPrecedent();

  public void addDependent(ICommand command);

  public List<ICommand> getDependents();

  void clearQuiet();

}