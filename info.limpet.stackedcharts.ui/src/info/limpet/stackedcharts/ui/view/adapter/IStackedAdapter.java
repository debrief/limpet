package info.limpet.stackedcharts.ui.view.adapter;

import java.util.List;

import info.limpet.stackedcharts.model.Dataset;

public interface IStackedAdapter
{
  /** whether this adapter can convert objects of the
   * supplied type.  This is expected to return
   * promptly, so it can be used when determining hover target
   * 
   * @param data the object to convert
   * @return yes/no
   */
  boolean canConvert(Object data);
  
  
  /** convert the supplied data object into a dataset,
   * if possible
   * @param data the object to convert
   * @return the dataset (or null if this class can't convert it)
   */
  List<Dataset> convert(Object data);
}
