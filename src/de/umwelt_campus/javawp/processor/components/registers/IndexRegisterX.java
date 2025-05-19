package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * Index Register X des Prozessors.
 * @author Mathis Ströhlein
 */
public class IndexRegisterX extends IndexRegister {
	
	/**
	 * Erstellt ein Index Register X mit Initialwert 0.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 */
	public IndexRegisterX(Memory memory, StatusRegister statusRegister) {
		super(memory, statusRegister, new INT8(0));
	}

	/**
	 * Lädt den Inhalt einer Speicherzelle in das Index Register X.
	 * @param address Speicheradresse
	 * @param offsetRegisterY Das Index Register Y dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void load(INT16 address, IndexRegisterY offsetRegisterY) {
		// Ruft Adressen-Version auf
		this.load(new INT16(address.getValue() + offsetRegisterY.getData().getUnsignedValue()));
	}

	/**
	 * Lädt den Inhalt einer Zero Page Speicherzelle in das Index Register X.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterY Das Index Register Y dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void load(INT8 zeroPageAddress, IndexRegisterY offsetRegisterY) {
		// Ruft Adressen-ZP-Version auf
		this.load(new INT8(zeroPageAddress.getValue() + offsetRegisterY.getData().getValue()));
	}

	/**
	 * Speichert den aktuellen Wert des Index Registers X in einer Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterY Das Index Register Y dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void store(INT8 zeroPageAddress, IndexRegisterY offsetRegisterY) {
		// Ruft Adressen-ZP-Version auf
		this.store(new INT8(zeroPageAddress.getValue() + offsetRegisterY.getData().getValue()));
	}

	/**
	 * Kopiert den aktuellen Wert des Index Register X in den Stack Pointer.
	 * @param stackPointer Stack Pointer
	 */
	public void transferTo(StackPointer stackPointer) {
		stackPointer.valueOf(this);
	}
}
