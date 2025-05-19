package de.umwelt_campus.javawp.integers;

import de.umwelt_campus.javawp.processor.components.registers.Register8;

/**
 * Eine 8 Bit Zahlen Klasse.
 * @author Mathis Ströhlein
 */
public class INT8 {
	private byte value;

	/**
	 * Erstellt eine 8 Bit Zahl.
	 */
	public INT8() {
		this.setValue(0);
	}

	/**
	 * Erstellt eine 8 Bit Zahl und setzt den Wert mit einem Integer (kein Fehler bei Overflow).
	 * @param value Integer Wert der übernommen wird
	 */
	public INT8(int value) {
		this.setValue(value);
	}

	/**
	 * Erstellt eine 8 Bit Zahl aus einem String. (Beginnt mit $: Hex, %: Binär, sonst: Dezimal).
	 * @param assemblyString String aus dem die Zahl interpretiert wird
	 * @throws NumberFormatException Wenn der String nicht als valide Zahl interpretiert werden kann
	 */
	public INT8(String assemblyString) throws NumberFormatException {
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
		return Byte.toUnsignedInt(this.value);
	}

	/**
	 * Setzt den Wert mit einem Integer (kein Fehler bei Overflow).
	 * @param value Integer Wert der übernommen wird
	 */
	public void setValue(int value) {
		this.value = (byte) value;
	}

	/**
	 * Kopiert den Wert einer anderen 8 Bit Zahl in diese Zahl.
	 * @param int8 Andere 8 Bit Zahl
	 */
	public void valueOf(INT8 int8) {
		this.value = int8.value;
	}

	/**
	 * Kopiert den Wert eines 8 Bit Registers in diese Zahl.
	 * @param register8 8 Bit Register
	 */
	public void valueOf(Register8 register8) {
		this.valueOf(register8.getData());
	}
	
	/**
	 * Setzt den Wert der Zahl aus einem String. (Beginnt mit $: Hex, %: Binär, sonst: Dezimal).
	 * @param assemblyString String aus dem die Zahl interpretiert wird
	 * @throws NumberFormatException Wenn der String nicht als valide Zahl interpretiert werden kann
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
		
		this.value = (byte) value;
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
		return String.valueOf(Byte.toUnsignedInt(this.value));
	}

	/**
	 * Gibt die Zahl als String in ihrer Hexadezimaldarstellung zurück.
	 * @return Hex String
	 */
	public String getHexString() {
		return String.format("%02X", this.value);
	}

	/**
	 * Gibt die Zahl als String in ihrer Binärdarstellung zurück.
	 * @return Binär String
	 */
	public String getBinaryString() {
		String binaryString = new String();
		for(int i = 7; i >= 0; i--)
			binaryString += this.getBit(i) ? 1 : 0;
		return binaryString;
	}
}
