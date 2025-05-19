package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * Befehlszähler des Prozessors.
 * @author Mathis Ströhlein
 */
public class ProgramCounter extends Register16 {
	private StackPointer stackPointer;

	/**
	 * Erstellt einen Befehlszähler.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 * @param stackPointer Stack Pointer
	 * @param initialValue Initialwert
	 */
	public ProgramCounter(Memory memory, StatusRegister statusRegister, StackPointer stackPointer, INT16 initialValue) {
		super(memory, statusRegister, initialValue);
		
		this.stackPointer = stackPointer;
	}
	
	/**
	 * Inkrementiert den Befehlszähler.
	 */
	public void increment() {
		this.data.setValue(this.data.getValue() + 1);
	}
	
	/**
	 * Kopiert eine Speicheradresse in den Befehlszähler.
	 * @param address Speicheradresse
	 */
	public void jump(INT16 address) {
		this.valueOf(address);
	}
	
	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Carry Bit nicht gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnCarryClear(INT8 offset) {
		if(!this.statusRegister.getCarry())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Carry Bit gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnCarrySet(INT8 offset) {
		if(this.statusRegister.getCarry())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Zero Bit gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnEqual(INT8 offset) {
		if(this.statusRegister.getZero())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Zero Bit nicht gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnNotEqual(INT8 offset) {
		if(!this.statusRegister.getZero())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Negative Bit nicht gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnPlus(INT8 offset) {
		if(!this.statusRegister.getNegative())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Negative Bit gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnMinus(INT8 offset) {
		if(this.statusRegister.getNegative())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Overflow Bit nicht gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnOverflowClear(INT8 offset) {
		if(!this.statusRegister.getOverflow())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Addiert einen Offset auf den Befehlszähler, falls das Overflow Bit gesetzt ist.
	 * @param offset Offset im Zwei Komplement
	 */
	public void branchOnOverflowSet(INT8 offset) {
		if(this.statusRegister.getOverflow())
			this.data.setValue(this.data.getValue() + offset.getValue());
	}

	/**
	 * Sprint in Unterprogramm: Schiebt die aktuelle Adresse + 2 (Rücksprungadresse) auf den Stapel und kopiert die angegebene Speicheradresse in den Befehlszähler.
	 * @param address Speicheradresse
	 */
	public void jumpToSubroutine(INT16 address) {
		INT16 returnAddress = new INT16(this.data.getValue() - 1);
		
		// setzt Stapel auf das High Byte der Adresse
		this.memory.cellValueOf(new INT16(this.stackPointer.getData().getUnsignedValue() + 256), returnAddress.getHighByte());
		// verringert Zeiger um 1
		this.stackPointer.getData().setValue(this.stackPointer.getData().getValue() - 1);
		// setzt Stapel auf das High Byte der Adresse
		this.memory.cellValueOf(new INT16(this.stackPointer.getData().getUnsignedValue() + 256), returnAddress.getLowByte());
		// verringert Zeiger um 1
		this.stackPointer.getData().setValue(this.stackPointer.getData().getValue() - 1);
		
		this.valueOf(address);
	}
	
	/**
	 * Kehrt aus Unterprogramm zurück: Setzt den Befehlszähler auf die Adresse vom Stapel + 1.
	 */
	public void returnFromSubroutine() {
		// erhöht Zeiger um 1
		this.stackPointer.getData().setValue(this.stackPointer.getData().getValue() + 1);
		// holt Low Byte vom Stack
		INT8 lowByte = this.memory.getCellData(new INT16(this.stackPointer.getData().getUnsignedValue() + 256));
		// erhöht Zeiger um 1
		this.stackPointer.getData().setValue(this.stackPointer.getData().getValue() + 1);
		// holt High Byte vom Stack
		INT8 highByte = this.memory.getCellData(new INT16(this.stackPointer.getData().getUnsignedValue() + 256));
		
		INT16 returnAddress = new INT16(lowByte, highByte);
		returnAddress.setValue(returnAddress.getValue() + 1);
		
		this.valueOf(returnAddress);
	}
}
