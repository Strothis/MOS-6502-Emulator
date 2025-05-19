package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * Index Register Überklasse
 * @author Mathis Ströhlein
 */
public abstract class IndexRegister extends Register8 {	
	
	/**
	 * Leitet Parameter an übergeordneten Konstruktor weiter.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 * @param initialValue Initialwert
	 */
	public IndexRegister(Memory memory, StatusRegister statusRegister, INT8 initialValue) {
		super(memory, statusRegister, initialValue);
	}

	/**
	 * Lädt eine Konstante in das Index Register.
	 * @param constant Konstante
	 */
	public void loadI(INT8 constant) {
		this.valueOf(constant);
		
		this.updateZNFlags();
	}

	/**
	 * Lädt den Inhalt einer Speicherzelle in das Index Register.
	 * @param address Speicheradresse
	 */
	public void load(INT16 address) {
		// Ruft Konstanten-Version auf
		this.loadI(this.memory.getCellData(address));		
	}

	/**
	 * Lädt den Inhalt einer Zero Page Speicherzelle in das Index Register.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void load(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.load(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Speichert den aktuellen Wert des Index Registers in einer Speicherzelle.
	 * @param address Speicheradresse
	 */	
	public void store(INT16 address) {
		this.memory.cellValueOf(address, this);
	}

	/**
	 * Speichert den aktuellen Wert des Index Registers in einer Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void store(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.store(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Kopiert den aktuellen Wert des Index Registers in den Akkumulator.
	 * @param accumulator Akkumulator
	 */
	public void transferTo(Accumulator accumulator) {
		accumulator.valueOf(this);
		
		accumulator.updateZNFlags();
	}
	
	/**
	 * Inkrementiert das Index Register
	 */
	public void increment() {
		this.data.setValue(this.data.getValue() + 1);
		
		this.updateZNFlags();
	}

	/**
	 * Dekrementiert das Index Register
	 */
	public void decrement() {
		this.data.setValue(this.data.getValue() - 1);
		
		this.updateZNFlags();
	}

	/**
	 * Vergleicht das Index Register mit einer Konstante und setzt die Prozessor Flags entsprechend.
	 * Carry: Index Register (unsigned) ist größer gleich Konstante (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Index Register (signed) ist kleiner als Konstante (signed)
	 * @param constant Konstante
	 */
	public void compareI(INT8 constant) {
		int difference = this.data.getValue() - constant.getValue();
		int differenceUnsigned = this.data.getUnsignedValue() - constant.getUnsignedValue();
		
		this.statusRegister.setCarry(differenceUnsigned >= 0);
		this.statusRegister.setZero(difference == 0);
		this.statusRegister.setNegative(difference < 0);
	}

	/**
	 * Vergleicht das Index Register mit dem Inhalt einer Speicherzelle und setzt die Prozessor Flags entsprechend.
	 * Carry: Index Register (unsigned) ist größer gleich Inhalt Speicherzelle (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Index Register (signed) ist kleiner als Inhalt Speicherzelle (signed)
	 * @param address Speicheradresse
	 */
	public void compare(INT16 address) {
		// Ruft Konstanten-Version auf
		this.compareI(this.memory.getCellData(address));
	}

	/**
	 * Vergleicht das Index Register mit dem Inhalt einer Zero Page Speicherzelle und setzt die Prozessor Flags entsprechend.
	 * Carry: Index Register (unsigned) ist größer gleich Inhalt Zero Page Speicherzelle (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Index Register (signed) ist kleiner als Inhalt Zero Page Speicherzelle (signed)
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void compare(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.compare(new INT16(zeroPageAddress, new INT8(0)));
	}
}
