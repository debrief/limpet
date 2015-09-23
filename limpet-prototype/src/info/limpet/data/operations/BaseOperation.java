package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;

import java.util.List;

import javax.measure.Dimension;
import javax.measure.Unit;


abstract public class BaseOperation implements IOperation
{

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allQuantity(List<ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);
			if (thisC.isQuantity())
			{
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * check if the series are all time series datasets (temporal)
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualDimensions(List<ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		Dimension theD = null;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);
			if (thisC.isQuantity())
			{
				IQuantityCollection<?> qc = (IQuantityCollection<?>) thisC;
				Dimension thisD = qc.getDimension();
				if (theD == null)
				{
					theD = thisD;
				}
				else
				{
					if(thisD.equals(theD))
					{
						// all fine.
					}
					else
					{
						allValid = false;
						break;
					}
				}
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}
		}
		return allValid;
	}


	/**
	 * check if the series are all time series datasets (temporal)
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualUnits(List<ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		Unit<?> theD = null;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);
			if (thisC.isQuantity())
			{
				IQuantityCollection<?> qc = (IQuantityCollection<?>) thisC;
				Unit<?> thisD = qc.getUnits();
				if (theD == null)
				{
					theD = thisD;
				}
				else
				{
					if(thisD.equals(theD))
					{
						// all fine.
					}
					else
					{
						allValid = false;
						break;
					}
				}
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}
		}
		return allValid;
	}
	
	/**
	 * check if the series are all time series datasets (temporal)
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allTemporal(List<ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);
			if (thisC.isTemporal())
			{
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	public boolean allEqualLength(List<ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);

			// valid, check the size
			if (size == -1)
			{
				size = thisC.size();
			}
			else
			{
				if (size != thisC.size())
				{
					// oops, no
					allValid = false;
					break;
				}
			}

		}

		return allValid;
	}
}
