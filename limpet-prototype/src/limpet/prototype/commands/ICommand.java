package limpet.prototype.commands;

public interface ICommand
{
	public String getTitle();
	public String getDescription();
	public void execute();
	public void undo();
	public void redo();
	public boolean canUndo();
	public boolean canRedo();
}
