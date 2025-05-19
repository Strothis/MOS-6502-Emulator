package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * Index Register Y des Prozessors.
 * @author Mathis Ströhlein
 */
public class IndexRegisterY extends IndexRegister {

	/**
	 * Erstellt ein Index Register Y mit Initialwert 0.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 */
	public IndexRegisterY(Memory memory, StatusRegister statusRegister) {
		super(memory, statusRegister, new INT8(0));
	}

	/**
	 * Lädt den Inhalt einer Speicherzelle in das Index Register Y.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void load(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.load(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}

	/**
	 * Lädt den Inhalt einer Zero Page Speicherzelle in das Index Register Y.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void load(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.load(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}

	/**
	 * Speichert den aktuellen Wert des Index Registers Y in einer Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void store(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.store(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
}
