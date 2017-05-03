package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;

import java.awt.geom.Point2D;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ObjectDataset;

public class LocationDocumentBuilder extends
    CoreDocumentBuilder<Point2D, LocationDocument> implements
    IDocumentBuilder<Point2D>
{
  public LocationDocumentBuilder(final String name, final ICommand predecessor,
      final Unit<?> indexUnits)
  {
    super(name, predecessor, indexUnits);
  }

  @Override
  protected IDataset getDataset(final List<Point2D> values)
  {
    final Object[] arr = values.toArray();
    return DatasetFactory.createFromObject(ObjectDataset.class, arr, null);
  }

  @Override
  protected LocationDocument getDocument(final IDataset dataset,
      final ICommand _predecessor2)
  {
    return new LocationDocument((ObjectDataset) dataset, _predecessor2);
  }

}