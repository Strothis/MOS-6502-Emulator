package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * 8 Bit Register Überklasse.
 * @author Mathis Ströhlein
 */
public abstract class Register8 {
	protected INT8 data; // 1 byte Zahl
	protected Memory memory; // Der Hauptspeicher
	protected StatusRegister statusRegister;

	/**
	 * Setzt Attribute auf Parameter.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 * @param initialValue Initialwert
	 */
	public Register8(Memory memory, StatusRegister statusRegister, INT8 initialValue) {
		this.data = new INT8();
		this.data.valueOf(initialValue);
		
		this.memory = memory;
		this.statusRegister = statusRegister;
	}

	/**
	 * Gibt 8 Bit Zahl Objekt des 8 Bit Registers zurück.
	 * @return 8 Bit Zahl Objekt
	 */
	public INT8 getData() {
		return this.data;
	}
	
	/**
	 * Setzt das 8 Bit Datenobjekt des Registers.
	 * @param data 8 Bit Zahl
	 */
	public void setData(INT8 data) {
		this.data = data;
	}

	/**
	 * Kopiert den Wert eines 8 Bit Zahl Objekts ins 8 Bit Register.
	 * @param data 8 Bit Zahl
	 */
	public void valueOf(INT8 data) {
		this.data.valueOf(data);
	}

	/**
	 * Kopiert den Wert eines anderen 8 Bit Registers ins 8 Bit Register.
	 * @param register8 8 Bit Register
	 */
	public void valueOf(Register8 register8) {
		this.valueOf(register8.getData());
	}
	
	/**
	 * Aktualisiert das Zero und Negative Flag des Status Registers entsprechend dem aktuellen Registerwert.
	 */
	protected void updateZNFlags() {
		// Zero Flag
		this.statusRegister.setZero(this.data.getValue() == 0);
		
		// Negative Flag
		this.statusRegister.setNegative(this.data.getBit(7));
	}
}
