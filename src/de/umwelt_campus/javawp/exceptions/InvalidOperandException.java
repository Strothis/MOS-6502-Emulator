package de.umwelt_campus.javawp.exceptions;

/**
 * Wird geworfen während des Assemblierens, wenn ein Operand nicht interpretiert werden kann.
 * @author Mathis Ströhlein
 */
public class InvalidOperandException extends IllegalArgumentException {

	/**
	 * Wirft eine InvalidOperand Exception, mit vorgefertigter Nachricht.
	 * @param lineNumber Zeilennummer
	 * @param line Unbearbeiteter Zeilenstring
	 * @param operand Fehlerhafter Operand
	 */
	public InvalidOperandException(int lineNumber, String line, String operand) {
		super("Syntax Fehler in Zeile " + lineNumber + ": **" + line + "** (Operand \"" + operand + "\" ist ungültig).");
	}

	/**
	 * Wirft eine InvalidOperand Exception (kein Operand gesetzt), mit vorgefertigter Nachricht.
	 * @param lineNumber Zeilennummer
	 * @param line Unbearbeiteter Zeilenstring
	 */
	public InvalidOperandException(int lineNumber, String line) {
		super("Syntax Fehler in Zeile " + lineNumber + ": **" + line + "** (Es wurde kein Operand angegeben).");
	}
}