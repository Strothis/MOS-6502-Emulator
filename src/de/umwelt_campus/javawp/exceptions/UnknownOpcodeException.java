package de.umwelt_campus.javawp.exceptions;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;

/**
 * Wird geworfen während der Laufzeit, wenn einem Opcode kein Befehl zugeordnet werden kann.
 * @author Mathis Ströhlein
 */
public class UnknownOpcodeException extends IllegalArgumentException {

	/**
	 * Wirft eine UnknownOpcode Exception, mit vorgefertigter Nachricht.
	 * @param memoryCell Speicheradresse mit unbekanntem Opcode
	 * @param opcode Unbekannter Operand
	 */
	public UnknownOpcodeException(INT16 memoryCell, INT8 opcode) {
		super("Laufzeit Fehler: Dem Opcode \"" + opcode.getHexString() + "\" (Speicherzelle: " + memoryCell.getUnsignedDecimalString() + " / $" + memoryCell.getHexString() + ") ist kein Befehl zugeordnet.");
	}
}