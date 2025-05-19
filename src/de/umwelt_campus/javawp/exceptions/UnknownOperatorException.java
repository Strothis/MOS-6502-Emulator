package de.umwelt_campus.javawp.exceptions;

/**
 * Wird geworfen während des Assemblierens, wenn ein Operator nicht existiert.
 * @author Mathis Ströhlein
 */
public class UnknownOperatorException extends IllegalArgumentException {

	/**
	 * Wirft eine UnknownOperator Exception, mit vorgefertigter Nachricht.
	 * @param lineNumber Zeilennummer
	 * @param line Unbearbeiteter Zeilenstring
	 * @param operator Unbekannter Operator
	 */
	public UnknownOperatorException(int lineNumber, String line, String operator) {
		super("Syntax Fehler in Zeile " + lineNumber + ": **" + line + "** (Der Operator \"" + operator + "\" existiert nicht).");
	}
}