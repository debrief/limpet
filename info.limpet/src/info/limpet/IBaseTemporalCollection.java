package info.limpet;

import java.util.List;

public interface IBaseTemporalCollection
{

	/**
	 * time of the first observation
	 * 
	 * @return
	 */
	public long start();

	/**
	 * time of the last observation
	 * 
	 * @return
	 */
	public long finish();

	/**
	 * time period between first & last measurement. 0 if just one observation, -1
	 * if set empty.
	 * 
	 * @return
	 */
	public long duration();

	/**
	 * number of observations per milli, across the whole collection
	 * 
	 * @return
	 */
	public double rate();
	
	/** retrieve the times
	 * 
	 * @return
	 */
	public List<Long> getTimes();
	
}