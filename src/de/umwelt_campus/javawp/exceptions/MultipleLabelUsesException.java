package de.umwelt_campus.javawp.exceptions;

/**
 * Wird geworfen während des Assemblierens, wenn ein Label bereits existiert.
 * @author Mathis Ströhlein
 */
public class MultipleLabelUsesException extends IllegalArgumentException {
	
	/**
	 * Wirft eine MultipleLabelUses Exception, mit vorgefertigter Nachricht.
	 * @param lineNumber Zeilennummer
	 * @param line Unbearbeiteter Zeilenstring
	 * @param labelName Doppelter Labelname
	 */
	public MultipleLabelUsesException(int lineNumber, String line, String labelName) {
		super("Syntax Fehler in Zeile " + lineNumber + ": **" + line + "** (Label \"" + labelName + "\" existiert bereits).");
	}
}