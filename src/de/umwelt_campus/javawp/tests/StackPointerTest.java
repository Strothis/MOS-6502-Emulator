package de.umwelt_campus.javawp.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.StackPointer;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Test für StackPointer Klasse.
 * @author Mathis Ströhlein
 */
class StackPointerTest {

	private final StatusRegister statusRegister = new StatusRegister();
	private final Memory memory = new Memory(this.statusRegister);
	private final Accumulator accumulator = new Accumulator(this.memory, this.statusRegister);
	private final StackPointer stackPointer = new StackPointer(this.memory, this.statusRegister);

	@Test
	void push() {
		// Befehle
		this.accumulator.loadI(new INT8(206));
		this.stackPointer.push(this.accumulator);
		
		// Stack Pointer
		assertEquals(this.stackPointer.getData().getValue(), new INT8(254).getValue());
		
		// Im Speicher
		assertEquals(this.memory.getCellData(new INT16(511)).getValue(), new INT8(206).getValue());
	}
	
	@Test
	void pushOverflow() {
		// Befehle
		this.accumulator.loadI(new INT8(5));
		this.stackPointer.push(this.accumulator);			
		
		this.accumulator.loadI(new INT8(3));
		for(int i = 0; i < 256; i++)
			this.stackPointer.push(this.accumulator);
		
		// Stack Pointer (wieder am Anfang)
		assertEquals(this.stackPointer.getData().getValue(), new INT8(254).getValue());
		
		// Im Speicher (5 mit 3 überschrieben)
		assertEquals(this.memory.getCellData(new INT16(511)).getValue(), new INT8(3).getValue());
	}
	
	@Test
	void pull() {
		// Befehle
		this.statusRegister.setInterrupt(true);
		
		// Aktuellen Status "zwischenspeichern"
		this.stackPointer.push(this.statusRegister);	
		
		this.statusRegister.setInterrupt(false);
		this.statusRegister.setCarry(true);
		this.statusRegister.setDecimal(true);
		
		this.stackPointer.pull(this.statusRegister);
		
		// Flags
		assertTrue(this.statusRegister.getInterrupt());
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getDecimal());
	}
}
