package de.umwelt_campus.javawp.processor.components.registers;

import de.umwelt_campus.javawp.integers.INT16;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.components.Memory;

/**
 * Der Akkumulator des Prozessors.
 * @author Mathis Ströhlein
 */
public class Accumulator extends Register8 {
	
	/**
	 * Erstellt einen Akkumulator mit dem Initialwert 0.
	 * @param memory Speicher
	 * @param statusRegister Status Register
	 */
	public Accumulator(Memory memory, StatusRegister statusRegister) {
		super(memory, statusRegister, new INT8(0));
	}
	
	/**
	 * Lädt eine Konstante in den Akkumulator.
	 * @param constant Konstante
	 */
	public void loadI(INT8 constant) {
		this.valueOf(constant);
		
		this.updateZNFlags();
	}
	
	/**
	 * Lädt den Inhalt einer Speicherzelle in den Akkumulator.
	 * @param address Speicheradresse
	 */
	public void load(INT16 address) {
		// Ruft Konstanten-Version auf
		this.loadI(this.memory.getCellData(address));
	}
	
	/**
	 * Lädt den Inhalt einer Speicherzelle in den Akkumulator.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void load(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.load(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Lädt den Inhalt einer Zero Page Speicherzelle in den Akkumulator.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void load(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.load(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Lädt den Inhalt einer Zero Page Speicherzelle in den Akkumulator.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void load(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.load(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Speichert den aktuellen Wert des Akkumulators in einer Speicherzelle.
	 * @param address Speicheradresse
	 */
	public void store(INT16 address) {
		this.memory.cellValueOf(address, this);
	}
	
	/**
	 * Speichert den aktuellen Wert des Akkumulators in einer Speicherzelle.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void store(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.store(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Speichert den aktuellen Wert des Akkumulators in einer Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void store(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.store(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Speichert den aktuellen Wert des Akkumulators in einer Zero Page Speicherzelle.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void store(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.store(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Kopiert den aktuellen Wert des Akkumulators in ein Index Register.
	 * @param indexRegister Index Register
	 */
	public void transferTo(IndexRegister indexRegister) {
		indexRegister.valueOf(this);
		
		indexRegister.updateZNFlags();
	}
	
	/**
	 * Das bitweise Und vom Wert des Akkumulators und der Konstante wird in den Akkumulator geschrieben.
	 * @param constant Konstante
	 */
	public void logicAndI(INT8 constant) {
		this.data.setValue(this.data.getValue() & constant.getValue());
		
		this.updateZNFlags();
	}
	
	/**
	 * Das bitweise Und vom Wert des Akkumulators und des Inhalts der Speicherzelle wird in den Akkumulator geschrieben.
	 * @param address Speicheradresse
	 */
	public void logicAnd(INT16 address) {
		// Ruft Konstanten-Version auf
		this.logicAndI(this.memory.getCellData(address));
	}
	
	/**
	 * Das bitweise Und vom Wert des Akkumulators und des Inhalts der Speicherzelle wird in den Akkumulator geschrieben.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void logicAnd(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.logicAnd(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Das bitweise Und vom Wert des Akkumulators und des Inhalts der Zero Page Speicherzelle wird in den Akkumulator geschrieben.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void logicAnd(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.logicAnd(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Das bitweise Und vom Wert des Akkumulators und des Inhalts der Zero Page Speicherzelle wird in den Akkumulator geschrieben.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void logicAnd(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.logicAnd(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}

	/**
	 * Das bitweise Oder vom Wert des Akkumulators und der Konstante wird in den Akkumulator geschrieben.
	 * @param constant Konstante
	 */
	public void logicOrI(INT8 constant) {
		this.data.setValue(this.data.getValue() | constant.getValue());
		
		this.updateZNFlags();
	}

	/**
	 * Das bitweise Oder vom Wert des Akkumulators und des Inhalts der Speicherzelle wird in den Akkumulator geschrieben.
	 * @param address Speicheradresse
	 */
	public void logicOr(INT16 address) {
		// Ruft Konstanten-Version auf
		this.logicOrI(this.memory.getCellData(address));
	}

	/**
	 * Das bitweise Oder vom Wert des Akkumulators und des Inhalts der Speicherzelle wird in den Akkumulator geschrieben.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void logicOr(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.logicOr(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}

	/**
	 * Das bitweise Oder vom Wert des Akkumulators und des Inhalts der Zero Page Speicherzelle wird in den Akkumulator geschrieben.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void logicOr(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.logicOr(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Das bitweise Oder vom Wert des Akkumulators und des Inhalts der Zero Page Speicherzelle wird in den Akkumulator geschrieben.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void logicOr(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.logicOr(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}

	/**
	 * Das bitweise Exklusiv-Oder vom Wert des Akkumulators und der Konstante wird in den Akkumulator geschrieben.
	 * @param constant Konstante
	 */
	public void logicXorI(INT8 constant) {
		this.data.setValue(this.data.getValue() ^ constant.getValue());
		
		this.updateZNFlags();
	}

	/**
	 * Das bitweise Exklusiv-Oder vom Wert des Akkumulators und des Inhalts der Speicherzelle wird in den Akkumulator geschrieben.
	 * @param address Speicheradresse
	 */
	public void logicXor(INT16 address) {
		// Ruft Konstanten-Version auf
		this.logicXorI(this.memory.getCellData(address));
	}

	/**
	 * Das bitweise Exklusiv-Oder vom Wert des Akkumulators und des Inhalts der Speicherzelle wird in den Akkumulator geschrieben.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void logicXor(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.logicXor(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}

	/**
	 * Das bitweise Exklusiv-Oder vom Wert des Akkumulators und des Inhalts der Zero Page Speicherzelle wird in den Akkumulator geschrieben.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void logicXor(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.logicXor(new INT16(zeroPageAddress, new INT8(0)));
	}

	/**
	 * Das Exklusiv-Oder Und vom Wert des Akkumulators und des Inhalts der Zero Page Speicherzelle wird in den Akkumulator geschrieben.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void logicXor(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.logicXor(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Addiert den Akkumulator, einer Konstante und das Carry Bit und speichert das Ergebnis im Akkumulator.
	 * @param constant Konstante
	 */
	public void addWithCarryI(INT8 constant) {
		int result = this.data.getValue() + constant.getValue() + (this.statusRegister.getCarry() ? 1 : 0);
		if(!this.statusRegister.getDecimal()) {
			// Binär-Modus
			int resultUnsigned = this.data.getUnsignedValue() + constant.getUnsignedValue() + (this.statusRegister.getCarry() ? 1 : 0);
			
			this.data.setValue(result);
			
			// Carry Flag
			this.statusRegister.setCarry(resultUnsigned > 255);
		} else {
			// Dezimal-Modus
			int lowNibble = this.data.getValue() & 15; // Maskiert nur die hinteren 4 Bits
			int highNibble = (this.data.getValue() >> 4) & 15; // Legt vordere 4 Bits auf hintere 4, Rest auf 0
			int decimalNumber1 = highNibble * 10 + lowNibble; // Interpretation der Stellen als Dezimalzahl
			
			lowNibble = constant.getValue() & 15;
			highNibble = (constant.getValue() >> 4) & 15;
			int decimalNumber2 = highNibble * 10 + lowNibble;
			
			int decimalResult = decimalNumber1 + decimalNumber2 + (this.statusRegister.getCarry() ? 1 : 0);
			
			lowNibble = decimalResult % 10; // Extrahiert Einerstelle
			highNibble = decimalResult / 10; // Extrahiert Zehnerstelle
			
			if(decimalResult > 99)
				highNibble %= 10; // Carry gebraucht. Setzt highNibble von Hunderter- auf Zehnerstelle
			
			this.data.setValue((highNibble << 4) | lowNibble);
			
			// Carry Flag
			this.statusRegister.setCarry(decimalResult > 99); // Carry Flag als Hunderterstelle
		}
		
		// Overflow Flag
		this.statusRegister.setOverflow(result < -128 || result > 127);
		
		this.updateZNFlags();
	}
	
	/**
	 * Addiert den Akkumulator, den Inhalt einer Speicherzelle und das Carry Bit und speichert das Ergebnis im Akkumulator.
	 * @param address Speicheradresse
	 */
	public void addWithCarry(INT16 address) {
		// Ruft Konstanten-Version auf
		this.addWithCarryI(this.memory.getCellData(address));
	}
	
	/**
	 * Addiert den Akkumulator, den Inhalt einer Speicherzelle und das Carry Bit und speichert das Ergebnis im Akkumulator.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void addWithCarry(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.addWithCarry(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Addiert den Akkumulator, den Inhalt einer Zero Page Speicherzelle und das Carry Bit und speichert das Ergebnis im Akkumulator.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void addWithCarry(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.addWithCarry(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Addiert den Akkumulator, den Inhalt einer Zero Page Speicherzelle und das Carry Bit und speichert das Ergebnis im Akkumulator.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void addWithCarry(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.addWithCarry(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}

	/**
	 * Subtrahiert eine Konstante und das komplementierte Carry Bit vom Akkumulator und speichert das Ergebnis im Akkumulator.
	 * @param constant Konstante
	 */
	public void subtractWithCarryI(INT8 constant) {
		int result = this.data.getValue() - constant.getValue() - (this.statusRegister.getCarry() ? 0 : 1);
		if(!this.statusRegister.getDecimal()) {
			// Binär-Modus
			int resultUnsigned = this.data.getUnsignedValue() - constant.getUnsignedValue() - (this.statusRegister.getCarry() ? 0 : 1);
			
			this.data.setValue(result);

			// Carry Flag (0 bei Subtrahend > Minuend)
			this.statusRegister.setCarry(!(resultUnsigned < 0));
		} else {
			// Dezimal-Modus
			int lowNibble = this.data.getValue() & 15; // analog zur Addition
			int highNibble = (this.data.getValue() >> 4) & 15;
			int decimalNumber1 = highNibble * 10 + lowNibble;
			
			lowNibble = constant.getValue() & 15;
			highNibble = (constant.getValue() >> 4) & 15;
			int decimalNumber2 = highNibble * 10 + lowNibble;
			
			int decimalResult = decimalNumber1 - decimalNumber2 - (this.statusRegister.getCarry() ? 0 : 1);
			
			if(decimalResult < 0)
				decimalResult += 100; // Overflow im Dezimalbereich. 10 - 20 = 90.
			
			lowNibble = decimalResult % 10;
			highNibble = decimalResult / 10;
			
			this.data.setValue((highNibble << 4) | lowNibble);
			
			// Carry Flag
			this.statusRegister.setCarry(!(decimalNumber1 < decimalNumber2)); // Carry Flag als Borge-Hunderter (0 bei Subtrahend > Minuend)
		}
		
		// Overflow Flag
		this.statusRegister.setOverflow(result < -128 || result > 127);
		
		this.updateZNFlags();
	}

	/**
	 * Subtrahiert eine den Inhalt einer Speicherzelle und das komplementierte Carry Bit vom Akkumulator und speichert das Ergebnis im Akkumulator.
	 * @param address Speicheradresse
	 */
	public void subtractWithCarry(INT16 address) {
		// Ruft Konstanten-Version auf
		this.subtractWithCarryI(this.memory.getCellData(address));
	}
	
	/**
	 * Subtrahiert eine den Inhalt einer Speicherzelle und das komplementierte Carry Bit vom Akkumulator und speichert das Ergebnis im Akkumulator.
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void subtractWithCarry(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.subtractWithCarry(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Subtrahiert eine den Inhalt einer Zero Page Speicherzelle und das komplementierte Carry Bit vom Akkumulator und speichert das Ergebnis im Akkumulator.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void subtractWithCarry(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.subtractWithCarry(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Subtrahiert eine den Inhalt einer Zero Page Speicherzelle und das komplementierte Carry Bit vom Akkumulator und speichert das Ergebnis im Akkumulator.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void subtractWithCarry(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.subtractWithCarry(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
		
	/**
	 * Schiebt alle Bits des Akkumulators 1 nach links. Das hinterste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 */
	public void arithmeticalShiftLeft() {
		this.statusRegister.setCarry(this.data.getBit(7));
		this.data.setValue(this.data.getValue() << 1);
		
		this.updateZNFlags();
	}
	
	/**
	 * Schiebt alle Bits des Akkumulators 1 nach rechts. Das vorderste wird auf 0 gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 */
	public void logicalShiftRight() {
		this.statusRegister.setCarry(this.data.getBit(0));
		this.data.setValue(this.data.getValue() >> 1);
		this.data.setBit(7, false);
		
		this.updateZNFlags();
	}
	
	/**
	 * Schiebt alle Bits des Akkumulators 1 nach links. Das hinterste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 */
	public void rotateLeft() {
		boolean carryBit = this.statusRegister.getCarry();
		this.statusRegister.setCarry(this.data.getBit(7));
		this.data.setValue(this.data.getValue() << 1);
		this.data.setBit(0, carryBit);
		
		this.updateZNFlags();
	}

	/**
	 * Schiebt alle Bits des Akkumulators 1 nach rechts. Das vorderste wird auf das Carry Bit gesetzt und das rausgeworfene wird im Carry Bit gespeichert.
	 */
	public void rotateRight() {
		boolean carryBit = this.statusRegister.getCarry();
		this.statusRegister.setCarry(this.data.getBit(0));
		this.data.setValue(this.data.getValue() >> 1);
		this.data.setBit(7, carryBit);
		
		this.updateZNFlags();
	}
	
	/**
	 * Vergleicht den Akkumulator mit einer Konstante und setzt die Prozessor Flags entsprechend.
	 * Carry: Akkumulator (unsigned) ist größer gleich Konstante (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Akkumulator (signed) ist kleiner als Konstante (signed)
	 * @param constant Konstante
	 */
	public void compareI(INT8 constant) {
		int difference = this.data.getValue() - constant.getValue();
		int differenceUnsigned = this.data.getUnsignedValue() - constant.getUnsignedValue();
		
		this.statusRegister.setCarry(differenceUnsigned >= 0);
		this.statusRegister.setZero(difference == 0);
		this.statusRegister.setNegative(difference < 0);
	}

	/**
	 * Vergleicht den Akkumulator mit dem Inhalt einer Speicherzelle und setzt die Prozessor Flags entsprechend.
	 * Carry: Akkumulator (unsigned) ist größer gleich Inhalt Speicherzelle (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Akkumulator (signed) ist kleiner als Inhalt Speicherzelle (signed)
	 * @param address Speicheradresse
	 */
	public void compare(INT16 address) {
		// Ruft Konstanten-Version auf
		this.compareI(this.memory.getCellData(address));
	}
	
	/**
	 * Vergleicht den Akkumulator mit dem Inhalt einer Speicherzelle und setzt die Prozessor Flags entsprechend.
	 * Carry: Akkumulator (unsigned) ist größer gleich Inhalt Speicherzelle (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Akkumulator (signed) ist kleiner als Inhalt Speicherzelle (signed)
	 * @param address Speicheradresse
	 * @param offsetRegister Das Index Register dessen Wert vor dem Laden des Speicherwerts auf die Adresse addiert wird
	 */
	public void compare(INT16 address, IndexRegister offsetRegister) {
		// Ruft Adressen-Version auf
		this.compare(new INT16(address.getValue() + offsetRegister.getData().getUnsignedValue()));
	}
	
	/**
	 * Vergleicht den Akkumulator mit dem Inhalt einer Zero Page Speicherzelle und setzt die Prozessor Flags entsprechend.
	 * Carry: Akkumulator (unsigned) ist größer gleich Inhalt Zero Page Speicherzelle (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Akkumulator (signed) ist kleiner als Inhalt Zero Page Speicherzelle (signed)
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void compare(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.compare(new INT16(zeroPageAddress, new INT8(0)));
	}
	
	/**
	 * Vergleicht den Akkumulator mit dem Inhalt einer Zero Page Speicherzelle und setzt die Prozessor Flags entsprechend.
	 * Carry: Akkumulator (unsigned) ist größer gleich Inhalt Zero Page Speicherzelle (unsigned),
	 * Zero: Beide Werte sind gleich,
	 * Negative: Akkumulator (signed) ist kleiner als Inhalt Zero Page Speicherzelle (signed)
	 * @param zeroPageAddress Zero Page Speicheradresse
	 * @param offsetRegisterX Das Index Register X dessen Wert vor dem Laden des Speicherwerts auf die Zero Page Adresse addiert wird
	 */
	public void compare(INT8 zeroPageAddress, IndexRegisterX offsetRegisterX) {
		// Ruft Adressen-ZP-Version auf
		this.compare(new INT8(zeroPageAddress.getValue() + offsetRegisterX.getData().getValue()));
	}
	
	/**
	 * Setzt das Negativ Flag auf das siebte Bit des Inhalts der Speicherzelle und das Overflow Flag auf das sechste Bit des Inhalts der Speicherzelle.
	 * Das Zero Flag wird gesetzt falls die bitweise Und Verknüpfung des Inhalts der Speicherzelle und des Akkumulators 0 ist.
	 * @param address Speicheradresse
	 */
	public void bitTest(INT16 address) {
		this.statusRegister.setNegative(this.memory.getCellData(address).getBit(7));
		this.statusRegister.setOverflow(this.memory.getCellData(address).getBit(6));
		
		this.statusRegister.setZero((this.data.getValue() & this.memory.getCellData(address).getValue()) == 0);
	}

	/**
	 * Setzt das Negativ Flag auf das siebte Bit des Inhalts der Zero Page Speicherzelle und das Overflow Flag auf das sechste Bit des Inhalts der Zero Page Speicherzelle.
	 * Das Zero Flag wird gesetzt falls die bitweise Und Verknüpfung des Inhalts der Zero Page Speicherzelle und des Akkumulators 0 ist.
	 * @param zeroPageAddress Zero Page Speicheradresse
	 */
	public void bitTest(INT8 zeroPageAddress) {
		// Ruft Adressen-Version auf
		this.bitTest(new INT16(zeroPageAddress, new INT8(0)));
	}
}
