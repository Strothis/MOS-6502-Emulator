package de.umwelt_campus.javawp.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Test für Memory Klasse.
 * @author Mathis Ströhlein
 */
class MemoryTest {

	private final StatusRegister statusRegister = new StatusRegister();
	private final Memory memory = new Memory(this.statusRegister);
	private final Accumulator accumulator = new Accumulator(this.memory, this.statusRegister);
	
	@Test
	void littleEndianAddress() {
		// Befehle
		this.accumulator.loadI(new INT8("$6C"));
		this.accumulator.store(new INT16(1123));
		
		this.accumulator.loadI(new INT8("$D7"));
		this.accumulator.store(new INT16(1124));
		
		// MEM Ergebnis
		assertEquals(this.memory.getLittleEndianAddress(new INT16(1123)).getValue(), new INT16("$D76C").getValue());
	}

	@Test
	void littleEndianAddressOverflow() {
		// Befehle
		this.accumulator.loadI(new INT8("$6C"));
		this.accumulator.store(new INT16(255));
		
		this.accumulator.loadI(new INT8("$D7"));
		this.accumulator.store(new INT16(256));
		
		// MEM Ergebnis
		assertNotEquals(this.memory.getLittleEndianAddress(new INT16(255)).getValue(), new INT16("$D76C").getValue());
	}
}
