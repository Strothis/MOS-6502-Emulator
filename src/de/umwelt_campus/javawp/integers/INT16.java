package de.umwelt_campus.javawp.integers;

import de.umwelt_campus.javawp.processor.components.registers.Register16;

/**
 * Eine 16 Bit Zahlen Klasse.
 * @author Mathis Ströhlein
 */
public class INT16 {
	private short value;
	
	/**
	 * Erstellt eine 16 Bit Zahl.
	 */
	public INT16() {
		this.setValue(0);
	}
	
	/**
	 * Erstellt eine 16 Bit Zahl und setzt den Wert mit einem Integer (kein Fehler bei Overflow).
	 * @param value Integer Wert der übernommen wird
	 */
	public INT16(int value) {
		this.setValue(value);
	}
	
	/**
	 * Erstellt eine 16 Bit Zahl aus zwei 8 Bit Zahlen.
	 * @param lowByte 8 Bit Zahl, die zum Lowbyte der 16 Bit Zahl wird (Bit 0 bis 7)
	 * @param highByte 8 Bit Zahl, die zum Highbyte der 16 Bit Zahl wird (Bit 8 bis 15)
	 */
	public INT16(INT8 lowByte, INT8 highByte) {
		this.setBytes(lowByte, highByte);
	}
	
	/**
	 * Erstellt eine 16 Bit Zahl aus einem String. (Beginnt mit $: Hex, %: Binär, sonst: Dezimal).
	 * @param assemblyString String aus dem die Zahl interpretiert wird
	 * @throws NumberFormatException Wenn der String nicht als valide Zahl interpretiert werden kann
	 */
	public INT16(String assemblyString) throws NumberFormatException {
		this.setValueFromAssemblyString(assemblyString);
	}
	
	/**
	 * Gibt den Integerwert der Zahl zurück (als 2 Komplement interpretiert).
	 * @return Integerwert
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Gibt den Integerwert der Zahl zurück (als vorzeichenfrei interpretiert).
	 * @return Integerwert
	 */
	public int getUnsignedValue() {
		return Short.toUnsignedInt(this.value);
	}

	/**
	 * Setzt den Wert mit einem Integer (kein Fehler bei Overflow).
	 * @param value Integer Wert der übernommen wird
	 */
	public void setValue(int value) {
		this.value = (short) value;
	}
	
	/**
	 * Kopiert den Wert einer anderen 16 Bit Zahl in diese Zahl.
	 * @param int16 Andere 16 Bit Zahl
	 */
	public void valueOf(INT16 int16) {
		this.value = int16.value;
	}
	
	/**
	 * Kopiert den Wert eines 16 Bit Registers in diese Zahl.
	 * @param register16 16 Bit Register
	 */
	public void valueOf(Register16 register16) {
		this.valueOf(register16.getData());
	}

	/**
	 * Setzt den Wert der Zahl aus einem String. (Beginnt mit $: Hex, %: Binär, sonst: Dezimal).
	 * @param assemblyString String aus dem die Zahl interpretiert wird
	 * @throws NumberFormatException Wenn der String nicht als Zahl interpretiert werden kann
	 */
	public void setValueFromAssemblyString(String assemblyString) throws NumberFormatException {
		// siehe Quellenverzeichnis (4)
		int value;
		if(assemblyString.startsWith("$"))
			value = Integer.parseInt(assemblyString.substring(1), 16);
		else if(assemblyString.startsWith("%"))
			value = Integer.parseInt(assemblyString.substring(1), 2);
		else
			value = Integer.parseInt(assemblyString);
		
		this.value = (short) value;
	}
	
	/**
	 * Gibt das Lowbyte (Bit 0 bis 7) als 8 Bit Zahl zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getLowByte() {
		return new INT8(this.value & 255);
	}

	/**
	 * Gibt das High (Bit 8 bis 15) als 8 Bit Zahl zurück.
	 * @return 8 Bit Zahl
	 */
	public INT8 getHighByte() {
		return new INT8((this.value >> 8) & 255);
	}
	
	/**
	 * Setzt den Wert der Zahl aus zwei 8 Bit Zahlen.
	 * @param lowByte 8 Bit Zahl, die zum Lowbyte der 16 Bit Zahl wird (Bit 0 bis 7)
	 * @param highByte 8 Bit Zahl, die zum Highbyte der 16 Bit Zahl wird (Bit 8 bis 15)
	 */
	public void setBytes(INT8 lowByte, INT8 highByte) {
		this.value = (short) ((highByte.getValue() << 8) | (lowByte.getValue() & 255));
	}
	
	/**
	 * Gibt den Wert eines Bits an der angegebenen Position zurück.
	 * @param position Bitposition
	 * @return Wert des Bits
	 */
	public boolean getBit(int position) {
		// überarbeitet aus: siehe Quellenverzeichnis (2)
		return ((this.value >>> position) & 1) == 1;
	}

	/**
	 * Setzt den Wert eines Bits an der angegebenen Position.
	 * @param position Bitposition
	 * @param state Wert des Bits
	 */
	public void setBit(int position, boolean state) {
		// siehe Quellenverzeichnis (1)
		if(state)
			this.value |= 1 << position;
		else
			this.value &= ~(1 << position);
	}
	
	// Für UI
	
	/**
	 * Gibt die Zahl als String in ihrer vorzeichenbehafteten Dezimaldarstellung zurück.
	 * @return Dezimal String
	 */
	public String getDecimalString() {
		return String.valueOf(this.value);
	}

	/**
	 * Gibt die Zahl als String in ihrer vorzeichenfreien Dezimaldarstellung zurück.
	 * @return Dezimal String
	 */
	public String getUnsignedDecimalString() {
		return String.valueOf(Short.toUnsignedInt(this.value));
	}

	/**
	 * Gibt die Zahl als String in ihrer Hexadezimaldarstellung zurück.
	 * @return Hex String
	 */
	public String getHexString() {
		return String.format("%04X", this.value);
	}

	/**
	 * Gibt die Zahl als String in ihrer Binärdarstellung zurück.
	 * @return Binär String
	 */
	public String getBinaryString() {
		String binaryString = new String();
		for(int i = 15; i >= 0; i--)
			binaryString += this.getBit(i) ? 1 : 0;
		return binaryString;
	}
}
