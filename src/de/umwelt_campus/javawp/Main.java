package de.umwelt_campus.javawp;
import java.awt.EventQueue;

import de.umwelt_campus.javawp.gui.GUI;
import de.umwelt_campus.javawp.processor.Processor;

/**
 * Startet das GUI mit einem neuen Prozessor Objekt.
 * @author Mathis Ströhlein
 * @author Lukas Müller
 * @author Michael Weber
 */
public class Main {
	/**
	 * Main Methode
	 * @param args Standardargument
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI(new Processor());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
