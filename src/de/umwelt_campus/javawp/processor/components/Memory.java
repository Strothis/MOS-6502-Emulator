package de.umwelt_campus.javawp.processor.components;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.registers.IndexRegister;
import de.umwelt_campus.javawp.processor.components.registers.IndexRegisterX;
import de.umwelt_campus.javawp.processor.components.registers.Register8;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Der Hauptspeicher des Prozessors.
 * @author Mathis Ströhlein
 */
public class Memory {
	private INT8[] data; // Speicher Array
	private StatusRegister statusRegister;
	
	/**
	 * Erstellt einen Speicher und initialisiert ihn mit 0.
	 * @param statusRegister Status Register
	 */
	public Memory(StatusRegister statusRegister) {
		this.data = new INT8[65536];
		for(int i = 0; i < this.data.length; i++)
			this.data[i] = new INT8(0);
		this.statusRegister = statusRegister;
	}
	
	/**
	 * Gibt 8 Bit Zahlen Array des Speichers zurück.
	 * @return 8 Bit Zahlen Array
	 */
	public INT8[] getData() {
		return this.data;
	}
	
	/**
	 * Setzt das 8 Bit Datenarray des Speichers.
	 * @param data 8 Bit Zahlen Array
	 */
	public void setData(INT8[] data) {
		this.data = data;
	}

	/**
	 * Kopiert alle Werte eines 8 Bit Zahlen Arrays in den Speicher.
	 * @param data 8 Bit Zahlen Array
	 */
	public void valueOf(INT8[] data) {
		for(int i = 0; i < this.data.length; i++)
			this.data[i].valueOf(data[i]);
	}
	
	/**
	 * Gibt den Inhalt der Speicherzelle mit der angegebenen Adresse zurück.
	 * @param address Speicheradresse
	 * @return 8 Bit Zahl
	 */
	public INT8 getCellData(INT16 address) {
		return this.data[address.getUnsignedValue()];
	}
	
	/**
	 * Kopiert eine 8 Bit Zahl in eine Speicherzelle.
	 * @param address Speicheradresse
	 * @param cellData 8 Bit Zahl
	 */
	public void cellValueOf(INT16 address, INT8 cellData) {
		this.data[address.getUnsignedValue()].valueOf(cellData);
	}
	
	/**
	 * Kopiert den Inhalt eines 8 Bit Registers in die angegebene Speicherzelle.
	 * @param address Speicheradresse
	 * @param register8 8 Bit Register
	 */
	public void cellValueOf(INT16 address, Register8 register8) {
		this.cellValueOf(address, register8.getData());
	}
	
	/**
	 * Inkrementiert eine Speicherzelle.
	 * @param address Speicheradresse
	 */
	public void increment(INT16 address) {
		this.data[address.getUnsignedValue()].setValue(this.data[address.getUnsignedValue()].getValue() + 1);
	
		this.updateZNFlags(address);
	}

	/**
	 * Inkrementiert eine Speicherzelle.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void increment(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.increment(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}

	/**
	 * Inkrementiert eine Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void increment(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.increment(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Inkrementiert eine Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void increment(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.increment(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Dekrementiert eine Speicherzelle.
	 * @param address Speicheradresse
	 */
	public void decrement(INT16 address) {
		this.data[address.getUnsignedValue()].setValue(this.data[address.getUnsignedValue()].getValue() - 1);
		
		this.updateZNFlags(address);
	}

	/**
	 * Dekrementiert eine Speicherzelle.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void decrement(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.decrement(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}

	/**
	 * Dekrementiert eine Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void decrement(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.decrement(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Inkrementiert eine Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void decrement(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.decrement(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}

	/**
	 * Schiebt alle Bits einer Speicherzelle 1 nach links. Das hinterste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 */
	public void arithmeticalShiftLeft(INT16 address) {
		this.statusRegister.setCarry(this.data[address.getUnsignedValue()].getBit(7));
		this.data[address.getUnsignedValue()].setValue(this.data[address.getUnsignedValue()].getValue() << 1);
		
		this.updateZNFlags(address);
	}
	
	/**
	 * Schiebt alle Bits einer Speicherzelle 1 nach links. Das hinterste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void arithmeticalShiftLeft(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.arithmeticalShiftLeft(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}
	
	/**
	 * Schiebt alle Bits einer Zero Page Speicherzelle 1 nach links. Das hinterste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void arithmeticalShiftLeft(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.arithmeticalShiftLeft(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Schiebt alle Bits einer Zero Page Speicherzelle 1 nach links. Das hinterste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void arithmeticalShiftLeft(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.arithmeticalShiftLeft(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}

	/**
	 * Schiebt alle Bits einer Speicherzelle 1 nach rechts. Das vorderste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 */
	public void logicalShiftRight(INT16 address) {
		this.statusRegister.setCarry(this.data[address.getUnsignedValue()].getBit(0));
		this.data[address.getUnsignedValue()].setValue(this.data[address.getUnsignedValue()].getValue() >> 1);
		this.data[address.getUnsignedValue()].setBit(7, false);
		
		this.updateZNFlags(address);
	}

	/**
	 * Schiebt alle Bits einer Speicherzelle 1 nach rechts. Das vorderste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void logicalShiftRight(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.logicalShiftRight(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}

	/**
	 * Schiebt alle Bits einer Zero Page Speicherzelle 1 nach rechts. Das vorderste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void logicalShiftRight(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.logicalShiftRight(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Schiebt alle Bits einer Zero Page Speicherzelle 1 nach rechts. Das vorderste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void logicalShiftRight(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.logicalShiftRight(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Schiebt alle Bits einer Speicherzelle 1 nach links. Das hinterste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 */
	public void rotateLeft(INT16 address) {
		boolean carryBit = this.statusRegister.getCarry();
		this.statusRegister.setCarry(this.data[address.getUnsignedValue()].getBit(7));
		this.data[address.getUnsignedValue()].setValue(this.data[address.getUnsignedValue()].getValue() << 1);
		this.data[address.getUnsignedValue()].setBit(0, carryBit);
		
		this.updateZNFlags(address);
	}
	
	/**
	 * Schiebt alle Bits einer Speicherzelle 1 nach links. Das hinterste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void rotateLeft(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.rotateLeft(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}

	/**
	 * Schiebt alle Bits einer Zero Page Speicherzelle 1 nach links. Das hinterste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void rotateLeft(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.rotateLeft(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Schiebt alle Bits einer Zero Page Speicherzelle 1 nach links. Das hinterste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void rotateLeft(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.rotateLeft(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Schiebt alle Bits der Speicherzelle 1 nach rechts. Das vorderste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 */
	public void rotateRight(INT16 address) {
		boolean carryBit = this.statusRegister.getCarry();
		this.statusRegister.setCarry(this.data[address.getUnsignedValue()].getBit(0));
		this.data[address.getUnsignedValue()].setValue(this.data[address.getUnsignedValue()].getValue() >> 1);
		this.data[address.getUnsignedValue()].setBit(7, carryBit);
		
		this.updateZNFlags(address);
	}

	/**
	 * Schiebt alle Bits der Speicherzelle 1 nach rechts. Das vorderste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param address Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void rotateRight(INT16 address, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-Version auf
		this.rotateRight(new INT16(address.getValue() + offsetRegisterX.getData().getUnsignedValue()));
	}

	/**
	 * Schiebt alle Bits der Zero Page Speicherzelle 1 nach rechts. Das vorderste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void rotateRight(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.rotateRight(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Schiebt alle Bits der Zero Page Speicherzelle 1 nach rechts. Das vorderste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void rotateRight(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.rotateRight(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Liefert eine 16 Bit Zahl zurück, deren Lowbyte dem Inhalt der Speicherzelle der Adresse und deren Highbyte dem Inhalt der Speicherzelle der Adresse + 1 entspricht.
	 * @param lowByteAddress Lowbyte Speicheradresse
	 * @return 16 Bit Zahl
	 */
	public INT16 getLittleEndianAddress(INT16 lowByteAddress) {
		// Überlauf bei indirekter Adresse auf verschiedenen Pages
		boolean isOnSamePage = (lowByteAddress.getLowByte().getUnsignedValue() != 255);
		
		return new INT16(this.data[lowByteAddress.getUnsignedValue()], this.data[lowByteAddress.getUnsignedValue() + (isOnSamePage ? 1 : -255)]);
	}
	
	/**
	 * Liefert eine 16 Bit Zahl zurück, deren Lowbyte dem Inhalt der Speicherzelle der Adresse und deren Highbyte dem Inhalt der Speicherzelle der Adresse + 1 entspricht.
	 * @param lowByteAddress Lowbyte Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 * @return 16 Bit Zahl
	 */
	public INT16 getLittleEndianAddress(INT16 lowByteAddress, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		return this.getLittleEndianAddress(new INT16(lowByteAddress.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Liefert eine 16 Bit Zahl zurück, deren Lowbyte dem Inhalt der Zero Page Speicherzelle der Adresse und deren Highbyte dem Inhalt der Zero Page Speicherzelle der Adresse + 1 entspricht.
	 * @param lowByteZeroPageAddress Lowbyte Zero Page Speicheradresse
	 * @return 16 Bit Zahl
	 */
	public INT16 getLittleEndianAddress(INT8 lowByteZeroPageAddress) {
		// Ruft Adressen-Version auf
		return this.getLittleEndianAddress(new INT16(lowByteZeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Liefert eine 16 Bit Zahl zurück, deren Lowbyte dem Inhalt der Zero Page Speicherzelle der Adresse und deren Highbyte dem Inhalt der Zero Page Speicherzelle der Adresse + 1 entspricht.
	 * @param lowByteZeroPageAddress Lowbyte Zero Page Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 * @return 16 Bit Zahl
	 */
	public INT16 getLittleEndianAddress(INT8 lowByteZeroPageAddress, IndexRegister offsetRegister) {
		// Ruft Adressen-ZP-Version auf
		return this.getLittleEndianAddress(new INT8(lowByteZeroPageAddress.getValue() + offsetRegister.getData().getValue()));
	}
	
	/**
	 * Aktualisiert das Zero und Negative Flag des Status Registers entsprechend dem Inhalt der angegebenen Speicherzelle.
	 * @param address Speicherzelle
	 */
	private void updateZNFlags(INT16 address) {
		// Zero Flag
		this.statusRegister.setZero(this.data[address.getUnsignedValue()].getValue() == 0);
		
		// Negative Flag
		this.statusRegister.setNegative(this.data[address.getUnsignedValue()].getBit(7));
	}
}
