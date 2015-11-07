package info.limpet.data.operations;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
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

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.unit.Unit;

abstract public class UnitaryMathOperation implements IOperation<ICollection>
{
	CollectionComplianceTests aTests = new CollectionComplianceTests();
	private final String _opName;

	public UnitaryMathOperation(String opName)
	{
		_opName = opName;
	}

	abstract public double calcFor(double val);

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
	{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			ICommand<ICollection> newC = new MathCommand("Math - " + _opName, selection,
					destination);
			res.add(newC);
		}

		return res;
	}


	protected Unit<?> getUnits(IQuantityCollection<?> input)
	{ 
		return input.getUnits();
	}
	
	private boolean appliesTo(List<ICollection> selection)
	{
		boolean notEmpty = aTests.nonEmpty(selection);
		boolean allQuantity = aTests.allQuantity(selection);

		return (notEmpty && allQuantity);
	}

	public class MathCommand extends AbstractCommand<ICollection>
	{

		public MathCommand(String operationName, List<ICollection> selection,
				IStore store)
		{
			super(operationName, "Convert units of the provided series", null, store,
					false, false, selection);
		}

		
		@Override
		public void execute()
		{
			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// ok, generate the new series
			Iterator<ICollection> iIter = getInputs().iterator();
			while (iIter.hasNext())
			{
				IQuantityCollection<?> thisInput = (IQuantityCollection<?>) iIter
						.next();
				final IQuantityCollection<?> thisOutput;
				if (thisInput.isTemporal())
				{
					thisOutput = new TemporalQuantityCollection<>(this.getName() + " - "
							+ thisInput.getName(), this, getUnits(thisInput));
				}
				else
				{
					thisOutput = new QuantityCollection<>(this.getName() + " - "
							+ thisInput.getName(), this, getUnits(thisInput));
				}

				thisInput.addDependent(this);
				outputs.add(thisOutput);

				// store the output
				super.addOutput(thisOutput);

			}
			getStore().addAll(outputs);

			// ok, now populate the outputs listings
			performCalc();

		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void processThis(IQuantityCollection<?> thisInput,
				IQuantityCollection<?> thisOutput)
		{
			Iterator<?> iter = thisInput.getValues().iterator();
			while (iter.hasNext())
			{
				Measurable<?> thisV = (Measurable<?>) iter.next();
				Unit theUnits =  getUnits(thisInput);
				double thisD = thisV.doubleValue((Unit) thisInput.getUnits());
				double newD = calcFor(thisD);
				thisOutput.add(Measure.valueOf(newD, theUnits));
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void processThisTemporal(IQuantityCollection<?> thisInput,
				ITemporalQuantityCollection<?> thisOutput)
		{
			Iterator<?> iter = thisInput.getValues().iterator();
			IBaseTemporalCollection tqc = (IBaseTemporalCollection) thisInput;
			Iterator<Long> tIter = tqc.getTimes().iterator();
			while (iter.hasNext())
			{
				Measurable<?> thisV = (Measurable<?>) iter.next();
				Long thisT = tIter.next();
				Unit theUnits = getUnits(thisInput);
				double thisD = thisV.doubleValue((Unit) thisInput.getUnits());
				double newD = calcFor(thisD);
				thisOutput.add(thisT, Measure.valueOf(newD, theUnits));
			}
		}

		@Override
		protected void recalculate()
		{
			// update the results
			performCalc();
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc()
		{
			// ok, generate the new series
			Iterator<ICollection> iIter = getInputs().iterator();
			Iterator<ICollection> oIter = getOutputs().iterator();

			while (iIter.hasNext())
			{
				IQuantityCollection<?> thisInput = (IQuantityCollection<?>) iIter
						.next();
				IQuantityCollection<?> thisOutput = (IQuantityCollection<?>) oIter
						.next();

				thisOutput.clearQuiet();

				if (thisInput.isTemporal())
				{
					// loop through, performing the operation
					processThisTemporal(thisInput,
							(ITemporalQuantityCollection<?>) thisOutput);
				}
				else
				{
					// loop through, performing the operation
					processThis(thisInput, thisOutput);
				}
			}
		}
	}

}
