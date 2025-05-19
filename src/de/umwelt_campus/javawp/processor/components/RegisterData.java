package de.umwelt_campus.javawp.processor.components;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.IndexRegisterX;
import de.umwelt_campus.javawp.processor.components.registers.IndexRegisterY;
import de.umwelt_campus.javawp.processor.components.registers.ProgramCounter;
import de.umwelt_campus.javawp.processor.components.registers.StackPointer;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Speichert Registerdaten, um sie auf dem Stack zu abzulegen und die undo() Methode zu ermöglichen.
 * @author Mathis Ströhlein
 */
public class RegisterData {
	private INT8 statusRegisterData;
	private INT16 programCounterData;
	private INT8[] memoryData;
	private INT8 accumulatorData;
	private INT8 indexRegisterXData;
	private INT8 indexRegisterYData;
	private INT8 stackPointerData;
	
	/**
	 * Erstellt ein Registerdaten Objekt mit den kopierten Werten der Register und dem Speicher.
	 * @param statusRegister Status Register
	 * @param programCounter Befehlszähler
	 * @param memory Speicher
	 * @param accumulator Akkumulator
	 * @param indexRegisterX Index Register X
	 * @param indexRegisterY Index Register Y
	 * @param stackPointer Stack Pointer
	 */
	public RegisterData(StatusRegister statusRegister, ProgramCounter programCounter, Memory memory, Accumulator accumulator, IndexRegisterX indexRegisterX, IndexRegisterY indexRegisterY, StackPointer stackPointer) {
		this.statusRegisterData = new INT8();
		this.statusRegisterData.valueOf(statusRegister);
		
		this.programCounterData = new INT16();
		this.programCounterData.valueOf(programCounter);
		
		this.memoryData = new INT8[memory.getData().length];
		for(int i = 0; i < this.memoryData.length; i++) {
			this.memoryData[i] = new INT8();
			this.memoryData[i].valueOf(memory.getData()[i]);
		}
		
		this.accumulatorData = new INT8();
		this.accumulatorData.valueOf(accumulator);
		
		this.indexRegisterXData = new INT8();
		this.indexRegisterXData.valueOf(indexRegisterX);
		
		this.indexRegisterYData = new INT8();
		this.indexRegisterYData.valueOf(indexRegisterY);
		
		this.stackPointerData = new INT8();
		this.stackPointerData.valueOf(stackPointer);
	}

	/**
	 * Gibt den Inhalt des Status Registers zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getStatusRegisterData() {
		return this.statusRegisterData;
	}

	/**
	 * Gibt den Inhalt des Befehlszählers zurück.
	 * @return 16 Bit Zahl
	 */
	public INT16 getProgramCounterData() {
		return this.programCounterData;
	}

	/**
	 * Gibt den Inhalt des Speichers zurück.
	 * @return 8 Bit Zahlen Array
	 */
	public INT8[] getMemoryData() {
		return this.memoryData;
	}

	/**
	 * Gibt den Inhalt des Akkumulators zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getAccumulatorData() {
		return this.accumulatorData;
	}

	/**
	 * Gibt den Inhalt des Index Registers X zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getIndexRegisterXData() {
		return this.indexRegisterXData;
	}

	/**
	 * Gibt den Inhalt des Index Registers Y zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getIndexRegisterYData() {
		return this.indexRegisterYData;
	}

	/**
	 * Gibt den Inhalt des Stack Pointers zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getStackPointerData() {
		return this.stackPointerData;
	}
}
