package de.umwelt_campus.javawp.exceptions;

/**
 * Wird geworfen während des Assemblierens, wenn ein Labelname einen Doppelpunkt beinhaltet.
 * @author Mathis Ströhlein
 */
public class InvalidLabelNameException extends IllegalArgumentException {
	
	/**
	 * Wirft eine InvalidLabelName Exception, mit vorgefertigter Nachricht.
	 * @param lineNumber Zeilennummer
	 * @param line Unbearbeiteter Zeilenstring
	 * @param labelName Fehlerhafter Labelname
	 */
	public InvalidLabelNameException(int lineNumber, String line, String labelName) {
		super("Syntax Fehler in Zeile " + lineNumber + ": **" + line + "** (Labelname \"" + labelName + "\" ist ungültig).");
	}
}