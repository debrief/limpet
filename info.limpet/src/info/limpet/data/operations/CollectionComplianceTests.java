package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;

import java.util.List;

import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

public class CollectionComplianceTests
{

	/**
	 * check if the specific number of arguments are supplied
	 * 
	 * @param selection
	 * @param num
	 * @return
	 */
	public boolean exactNumber(final List<ICollection> selection, final int num)
	{
		return selection.size() == num;
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allNonQuantity(List<ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);
			if (thisC.isQuantity())
			{
				// oops, no
				allValid = false;
				break;
			}
			else
			{
			}
		}
		return allValid;
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allQuantity(List<? extends ICollection> selection)
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
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean nonEmpty(List<? extends ICollection> selection)
	{
		return selection.size() > 0;
	}

	/**
	 * check if the series are all have equal dimensions
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualDimensions(List<? extends ICollection> selection)
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
					if (thisD.equals(theD))
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
	 * check if the series all have equal units
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualUnits(List<? extends ICollection> selection)
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
					if (thisD.equals(theD))
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

	/**
	 * check if the series are all of equal length, or singletons
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualLengthOrSingleton(List<? extends ICollection> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			ICollection thisC = selection.get(i);

			int thisSize = thisC.size();

			// valid, check the size
			if (size == -1)
			{
				// ok, is this a singleton?
				if (thisSize != 1)
				{
					// nope, it's a real array store it.
					size = thisSize;
				}
			}
			else
			{
				if ((thisSize != size) && (thisSize != 1))
				{
					// oops, no
					allValid = false;
					break;
				}
			}

		}

		return allValid;
	}

	/**
	 * check if the series are all of equal length
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualLength(List<? extends ICollection> selection)
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
