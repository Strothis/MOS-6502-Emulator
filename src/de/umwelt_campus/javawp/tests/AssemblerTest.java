package de.umwelt_campus.javawp.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.umwelt_campus.javawp.exceptions.InvalidOperandException;
import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Assembler;
import de.umwelt_campus.javawp.processor.components.Memory;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;

/**
 * Test für Assembler Klasse.
 * @author Mathis Ströhlein
 */
class AssemblerTest {
	
	private final StatusRegister statusRegister = new StatusRegister();
	private final Memory memory = new Memory(this.statusRegister);
	private final Assembler assembler = new Assembler(this.memory, new INT16(0x0600));
	
	@Test
	void assemble() {
		this.assembler.assemble(
				  "lda #5\n"
				+ "sec\n"
				+ "sbc #3\n"
				+ "sta $0700"
				);
		
		// Memory Einträge
		assertEquals(this.memory.getCellData(new INT16("$0600")).getValue(), new INT8("$A9").getValue()); // LDA #$nn
		assertEquals(this.memory.getCellData(new INT16("$0601")).getValue(), new INT8("$05").getValue()); // $05 (= 5)
		assertEquals(this.memory.getCellData(new INT16("$0602")).getValue(), new INT8("$38").getValue()); // SEC
		assertEquals(this.memory.getCellData(new INT16("$0603")).getValue(), new INT8("$E9").getValue()); // SBC #$nn
		assertEquals(this.memory.getCellData(new INT16("$0604")).getValue(), new INT8("$03").getValue()); // $03 (= 3)
		assertEquals(this.memory.getCellData(new INT16("$0605")).getValue(), new INT8("$8D").getValue()); // STA $hhll
		assertEquals(this.memory.getCellData(new INT16("$0606")).getValue(), new INT8("$00").getValue()); // $00
		assertEquals(this.memory.getCellData(new INT16("$0607")).getValue(), new INT8("$07").getValue()); // $07
	}

	@Test
	void assembleException() {
		Exception e = assertThrows(InvalidOperandException.class , () -> this.assembler.assemble(
				  "loop: ldx #-23\n"
				+ "inx\n"
				+ "cpx #-22\n"
				+ "beq lop"
				));
		
		assertEquals("Syntax Fehler in Zeile 4: **beq lop** (Operand \"lop\" ist ungültig).", e.getMessage());
		
		// Memory Einträge
		assertEquals(this.memory.getCellData(new INT16("$0600")).getValue(), new INT8("$A2").getValue()); // LDX #$nn
		assertEquals(this.memory.getCellData(new INT16("$0601")).getValue(), new INT8("$E9").getValue()); // $E9 (= -23)
		assertEquals(this.memory.getCellData(new INT16("$0602")).getValue(), new INT8("$E8").getValue()); // INX
		assertEquals(this.memory.getCellData(new INT16("$0603")).getValue(), new INT8("$E0").getValue()); // CPX #$nn
		assertEquals(this.memory.getCellData(new INT16("$0604")).getValue(), new INT8("$EA").getValue()); // $EA (= -22)
		assertEquals(this.memory.getCellData(new INT16("$0605")).getValue(), new INT8("$F0").getValue()); // BEQ $nn
	}
}