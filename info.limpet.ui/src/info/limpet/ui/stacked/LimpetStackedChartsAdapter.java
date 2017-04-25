package info.limpet.ui.stacked;

import info.limpet.IChangeListener;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;
import info.limpet.ui.data_provider.data.DocumentWrapper;
import info.limpet.ui.data_provider.data.GroupWrapper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

public class LimpetStackedChartsAdapter implements IStackedDatasetAdapter,
		IStackedScatterSetAdapter {

	protected static class CollectionChangeListener implements IChangeListener {

		private final NumberDocument _collection;
		private final Dataset _dataset;

		public CollectionChangeListener(
				NumberDocument collection, Dataset subject) {
			_dataset = subject;
			_collection = collection;
			_collection.addChangeListener(this);
		}

		@Override
		public void dataChanged(IStoreItem subject) {
			StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

			// ok, repopulate the dataset
			populateDataset(factory, _collection, _dataset);
		}

		@Override
		public void metadataChanged(IStoreItem subject) {
			// ignore metadata change
		}

		@Override
		public void collectionDeleted(IStoreItem subject) {
			_collection.removeChangeListener(this);
		}

	}

	protected static void populateDataset(
			final StackedchartsFactoryImpl factory,
			final NumberDocument tqc,
			final Dataset dataset) {
		// get ready to store the data
		dataset.setName(tqc.getName() + "(" + tqc.getUnits() + ")");

		// and the units
		dataset.setUnits(tqc.getUnits().toString());

		// clear the dataset
		dataset.getMeasurements().clear();

		Iterator<Double> times = tqc.getIndex();
		Iterator<Double> values = tqc.getIterator();
		while (times.hasNext()) {
		  double thisTime = times.next();
			final double value = values.next();
			final DataItem item = factory.createDataItem();
			item.setIndependentVal(thisTime);
			item.setDependentVal(value);

			// and store it
			dataset.getMeasurements().add(item);
		}
	}

	@SuppressWarnings({ })
	@Override
	public List<Dataset> convertToDataset(Object data) {
		List<Dataset> res = null;

		// we should have already checked, but just
		// double-check we can handle it
		if (canConvertToDataset(data)) {
			final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

			// have a look at the type
      final PlainStyling plainS = factory.createPlainStyling();

      Random random = new Random();
      float h = random.nextFloat();
      float s = random.nextFloat();
      float b = 0.8f + ((1f - 0.8f) * random.nextFloat());
      Color c = Color.getHSBColor(h, s, b);
      plainS.setColor(c);
      
      if (data instanceof DocumentWrapper) {
				DocumentWrapper cw = (DocumentWrapper) data;
				IDocument collection = cw.getDocument();
				if (collection.isQuantity() && collection.isIndexed()) {

					NumberDocument qq = (NumberDocument) collection;
					final Dataset dataset = factory.createDataset();
					populateDataset(factory, qq, dataset);

					// ok, register a listener for collection changes
					@SuppressWarnings("unused")
					CollectionChangeListener newListener = new CollectionChangeListener(
							qq, dataset);

					// have we got a results object yet?
					if (res == null) {
						res = new ArrayList<Dataset>();
					}

					// give it some style
					dataset.setStyling(plainS);

					res.add(dataset);
				}
			} else if (data instanceof GroupWrapper) {
				GroupWrapper groupW = (GroupWrapper) data;
				IStoreGroup group = groupW.getGroup();
				Iterator<IStoreItem> cIter = group.iterator();
				while (cIter.hasNext()) {
					IStoreItem thisI = (IStoreItem) cIter.next();
					if (thisI instanceof IDocument) {
					  IDocument thisC = (IDocument) thisI;
						if (thisC.isQuantity() && thisC.isIndexed()) {
							List<Dataset> newItems = convertToDataset(thisC);

							if (newItems != null && newItems.size() > 0) {
								if (res == null) {
									res = new ArrayList<Dataset>();
								}
								res.addAll(newItems);
							}
						}
					}

				}
			} else if (data instanceof IDocument) {
			  IDocument coll = (IDocument) data;
				if (coll.isQuantity() && coll.isIndexed()) {
					Dataset dataset = factory.createDataset();
					populateDataset(factory,
							(NumberDocument) coll,
							dataset);

					// setup the listener
					NumberDocument tempColl = (NumberDocument) coll;
					@SuppressWarnings("unused")
					CollectionChangeListener listener = new CollectionChangeListener(
							tempColl, dataset);

					// give it some style
					dataset.setStyling(plainS);

					// collate the results
					if (res == null) {
						res = new ArrayList<Dataset>();
					}
					res.add(dataset);
				}
			} else if (data instanceof List) {
				List<?> list = (List<?>) data;
				for (Object item : list) {
					List<Dataset> items = convertToDataset(item);
					if (items != null) {
						if (res == null) {
							res = new ArrayList<Dataset>();
						}
						res.addAll(items);
					}
				}
			}
		}

		return res;
	}

	@Override
	public boolean canConvertToDataset(Object data) {
		boolean res = false;

		// have a look at the type
		if (data instanceof DocumentWrapper) {
			DocumentWrapper cw = (DocumentWrapper) data;
			IDocument collection = cw.getDocument();
			if (collection.isQuantity() && collection.isIndexed()) {
				res = true;
			} else if (collection.isQuantity() && !collection.isIndexed()) {
				// check if its' a series of timestampes
				NumberDocument qc = (NumberDocument) collection;
				Unit<?> units = qc.getUnits();
				if (units.equals(Duration.UNIT)) {
					res = true;
				}
			}
		} else if (data instanceof GroupWrapper) {
			res = true;
		} else if (data instanceof NumberDocument) {
			res = true;
		} else if (data instanceof List) {
			List<?> list = (List<?>) data;
			for (Object item : list) {
				boolean thisRes = canConvertToDataset(item);
				if (!thisRes) {
					break;
				}
			}
			res = true;

		}

		return res;
	}

	@SuppressWarnings({ })
	@Override
	public List<ScatterSet> convertToScatterSet(Object data) {
		List<ScatterSet> res = null;

		// we should have already checked, but just
		// double-check we can handle it
		if (canConvertToScatterSet(data)) {
			final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

			// have a look at the type
			if (data instanceof DocumentWrapper) {
				DocumentWrapper cw = (DocumentWrapper) data;
				IDocument collection = cw.getDocument();
				if (collection.isQuantity() && !collection.isIndexed()) {
					// check if its' a series of timestampes
					NumberDocument qc = (NumberDocument) collection;
					Unit<?> units = qc.getUnits();
					if (units.equals(Duration.UNIT)) {
						// ok, create scatter set
						if (qc.size() > 0) {
							ScatterSet scatter = factory.createScatterSet();
							scatter.setName(qc.getName());
							final Iterator<Double> times = qc.getIndex();
              while (times.hasNext())
              {
								final double time = times.next();
								Datum datum = factory.createDatum();
								datum.setVal(time);
								scatter.getDatums().add(datum);
							}
							// do we have a results store?
							if (res == null) {
								res = new ArrayList<ScatterSet>();
							}

							// ok, store it
							res.add(scatter);
						}
					}
				}

				// now store the data
				// hook up listener
			} else if (data instanceof List) {
				List<?> list = (List<?>) data;
				for (Object item : list) {
					List<ScatterSet> items = convertToScatterSet(item);
					if (items != null) {
						if (res == null) {
							res = new ArrayList<ScatterSet>();
						}
						res.addAll(items);
					}
				}
			}
		}

		return res;
	}

	@Override
	public boolean canConvertToScatterSet(Object data) {
		boolean res = false;

		// have a look at the type
		if (data instanceof DocumentWrapper) {
			DocumentWrapper cw = (DocumentWrapper) data;
			IDocument collection = cw.getDocument();
			if (collection.isQuantity() && !collection.isIndexed()) {
				// check if its' a series of timestampes
        NumberDocument qc = (NumberDocument) collection;
				Unit<?> units = qc.getUnits();
				if (units.equals(Duration.UNIT)) {
					res = true;
				}
			}
		} else if (data instanceof List) {
			List<?> list = (List<?>) data;
			for (Object item : list) {
				boolean thisRes = canConvertToScatterSet(item);
				if (!thisRes) {
					break;
				}
			}
			res = true;

		}

		return res;
	}
}
