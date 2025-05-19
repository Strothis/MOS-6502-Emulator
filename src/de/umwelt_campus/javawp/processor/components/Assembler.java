package de.umwelt_campus.javawp.processor.components;

import java.util.HashMap;

import de.umwelt_campus.javawp.exceptions.InvalidLabelNameException;
import de.umwelt_campus.javawp.exceptions.InvalidOperandException;
import de.umwelt_campus.javawp.exceptions.MultipleLabelUsesException;
import de.umwelt_campus.javawp.exceptions.UnknownOperatorException;
import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;

/**
 * Der Assembler des Prozessors. Konvertiert Benutzerbefehlscode in Bytecode.
 * @author Michael Weber
 * @author Mathis Ströhlein
 */
public class Assembler {
	private INT16 startAddress;
	private Memory memory;

	/**
	 * Erstellt einen Assembler.
	 * @param memory Speicher
	 * @param startAddress Startadresse
	 */
	public Assembler(Memory memory, INT16 startAddress) {
		this.startAddress = startAddress;
		this.memory = memory;
	}
	
	/**
	 * Konviertiert den eingegebenen Befehlscode in Bytecode. Berechnet Werte für Labels.
	 * @param mnemonicsString Der eingegebene Befehlscode
	 * @return Die Anzahl der konvertierten Bytes (Größe des resultierenden Bytecodes)
	 * @throws UnknownOperatorException Der Operator wurde nicht erkannt
	 * @throws InvalidOperandException Der Operand passt nicht
	 * @throws InvalidLabelNameException Ein Label darf keinen Doppelpunkt beinhalten
	 */
	public int assemble(String mnemonicsString) throws UnknownOperatorException, InvalidOperandException, InvalidLabelNameException {
		String[] mnemonics = mnemonicsString.split("\n");

		int memoryAddress = this.startAddress.getUnsignedValue();
		HashMap<String, INT16> labels = new HashMap<String, INT16>();
		
		String[] operators = new String[mnemonics.length];
		String[] operands = new String[mnemonics.length];
		INT8[] opcodes = new INT8[mnemonics.length];
		
		this.filterMnemonics(mnemonics, operators, operands, opcodes, labels);
		
		String operator;
		String operand;
		INT8 opcode;
		for (int i = 0; i < mnemonics.length; i++) {
			operator = operators[i];
			operand = operands[i];
			opcode = opcodes[i];
			
			if (operator == null) // Leere Zeilen werden übersprungen
				continue;
			
			this.memory.cellValueOf(new INT16(memoryAddress++), opcode);
			
			// Nicht relevante Zeichen (an richtiger Position) entfernen
			// Bei falschen oder Zeichen an falschen Postionen, wirft "new INT" eine Exception
			operand = cleanOperand(opcode, operand);
			
			switch(getCommandByteSize(opcode)) {
				case 1:
					if(!operand.isEmpty())
						throw new InvalidOperandException(i + 1, mnemonics[i], operands[i]);
					break;
				case 2:
					// Operand in 1 8-bit speichern
					INT8 value8 = null;
					try {
						value8 = new INT8(operand);
					} catch(NumberFormatException e) {
						
						// Labels einsetzen probieren
						switch((byte) opcode.getValue()) {
							case (byte) 0x90: // BCC $nn
							case (byte) 0xB0: // BCS $nn
							case (byte) 0xF0: // BEQ $nn
							case (byte) 0xD0: // BNE $nn
							case (byte) 0x10: // BPL $nn
							case (byte) 0x30: // BMI $nn
							case (byte) 0x50: // BVC $nn
							case (byte) 0x70: // BVS $nn
								INT16 labelAddress = labels.get(operand);
								if(labelAddress != null) {
									value8 = new INT8(labelAddress.getUnsignedValue() - (memoryAddress + 1));
									break;
								}
							default:
								if(operand.isEmpty())
									throw new InvalidOperandException(i + 1, mnemonics[i]);		
								else
									throw new InvalidOperandException(i + 1, mnemonics[i], operands[i]);
						}
					}
					
					this.memory.cellValueOf(new INT16(memoryAddress++), value8);
					break;
				case 3:
					// Operand in 2 8-bit (Lowbyte und Highbyte) speichern
					INT16 value16 = null;
					try {
						value16 = new INT16(operand);
					} catch(NumberFormatException e) {
						
						// Nach Labels suchen und gegebenfalls einsetzen
						switch((byte) opcode.getValue()) {
							case (byte) 0x4C: // JMP $hhll
							case (byte) 0x20: // JSR $hhll
								INT16 labelAddress = labels.get(operand);
								if(labelAddress != null) {
									value16 = labelAddress;
									break;
								}
							default:
								if(operand.isEmpty())
									throw new InvalidOperandException(i + 1, mnemonics[i]);		
								else
									throw new InvalidOperandException(i + 1, mnemonics[i], operands[i]);								
						}
					}
					
					this.memory.cellValueOf(new INT16(memoryAddress++), value16.getLowByte()); // Low Byte
					this.memory.cellValueOf(new INT16(memoryAddress++), value16.getHighByte()); // High Byte
			}
		}
		
		// Anzahl der assemblierten Bytes zurückgeben
		return memoryAddress - this.startAddress.getUnsignedValue();
	}
	
	/**
	 * Spaltet die Befehlscodezeilen in einzelne Arrays für Operator, Operand und zugehöriger Opcode und speichert die Labels in einer HashMap. Entfernt außerdem Kommentare.
	 * @param mnemonics Befehlscodezeilen
	 * @param operators Operatorarray
	 * @param operands Operandenarray
	 * @param opcodes Opcodearray
	 * @param labels Label HashMap
	 */
	private void filterMnemonics(String[] mnemonics, String[] operators, String[] operands, INT8[] opcodes, HashMap<String, INT16> labels) {
		int memoryAddress = this.startAddress.getUnsignedValue();
		String mnemonic = new String();
		
		for (int i = 0; i < mnemonics.length; i++) {
			mnemonic = mnemonics[i];
			
			// Kommentare entfernen (alles nach ";")
			if(mnemonic.contains(";"))
				mnemonic = mnemonic.substring(0, mnemonic.indexOf(";"));
			
			// Alles vor dem letzten Doppelpunkt wird gestrippt und als Label gespeichert
			if(mnemonic.contains(":")) {
				String labelName = mnemonic.substring(0, mnemonic.lastIndexOf(":")).strip();
				if(labelName.contains(":"))
					throw new InvalidLabelNameException(i + 1, mnemonics[i], labelName);
				
				INT16 labelAddress = new INT16(memoryAddress);
				
				// Label existiert bereits
				if(labels.get(labelName) != null)
					throw new MultipleLabelUsesException(i + 1, mnemonics[i], labelName);
				
				labels.put(labelName, labelAddress);
				mnemonic = mnemonic.substring(mnemonic.lastIndexOf(":") + 1, mnemonic.length());
			}
			
			mnemonic = mnemonic.strip();
			if (mnemonic.isEmpty())
				continue;
			
			int firstWhitespaceIndex = mnemonic.length();
			for(int j = 0; j < mnemonic.length(); j++)
				if(Character.isWhitespace(mnemonic.charAt(j))) {
					firstWhitespaceIndex = j;
					break;
				}
			
			operators[i] = mnemonic.substring(0, firstWhitespaceIndex);
			operands[i] = mnemonic.substring(firstWhitespaceIndex).strip();
			
			opcodes[i] = getOpcode(operators[i], operands[i], i + 1, mnemonics[i]);
			memoryAddress += getCommandByteSize(opcodes[i]);
		}
	}
	
	/**
	 * Gibt zu einem Opcode die Befehlsgröße in Bytes zurück.
	 * @param opcode Opcode
	 * @return Befehlsgröße in Bytes
	 */
	public static byte getCommandByteSize(INT8 opcode) {
		switch ((byte) opcode.getValue()) {
			case (byte) 0xA9: // LDA #$nn
			case (byte) 0xA2: // LDX #$nn
			case (byte) 0xA0: // LDY #$nn
			case (byte) 0x29: // AND #$nn
			case (byte) 0x09: // ORA #$nn
			case (byte) 0x49: // EOR #$nn
			case (byte) 0x69: // ADC #$nn
			case (byte) 0xE9: // SBC #$nn
			case (byte) 0xC9: // CMP #$nn
			case (byte) 0xE0: // CPX #$nn
			case (byte) 0xC0: // CPY #$nn
			case (byte) 0x90: // BCC $nn
			case (byte) 0xB0: // BCS $nn
			case (byte) 0xF0: // BEQ $nn
			case (byte) 0xD0: // BNE $nn
			case (byte) 0x10: // BPL $nn
			case (byte) 0x30: // BMI $nn
			case (byte) 0x50: // BVC $nn
			case (byte) 0x70: // BVS $nn
			case (byte) 0x65: // ADC $nn
			case (byte) 0x25: // AND $nn
			case (byte) 0x06: // ASL $nn
			case (byte) 0x24: // BIT $nn
			case (byte) 0xC5: // CMP $nn
			case (byte) 0xE4: // CPX $nn
			case (byte) 0xC4: // CPY $nn
			case (byte) 0xC6: // DEC $nn
			case (byte) 0x45: // EOR $nn
			case (byte) 0xE6: // INC $nn
			case (byte) 0xA5: // LDA $nn
			case (byte) 0xA6: // LDX $nn
			case (byte) 0xA4: // LDY $nn
			case (byte) 0x46: // LSR $nn
			case (byte) 0x05: // ORA $nn
			case (byte) 0x26: // ROL $nn
			case (byte) 0x66: // ROR $nn
			case (byte) 0xE5: // SBC $nn
			case (byte) 0x85: // STA $nn
			case (byte) 0x86: // STX $nn
			case (byte) 0x84: // STY $nn
			case (byte) 0x75: // ADC $nn,X
			case (byte) 0x35: // AND $nn,X
			case (byte) 0x16: // ASL $nn,X
			case (byte) 0xD5: // CMP $nn,X
			case (byte) 0xD6: // DEC $nn,X
			case (byte) 0x55: // EOR $nn,X
			case (byte) 0xF6: // INC $nn,X
			case (byte) 0xB5: // LDA $nn,X
			case (byte) 0xB6: // LDX $nn,Y
			case (byte) 0xB4: // LDY $nn,X
			case (byte) 0x56: // LSR $nn,X
			case (byte) 0x15: // ORA $nn,X
			case (byte) 0x36: // ROL $nn,X
			case (byte) 0x76: // ROR $nn,X
			case (byte) 0xF5: // SBC $nn,X
			case (byte) 0x95: // STA $nn,X
			case (byte) 0x96: // STX $nn,Y
			case (byte) 0x94: // STY $nn,X
			case (byte) 0xA1: // LDA ($nn,X)
			case (byte) 0xB1: // LDA ($nn),Y
			case (byte) 0x81: // STA ($nn,X)
			case (byte) 0x91: // STA ($nn),Y
			case (byte) 0x21: // AND ($nn,X)
			case (byte) 0x31: // AND ($nn),Y
			case (byte) 0x01: // ORA ($nn,X)
			case (byte) 0x11: // ORA ($nn),Y
			case (byte) 0x41: // EOR ($nn,X)
			case (byte) 0x51: // EOR ($nn),Y
			case (byte) 0x61: // ADC ($nn,X)
			case (byte) 0x71: // ADC ($nn),Y
			case (byte) 0xE1: // SBC ($nn,X)
			case (byte) 0xF1: // SBC ($nn),Y
			case (byte) 0xC1: // CMP ($nn,X)
			case (byte) 0xD1: // CMP ($nn),Y
				return 2;
			case (byte) 0x6D: // ADC $hhll
			case (byte) 0x2D: // AND $hhll
			case (byte) 0x0E: // ASL $hhll
			case (byte) 0x2C: // BIT $hhll
			case (byte) 0xCD: // CMP $hhll
			case (byte) 0xEC: // CPX $hhll
			case (byte) 0xCC: // CPY $hhll
			case (byte) 0xCE: // DEC $hhll
			case (byte) 0x4D: // EOR $hhll
			case (byte) 0xEE: // INC $hhll
			case (byte) 0x4C: // JMP $hhll
			case (byte) 0x20: // JSR $hhll
			case (byte) 0xAD: // LDA $hhll
			case (byte) 0xAE: // LDX $hhll
			case (byte) 0xAC: // LDY $hhll
			case (byte) 0x4E: // LSR $hhll
			case (byte) 0x0D: // ORA $hhll
			case (byte) 0x2E: // ROL $hhll
			case (byte) 0x6E: // ROR $hhll
			case (byte) 0xED: // SBC $hhll
			case (byte) 0x8D: // STA $hhll
			case (byte) 0x8E: // STX $hhll
			case (byte) 0x8C: // STY $hhll
			case (byte) 0x7D: // ADC $hhll,X
			case (byte) 0x3D: // AND $hhll,X
			case (byte) 0x1E: // ASL $hhll,X
			case (byte) 0xDD: // CMP $hhll,X
			case (byte) 0xDE: // DEC $hhll,X
			case (byte) 0x5D: // EOR $hhll,X
			case (byte) 0xFE: // INC $hhll,X
			case (byte) 0xBD: // LDA $hhll,X
			case (byte) 0xBC: // LDY $hhll,X
			case (byte) 0x5E: // LSR $hhll,X
			case (byte) 0x1D: // ORA $hhll,X
			case (byte) 0x3E: // ROL $hhll,X
			case (byte) 0x7E: // ROR $hhll,X
			case (byte) 0xFD: // SBC $hhll,X
			case (byte) 0x9D: // STA $hhll,X
			case (byte) 0x79: // ADC $hhll,Y
			case (byte) 0x39: // AND $hhll,Y
			case (byte) 0xD9: // CMP $hhll,Y
			case (byte) 0x59: // EOR $hhll,Y
			case (byte) 0xB9: // LDA $hhll,Y
			case (byte) 0xBE: // LDX $hhll,Y
			case (byte) 0x19: // ORA $hhll,Y
			case (byte) 0xF9: // SBC $hhll,Y
			case (byte) 0x99: // STA $hhll,Y
			case (byte) 0x6C: // JMP ($hhll)
				return 3;
			default:
				return 1;
		}
	}
	
	/**
	 * Entfernt alle Zusätze vom Operand, die zu den Befehlen gehören.
	 * @param opcode Opcode
	 * @param operand Operand
	 * @return Operand (besteht, falls valide, nur noch aus einer Zahl)
	 */
	private static String cleanOperand(INT8 opcode, String operand) {
		switch ((byte) opcode.getValue()) {
			case (byte) 0x0A: // ASL
	    	case (byte) 0x4A: // LSR
	    	case (byte) 0x2A: // ROL
	    	case (byte) 0x6A: // ROR
	    		return operand.replaceAll("\\s*[aA]$", "");
			case (byte) 0xA9: // LDA #$nn
			case (byte) 0xA2: // LDX #$nn
			case (byte) 0xA0: // LDY #$nn
			case (byte) 0x29: // AND #$nn
			case (byte) 0x09: // ORA #$nn
			case (byte) 0x49: // EOR #$nn
			case (byte) 0x69: // ADC #$nn
			case (byte) 0xE9: // SBC #$nn
			case (byte) 0xC9: // CMP #$nn
			case (byte) 0xE0: // CPX #$nn
			case (byte) 0xC0: // CPY #$nn
				return operand.replaceAll("^#\\s*", "");
			case (byte) 0x75: // ADC $nn,X
			case (byte) 0x35: // AND $nn,X
			case (byte) 0x16: // ASL $nn,X
			case (byte) 0xD5: // CMP $nn,X
			case (byte) 0xD6: // DEC $nn,X
			case (byte) 0x55: // EOR $nn,X
			case (byte) 0xF6: // INC $nn,X
			case (byte) 0xB5: // LDA $nn,X
			case (byte) 0xB6: // LDX $nn,Y
			case (byte) 0xB4: // LDY $nn,X
			case (byte) 0x56: // LSR $nn,X
			case (byte) 0x15: // ORA $nn,X
			case (byte) 0x36: // ROL $nn,X
			case (byte) 0x76: // ROR $nn,X
			case (byte) 0xF5: // SBC $nn,X
			case (byte) 0x95: // STA $nn,X
			case (byte) 0x96: // STX $nn,Y
			case (byte) 0x94: // STY $nn,X
			case (byte) 0x7D: // ADC $hhll,X
			case (byte) 0x3D: // AND $hhll,X
			case (byte) 0x1E: // ASL $hhll,X
			case (byte) 0xDD: // CMP $hhll,X
			case (byte) 0xDE: // DEC $hhll,X
			case (byte) 0x5D: // EOR $hhll,X
			case (byte) 0xFE: // INC $hhll,X
			case (byte) 0xBD: // LDA $hhll,X
			case (byte) 0xBC: // LDY $hhll,X
			case (byte) 0x5E: // LSR $hhll,X
			case (byte) 0x1D: // ORA $hhll,X
			case (byte) 0x3E: // ROL $hhll,X
			case (byte) 0x7E: // ROR $hhll,X
			case (byte) 0xFD: // SBC $hhll,X
			case (byte) 0x9D: // STA $hhll,X
			case (byte) 0x79: // ADC $hhll,Y
			case (byte) 0x39: // AND $hhll,Y
			case (byte) 0xD9: // CMP $hhll,Y
			case (byte) 0x59: // EOR $hhll,Y
			case (byte) 0xB9: // LDA $hhll,Y
			case (byte) 0xBE: // LDX $hhll,Y
			case (byte) 0x19: // ORA $hhll,Y
			case (byte) 0xF9: // SBC $hhll,Y
			case (byte) 0x99: // STA $hhll,Y
				return operand.replaceAll("\\s*,\\s*[xXyY]$", "");
			case (byte) 0xA1: // LDA ($nn,X)
			case (byte) 0x81: // STA ($nn,X)
			case (byte) 0x21: // AND ($nn,X)
			case (byte) 0x01: // ORA ($nn,X)
			case (byte) 0x41: // EOR ($nn,X)
			case (byte) 0x61: // ADC ($nn,X)
			case (byte) 0xE1: // SBC ($nn,X)
			case (byte) 0xC1: // CMP ($nn,X)
				return operand.replaceAll("^\\(\\s*|\\s*,\\s*[xX]\\s*\\)$", "");
			case (byte) 0xB1: // LDA ($nn),Y
			case (byte) 0x91: // STA ($nn),Y
			case (byte) 0x31: // AND ($nn),Y
			case (byte) 0x11: // ORA ($nn),Y
			case (byte) 0x51: // EOR ($nn),Y
			case (byte) 0x71: // ADC ($nn),Y
			case (byte) 0xF1: // SBC ($nn),Y
			case (byte) 0xD1: // CMP ($nn),Y
				return operand.replaceAll("^\\(\\s*|\\s*\\)\\s*,\\s*[yY]$", "");
			case (byte) 0x6C: // JMP ($hhll)
				return operand.replaceAll("^\\(\\s*|\\s*\\)$", "");
			default:
				return operand;
		}
	}
	
	/**
	 * Liefert zu einem Operator und Operanden den passenden Opcode.
	 * @param operator Operator
	 * @param operand Operand
	 * @param lineNumber Zeilennummer (Für Exception Nachricht)
	 * @param line Zeileninhalt (Für Exception Nachricht)
	 * @return Opcode als 8 Bit Zahl
	 * @throws UnknownOperatorException Falls der Operator nicht existiert
	 */
	private static INT8 getOpcode(String operator, String operand, int lineNumber, String line) throws UnknownOperatorException {
		// Hier werden die Anweisungen in ihre opcodes umgewandelt
		switch (operator.toUpperCase()) {
		case "LDA":
			if (operand.matches("^#.*")) {
				return new INT8(0xA9); // LDA Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0xA1); // LDA Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0xB1); // LDA Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xB5); // LDA Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0xBD); // LDA Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0xB9); // LDA Absolut, Y
				
			} else if (useZeroPage(operand)) {
				return new INT8(0xA5); // LDA Zero Page

			} else {
				return new INT8(0xAD); // LDA Absolut
			}

		case "LDX":
			if (operand.matches("^#.*")) {
				return new INT8(0xA2); // LDX Unmittelbar
				
			} else if (operand.matches(".*,\\s*[yY]$") && useZeroPage(operand)) {
				return new INT8(0xB6); // LDX Zero Page, Y

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0xBE); // LDX Absolut, Y
				
			} else if (useZeroPage(operand)) {
				return new INT8(0xA6); // LDX Zero Page

			} else {
				return new INT8(0xAE); // LDX Absolut
			}

		case "LDY":
			if (operand.matches("^#.*")) {
				return new INT8(0xA0); // LDY Unmittelbar
				
			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xB4); // LDY Zero Page, X
				
			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0xBC); // LDY Absolut, X

			} else if (useZeroPage(operand)) {
				return new INT8(0xA4); // LDY Zero Page

			} else {
				return new INT8(0xAC); // LDY Absolut
			}

		case "STA":
			
			 if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0x81); // STA Indirekt, X
			
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0x91); // STA Indirekt, Y
				
			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x95); // STA Zero Page, X
				
			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x9D); // STA Absolut, X
				
			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0x99); // STA Absolut, Y
			
			} else if (useZeroPage(operand)) {
				return new INT8(0x85); // STA Zero Page

			} else {
				return new INT8(0x8D); // STA Absolut
			}

		case "STX":
			
			if (operand.matches(".*,\\s*[yY]$") && useZeroPage(operand)) {
				return new INT8(0x96); // STX Zero Page, Y
			
			} else if (useZeroPage(operand)) {
				return new INT8(0x86); // STX Zero Page

			} else {
				return new INT8(0x8E); // STX Absolut
			}

		case "STY":
			if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x94); // STY Zero Page, X
			
			} else if (useZeroPage(operand)) {
				return new INT8(0x84); // STY Zero Page

			} else {
				return new INT8(0x8C); // STY Absolut
			}

		case "TAX":
			return new INT8(0xAA); // TAX

		case "TAY":
			return new INT8(0xA8); // TAY

		case "TXA":
			return new INT8(0x8A); // TXA

		case "TYA":
			return new INT8(0x98); // TYA

		case "TSX":
			return new INT8(0xBA); // TSX

		case "TXS":
			return new INT8(0x9A); // TXS

		case "AND":
			if (operand.matches("^#.*")) {
				return new INT8(0x29); // AND Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0x21); // AND Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0x31); // AND Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x35); // AND Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x3D); // AND Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0x39); // AND Absolut, Y
				
			} else if (useZeroPage(operand)) {
				return new INT8(0x25); // AND Zero Page

			} else {
				return new INT8(0x2D); // AND Absolut
			}

		case "ORA":
			if (operand.matches("^#.*")) {
				return new INT8(0x09); // ORA Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0x01); // ORA Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0x11); // ORA Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x35); // ORA Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x1D); // ORA Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0x19); // ORA Absolut, Y
				
			} else if (useZeroPage(operand)) {
				return new INT8(0x25); // ORA Zero Page

			} else {
				return new INT8(0x0D); // ORA Absolut
			}

		case "EOR":
			if (operand.matches("^#.*")) {
				return new INT8(0x49); // EOR Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0x41); // EOR Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0x51); // EOR Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x55); // EOR Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x5D); // EOR Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0x59); // EOR Absolut, Y

			} else if (useZeroPage(operand)) {
				return new INT8(0x45); // EOR Zero Page
				
			} else {
				return new INT8(0x4D); // EOR Absolut
			}

		case "ADC":
			if (operand.matches("^#.*")) {
				return new INT8(0x69); // ADC Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0x61); // ADC Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0x71); // ADC Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x75); // ADC Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x7D); // ADC Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0x79); // ADC Absolut, Y

			} else if (useZeroPage(operand)) {
				return new INT8(0x65); // ADC Zero Page
				
			} else {
				return new INT8(0x6D); // ADC Absolut
			}

		case "SBC":
			if (operand.matches("^#.*")) {
				return new INT8(0xE9); // SBC Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0xE1); // SBC Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0xF1); // SBC Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xF5); // SBC Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0xFD); // SBC Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0xF9); // SBC Absolut, Y

			} else if (useZeroPage(operand)) {
				return new INT8(0xE5); // SBC Zero Page
				
			} else {
				return new INT8(0xED); // SBC Absolut
			}

		case "INC":

			if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xF6); // INC Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0xFE); // INC Absolut, X
				
			} else if (useZeroPage(operand)) {
					return new INT8(0xE6); // INC Zero Page

			} else {
				return new INT8(0xEE); // INC Absolut
			}

		case "DEC":

			if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xD6); // DEC Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0xDE); // DEC Absolut, X

			} else if (useZeroPage(operand)) {
					return new INT8(0xC6); // DEC Zero Page
			} else {
				return new INT8(0xCE); // DEC Absolut
			}

		case "INX":
			return new INT8(0xE8); // INX

		case "INY":
			return new INT8(0xC8); // INY

		case "DEX":
			return new INT8(0xCA); // DEX

		case "DEY":
			return new INT8(0x88); // DEY

		case "ASL":
			if (operand.matches("^[aA]?$")) {
				return new INT8(0x0A); // ASL Akkumulator
				
			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x16); // ASL Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x1E); // ASL Absolut, X

			} else if (useZeroPage(operand)) {
				return new INT8(0x06); // ASL Zero Page
				
			} else {
				return new INT8(0x0E); // ASL Absolut
			}

		case "LSR":
			if (operand.matches("^[aA]?$")) {
				return new INT8(0x4A); // LSR Akkumulator

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x56); // LSR Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x4E); // LSR Absolut, X
				
			} else if (useZeroPage(operand)) {
				return new INT8(0x46); // LSR Zero Page

			} else {
				return new INT8(0x5E); // LSR Absolut
			}

		case "ROL":
			if (operand.matches("^[aA]?$")) {
				return new INT8(0x2A); // ROL Akkumulator

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0x36); // ROL Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x3E); // ROL Absolut, X
				
			} else if (useZeroPage(operand)) {
				return new INT8(0x26); // ROL Zero Page

			} else {
				return new INT8(0x2E); // ROL Absolut
			}

		case "ROR":
			if (operand.matches("^[aA]?$")) {
				return new INT8(0x6A); // ROR Akkumulator

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xB4); // ROR Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0x7E); // ROR Absolut, X

			} else if (useZeroPage(operand)) {
				return new INT8(0xA4); // ROR Zero Page
				
			} else {
				return new INT8(0x6E); // ROR Absolut
			}

		case "CMP":
			if (operand.matches("^#.*")) {
				return new INT8(0xC9); // CMP Unmittelbar
				
			} else if (operand.matches("^\\(.*,\\s*[xX]\\s*\\)$")) {
				return new INT8(0xC1); // CMP Indirekt, X
				
			} else if (operand.matches("^\\(.*\\)\\s*,\\s*[yY]$")) {
				return new INT8(0xD1); // CMP Indirekt, Y

			} else if (operand.matches(".*,\\s*[xX]$") && useZeroPage(operand)) {
				return new INT8(0xD5); // CMP Zero Page, X

			} else if (operand.matches(".*,\\s*[xX]$")) {
				return new INT8(0xDD); // CMP Absolut, X

			} else if (operand.matches(".*,\\s*[yY]$")) {
				return new INT8(0xD9); // CMP Absolut, Y

			} else if (useZeroPage(operand)) {
				return new INT8(0xC5); // CMP Zero Page
				
			} else {
				return new INT8(0xCD); // CMP Absolut
			}

		case "CPX":
			if (operand.matches("^#.*")) {
				return new INT8(0xE0); // CPX Unmittelbar

			} else if (useZeroPage(operand)) {
				return new INT8(0xE4); // CPX Zero Page

			} else {
				return new INT8(0xEC); // CPX Absolut
			}

		case "CPY":
			if (operand.matches("^#.*")) {
				return new INT8(0xC0); // CPY Unmittelbar

			} else if (useZeroPage(operand)) {
				return new INT8(0xC4); // CPY Zero Page

			} else {
				return new INT8(0xCC); // CPY Absolut
			}

		case "BIT":
			if (useZeroPage(operand)) {
				return new INT8(0x24); // BIT Zero Page

			} else {
				return new INT8(0x2C); // BIT Absolut
			}

		case "JMP":
			if (operand.matches("^\\(.*\\)$")) {
				return new INT8(0x6C); // JMP Indirekt

			} else {
				return new INT8(0x4C); // JMP Absolut
			}

		case "JSR":
			return new INT8(0x20); // JSR

		case "RTS":
			return new INT8(0x60); // RTS

		case "BCC":
			return new INT8(0x90); // BCC

		case "BCS":
			return new INT8(0xB0); // BCS

		case "BEQ":
			return new INT8(0xF0); // BEQ

		case "BNE":
			return new INT8(0xD0); // BNE

		case "BPL":
			return new INT8(0x10); // BPL

		case "BMI":
			return new INT8(0x30); // BMI

		case "BVC":
			return new INT8(0x50); // BVC

		case "BVS":
			return new INT8(0x70); // BVS

		case "SEC":
			return new INT8(0x38); // SEC

		case "CLC":
			return new INT8(0x18); // CLC

		case "SEI":
			return new INT8(0x78); // SEI

		case "CLI":
			return new INT8(0x58); // CLI

		case "CLV":
			return new INT8(0xB8); // CLV

		case "SED":
			return new INT8(0xF8); // SED

		case "CLD":
			return new INT8(0xD8); // CLD

		case "PHA":
			return new INT8(0x48); // PHA

		case "PLA":
			return new INT8(0x68); // PLA

		case "PHP":
			return new INT8(0x08); // PHP

		case "PLP":
			return new INT8(0x28); // PLP

		case "NOP":
			return new INT8(0xEA); // NOP
			
		case "BRK":
			return new INT8(0x00); // BRK

		default:
			throw new UnknownOperatorException(lineNumber, line, operator);
		}
	}
	
	/**
	 * Entscheidet, ob die Zero Page Version des Befehls zurückgegeben werden sollte (bei größeren Werten oder manuell durch führende Nullen).
	 * @param operand OPerand
	 * @return Wahrheitswert, ob Zero Page Version angemessen
	 */
	private static boolean useZeroPage(String operand) {
		operand = operand.replaceAll("\\s*,\\s*[xXyY]$", "");
		
		// Hex bei maximal 2 Zeichen (Minuszeichen wird ignoriert)
		if(operand.matches("^\\$.*"))
			return operand.length() <= (operand.startsWith("$-") ? 4 : 3);
			
		// Binär bei maximal 8 Zeichen (Minuszeichen wird ignoriert)
		else if(operand.matches("^%.*"))
			return operand.length() <= (operand.startsWith("%-") ? 10 : 9);
		
		// Dezimal bei maximal 3 Zeichen und Wert von minimal -128 und maximal 255 (Minuszeichen wird ignoriert)
		else {
			try {
				if(operand.startsWith("-"))
					return operand.length() <= 4 && Integer.parseInt(operand) >= -128;
				else
					return operand.length() <= 3 && Integer.parseInt(operand) <= 255;
			} catch(NumberFormatException e) {
				return false;
			}
		}
	}
}
