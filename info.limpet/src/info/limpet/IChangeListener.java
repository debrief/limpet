package info.limpet;

public interface IChangeListener
{
	public void dataChanged(ICollection subject);
	
	public void collectionDeleted(ICollection subject);
}
