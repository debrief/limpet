package limpet.prototype.ian_generics;

public interface ITemporalCollection {

	/** time of the first observation
	 * 
	 * @return
	 */
	public long start();
	
	/** time of the last observation
	 * 
	 * @return
	 */
	public long finish();
	
	/** time period between first & last measurement.  0 if just one observation, -1 if set empty.
	 * 
	 * @return
	 */
	public long duration();
	
	/** number of observations per milli, across the whole collection
	 * 
	 * @return
	 */
	public double rate();
}
