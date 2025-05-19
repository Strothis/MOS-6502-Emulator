package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * 16 Bit Register Überklasse.
 * @author Mathis Ströhlein
 */
public abstract class Register16 {
	protected INT16 data;
	protected Memory memory;
	protected StatusRegister statusRegister;
	
	/**
	 * Setzt Attribute auf Parameter.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 * @param initialValue Initialwert
	 */
	public Register16(Memory memory, StatusRegister statusRegister, INT16 initialValue) {
		this.data = new INT16();
		this.data.valueOf(initialValue);
		
		this.memory = memory;
		this.statusRegister = statusRegister;
	}
	
	/**
	 * Gibt 16 Bit Zahl Objekt des 16 Bit Registers zurück.
	 * @return 16 Bit Zahl Objekt
	 */
	public INT16 getData() {
		return this.data;
	}
	
	/**
	 * Setzt das 16 Bit Datenobjekt des Registers.
	 * @param data 16 Bit Zahl
	 */
	public void setData(INT16 data) {
		this.data = data;
	}
	
	/**
	 * Kopiert den Wert eines 16 Bit Zahl Objekts ins 16 Bit Register.
	 * @param data 16 Bit Zahl
	 */
	public void valueOf(INT16 data) {
		this.data.valueOf(data);
	}

	/**
	 * Kopiert den Wert eines anderen 16 Bit Registers ins 16 Bit Register.
	 * @param register16 16 Bit Register
	 */
	public void valueOf(Register16 register16) {
		this.valueOf(register16.getData());
	}
}
