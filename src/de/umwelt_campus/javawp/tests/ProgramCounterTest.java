package de.umwelt_campus.javawp.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.ProgramCounter;
import de.umwelt_campus.javawp.processor.components.registers.StackPointer;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Test für ProgramCounter Klasse.
 * @author Mathis Ströhlein
 */
class ProgramCounterTest {

	private final StatusRegister statusRegister = new StatusRegister();
	private final Memory memory = new Memory(this.statusRegister);
	private final Accumulator accumulator = new Accumulator(this.memory, this.statusRegister);
	private final StackPointer stackPointer = new StackPointer(this.memory, this.statusRegister);
	private final ProgramCounter programCounter = new ProgramCounter(this.memory, this.statusRegister, this.stackPointer, new INT16(0x0600));
	
	@Test
	void subroutines() {
		// Befehle (increments simulieren Bytecode einlesen)
		this.programCounter.increment();
		this.programCounter.increment();
		this.accumulator.loadI(new INT8(10));
		
		// Hier Adresse 0x0602
		assertEquals(this.programCounter.getData().getValue(), new INT16(0x0602).getValue());
		
		this.programCounter.increment();
		this.programCounter.increment();
		this.programCounter.increment();
		// Hier Adresse 0x0605 (Die Rücksprungadresse)
		this.programCounter.jumpToSubroutine(new INT16(1234));
		
		// Stack Pointer (-2 wegen 2 Byte Adresse auf Stack)
		assertEquals(this.stackPointer.getData().getValue(), new INT8(253).getValue());
		
		// Irgendein Befehl
		this.programCounter.increment();
		this.programCounter.increment();
		this.accumulator.loadI(new INT8(10));
		
		// Hier Adresse 1236
		assertEquals(this.programCounter.getData().getValue(), new INT16(1236).getValue());
		
		this.programCounter.increment();
		this.programCounter.returnFromSubroutine();
		
		// Adresse sollte bei Rücksprung sein (nach JSR)
		assertEquals(this.programCounter.getData().getValue(), new INT16(0x0605).getValue());
	}

}
