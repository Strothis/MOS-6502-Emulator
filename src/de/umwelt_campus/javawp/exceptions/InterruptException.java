package de.umwelt_campus.javawp.exceptions;

/**
 * Wird geworfen wenn das Programm entweder pausiert wird, nachdem man Run drueckt oder vollständig ausgeführt wurde.
 * Funktioniert wie ein Breakpoint.
 * @author Michael Weber
 */
public class InterruptException extends RuntimeException {
	
	/**
	 * Wirft eine Interrupt Exception, mit vorgefertigter Nachricht.
	 */
	public InterruptException() {
		super("Info: Das Programm wurde unterbrochen.");
	}
}
