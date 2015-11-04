package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.IStore.IStoreItem;

import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

import org.opengis.geometry.Geometry;

public class CollectionComplianceTests
{

	/**
	 * check if the specific number of arguments are supplied
	 * 
	 * @param selection
	 * @param num
	 * @return
	 */
	public boolean exactNumber(final List<? extends IStoreItem> selection,
			final int num)
	{
		return selection.size() == num;
	}

	/**
	 * check if the series are all non locations
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allNonLocation(List<? extends IStoreItem> selection)
	{
		// are they all non location?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				Class<?> theClass = thisC.storedClass();
				if (Geometry.class.equals(theClass))
				{
					allValid = false;
					break;
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * check if the series are all non locations
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allLocation(List<? extends IStoreItem> selection)
	{
		// are they all non location?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				Class<?> theClass = thisC.storedClass();
				if (!Geometry.class.equals(theClass))
				{
					allValid = false;
					break;
				}
			}
			else
			{
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
	public boolean allNonTemporal(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isTemporal())
				{
					// oops, no
					allValid = false;
					break;
				}
				else
				{
				}
			}
			else
			{
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
	public boolean allNonQuantity(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
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
			else
			{
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * determine if these datasets are suited to a temporal operation - where we
	 * interpolate time values
	 * 
	 * @param selection
	 * @return
	 */
	public boolean suitableForTimeInterpolation(
			List<? extends IStoreItem> selection)
	{
		// are suitable
		boolean suitable = selection.size() >= 2;

		Long startT = null;
		Long endT = null;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isQuantity())
				{
					if (thisC.isTemporal())
					{
						ITemporalQuantityCollection<?> tq = (ITemporalQuantityCollection<?>) thisC;
						long thisStart = tq.start();
						long thisEnd = tq.finish();

						if (startT == null)
						{
							startT = thisStart;
							endT = thisEnd;
						}
						else
						{
							// check the overlap
							if (thisStart > endT || thisEnd < startT)
							{
								suitable = false;
								break;
							}
						}
					}
				}
				else
				{
					// oops, no
					suitable = false;
					break;
				}
			}
			else
			{
				suitable = false;
				break;
			}

		}
		return suitable;
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allQuantity(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
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
			else
			{
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
	public boolean nonEmpty(List<? extends IStoreItem> selection)
	{
		return selection.size() > 0;
	}

	/**
	 * check if the series are all have equal dimensions
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualDimensions(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = false;
		Dimension theD = null;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
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
							allValid = true;
						}
						else
						{
							allValid = false;
							break;
						}
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
	public boolean allEqualUnits(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		Unit<?> theD = null;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
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
			else
			{
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
	public boolean allTemporal(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
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
	public boolean allEqualLengthOrSingleton(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;

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
			else
			{
				allValid = false;
				break;
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
	public boolean allEqualLength(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;

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
			else
			{
				allValid = false;
				break;
			}
		}

		return allValid;
	}

	public boolean allCollections(List<? extends IStoreItem> selection)
	{
		boolean res = true;
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			if (!(storeItem instanceof ICollection))
			{
				res = false;
				break;
			}
		}
		return res;
	}
}
