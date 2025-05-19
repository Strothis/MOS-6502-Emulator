package de.umwelt_campus.javawp.gui;

import de.umwelt_campus.javawp.exceptions.InterruptException;
import de.umwelt_campus.javawp.exceptions.UnknownOpcodeException;
import de.umwelt_campus.javawp.integers.INT8;
import de.umwelt_campus.javawp.processor.Processor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.border.LineBorder;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.*;
import javax.swing.Timer;

/**
 * Die Grafische Benutzeroberfläche des MOS 6502 Emulator.
 * @author Lukas Müller
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane memoryScrollPane;
	private JTable memoryTable;
	private JScrollPane addressNumberScrollPane;
	private JTable addressNumberTable;
	private JTextField currentPageTextField;
	private JTextField intervalTextField;
	private JTextArea codeTextArea;
	private JLabel accumulatorLabel;
	private JLabel	programCounterLabel;
	private JLabel	indexregisterXLabel;
	private JLabel	indexregisterYLabel;
	private JLabel stackpointerLabel;
	private JLabel errorLabel;
	private JButton nextLineButton;
	private JButton previousLineButton;
	private JButton stopButton;
	private JButton resetMemoryButton;
	private JButton resetAllButton;
	private JButton resetRegistersButton;
	private JLabel negativeLabel;
	private JLabel overflowLabel;
	private JLabel expansionLabel;
	private JLabel breakLabel;
	private JLabel decimalLabel;
	private JLabel interruptDisableLabel;
	private JLabel zeroLabel;
	private JLabel carryLabel;
	private JButton nextPageButton;
	private JButton prevPageButton;
	private JButton nextNextPageButton;
	private JButton prevPrevPageButton;
	private JButton assembleButton;
	private JLabel currentPageLabel;
	private JButton runButton;
	
	private Processor processor;
	
	private  Timer timer;
	private int speed = 0;
	
	private int base = 11;
	private int pageNumber = 0;

	/**
	 * Erstellt eine Grafische Benutzeroberfläche, die die Darstellung der Register,
	 * des Speichers und die der Code Eingabe, sowie Funktionen zur Manipulierung
	 * dieser realisiert.
	 * Dargestellt werden Speicher, Befehlszähler, Akkumulator, Indexregister X,
	 * Indexregister Y, Stackpointer und Statusregister mit Flags. 
	 * @param processor Der emulierte Prozessor der durch die GUI dargestellt wird
	 */
	public GUI(Processor processor) {
		this.processor = processor;
		
		
		setResizable(false);
		setTitle("MOS 6502 Emulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 665);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		
		
		this.memoryScrollPane = new JScrollPane();
		this.memoryScrollPane.setBounds(94, 44, 179, 392);
		
		this.memoryTable = new JTable();
		this.memoryTable.setModel(new DefaultTableModel(
			new Object[256][1],
			new String[] {
				"Gespeicherter Wert"
			}
		));
		this.memoryScrollPane.setViewportView(memoryTable);

		this.addressNumberScrollPane = new JScrollPane();
		this.addressNumberScrollPane.setBounds(27, 44, 67, 392);
		
		this.addressNumberTable = new JTable();
		this.addressNumberTable.setEnabled(false);
		this.addressNumberTable.setModel(new DefaultTableModel(
			new Object[256][1],
			new String[] {
				"#"
			}
		));
		this.addressNumberScrollPane.setViewportView(this.addressNumberTable);
		
		//Beide Tabellen nutzen den selben Scroller
		this.memoryScrollPane.setVerticalScrollBar(this.addressNumberScrollPane.getVerticalScrollBar());
		
		//http://www.java2s.com/Code/Java/Swing-JFC/PagingorpagableJTableTableModelforlargedataset.htm
		this.nextPageButton = new JButton(">");
		this.nextPageButton.setBounds(150, 446, 50, 21);
		this.nextPageButton.setToolTipText("Spring eine Seite nach vorne.");
		
		this.prevPageButton = new JButton("<");
		this.prevPageButton.setBounds(99, 446, 50, 21);
		this.prevPageButton.setToolTipText("Spring eine Seite zurück.");
		this.prevPageButton.setEnabled(false);
		
		this.nextNextPageButton = new JButton(">>");
		this.nextNextPageButton.setBounds(201, 446, 50, 21);
		this.nextNextPageButton.setToolTipText("Spring 5 Seiten nach vorne.");
		
		this.prevPrevPageButton = new JButton("<<");
		this.prevPrevPageButton.setBounds(48, 446, 50, 21);
		this.prevPrevPageButton.setToolTipText("Spring 5 Seiten zurück.");
		this.prevPrevPageButton.setEnabled(false);
		
		this.currentPageTextField = new JTextField();
		this.currentPageTextField.setBounds(171, 496, 61, 21);
		this.currentPageTextField.setHorizontalAlignment(SwingConstants.CENTER);
		this.currentPageTextField.setColumns(10);
		
		JLabel memoryLabel = new JLabel("Datenspeicher");
		memoryLabel.setBounds(27, 14, 121, 30);
		
		JLabel jumpToPageLabel = new JLabel("Springe zu Seite");
		jumpToPageLabel.setBounds(48, 496, 113, 21);
		
		JLabel currentPageLabelText = new JLabel("Aktuelle Seite");
		currentPageLabelText.setBounds(48, 469, 90, 21);
		
		this.currentPageLabel = new JLabel(String.valueOf(this.pageNumber));
		this.currentPageLabel.setBounds(171, 469, 61, 21);
		this.currentPageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.currentPageLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.currentPageLabel.setOpaque(true);
		this.currentPageLabel.setBackground(new Color(255, 255, 255));
		
		
		JLabel codeLabel = new JLabel("Code Eingabe");
		codeLabel.setBounds(341, 204, 113, 30);
		
		JScrollPane codeScrollPane = new JScrollPane();
		codeScrollPane.setBounds(341, 234, 370, 333);
		
		this.codeTextArea = new JTextArea();
		codeScrollPane.setViewportView(this.codeTextArea);
		this.codeTextArea.setMargin(new Insets(6,6,6,6));
		this.codeTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
		
		this.programCounterLabel = new JLabel();
		this.programCounterLabel.setBounds(364, 55, 156, 30);
		this.programCounterLabel.setOpaque(true);
		this.programCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.programCounterLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.programCounterLabel.setBackground(new Color(255, 255, 255));
		
		this.accumulatorLabel = new JLabel();
		this.accumulatorLabel.setBounds(530, 55, 156, 30);
		this.accumulatorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.accumulatorLabel.setOpaque(true);
		this.accumulatorLabel.setBackground(new Color(255, 255, 255));
		this.accumulatorLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
				
		this.indexregisterXLabel = new JLabel();
		this.indexregisterXLabel.setBounds(364, 95, 156, 30);
		this.indexregisterXLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.indexregisterXLabel.setBackground(new Color(255, 255, 255));
		this.indexregisterXLabel.setOpaque(true);
		this.indexregisterXLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.indexregisterYLabel = new JLabel();
		this.indexregisterYLabel.setBounds(530, 95, 156, 30);
		this.indexregisterYLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.indexregisterYLabel.setOpaque(true);
		this.indexregisterYLabel.setBackground(new Color(255, 255, 255));
		this.indexregisterYLabel.setBorder(new LineBorder(new Color(0, 0, 0)));

		this.stackpointerLabel = new JLabel();
		this.stackpointerLabel.setOpaque(true);
		this.stackpointerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.stackpointerLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.stackpointerLabel.setBackground(Color.WHITE);
		this.stackpointerLabel.setBounds(447, 135, 156, 30);
			
		JLabel programCounterLabelText = new JLabel("PC");
		programCounterLabelText.setBounds(307, 55, 45, 30);
		programCounterLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		programCounterLabelText.setToolTipText("Befehlszähler");
				
		JLabel accumulatorLabelText = new JLabel("ACC");
		accumulatorLabelText.setBounds(696, 55, 45, 30);
		accumulatorLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		accumulatorLabelText.setToolTipText("Akkumulator");
		
		JLabel indexregisterXLabelText = new JLabel("X");
		indexregisterXLabelText.setBounds(309, 95, 45, 30);
		indexregisterXLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		indexregisterXLabelText.setToolTipText("Indexregister X");
		
		JLabel indexregisterYLabelText = new JLabel("Y");
		indexregisterYLabelText.setBounds(696, 95, 45, 30);
		indexregisterYLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		indexregisterYLabelText.setToolTipText("Indexregister Y");
		
		JLabel stackpointerLabelText = new JLabel("Stackpointer");
		stackpointerLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		stackpointerLabelText.setBounds(356, 135, 85, 30);

		this.errorLabel = new JLabel();
		this.errorLabel.setBounds(65, 570, 920, 44);
		this.errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton binaryButton = new JButton("Binär");
		binaryButton.setBounds(305, 14, 117, 30);
		binaryButton.setToolTipText("<html>Setzt die dargestellten Werte in den Indexregistern,<br>\r\nAkkumulator und Datenspeicher auf binär.</html>");
		
		JButton decimalButton = new JButton("Dezimal");
		decimalButton.setBounds(415, 14, 110, 30);
		decimalButton.setToolTipText("<html>Setzt die dargestellten Werte in den Indexregistern,<br>\r\nAkkumulator und Datenspeicher auf dezimal.</html>");
		
		JButton decimalUnsignedButton = new JButton("U-Dezimal");
		decimalUnsignedButton.setToolTipText("<html>Setzt die dargestellten Werte in den Indexregistern,<br>\r\nAkkumulator und Datenspeicher auf unsigned dezimal.</html>");
		decimalUnsignedButton.setBounds(525, 14, 110, 30);
		decimalUnsignedButton.setEnabled(false);

		JButton hexButton = new JButton("Hexadezimal");
		hexButton.setBounds(635, 14, 110, 30);
		hexButton.setToolTipText("<html>Setzt die dargestellten Werte in den Indexregistern,<br>\r\nAkkumulator und Datenspeicher auf hexadezimal.<br>\r\nDie Seitenzahlen und Adressen werden auch<br>\r\n auf hexadezimal umgerechnet.</html>");
		
		
		JLabel controlLabel = new JLabel("Steuerung");
		controlLabel.setBounds(804, 14, 90, 30);		
		
		this.assembleButton = new JButton("Assemblieren");
		this.assembleButton.setBounds(804, 44, 160, 30);
		this.assembleButton.setToolTipText("<html>Eingabefelder für Code und <br>\r\nDatenspeicher werden gelockt.</html>");
		this.assembleButton.setEnabled(false);
		
		JLabel intervalLabel = new JLabel("Schrittintervall [ms]");
		intervalLabel.setBounds(803, 76, 160, 30);
		intervalLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.intervalTextField = new JTextField();
		this.intervalTextField.setBounds(803, 106, 160, 30);
		this.intervalTextField.setColumns(10);
		this.intervalTextField.setText("0");
		this.intervalTextField.setEnabled(false);
		
		this.runButton = new JButton("Start");
		this.runButton.setBounds(803, 138, 160, 30);
		this.runButton.setToolTipText("Benötigt Schrittintervall.");
		this.runButton.setEnabled(false);
		
		this.stopButton = new JButton("Stopp");
		this.stopButton.setBounds(803, 170, 160, 30);
		this.stopButton.setEnabled(false);
		
		this.nextLineButton = new JButton("Nächste Zeile");
		this.nextLineButton.setBounds(803, 215, 160, 30);
		this.nextLineButton.setEnabled(false);
		
		this.previousLineButton = new JButton("Vorherige Zeile");
		this.previousLineButton.setBounds(803, 247, 160, 30);
		this.previousLineButton.setEnabled(false);
		
		JLabel resetLabel = new JLabel("Zurücksetzten");
		resetLabel.setBounds(803, 292, 160, 30);
		
		this.resetRegistersButton = new JButton("Register");
		this.resetRegistersButton.setBounds(803, 322, 160, 30);
		
		this.resetMemoryButton = new JButton("Datenspeicher");
		this.resetMemoryButton.setBounds(803, 354, 160, 30);
		
		this.resetAllButton = new JButton("Alles");
		this.resetAllButton.setBounds(803, 386, 160, 30);

		
		this.negativeLabel = new JLabel("0");
		this.negativeLabel.setBounds(351, 170, 45, 13);
		this.negativeLabel.setOpaque(true);
		this.negativeLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.negativeLabel.setBackground(new Color(255, 255, 255));
		this.negativeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.overflowLabel = new JLabel("0");
		this.overflowLabel.setBounds(396, 170, 45, 13);
		this.overflowLabel.setOpaque(true);
		this.overflowLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.overflowLabel.setBackground(new Color(255, 255, 255));
		this.overflowLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.expansionLabel = new JLabel("0");
		this.expansionLabel.setBounds(441, 170, 45, 13);
		this.expansionLabel.setOpaque(true);
		this.expansionLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.expansionLabel.setBackground(new Color(255, 255, 255));
		this.expansionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.breakLabel = new JLabel("0");
		this.breakLabel.setBounds(486, 170, 45, 13);
		this.breakLabel.setOpaque(true);
		this.breakLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.breakLabel.setBackground(new Color(255, 255, 255));
		this.breakLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.decimalLabel = new JLabel("0");
		this.decimalLabel.setBounds(531, 170, 45, 13);
		this.decimalLabel.setOpaque(true);
		this.decimalLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.decimalLabel.setBackground(new Color(255, 255, 255));
		this.decimalLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.interruptDisableLabel = new JLabel("0");
		this.interruptDisableLabel.setBounds(576, 170, 45, 13);
		this.interruptDisableLabel.setOpaque(true);
		this.interruptDisableLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.interruptDisableLabel.setBackground(new Color(255, 255, 255));
		this.interruptDisableLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.zeroLabel = new JLabel("0");
		this.zeroLabel.setBounds(621, 170, 45, 13);
		this.zeroLabel.setOpaque(true);
		this.zeroLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.zeroLabel.setBackground(new Color(255, 255, 255));
		this.zeroLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.carryLabel = new JLabel("0");
		this.carryLabel.setBounds(666, 170, 45, 13);
		this.carryLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.carryLabel.setBackground(new Color(255, 255, 255));
		this.carryLabel.setOpaque(true);
		this.carryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel flagsLabel = new JLabel("Flags");
		flagsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		flagsLabel.setBounds(306, 170, 45, 13);
		flagsLabel.setToolTipText("<html>Werden entsprechend diverser Operation automatisch gesetzt, können aber auch teilweise manuell verändert werden.<br>\r\nDie Flagbeschreibungen beschreiben nur die wichtigsten Funktionsweisen der einzelnen Flags.");
		
		JLabel negativeLabelText = new JLabel("N");
		negativeLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		negativeLabelText.setBackground(Color.WHITE);
		negativeLabelText.setBounds(351, 187, 45, 13);
		negativeLabelText.setToolTipText("<html>Negative Flag - Kopie des 7. Bits einer Operationsergebnisses.<br>\r\nWird gestzt, falls die Darstellung im 2-Komplement negativ wäre. Compare: Kleiner</html>");
		
		JLabel overflowLabelText = new JLabel("V");
		overflowLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		overflowLabelText.setBackground(Color.WHITE);
		overflowLabelText.setBounds(396, 187, 45, 13);
		overflowLabelText.setToolTipText("<html>Overflow Flag - Wird gesetzt, falls ein Rechenergebnis außerhalb -128 und 127 liegt.<html>");
		
		JLabel expansionLabelText = new JLabel("-");
		expansionLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		expansionLabelText.setBackground(Color.WHITE);
		expansionLabelText.setBounds(441, 187, 45, 13);
		expansionLabelText.setToolTipText("<html>Expansion Flag - Immer 1.</html>");
		
		JLabel breakLabelText = new JLabel("B");
		breakLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		breakLabelText.setBackground(Color.WHITE);
		breakLabelText.setBounds(486, 187, 45, 13);
		breakLabelText.setToolTipText("<html>Break Flag - Wird bei manuellem Interrupt gesetzt (nicht implementiert).</html>");
		
		JLabel decimalLabelText = new JLabel("D");
		decimalLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		decimalLabelText.setBackground(Color.WHITE);
		decimalLabelText.setBounds(531, 187, 45, 13);
		decimalLabelText.setToolTipText("<html>Decimal Flag - Wird manuell gesetzt und gelöscht.<br>\r\nAddition und Subtraktion, werden bei gesetztem Flag mit binärcodierten Dezimalzahlen durchgeführt.</html>");
		
		JLabel interruptDisableLabelText = new JLabel("I");
		interruptDisableLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		interruptDisableLabelText.setBackground(Color.WHITE);
		interruptDisableLabelText.setBounds(576, 187, 45, 13);
		interruptDisableLabelText.setToolTipText("<html>Interrupt Flag - Verhindert bei gesetztem Flag Interrupts von außen (hier keinen Einfluss).</html>");
		
		JLabel zeroLabelText = new JLabel("Z");
		zeroLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		zeroLabelText.setBackground(Color.WHITE);
		zeroLabelText.setBounds(621, 187, 45, 13);
		zeroLabelText.setToolTipText("<html>Zero Flag - Wird gesetzt, wenn ein Operationsergebnis 0 ergibt. Compare: Gleichheit</html>");
		
		JLabel carryLabelText = new JLabel("C");
		carryLabelText.setHorizontalAlignment(SwingConstants.CENTER);
		carryLabelText.setBackground(Color.WHITE);
		carryLabelText.setBounds(666, 187, 45, 13);
		carryLabelText.setToolTipText("<html>Carry Flag - Wird als Übertragsbit und für bitweises Schieben und Rotieren verwendet.</html>");
		
		
		updateAll();
		
		
		//Wird wiederholt aktiviert mit dem Intervall speed und wird solange wiederholt bis time.stop() oder InterruptedException
		this.timer = new Timer(this.speed, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeNext();
			}
		});
		
		//Setzt Intervall Geschwindigkeit
		this.intervalTextField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  try {
					  speed = Integer.parseInt(intervalTextField.getText());
					  timer.setDelay(speed);
					  runButton.setEnabled(true);
				  } catch(Exception e1) {
					  runButton.setEnabled(false);
				  }
			  }
			  public void removeUpdate(DocumentEvent e) {
				  try {
					  speed = Integer.parseInt(intervalTextField.getText());
					  timer.setDelay(speed);
					  runButton.setEnabled(true);
				  } catch(Exception e1) {
					  runButton.setEnabled(false);
				  }
			  }
			  public void insertUpdate(DocumentEvent e) {
				  try {
					  speed = Integer.parseInt(intervalTextField.getText());
					  timer.setDelay(speed);
					  runButton.setEnabled(true);
				  } catch(Exception e1) {
					  runButton.setEnabled(false);
				  }
			  }
		});
		
		this.codeTextArea.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  assembleButton.setEnabled(!codeTextArea.getText().isBlank());
			  }
			  public void removeUpdate(DocumentEvent e) {
				  assembleButton.setEnabled(!codeTextArea.getText().isBlank());
			  }
			  public void insertUpdate(DocumentEvent e) {
				  assembleButton.setEnabled(!codeTextArea.getText().isBlank());
			  }
		});
		
		
		//Aktuelle Seite springt zur eingegebenen Seite
		this.currentPageTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					updateErrorLabel("");
					
					int newPageNumber;
					if(base == 16)
						newPageNumber = Integer.parseInt(currentPageTextField.getText(), 16);
					else
						newPageNumber = Integer.parseInt(currentPageTextField.getText());
					
					if(newPageNumber >= 0 && newPageNumber <= 255) {
						if(pageNumber != newPageNumber) {
							pageNumber = newPageNumber;
							currentPageTextField.setText("");
							updateMemory();
							
							updateCurrentPageLabel(pageNumber);
							refreshPageButtons();
						} else
							currentPageTextField.setText("");
					}
					else
						updateErrorLabel("Fehler: Eingegebene Seitenzahl befindet sich außerhalb des gültigen Bereichs.");
				} catch(NumberFormatException e1) {
					updateErrorLabel("Fehler: Es wurde keine Zahl als Seitenzahl eingegen.");
				}
		    }
			
		});
				
		
		this.prevPageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pageNumber > 0) {
					pageNumber--;
					updateMemory();
				}
				
				updateCurrentPageLabel(pageNumber);
				refreshPageButtons();
			}
		});
		
		this.nextPageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pageNumber < 255) {
					pageNumber++;
					updateMemory();
				}
				
				updateCurrentPageLabel(pageNumber);
				refreshPageButtons();
			}
		});
		
		this.nextNextPageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pageNumber < 251) {
					pageNumber = pageNumber + 5;
					updateMemory();
				}
				
				updateCurrentPageLabel(pageNumber);
				refreshPageButtons();
			}
		});
		
		this.prevPrevPageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pageNumber > 4) {
					pageNumber = pageNumber - 5;
					updateMemory();
				}
				
				updateCurrentPageLabel(pageNumber);
				refreshPageButtons();
			}
		});
		
		
		binaryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				base = 2;
				
				binaryButton.setEnabled(false);
				decimalButton.setEnabled(true);
				decimalUnsignedButton.setEnabled(true);
				hexButton.setEnabled(true);
				
				updateCurrentPageLabel(pageNumber);
				updateAll();
			}
		});
		
		decimalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				base = 10;

				binaryButton.setEnabled(true);
				decimalButton.setEnabled(false);
				decimalUnsignedButton.setEnabled(true);
				hexButton.setEnabled(true);
				
				updateCurrentPageLabel(pageNumber);
				updateAll();
			}
		});
		
		decimalUnsignedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				base = 11;

				binaryButton.setEnabled(true);
				decimalButton.setEnabled(true);
				decimalUnsignedButton.setEnabled(false);
				hexButton.setEnabled(true);
				
				updateCurrentPageLabel(pageNumber);
				updateAll();
			}
		});
		
		hexButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				base = 16;

				binaryButton.setEnabled(true);
				decimalButton.setEnabled(true);
				decimalUnsignedButton.setEnabled(true);
				hexButton.setEnabled(false);
				
				updateCurrentPageLabel(pageNumber);
				updateAll();
			}
		});
		
		
		//Speichert aktuelle Seite wenn etwas in die Tabelle eingegeben wird und überprüft die Eingabe auf Richtigkeit
		this.memoryTable.getModel().addTableModelListener(new TableModelListener() {
			  public void tableChanged(TableModelEvent e) {
				  int element = e.getFirstRow();
				  
				  INT8 value = null;
				  try {
						switch(base) {
							case 2:
								value = new INT8(Integer.parseInt(memoryTable.getValueAt(element, 0).toString(), 2));
								break;
							case 10:
							case 11:
								value = new INT8(Integer.parseInt(memoryTable.getValueAt(element, 0).toString()));
								break;
							case 16:
								value = new INT8(Integer.parseInt(memoryTable.getValueAt(element, 0).toString(), 16));
								break;
						}
					} catch (NumberFormatException e3) {
						updateErrorLabel("Fehler: Die Eingabe \"" + memoryTable.getValueAt(element, 0).toString() + "\" ist nicht valide! Es wurde 0 eingetragen.");
						
						value = new INT8(0);
						switch(base) {
							case 2: memoryTable.setValueAt("00000000", element, 0); break;
							case 10:
							case 11: memoryTable.setValueAt("0", element, 0); break;
							case 16: memoryTable.setValueAt("00", element, 0); break;
						}
					}
					processor.getMemoryData()[pageNumber << 8 | element].valueOf(value);
			  }
		});
		
		//Code aus der codeTextArea wird assembliert
		this.assembleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				assembleButton.setEnabled(false);
				
				intervalTextField.setEnabled(true);
				try {
					speed = Integer.parseInt(intervalTextField.getText());
					timer.setDelay(speed);
					runButton.setEnabled(true);
				} catch(Exception e1) {
					runButton.setEnabled(false);
				}
				
				nextLineButton.setEnabled(true);
				previousLineButton.setEnabled(false);
				processor.getRegisterDataStack().clear();
				
				assemble();
			}
		});
		
		//Code wird iterativ abgearbeitet
		this.runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				intervalTextField.setEnabled(false);
				runButton.setEnabled(false);
				stopButton.setEnabled(true);
				
				nextLineButton.setEnabled(false);
				previousLineButton.setEnabled(false);
				
				resetMemoryButton.setEnabled(false);
				resetAllButton.setEnabled(false);
				resetRegistersButton.setEnabled(false);
				
				timer.start();
			}
		});
		
		this.stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				intervalTextField.setEnabled(true);
				try {
					speed = Integer.parseInt(intervalTextField.getText());
					timer.setDelay(speed);
					runButton.setEnabled(true);
				} catch(Exception e1) {
					runButton.setEnabled(false);
				}
				stopButton.setEnabled(false);
				
				nextLineButton.setEnabled(true);
				previousLineButton.setEnabled(!processor.getRegisterDataStack().isEmpty());
				
				resetMemoryButton.setEnabled(true);
				resetAllButton.setEnabled(true);
				resetRegistersButton.setEnabled(true);
				
				timer.stop();
			}
		});
		
		this.nextLineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				previousLineButton.setEnabled(true);
				
				executeNext();
			}
		});
		
		this.previousLineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				intervalTextField.setEnabled(true);
				try {
					speed = Integer.parseInt(intervalTextField.getText());
					timer.setDelay(speed);
					runButton.setEnabled(true);
				} catch(Exception e1) {
					runButton.setEnabled(false);
				}
				
				nextLineButton.setEnabled(true);

				undo();
			}
		});
		
		this.resetMemoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				assembleButton.setEnabled(!codeTextArea.getText().isBlank());
				
				nextLineButton.setEnabled(false);
				previousLineButton.setEnabled(false);
				processor.getRegisterDataStack().clear();
				
				intervalTextField.setEnabled(false);
				runButton.setEnabled(false);
				
				resetMemory();
				updateMemory();
			}
		});
		
		this.resetRegistersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				previousLineButton.setEnabled(false);
				processor.getRegisterDataStack().clear();
				
				resetRegisters();
				updateRegisters();
			}
		});
		
		this.resetAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateErrorLabel("");
				
				assembleButton.setEnabled(!codeTextArea.getText().isBlank());
				
				nextLineButton.setEnabled(false);
				previousLineButton.setEnabled(false);
				processor.getRegisterDataStack().clear();
				
				intervalTextField.setEnabled(false);
				runButton.setEnabled(false);
				
				resetAll();
				updateAll();
			}
		});
		

		this.contentPane.setLayout(null);
		this.contentPane.add(memoryScrollPane);
		this.contentPane.add(addressNumberScrollPane);
		this.contentPane.add(prevPageButton);
		this.contentPane.add(nextPageButton);
		this.contentPane.add(nextNextPageButton);
		this.contentPane.add(prevPrevPageButton);
		this.contentPane.add(currentPageTextField);
		this.contentPane.add(memoryLabel);
		this.contentPane.add(jumpToPageLabel);
		this.contentPane.add(currentPageLabelText);
		this.contentPane.add(currentPageLabel);
		this.contentPane.add(codeLabel);
		this.contentPane.add(accumulatorLabel);
		this.contentPane.add(programCounterLabel);
		this.contentPane.add(indexregisterYLabel);
		this.contentPane.add(indexregisterXLabel);
		this.contentPane.add(programCounterLabelText);
		this.contentPane.add(indexregisterXLabelText);
		this.contentPane.add(accumulatorLabelText);
		this.contentPane.add(indexregisterYLabelText);
		this.contentPane.add(errorLabel);
		this.contentPane.add(binaryButton);
		this.contentPane.add(decimalButton);
		this.contentPane.add(hexButton);
		this.contentPane.add(codeScrollPane);
		this.contentPane.add(assembleButton);
		this.contentPane.add(runButton);
		this.contentPane.add(stopButton);
		this.contentPane.add(nextLineButton);
		this.contentPane.add(previousLineButton);
		this.contentPane.add(resetMemoryButton);
		this.contentPane.add(resetAllButton);
		this.contentPane.add(resetRegistersButton);
		this.contentPane.add(intervalLabel);
		this.contentPane.add(intervalTextField);
		this.contentPane.add(controlLabel);
		this.contentPane.add(negativeLabel);
		this.contentPane.add(overflowLabel);
		this.contentPane.add(expansionLabel);
		this.contentPane.add(breakLabel);
		this.contentPane.add(decimalLabel);
		this.contentPane.add(interruptDisableLabel);
		this.contentPane.add(zeroLabel);
		this.contentPane.add(carryLabel);
		this.contentPane.add(flagsLabel);
		this.contentPane.add(breakLabelText);
		this.contentPane.add(negativeLabelText);
		this.contentPane.add(expansionLabelText);
		this.contentPane.add(overflowLabelText);
		this.contentPane.add(decimalUnsignedButton);
		this.contentPane.add(carryLabelText);
		this.contentPane.add(zeroLabelText);
		this.contentPane.add(interruptDisableLabelText);
		this.contentPane.add(decimalLabelText);
		this.contentPane.add(stackpointerLabelText);
		this.contentPane.add(stackpointerLabel);
		this.contentPane.add(resetLabel);
	}
	
	
	/**
	 * Die Knöpfe über die die Seiten gesteuert werden, werden je nachdem 
	 * auf welcher Seite man sich befindet aktiviert oder deaktiviert.
	 */
	public void refreshPageButtons() {
		this.nextPageButton.setEnabled(this.pageNumber != 255);
		this.prevPageButton.setEnabled(this.pageNumber != 0);
		this.nextNextPageButton.setEnabled(this.pageNumber <= 250);
		this.prevPrevPageButton.setEnabled(this.pageNumber >= 5);
	}
	
	/**
	 * Lädt Akkumulator Label, Programmzähler Label, Indexregister X Label,
	 * Indexregister Y Label, Stackpointer Label, Statusregister Label
	 * und die Datenspeicher Tabelle mit aktualisierten Werten neu.
	 */
	public void updateAll() {
		this.updateRegisters();
		this.updateMemory();
	}
	
	/**
	 * Lädt Akkumulator Label, Befehlszähler Label, Indexregister X Label,
	 * Indexregister Y Label, Stackpointer Label und das Statusregister
	 * Label mit aktualisierten Werten neu.
	 */
	public void updateRegisters() {
		this.updateAccumulator();
		this.updateProgramCounter();
		this.updateIndexRegisterX();
		this.updateIndexRegisterY();
		this.updateStackpointer();
		this.updateStatusRegister();
	}
	
	/**
	 * Lädt das Akkumulator Label mit dem aktuellen Wert vom Prozessor und 
	 * konvertiert diesen je nach Einstellung zu Binär, Dezimal, 
	 * Unsigned Dezimal oder Hexadezimal.
	 */
	public void updateAccumulator() {
		switch(this.base) {
			case 2:
				this.accumulatorLabel.setText(this.processor.getAccumulatorData().getBinaryString());
				break;
			case 10:
				this.accumulatorLabel.setText(this.processor.getAccumulatorData().getDecimalString());
				break;
			case 11:
				this.accumulatorLabel.setText(this.processor.getAccumulatorData().getUnsignedDecimalString());
				break;
			case 16:
				this.accumulatorLabel.setText(this.processor.getAccumulatorData().getHexString());
				break;
		}
	}
	
	/**
	 * Lädt das Befehlszähler Label mit dem aktuellen Wert vom Prozessor und 
	 * konvertiert diesen je nach Einstellung zu Binär, Dezimal, 
	 * Unsigned Dezimal oder Hexadezimal.
	 */
	public void updateProgramCounter() {
		switch(this.base) {
			case 2:
				this.programCounterLabel.setText(this.processor.getProgramCounterData().getBinaryString());
				break;
			case 10:
				this.programCounterLabel.setText(this.processor.getProgramCounterData().getDecimalString());
				break;
			case 11:
				this.programCounterLabel.setText(this.processor.getProgramCounterData().getUnsignedDecimalString());
				break;
			case 16:
				this.programCounterLabel.setText(this.processor.getProgramCounterData().getHexString());
				break;
		}
	}
	
	/**
	 * Lädt das Indexregister X Label mit dem aktuellen Wert vom Prozessor und 
	 * konvertiert diesen je nach Einstellung zu Binär, Dezimal, 
	 * Unsigned Dezimal oder Hexadezimal.
	 */
	public void updateIndexRegisterX() {
		switch(this.base) {
			case 2:
				this.indexregisterXLabel.setText(this.processor.getIndexRegisterXData().getBinaryString());
				break;
			case 10:
				this.indexregisterXLabel.setText(this.processor.getIndexRegisterXData().getDecimalString());
				break;
			case 11:
				this.indexregisterXLabel.setText(this.processor.getIndexRegisterXData().getUnsignedDecimalString());
				break;
			case 16:
				this.indexregisterXLabel.setText(this.processor.getIndexRegisterXData().getHexString());
				break;
		}
	}
	
	/**
	 * Lädt das Indexregister Y Label mit dem aktuellen Wert vom Prozessor und 
	 * konvertiert diesen je nach Einstellung zu Binär, Dezimal, 
	 * Unsigned Dezimal oder Hexadezimal.
	 */
	public void updateIndexRegisterY() {
		switch(this.base) {
			case 2:
				this.indexregisterYLabel.setText(this.processor.getIndexRegisterYData().getBinaryString());
				break;
			case 10:
				this.indexregisterYLabel.setText(this.processor.getIndexRegisterYData().getDecimalString());
				break;
			case 11:
				this.indexregisterYLabel.setText(this.processor.getIndexRegisterYData().getUnsignedDecimalString());
				break;
			case 16:
				this.indexregisterYLabel.setText(this.processor.getIndexRegisterYData().getHexString());
				break;
		}
	}

	/**
	 * Lädt das Stackpointer Label mit dem aktuellen Wert vom Prozessor und 
	 * konvertiert diesen je nach Einstellung zu Binär, Dezimal, 
	 * Unsigned Dezimal oder Hexadezimal.
	 */
	public void updateStackpointer() {
		switch(this.base) {
			case 2:
				this.stackpointerLabel.setText(this.processor.getStackPointerData().getBinaryString());
				break;
			case 10:
				this.stackpointerLabel.setText(this.processor.getStackPointerData().getDecimalString());
				break;
			case 11:
				this.stackpointerLabel.setText(this.processor.getStackPointerData().getUnsignedDecimalString());
				break;
			case 16:
				this.stackpointerLabel.setText(this.processor.getStackPointerData().getHexString());
				break;
		}
	}
	
	/**
	 * Lädt die Datenspeicher Tabelle mit den aktuellen Werten vom Prozessor und 
	 * konvertiert diese je nach Einstellung zu Binär, Dezimal, 
	 * Unsigned Dezimal oder Hexadezimal.
	 * Lädt auch die Adresszeilen Tabelle neu und wenn der Hexadezimal
	 * Button aktiviert ist diese mit in hexadezimal umgerechneten Werten.
	 */
	public void updateMemory() {
		INT8 value = null;
		for(int i = 0; i < 256; i++) {
			value = this.processor.getMemoryData()[this.pageNumber << 8 | i];
			switch(base) {
				case 2:
					this.memoryTable.setValueAt(value.getBinaryString(), i, 0);
					break;
				case 10:
					this.memoryTable.setValueAt(value.getDecimalString(), i, 0);
					break;
				case 11:
					this.memoryTable.setValueAt(value.getUnsignedDecimalString(), i, 0);
					break;
				case 16:
					this.memoryTable.setValueAt(value.getHexString(), i, 0);
					break;
			}
		}
		for(int i = 0; i < 256; i++) {
			int address = this.pageNumber << 8 | i;
			if(this.base == 16)
				this.addressNumberTable.setValueAt(String.format("%04X", address), i, 0);
			else
				this.addressNumberTable.setValueAt(address, i, 0);
		}
	}
	
	/**
	 * Setzt alle Register und den Speicher im Prozesseor zurück.
	 */
	public void resetAll() {
		this.resetRegisters();
		this.resetMemory();
	}
	
	/**
	 * Setzt Befehlszähler, Akkumulator, Indexregister X, Indexregister Y,
	 * Stackpointer und Statusregister auf ihre Ursprungswerte zurück.
	 */
	public void resetRegisters() {
		this.processor.getProgramCounterData().valueOf(this.processor.getStartAddress());;
		this.processor.getAccumulatorData().setValue(0);
		this.processor.getIndexRegisterXData().setValue(0);
		this.processor.getIndexRegisterYData().setValue(0);
		this.processor.getStackPointerData().setValue(255);
		this.processor.getStatusRegisterData().setValue(34);
	}
	
	/**
	 * Setzt alle Zeilen des Speicher Arrays im Prozessor auf 0.
	 */
	public void resetMemory() {
		for(int i = 0; i < this.processor.getMemoryData().length; i++)
			this.processor.getMemoryData()[i].setValue(0);
	}
	
	/**
	 * Assembliert den Code, der in dem Code Eingabe Textfeld steht und
	 * gibt die Anzahl der assemblierten Bytes in der UI im ErrorLabel aus.
	 * Zusätzlich wird die Datenspeicher Tabelle aktualisiert.
	 */
	public void assemble() {
		try {
			this.updateErrorLabel(this.processor.assemble(this.codeTextArea.getText()));
		} catch(Exception e) {
			this.updateErrorLabel(e.getMessage());
			this.nextLineButton.setEnabled(false);
			
			this.intervalTextField.setEnabled(false);
			this.runButton.setEnabled(false);
		}
		this.updateMemory();
	}
	
	/**
	 * Führt den aktuellen Befehl, auf den der Befehlszähler zeift aus.
	 * Aktualisiert die Register Label und die Datenspeicher Tabelle.
	 * Wenn ein Fehler geworfen wird, wird der Timer, der für die iterative
	 * Abarbeitung des Codes zuständig ist gestoppt.
	 */
	public void executeNext() {
		try {	
			this.processor.executeNext();
		} catch(UnknownOpcodeException e) {
			this.updateErrorLabel(e.getMessage());
			
			this.nextLineButton.setEnabled(false);
			this.previousLineButton.setEnabled(!this.processor.getRegisterDataStack().isEmpty());
			
			this.intervalTextField.setEnabled(false);
			this.runButton.setEnabled(false);
			this.stopButton.setEnabled(false);
			
			this.resetRegistersButton.setEnabled(true);
			this.resetMemoryButton.setEnabled(true);
			this.resetAllButton.setEnabled(true);
			
			this.timer.stop();
		} catch(InterruptException e) {
			this.updateErrorLabel(e.getMessage());
			
			this.nextLineButton.setEnabled(true);
			this.previousLineButton.setEnabled(!this.processor.getRegisterDataStack().isEmpty());
			
			this.intervalTextField.setEnabled(true);
			try {
				this.speed = Integer.parseInt(this.intervalTextField.getText());
				this.timer.setDelay(this.speed);
				this.runButton.setEnabled(true);
			} catch(Exception e1) {
				this.runButton.setEnabled(false);
			}
			this.stopButton.setEnabled(false);
			
			this.resetRegistersButton.setEnabled(true);
			this.resetMemoryButton.setEnabled(true);
			this.resetAllButton.setEnabled(true);
			
			this.timer.stop();
		}
			
		this.updateAll();
	}

	/**
	 * Setzt den Speicher und die Register auf die Werte vor dem letzten executeNext()
	 * und aktualisiert die Register Label und die Datenspeicher Tabelle.
	 */
	public void undo() {
		this.processor.undo();		
		this.previousLineButton.setEnabled(!this.processor.getRegisterDataStack().isEmpty());
		
		this.updateAll();
	}
	
	/**
	 * Das Error Label in der UI wird mit einem neuen String
	 * aktualisiert.
	 */
	public void updateErrorLabel(String string) {
		this.errorLabel.setText("<html>" + string + "<html>");
	}
	
	/**
	 * Aktualisiert die Labels der Flags im Statusregister.
	 */
	public void updateStatusRegister() {
		INT8 statusRegister = this.processor.getStatusRegisterData();
		
		this.carryLabel.setText(statusRegister.getBit(0) ? "1" : "0");
		this.zeroLabel.setText(statusRegister.getBit(1) ? "1" : "0");
		this.interruptDisableLabel.setText(statusRegister.getBit(2) ? "1" : "0");
		this.decimalLabel.setText(statusRegister.getBit(3) ? "1" : "0");
		this.breakLabel.setText(statusRegister.getBit(4) ? "1" : "0");
		this.expansionLabel.setText(statusRegister.getBit(5) ? "1" : "0");
		this.overflowLabel.setText(statusRegister.getBit(6) ? "1" : "0");
		this.negativeLabel.setText(statusRegister.getBit(7) ? "1" : "0");
	}
	
	/**
	 * Aktualisiert das Label in dem die aktuelle Seitenzahl steht.
	 * Wenn der Hexadezimal Button aktiviert ist, wird die Seitenzahl in
	 * Hexadezimal umgerechnet, ansonsten in Dezimal.
	 * @param pageNumber Aktuelle Seitenzahl
	 */
	public void updateCurrentPageLabel(int pageNumber) {
		if(this.base == 16)
			this.currentPageLabel.setText(String.format("%02X", pageNumber));
		else
			this.currentPageLabel.setText(Integer.toString(pageNumber));
	}
}
