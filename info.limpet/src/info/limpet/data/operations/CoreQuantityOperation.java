package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public abstract class CoreQuantityOperation<Q extends Quantity>
{

	protected CollectionComplianceTests aTests = new CollectionComplianceTests();

	public Collection<ICommand<IQuantityCollection<Q>>> actionsFor(
			List<IQuantityCollection<Q>> selection, IStore destination,
			IContext context)
	{
		Collection<ICommand<IQuantityCollection<Q>>> res = new ArrayList<ICommand<IQuantityCollection<Q>>>();
		if (appliesTo(selection))
		{

			// so, do we do our indexed commands?
			if (aTests.allEqualLength(selection))
			{
				addIndexedCommands(selection, destination, res, context);
			}

			// aah, what about temporal (interpolated) values?
			if (aTests.allTemporal(selection)
					&& aTests.suitableForTimeInterpolation(selection))
			{
				addInterpolatedCommands(selection, destination, res, context);
			}

		}

		return res;
	}

	protected ITemporalQuantityCollection<Q> getLongestTemporalCollections(
			List<IQuantityCollection<Q>> selection)
	{
		// find the longest time series.
		Iterator<IQuantityCollection<Q>> iter = selection.iterator();
		ITemporalQuantityCollection<Q> longest = null;

		while (iter.hasNext())
		{
			ITemporalQuantityCollection<Q> thisC = (ITemporalQuantityCollection<Q>) iter
					.next();
			if (longest == null)
			{
				longest = thisC;
			}
			else
			{
				// store the longest one
				longest = thisC.size() > longest.size() ? thisC : longest;
			}
		}
		return longest;
	}

	/**
	 * determine if this dataset is suitable
	 * 
	 * @param selection
	 * @return
	 */
	abstract protected boolean appliesTo(List<IQuantityCollection<Q>> selection);

	/**
	 * produce any new commands for this s election
	 * 
	 * @param selection
	 *          current selection
	 * @param destination
	 *          where the results will end up
	 * @param commands
	 *          the list of commands
	 */
	abstract protected void addIndexedCommands(
			List<IQuantityCollection<Q>> selection, IStore destination,
			Collection<ICommand<IQuantityCollection<Q>>> commands, IContext context);

	/**
	 * add any commands that require temporal interpolation
	 * 
	 * @param selection
	 * @param destination
	 * @param res
	 */
	abstract protected void addInterpolatedCommands(
			List<IQuantityCollection<Q>> selection, IStore destination,
			Collection<ICommand<IQuantityCollection<Q>>> res, IContext context);

	/**
	 * the command that actually produces data
	 * 
	 * @author ian
	 * 
	 */
	abstract public class CoreQuantityCommand extends
			AbstractCommand<IQuantityCollection<Q>>
	{

		final private ITemporalQuantityCollection<Q> _timeProvider;

		public CoreQuantityCommand(String title, String description,
				IStore store, boolean canUndo, boolean canRedo, List<IQuantityCollection<Q>> inputs,
				IContext context)
		{
			this(title, description, store, canUndo, canRedo, inputs, null,
					context);
		}

		public CoreQuantityCommand(String title, String description,
				IStore store, boolean canUndo, boolean canRedo, List<IQuantityCollection<Q>> inputs,
				ITemporalQuantityCollection<Q> timeProvider,
				IContext context)
		{
			super(title, description, store, canUndo, canRedo, inputs, context);

			_timeProvider = timeProvider;
		}

		/**
		 * empty the contents of any results collections
		 * 
		 * @param outputs
		 */
		private void clearOutputs(List<IQuantityCollection<Q>> outputs)
		{
			// clear out the lists, first
			Iterator<IQuantityCollection<Q>> iter = outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<Q> qC = (IQuantityCollection<Q>) iter.next();
				qC.clearQuiet();
			}
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 *          the units to use
		 * @param outputs
		 *          the list of output series
		 */
		protected void performCalc(Unit<Q> unit,
				List<IQuantityCollection<Q>> outputs)
		{
			IQuantityCollection<Q> target = outputs.iterator().next();

			clearOutputs(outputs);

			if (_timeProvider != null)
			{
				Collection<Long> times = _timeProvider.getTimes();
				Iterator<Long> iter = times.iterator();
				while (iter.hasNext())
				{
					long thisT = (long) iter.next();
					Double val = calcThisInterpolatedElement(thisT);
					if (val != null)
					{
						storeTemporalValue(target, thisT, val);
					}
				}
			}
			else
			{
				for (int elementCount = 0; elementCount < numElements(); elementCount++)
				{
					Double thisResult = calcThisElement(elementCount);

					// ok, done - store it!
					storeValue(target, elementCount, thisResult);
				}
			}

		}

		protected int numElements()
		{
			return inputs.get(0).size();
		}

		private void storeTemporalValue(IQuantityCollection<Q> target, long thisT,
				double val)
		{
			ITemporalQuantityCollection<Q> qc = (ITemporalQuantityCollection<Q>) target;
			qc.add(thisT, Measure.valueOf(val, determineOutputUnit(target)));
		}

		/**
		 * store this value into the target (optionally including temporal aspects)
		 * 
		 * @param target
		 *          destination
		 * @param count
		 *          index for this value
		 * @param value
		 *          the value to store
		 */
		private void storeValue(IQuantityCollection<Q> target, int count,
				Double value)
		{
			if (target.isTemporal())
			{
				// ok, the input and output arrays must be temporal.
				ITemporalQuantityCollection<Q> qc = (ITemporalQuantityCollection<Q>) target;
				ITemporalQuantityCollection<Q> qi = (ITemporalQuantityCollection<Q>) inputs
						.get(0);
				Long[] timeData = qi.getTimes().toArray(new Long[]
				{});
				qc.add(timeData[count],
						Measure.valueOf(value, determineOutputUnit(target)));
			}
			else
			{
				target.add(Measure.valueOf(value, determineOutputUnit(target)));
			}
		}

		/**
		 * produce a calculated value for the relevant index of the first input
		 * collection
		 * 
		 * @param elementCount
		 * @return
		 */
		abstract protected Double calcThisElement(int elementCount);

		/**
		 * produce a calculated value for the relevant index of the first input
		 * collection
		 * 
		 * @param elementCount
		 * @return
		 */
		abstract protected Double calcThisInterpolatedElement(long time);

		@Override
		protected void recalculate()
		{
			// get the unit
			IQuantityCollection<Q> first = inputs.get(0);
			Unit<Q> unit = determineOutputUnit(first);

			// update the results
			performCalc(unit, outputs);
		}

		/**
		 * produce a target of the correct type
		 * 
		 * @param input
		 *          one of the input series
		 * @param unit
		 *          the units to use
		 * @return
		 */
		protected IQuantityCollection<Q> createQuantityTarget(
				IQuantityCollection<Q> input, Unit<Q> unit)
		{
			// double check the name is ok
			final String outName = getContext().getInput(super.getName(),
					"Please provide a name for the new dataset", getOutputName());

			IQuantityCollection<Q> target = null;

			if (outName != null)
			{
				if (input.isTemporal())
				{
					target = new TemporalQuantityCollection<Q>(outName, this, unit);
				}
				else
				{
					target = new QuantityCollection<Q>(outName, this, unit);
				}
			}

			return target;
		}

		@Override
		public void execute()
		{
			// get the unit
			IQuantityCollection<Q> first = inputs.get(0);

			List<IQuantityCollection<Q>> outputs = new ArrayList<IQuantityCollection<Q>>();

			// sort out the output unit
			Unit<Q> unit = determineOutputUnit(first);

			// ok, generate the new series
			final IQuantityCollection<Q> target = createQuantityTarget(first, unit);

			if (target == null)
			{
				getContext().logError(IContext.Status.WARNING,
						"User cancelled create operation", null);
				return;
			}

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(unit, outputs);

			// tell each series that we're a dependent
			Iterator<IQuantityCollection<Q>> iter = inputs.iterator();
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

		protected Unit<Q> determineOutputUnit(IQuantityCollection<Q> first)
		{
			return first.getUnits();
		}

	}

}