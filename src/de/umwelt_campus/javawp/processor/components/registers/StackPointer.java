package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * Das Stack Pointer Register des Prozessors. Der Stack liegt im Speicher im Bereich 511 bis 256.
 * @author Mathis Ströhlein
 */
public class StackPointer extends Register8 {	

	/**
	 * Erstellt ein Stack Pointer Register mit Initialwert 255.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 */
	public StackPointer(Memory memory, StatusRegister statusRegister) {
		super(memory, statusRegister, new INT8(255));
	}
	
	/**
	 * Kopiert den Wert des Akkumulators und legt ihn auf den Stack.
	 * @param accumulator Akkumulator
	 */
	public void push(Accumulator accumulator) {
		// setzt Stapel auf das was im ACC stand
		this.memory.cellValueOf(new INT16(this.data.getUnsignedValue() + 256), accumulator);
		// verringert Zeiger um 1
		this.data.setValue(this.data.getValue() - 1);
	}

	/**
	 * Kopiert den Wert des Status Registers und legt ihn auf den Stack.
	 * @param statusRegister Status Register
	 */
	public void push(StatusRegister statusRegister) {
		// setzt Stapel auf das was im SR stand
		this.memory.cellValueOf(new INT16(this.data.getUnsignedValue() + 256), statusRegister);
		// verringert Zeiger um 1
		this.data.setValue(this.data.getValue() - 1);
	}
	
	/**
	 * Holt den obersten Eintrag vom Stack und schreibt ihn in den Akkumulator.
	 * @param accumulator Akkumulator
	 */
	public void pull(Accumulator accumulator) {
		// erhöht Zeiger um 1
		this.data.setValue(this.data.getValue() + 1);
		// setzt ACC auf das was im Stapel stand
		accumulator.valueOf(this.memory.getCellData(new INT16(this.data.getUnsignedValue() + 256)));
		
		accumulator.updateZNFlags();
	}

	/**
	 * Holt den obersten Eintrag vom Stack und schreibt ihn in das Status Register. Expansion Flag bleibt immer gesetzt.
	 * @param statusRegister Status Register
	 */
	public void pull(StatusRegister statusRegister) {
		// erhöht Zeiger um 1
		this.data.setValue(this.data.getValue() + 1);
		// setzt SR auf das was im Stapel stand
		statusRegister.valueOf(this.memory.getCellData(new INT16(this.data.getUnsignedValue() + 256)));
		statusRegister.setExpansion(true); // Expansion Flag auf 1 lassen
	}

	/**
	 * Kopiert den aktuellen Wert des Stack Pointer Registers in ein Index Register X.
	 * @param indexRegisterX Index Register X
	 */
	public void transferTo(IndexRegisterX indexRegisterX) {
		indexRegisterX.valueOf(this);
		
		indexRegisterX.updateZNFlags();
	}
}
