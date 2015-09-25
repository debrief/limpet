package info.limpet;

import java.util.List;


/** encapsulation of some change to data
 * 
 * @author ian
 *
 */
public interface ICommand<T extends ICollection>
{
	public String getTitle();
	public String getDescription();
	public void execute();
	public void undo();
	public void redo();
	public boolean canUndo();
	public boolean canRedo();
	public List<T> getOutputs();
	public List<T> getInputs();
}
