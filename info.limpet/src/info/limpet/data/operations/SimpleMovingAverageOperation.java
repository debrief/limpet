package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.UIProperty;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.math.SimpleMovingAverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

public class SimpleMovingAverageOperation implements IOperation<ICollection>
{
	public static final String SERIES_NAME_TEMPLATE = "Simple Moving Average";

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	final protected int _windowSize;

	public SimpleMovingAverageOperation(int windowSize)
	{
		this._windowSize = windowSize;
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination, IContext context)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new SimpleMovingAverageCommand(
					SERIES_NAME_TEMPLATE, selection, destination, _windowSize,
					context);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean singleSeries = selection.size() == 1;
		boolean allQuantity = aTests.allQuantity(selection);
		return (singleSeries && allQuantity);
	}

	public class SimpleMovingAverageCommand extends AbstractCommand<ICollection>
	{

		private int winSize;

		public SimpleMovingAverageCommand(String operationName, List<ICollection> selection,
				IStore store, int windowSize, IContext context)
		{
			super(operationName, "Calculates a Simple Moving Average", store,
					false, false, selection, context);
			winSize = windowSize;
		}

		@UIProperty(name="Window", category=UIProperty.CATEGORY_CALCULATION, min=1, max=20)
		public int getWindowSize()
		{
			return winSize;
		}

		@Override
		protected String getOutputName()
		{
			return getContext().getInput("Generate simple moving average",
					NEW_DATASET_MESSAGE, "Moving average of " + super.getSubjectList());
		}

		public void setWindowSize(int winSize)
		{
			this.winSize = winSize;
			
			// ok, we now need to update!
			super.dataChanged(this.getOutputs().iterator().next());
		}

		@Override
		public void execute()
		{
			IQuantityCollection<?> input = (IQuantityCollection<?>) inputs.get(0);

			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>(getOutputName(),
					this, input.getUnits());

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(outputs);

			// tell each series that we're a dependent
			Iterator<ICollection> iter = inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<IStoreItem> res = new ArrayList<IStoreItem>();
			res.add(target);
			getStore().addAll(res);
		}

		@Override
		public void recalculate()
		{
			// update the results
			performCalc(outputs);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(List<ICollection> outputs)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) outputs
					.iterator().next();

			// clear out the lists, first
			Iterator<ICollection> iter = outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.clearQuiet();
			}

			SimpleMovingAverage sma = new SimpleMovingAverage(winSize);
			@SuppressWarnings("unchecked")
			IQuantityCollection<Quantity> input = (IQuantityCollection<Quantity>) inputs
					.get(0);

			for (Measurable<Quantity> quantity : input.getValues())
			{
				sma.newNum(quantity.doubleValue(input.getUnits()));
				target.add(sma.getAvg());
			}
		}
	}

}
