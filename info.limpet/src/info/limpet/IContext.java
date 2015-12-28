package info.limpet;

public interface IContext
{
	public static enum Status
	{
		INFO, WARNING, ERROR;
	}
	
	/** get a string from the user, or null if the user cancelled the operation
	 * 
	 * @param title  shown in the dialog heading
	 * @param description what the text is being input for
	 * @param defaultText the text to pre-populate the input box
	 * @return user-entered string, or null for cancel
	 */
	public String getInput(String title, String description, String defaultText);

	/** indicate this warning
	 * 
	 * @param string
	 */
	public void logError(Status status, String message, Exception e);
}
