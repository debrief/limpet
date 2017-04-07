package info.limpet2;

/** parent for document builder classes
 * 
 * @author Ian
 *
 */
public interface IDocumentBuilder
{
  /** convert one-self into a document
   * 
   * @return
   */
  Document toDocument();
}
