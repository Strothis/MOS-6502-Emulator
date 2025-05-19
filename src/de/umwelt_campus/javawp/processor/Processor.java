package de.umwelt_campus.javawp.processor;

import java.util.Stack;

import de.umwelt_campus.javawp.processor.components.Assembler;
import de.umwelt_campus.javawp.processor.components.Memory;
import de.umwelt_campus.javawp.processor.components.RegisterData;
import de.umwelt_campus.javawp.processor.components.registers.Accumulator;
import de.umwelt_campus.javawp.processor.components.registers.IndexRegisterX;
import de.umwelt_campus.javawp.processor.components.registers.IndexRegisterY;
import de.umwelt_campus.javawp.processor.components.registers.ProgramCounter;
import de.umwelt_campus.javawp.processor.components.registers.StackPointer;
import de.umwelt_campus.javawp.processor.components.registers.StatusRegister;
import de.umwelt_campus.javawp.exceptions.InvalidLabelNameException;
import de.umwelt_campus.javawp.exceptions.InvalidOperandException;
import de.umwelt_campus.javawp.exceptions.UnknownOpcodeException;
import de.umwelt_campus.javawp.exceptions.UnknownOperatorException;
import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.exceptions.InterruptException;

/**
 * Der Prozessor verarbeitet die assemblierten Befehle und verwaltet Register und Speicher.
 * @author Michael Weber
 * @author Mathis Ströhlein
 */
public class Processor {
	private Stack<RegisterData> registerDataStack;

	private StatusRegister statusRegister;
	private Memory memory;
	private ProgramCounter programCounter;
	private Assembler assembler;
	private Accumulator accumulator;
	private IndexRegisterX indexRegisterX;
	private IndexRegisterY indexRegisterY;
	private StackPointer stackPointer;
	
	private INT16 startAddress;
	
	/**
	 * Erstellt einen Prozessor.
	 */
	public Processor() {
		// 0x0600: Page 6 (beginnend mit 0) im Speicher
		this.startAddress = new INT16(0x600);
		
		this.registerDataStack = new Stack<RegisterData>();

		this.statusRegister = new StatusRegister();
		this.memory = new Memory(this.statusRegister);
		this.stackPointer = new StackPointer(this.memory, statusRegister);

		this.assembler = new Assembler(this.memory, startAddress);
		this.programCounter = new ProgramCounter(this.memory, this.statusRegister, this.stackPointer, startAddress);
		
		this.accumulator = new Accumulator(this.memory, this.statusRegister);
		this.indexRegisterX = new IndexRegisterX(this.memory, this.statusRegister);
		this.indexRegisterY = new IndexRegisterY(this.memory, this.statusRegister);
	}
	
	/**
	 * Ruft den Assembler auf, der den Bytecode in den Speicher an die Startadresse 
	 * @param mnemonicsString Der eingegebene Befehlscode
	 * @throws UnknownOperatorException Der Operator wurde nicht erkannt
	 * @throws InvalidOperandException Der Operand passt nicht
	 * @throws InvalidLabelNameException Ein Label darf keinen Doppelpunkt beinhalten
	 * @return Gibt die Anzahl der assemblierten Bytes zurück
	 */
	public String assemble(String mnemonicsString) throws UnknownOperatorException, InvalidOperandException, InvalidLabelNameException {
		return "Info: Erfolgreich " + assembler.assemble(mnemonicsString) + " bytes assembliert.";
	}
	
	/**
	 * Führt den aktuellen Befehl aus, auf den der Befehlszähler zeigt.
	 * @throws UnknownOpcodeException Der eingelesene Opcode hat keinen Befehl hinterlegt
	 * @throws InterruptException Das Programm wird unterbrochen, da keine Befehle mehr existieren oder BRK verwendet wurde.
	 */
	public void executeNext() throws UnknownOpcodeException, InterruptException {
		// aktuelle Registerdaten auf Stack schieben
		registerDataStack.push(new RegisterData(this.statusRegister, this.programCounter, this.memory, this.accumulator,
				this.indexRegisterX, this.indexRegisterY, this.stackPointer));

		INT8 opcode = this.memory.getCellData(this.programCounter.getData());
		this.programCounter.increment();
		
		byte commandSize = Assembler.getCommandByteSize(opcode);
		
		INT8 operand8 = null;
		INT16 operand16 = null;
		
		// Je nach Befehlsgröße, die Operanden aus den nächsten Bytes einlesen (Lowbyte vor Highbyte)
		switch(commandSize) {
			case 2:
				operand8 = this.memory.getCellData(this.programCounter.getData());
				this.programCounter.increment();
				break;
			case 3:
				INT8 lowByte = this.memory.getCellData(this.programCounter.getData());
				this.programCounter.increment();
				INT8 highByte = this.memory.getCellData(this.programCounter.getData());
				this.programCounter.increment();
				
				operand16 = new INT16(lowByte, highByte);
		}
		
		// Hier werden die Befehle letztendlich ausgeführt:
		switch ((byte) opcode.getValue()) {
			case (byte) 0xAA: this.accumulator.transferTo(indexRegisterX); break; // TAX
			case (byte) 0xA8: this.accumulator.transferTo(indexRegisterY); break; // TAY
			case (byte) 0x8A: this.indexRegisterX.transferTo(accumulator); break; // TXA
			case (byte) 0x98: this.indexRegisterY.transferTo(accumulator); break; // TYA
			case (byte) 0xBA: this.stackPointer.transferTo(indexRegisterX); break; // TSX
        	case (byte) 0x9A: this.indexRegisterX.transferTo(stackPointer); break; // TXS
        	case (byte) 0xE8: this.indexRegisterX.increment(); break; // INX
        	case (byte) 0xC8: this.indexRegisterY.increment(); break; // INY
        	case (byte) 0xCA: this.indexRegisterX.decrement(); break; // DEX
        	case (byte) 0x88: this.indexRegisterY.decrement(); break; // DEY
        	case (byte) 0x0A: this.accumulator.arithmeticalShiftLeft(); break; // ASL
        	case (byte) 0x4A: this.accumulator.logicalShiftRight(); break; // LSR
        	case (byte) 0x2A: this.accumulator.rotateLeft(); break; // ROL
        	case (byte) 0x6A: this.accumulator.rotateRight(); break; // ROR
        	case (byte) 0x60: this.programCounter.returnFromSubroutine(); break; // RTS
        	case (byte) 0x38: this.statusRegister.setCarry(true); break; // SEC
        	case (byte) 0x18: this.statusRegister.setCarry(false); break; // CLC
        	case (byte) 0x78: this.statusRegister.setInterrupt(true); break; // SEI
        	case (byte) 0x58: this.statusRegister.setInterrupt(false); break; // CLI
        	case (byte) 0xB8: this.statusRegister.setOverflow(false); break; // CLV
        	case (byte) 0xF8: this.statusRegister.setDecimal(true); break; // SED
        	case (byte) 0xD8: this.statusRegister.setDecimal(false); break; // CLD
	        case (byte) 0x48: this.stackPointer.push(accumulator); break; // PHA
	        case (byte) 0x68: this.stackPointer.pull(accumulator); break; // PLA
	        case (byte) 0x08: this.stackPointer.push(statusRegister); break; // PHP
	        case (byte) 0x28: this.stackPointer.pull(statusRegister); break; // PLP
	        case (byte) 0xA9: this.accumulator.loadI(operand8); break; // LDA #$nn
	        case (byte) 0xA2: this.indexRegisterX.loadI(operand8); break; // LDX #$nn
	        case (byte) 0xA0: this.indexRegisterY.loadI(operand8); break; // LDY #$nn
	        case (byte) 0x29: this.accumulator.logicAndI(operand8); break; // AND #$nn
	        case (byte) 0x09: this.accumulator.logicOrI(operand8); break; // ORA #$nn
	        case (byte) 0x49: this.accumulator.logicXorI(operand8); break; // EOR #$nn
	        case (byte) 0x69: this.accumulator.addWithCarryI(operand8); break; // ADC #$nn
	        case (byte) 0xE9: this.accumulator.subtractWithCarryI(operand8); break; // SBC #$nn
	        case (byte) 0xC9: this.accumulator.compareI(operand8); break; // CMP #$nn
	        case (byte) 0xE0: this.indexRegisterX.compareI(operand8); break; // CPX #$nn
	        case (byte) 0xC0: this.indexRegisterY.compareI(operand8); break; // CPY #$nn
	        case (byte) 0x90: this.programCounter.branchOnCarryClear(operand8); break; // BCC $nn
	        case (byte) 0xB0: this.programCounter.branchOnCarrySet(operand8); break; // BCS $nn
	        case (byte) 0xF0: this.programCounter.branchOnEqual(operand8); break; // BEQ $nn
	        case (byte) 0xD0: this.programCounter.branchOnNotEqual(operand8); break; // BNE $nn
	        case (byte) 0x10: this.programCounter.branchOnPlus(operand8); break; // BPL $nn
	        case (byte) 0x30: this.programCounter.branchOnMinus(operand8); break; // BMI $nn
	        case (byte) 0x50: this.programCounter.branchOnOverflowClear(operand8); break; // BVC $nn
	        case (byte) 0x70: this.programCounter.branchOnOverflowSet(operand8); break; // BVS $nn
	        case (byte) 0x65: this.accumulator.addWithCarry(operand8); break; // ADC $nn
	        case (byte) 0x25: this.accumulator.logicAnd(operand8); break; // AND $nn
	        case (byte) 0x06: this.memory.arithmeticalShiftLeft(operand8); break; // ASL $nn
	        case (byte) 0x24: this.accumulator.bitTest(operand8); break; // BIT $nn
	        case (byte) 0xC5: this.accumulator.compare(operand8); break; // CMP $nn
	        case (byte) 0xE4: this.indexRegisterX.compare(operand8); break; // CPX $nn
	        case (byte) 0xC4: this.indexRegisterY.compare(operand8); break; // CPY $nn
	        case (byte) 0xC6: this.memory.decrement(operand8); break; // DEC $nn
	        case (byte) 0x45: this.accumulator.logicXor(operand8); break; // EOR $nn
	        case (byte) 0xE6: this.memory.increment(operand8); break; // INC $nn
	        case (byte) 0xA5: this.accumulator.load(operand8); break; // LDA $nn
	        case (byte) 0xA6: this.indexRegisterX.load(operand8); break; // LDX $nn
	        case (byte) 0xA4: this.indexRegisterY.load(operand8); break; // LDY $nn
	        case (byte) 0x46: this.memory.logicalShiftRight(operand8); break; // LSR $nn
	        case (byte) 0x05: this.accumulator.logicOr(operand8); break; // ORA $nn
	        case (byte) 0x26: this.memory.rotateLeft(operand8); break; // ROL $nn
	        case (byte) 0x66: this.memory.rotateRight(operand8); break; // ROR $nn
	        case (byte) 0xE5: this.accumulator.subtractWithCarry(operand8); break; // SBC $nn
	        case (byte) 0x85: this.accumulator.store(operand8); break; // STA $nn
	        case (byte) 0x86: this.indexRegisterX.store(operand8); break; // STX $nn
	        case (byte) 0x84: this.indexRegisterY.store(operand8); break; // STY $nn
	        case (byte) 0x75: this.accumulator.addWithCarry(operand8, indexRegisterX); break; // ADC $nn,X
	        case (byte) 0x35: this.accumulator.logicAnd(operand8, indexRegisterX); break; // AND $nn,X
	        case (byte) 0x16: this.memory.arithmeticalShiftLeft(operand8, indexRegisterX); break; // ASL $nn,X
	        case (byte) 0xD5: this.accumulator.compare(operand8, indexRegisterX); break; // CMP $nn,X
	        case (byte) 0xD6: this.memory.decrement(operand8, indexRegisterX); break; // DEC $nn,X
	        case (byte) 0x55: this.accumulator.logicXor(operand8, indexRegisterX); break; // EOR $nn,X
	        case (byte) 0xF6: this.memory.increment(operand8, indexRegisterX); break; // INC $nn,X
	        case (byte) 0xB5: this.accumulator.load(operand8, indexRegisterX); break; // LDA $nn,X
	        case (byte) 0xB6: this.indexRegisterX.load(operand8, indexRegisterY); break; // LDX $nn,Y
	        case (byte) 0xB4: this.indexRegisterY.load(operand8, indexRegisterX); break; // LDY $nn,X
	        case (byte) 0x56: this.memory.logicalShiftRight(operand8, indexRegisterX); break; // LSR $nn,X
	        case (byte) 0x15: this.accumulator.logicOr(operand8, indexRegisterX); break; // ORA $nn,X
	        case (byte) 0x36: this.memory.rotateLeft(operand8, indexRegisterX); break; // ROL $nn,X
	        case (byte) 0x76: this.memory.rotateRight(operand8, indexRegisterX); break; // ROR $nn,X
	        case (byte) 0xF5: this.accumulator.subtractWithCarry(operand8, indexRegisterX); break; // SBC $nn,X
	        case (byte) 0x95: this.accumulator.store(operand8, indexRegisterX); break; // STA $nn,X
	        case (byte) 0x96: this.indexRegisterX.store(operand8, indexRegisterY); break; // STX $nn,Y
	        case (byte) 0x94: this.indexRegisterY.store(operand8, indexRegisterX); break; // STY $nn,X
	        case (byte) 0xA1: this.accumulator.load(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // LDA ($nn,X)					  
	        case (byte) 0xB1: this.accumulator.load(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // LDA ($nn),Y
	        case (byte) 0x81: this.accumulator.store(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // STA ($nn,X)
	        case (byte) 0x91: this.accumulator.store(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // STA ($nn),Y
	        case (byte) 0x21: this.accumulator.logicAnd(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // AND ($nn,X)
	        case (byte) 0x31: this.accumulator.logicAnd(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // AND ($nn),Y
	        case (byte) 0x01: this.accumulator.logicOr(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // ORA ($nn,X)
	        case (byte) 0x11: this.accumulator.logicOr(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // ORA ($nn),Y
	        case (byte) 0x41: this.accumulator.logicXor(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // EOR ($nn,X)
	        case (byte) 0x51: this.accumulator.logicXor(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // EOR ($nn),Y
	        case (byte) 0x61: this.accumulator.addWithCarry(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // ADC ($nn,X)
	        case (byte) 0x71: this.accumulator.addWithCarry(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // ADC ($nn),Y
	        case (byte) 0xE1: this.accumulator.subtractWithCarry(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // SBC ($nn,X)
	        case (byte) 0xF1: this.accumulator.subtractWithCarry(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // SBC ($nn),Y
	        case (byte) 0xC1: this.accumulator.compare(this.memory.getLittleEndianAddress(operand8, indexRegisterX)); break; // CMP ($nn,X)
	        case (byte) 0xD1: this.accumulator.compare(this.memory.getLittleEndianAddress(operand8), indexRegisterY); break; // CMP ($nn),Y
	        case (byte) 0x6D: this.accumulator.addWithCarry(operand16); break; // ADC $hhll
	        case (byte) 0x2D: this.accumulator.logicAnd(operand16); break; // AND $hhll
	        case (byte) 0x0E: this.memory.arithmeticalShiftLeft(operand16); break; // ASL $hhll
	        case (byte) 0x2C: this.accumulator.bitTest(operand16); break; // BIT $hhll
	        case (byte) 0xCD: this.accumulator.compare(operand16); break; // CMP $hhll
	        case (byte) 0xEC: this.indexRegisterX.compare(operand16); break; // CPX $hhll
	        case (byte) 0xCC: this.indexRegisterY.compare(operand16); break; // CPY $hhll
	        case (byte) 0xCE: this.memory.decrement(operand16); break; // DEC $hhll
	        case (byte) 0x4D: this.accumulator.logicXor(operand16); break; // EOR $hhll
	        case (byte) 0xEE: this.memory.increment(operand16); break; // INC $hhll
	        case (byte) 0x4C: this.programCounter.jump(operand16); break; // JMP $hhll
	        case (byte) 0x20: this.programCounter.jumpToSubroutine(operand16); break; // JSR $hhll
	        case (byte) 0xAD: this.accumulator.load(operand16); break; // LDA $hhll
	        case (byte) 0xAE: this.indexRegisterX.load(operand16); break; // LDX $hhll
	        case (byte) 0xAC: this.indexRegisterY.load(operand16); break; // LDY $hhll
	        case (byte) 0x4E: this.memory.logicalShiftRight(operand16); break; // LSR $hhll
	        case (byte) 0x0D: this.accumulator.logicOr(operand16); break; // ORA $hhll
	        case (byte) 0x2E: this.memory.rotateLeft(operand16); break; // ROL $hhll
	        case (byte) 0x6E: this.memory.rotateRight(operand16); break; // ROR $hhll
	        case (byte) 0xED: this.accumulator.subtractWithCarry(operand16); break; // SBC $hhll
	        case (byte) 0x8D: this.accumulator.store(operand16); break; // STA $hhll
	        case (byte) 0x8E: this.indexRegisterX.store(operand16); break; // STX $hhll
	        case (byte) 0x8C: this.indexRegisterY.store(operand16); break; // STY $hhll
	        case (byte) 0x7D: this.accumulator.addWithCarry(operand16, indexRegisterX); break; // ADC $hhll,X
	        case (byte) 0x3D: this.accumulator.logicAnd(operand16, indexRegisterX); break; // AND $hhll,X
	        case (byte) 0x1E: this.memory.arithmeticalShiftLeft(operand16, indexRegisterX); break; // ASL $hhll,X
	        case (byte) 0xDD: this.accumulator.compare(operand16, indexRegisterX); break; // CMP $hhll,X
	        case (byte) 0xDE: this.memory.decrement(operand16, indexRegisterX); break; // DEC $hhll,X
	        case (byte) 0x5D: this.accumulator.logicXor(operand16, indexRegisterX); break; // EOR $hhll,X
	        case (byte) 0xFE: this.memory.increment(operand16, indexRegisterX); break; // INC $hhll,X
	        case (byte) 0xBD: this.accumulator.load(operand16, indexRegisterX); break; // LDA $hhll,X
	        case (byte) 0xBC: this.indexRegisterY.load(operand16, indexRegisterX); break; // LDY $hhll,X
	        case (byte) 0x5E: this.memory.logicalShiftRight(operand16, indexRegisterX); break; // LSR $hhll,X
	        case (byte) 0x1D: this.accumulator.logicOr(operand16, indexRegisterX); break; // ORA $hhll,X
	        case (byte) 0x3E: this.memory.rotateLeft(operand16, indexRegisterX); break; // ROL $hhll,X
	        case (byte) 0x7E: this.memory.rotateRight(operand16, indexRegisterX); break; // ROR $hhll,X
	        case (byte) 0xFD: this.accumulator.subtractWithCarry(operand16, indexRegisterX); break; // SBC $hhll,X
	        case (byte) 0x9D: this.accumulator.store(operand16, indexRegisterX); break; // STA $hhll,X
	        case (byte) 0x79: this.accumulator.addWithCarry(operand16, indexRegisterY); break; // ADC $hhll,Y
	        case (byte) 0x39: this.accumulator.logicAnd(operand16, indexRegisterY); break; // AND $hhll,Y
	        case (byte) 0xD9: this.accumulator.compare(operand16, indexRegisterY); break; // CMP $hhll,Y
	        case (byte) 0x59: this.accumulator.logicXor(operand16, indexRegisterY); break; // EOR $hhll,Y
	        case (byte) 0xB9: this.accumulator.load(operand16, indexRegisterY); break; // LDA $hhll,Y
	        case (byte) 0xBE: this.indexRegisterX.load(operand16, indexRegisterY); break; // LDX $hhll,Y
	        case (byte) 0x19: this.accumulator.logicOr(operand16, indexRegisterY); break; // ORA $hhll,Y
	        case (byte) 0xF9: this.accumulator.subtractWithCarry(operand16, indexRegisterY); break; // SBC $hhll,Y
	        case (byte) 0x99: this.accumulator.store(operand16, indexRegisterY); break; // STA $hhll,Y
	        case (byte) 0x6C: this.programCounter.jump(this.memory.getLittleEndianAddress(operand16)); break; // JMP ($hhll)
	        case (byte) 0xEA: break; // NOP
	        case (byte) 0x00: throw new InterruptException(); // BRK	        
	        default: 
	        	throw new UnknownOpcodeException(new INT16(this.programCounter.getData().getValue() - 1), opcode);
		}
	}
	
	/**
	 * Setzt alle Register und den Speicher auf das, was sie vor dem letzten executeNext() beinhalteten.
	 */
	public void undo() {
		// Hier werden die alten Registerdaten vom Stack geholt
		RegisterData registerData = registerDataStack.pop();

		this.statusRegister.setData(registerData.getStatusRegisterData());
		this.programCounter.setData(registerData.getProgramCounterData());
		this.memory.setData(registerData.getMemoryData());
		this.accumulator.setData(registerData.getAccumulatorData());
		this.indexRegisterX.setData(registerData.getIndexRegisterXData());
		this.indexRegisterY.setData(registerData.getIndexRegisterYData());
		this.stackPointer.setData(registerData.getStackPointerData());
	}
	
	// Getter/Setter für UI
	
	/**
	 * Gibt die Startadresse zurück (Wo der Assembler den Bytecode ablegt und der Befehlszähler startet).
	 * @return Startadresse
	 */
	public INT16 getStartAddress() {
		return this.startAddress;
	}
	
	/**
	 * Gibt die Startadresse zurück (Wo der Assembler den Bytecode ablegt und der Befehlszähler startet).
	 * @return Startadresse
	 */
	public Stack<RegisterData> getRegisterDataStack() {
		return this.registerDataStack;
	}
	
	/**
	 * Gibt den Inhalt des Status Registers zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getStatusRegisterData() {
		return this.statusRegister.getData();
	}

	/**
	 * Gibt den Inhalt des Speichers zurück.
	 * @return 8 Bit Zahlen Array
	 */
	public INT8[] getMemoryData() {
		return this.memory.getData();
	}

	/**
	 * Gibt den Inhalt des Befehlszählers zurück.
	 * @return 16 Bit Zahl
	 */
	public INT16 getProgramCounterData() {
		return this.programCounter.getData();
	}

	/**
	 * Gibt den Inhalt des Akkumulators zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getAccumulatorData() {
		return this.accumulator.getData();
	}

	/**
	 * Gibt den Inhalt des Index Registers X zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getIndexRegisterXData() {
		return this.indexRegisterX.getData();
	}

	/**
	 * Gibt den Inhalt des Index Registers Y zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getIndexRegisterYData() {
		return this.indexRegisterY.getData();
	}

	/**
	 * Gibt den Inhalt des Stack Pointers zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getStackPointerData() {
		return this.stackPointer.getData();
	}
}
	