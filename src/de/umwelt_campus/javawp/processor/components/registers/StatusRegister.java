package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT8;

/**
 * Das Status Register des Prozessors.
 * Bitbelegung: 7: Negative, 6: Overflow, 5: Expansion, 4: Break, 3: Decimal, 2: Interrupt, 1: Zero, 0: Carry
 * @author Mathis Ströhlein
 */
public class StatusRegister extends Register8 {
	
	/**
	 * Erstellt ein Status Register mit Expansion und Zero Flag gesetzt.
	 */
	public StatusRegister() {
		super(null, null, new INT8(34));
	}
	
	/**
	 * Gibt den Wert des Carry Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getCarry() {
		return this.data.getBit(0);
	}
	
	/**
	 * Setzt das Carry Flag.
	 * @param state Neuer Wert
	 */
	public void setCarry(boolean state) {
		this.data.setBit(0, state);
	}

	/**
	 * Gibt den Wert des Zero Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getZero() {
		return this.data.getBit(1);
	}

	/**
	 * Setzt das Zero Flag.
	 * @param state Neuer Wert
	 */
	public void setZero(boolean state) {
		this.data.setBit(1, state);
	}

	/**
	 * Gibt den Wert des Interrupt Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getInterrupt() {
		return this.data.getBit(2);
	}

	/**
	 * Setzt das Interrupt Flag.
	 * @param state Neuer Wert
	 */
	public void setInterrupt(boolean state) {
		this.data.setBit(2, state);
	}

	/**
	 * Gibt den Wert des Decimal Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getDecimal() {
		return this.data.getBit(3);
	}

	/**
	 * Setzt das Decimal Flag.
	 * @param state Neuer Wert
	 */
	public void setDecimal(boolean state) {
		this.data.setBit(3, state);
	}

	/**
	 * Gibt den Wert des Break Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getBreak() {
		return this.data.getBit(4);
	}

	/**
	 * Setzt das Break Flag.
	 * @param state Neuer Wert
	 */
	public void setBreak(boolean state) {
		this.data.setBit(4, state);
	}

	/**
	 * Gibt den Wert des Expansion Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getExpansion() {
		return this.data.getBit(5);
	}

	/**
	 * Setzt das Expansion Flag.
	 * @param state Neuer Wert
	 */
	public void setExpansion(boolean state) {
		this.data.setBit(5, state);
	}

	/**
	 * Gibt den Wert des Overflow Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getOverflow() {
		return this.data.getBit(6);
	}

	/**
	 * Setzt das Overflow Flag.
	 * @param state Neuer Wert
	 */
	public void setOverflow(boolean state) {
		this.data.setBit(6, state);
	}

	/**
	 * Gibt den Wert des Negative Flags zurück.
	 * @return Wert des Bits
	 */
	public boolean getNegative() {
		return this.data.getBit(7);
	}

	/**
	 * Setzt das Negative Flag.
	 * @param state Neuer Wert
	 */
	public void setNegative(boolean state) {
		this.data.setBit(7, state);
	}
}
