package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.IStore.IStoreItem;
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
	protected final String outputName;

	public CoreQuantityOperation(String outputName)
	{
		this.outputName = outputName;
	}

	abstract protected boolean appliesTo(List<IQuantityCollection<Q>> selection);

	public Collection<ICommand<IQuantityCollection<Q>>> actionsFor(
			List<IQuantityCollection<Q>> selection, IStore destination)
	{
		Collection<ICommand<IQuantityCollection<Q>>> res = new ArrayList<ICommand<IQuantityCollection<Q>>>();
		if (appliesTo(selection))
		{
			addCommands(selection, destination, res);
		}

		return res;
	}

	abstract protected void addCommands(List<IQuantityCollection<Q>> selection,
			IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res);

	abstract public class CoreQuantityCommand extends
			AbstractCommand<IQuantityCollection<Q>>
	{
		public CoreQuantityCommand(String title, String description,
				String outputName, IStore store, boolean canUndo, boolean canRedo,
				List<IQuantityCollection<Q>> inputs)
		{
			super("Add series", "Add numeric values in provided series", outputName,
					store, false, false, inputs);

		}

		protected void clearOutputs(List<IQuantityCollection<Q>> outputs)
		{
			// clear out the lists, first
			Iterator<IQuantityCollection<Q>> iter = outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<Q> qC = (IQuantityCollection<Q>) iter.next();
				qC.getValues().clear();
			}
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		protected void performCalc(Unit<Q> unit,
				List<IQuantityCollection<Q>> outputs)
		{
			IQuantityCollection<Q> target = outputs.iterator().next();

			clearOutputs(outputs);

			for (int elementCount = 0; elementCount < inputs.get(0).size(); elementCount++)
			{
				Double thisResult = calcThisElement(elementCount);

				// ok, done - store it!
				storeValue(target, elementCount, thisResult);
			}
		}

		abstract protected Double calcThisElement(int elementCount);

		@Override
		protected void recalculate()
		{
			// get the unit
			IQuantityCollection<Q> first = inputs.get(0);
			Unit<Q> unit = first.getUnits();

			// update the results
			performCalc(unit, outputs);
		}

		protected IQuantityCollection<Q> createQuantityTarget(
				IQuantityCollection<Q> first, Unit<Q> unit)
		{
			final IQuantityCollection<Q> target;
			if (first.isTemporal())
			{
				target = new TemporalQuantityCollection<Q>(getOutputName(), this, unit);
			}
			else
			{
				target = new QuantityCollection<Q>(getOutputName(), this, unit);
			}

			return target;
		}

		@Override
		public void execute()
		{
			// get the unit
			IQuantityCollection<Q> first = inputs.get(0);
			Unit<Q> unit = first.getUnits();

			List<IQuantityCollection<Q>> outputs = new ArrayList<IQuantityCollection<Q>>();

			// ok, generate the new series
			final IQuantityCollection<Q> target = createQuantityTarget(first, unit);

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

		protected void storeValue(IQuantityCollection<Q> target, int count,
				Double runningTotal)
		{
			if (target.isTemporal())
			{
				ITemporalQuantityCollection<Q> qc = (ITemporalQuantityCollection<Q>) target;
				ITemporalQuantityCollection<Q> qi = (ITemporalQuantityCollection<Q>) inputs
						.get(0);
				Long[] timeData = qi.getTimes().toArray(new Long[]
				{});
				qc.add(timeData[count],
						Measure.valueOf(runningTotal, target.getUnits()));
			}
			else
			{
				target.add(Measure.valueOf(runningTotal, target.getUnits()));
			}
		}

	}

}