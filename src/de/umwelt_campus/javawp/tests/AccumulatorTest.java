package de.umwelt_campus.javawp.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Test für Accumulator Klasse.
 * @author Mathis Ströhlein
 */
class AccumulatorTest {
	
	private final StatusRegister statusRegister = new StatusRegister();
	private final Memory memory = new Memory(this.statusRegister);
	private final Accumulator accumulator = new Accumulator(this.memory, this.statusRegister);
	
	@Test
	void load() {
		// Befehle
		this.accumulator.loadI(new INT8(130));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(130).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getZero());
		assertTrue(this.statusRegister.getNegative());
	}

	@Test
	void store() {
		// Befehle
		this.accumulator.loadI(new INT8("$22"));
		this.accumulator.store(new INT16("$30"));
		
		// ACC Ergebnis
		assertEquals(this.memory.getCellData(new INT16("$30")).getValue(), new INT8("$22").getValue());
		
		// Flags
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void logicXor() {
		// Befehle
		this.accumulator.loadI(new INT8("%00101001"));
		this.accumulator.logicXorI(new INT8("%10110010"));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8("%10011011").getValue());
		
		// Flags
		assertFalse(this.statusRegister.getZero());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void rotateLeft() {
		// Befehle
		this.accumulator.loadI(new INT8("%11101001"));
		this.statusRegister.setCarry(true);
		this.accumulator.rotateLeft();
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8("%11010011").getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void compare() {
		// Befehle
		this.accumulator.loadI(new INT8(5));
		this.accumulator.compareI(new INT8(8));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(5).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void bitTestZeroPage() {
		// Befehle
		this.accumulator.loadI(new INT8(5));
		this.accumulator.store(new INT8(123));
		
		this.accumulator.loadI(new INT8(64));
		this.accumulator.bitTest(new INT8(123));
		
		// Flags
		assertFalse(this.statusRegister.getOverflow());
		assertTrue(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void additionSigned() {
		// Befehle
		this.accumulator.loadI(new INT8(23));
		this.accumulator.addWithCarryI(new INT8(-45));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(-22).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void additionUnsigned() {
		// Befehle
		this.accumulator.loadI(new INT8(123));
		this.accumulator.addWithCarryI(new INT8(58));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(181).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertTrue(this.statusRegister.getOverflow());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void additionUnsignedCarryNeeded() {
		// Befehle
		this.accumulator.loadI(new INT8(123));
		this.accumulator.addWithCarryI(new INT8(254));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(377-256).getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void additionCarrySet() {
		// Befehle
		this.accumulator.loadI(new INT8(-5));
		this.statusRegister.setCarry(true);
		this.accumulator.addWithCarryI(new INT8(4));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(0).getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertTrue(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void additionBinaryCodedDecimals() {
		// Befehle
		this.accumulator.loadI(new INT8(0x05));
		this.statusRegister.setDecimal(true);
		this.accumulator.addWithCarryI(new INT8(0x06));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(0x11).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void additionBinaryCodedDecimalsCarryNeeded() {
		// Befehle
		this.accumulator.loadI(new INT8(0x50)); // $5
		this.statusRegister.setDecimal(true);
		this.accumulator.addWithCarryI(new INT8(0x60)); // $6
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(0x10).getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertTrue(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void subtractionSigned() {
		// Befehle
		this.accumulator.loadI(new INT8(23));
		this.statusRegister.setCarry(true);
		this.accumulator.subtractWithCarryI(new INT8(-45));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(68).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void subtractionUnsigned() {
		// Befehle
		this.accumulator.loadI(new INT8(217));
		this.statusRegister.setCarry(true);
		this.accumulator.subtractWithCarryI(new INT8(18));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(199).getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void subtractionUnsignedBorrowBitNeeded() {
		// Befehle
		this.accumulator.loadI(new INT8(118));
		this.statusRegister.setCarry(true);
		this.accumulator.subtractWithCarryI(new INT8(127));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(-9).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertTrue(this.statusRegister.getNegative());
	}
	
	@Test
	void subtractionCarryCleared() {
		// Befehle
		this.accumulator.loadI(new INT8(5));
		this.statusRegister.setCarry(false);
		this.accumulator.subtractWithCarryI(new INT8(4));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(0).getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertTrue(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void subtractionBinaryCodedDecimals() {
		// Befehle
		this.accumulator.loadI(new INT8(0x34));
		this.statusRegister.setDecimal(true);
		this.statusRegister.setCarry(true);
		this.accumulator.subtractWithCarryI(new INT8(0x06));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(0x28).getValue());
		
		// Flags
		assertTrue(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertFalse(this.statusRegister.getNegative());
	}
	
	@Test
	void subtractionBinaryCodedDecimalsBorrowBitNeeded() {
		// Befehle
		this.accumulator.loadI(new INT8(0x50));
		this.statusRegister.setDecimal(true);
		this.statusRegister.setCarry(true);
		this.accumulator.subtractWithCarryI(new INT8(0x60));
		
		// ACC Ergebnis
		assertEquals(this.accumulator.getData().getValue(), new INT8(0x90).getValue());
		
		// Flags
		assertFalse(this.statusRegister.getCarry());
		assertFalse(this.statusRegister.getZero());
		assertFalse(this.statusRegister.getOverflow());
		assertTrue(this.statusRegister.getNegative());
	}
}
