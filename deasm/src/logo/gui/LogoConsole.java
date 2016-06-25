package logo.gui;

import logo.MainClass;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;

public class LogoConsole extends JFrame {
	private static JTextComponent status;
	private static JTextComponent filename;
	private static String currentdir = "";
	private JPanel buttons;
	private JConsole console;

	private LogoButtonListener bl;

	public LogoConsole(String title) {
		super(title);
	}

	private static String[] getFileName(JFrame parent, String title) {
		FileDialog dialog = new FileDialog(parent, title);
		dialog.setDirectory(currentdir);
		dialog.setVisible(true);
		currentdir = dialog.getDirectory();
		String[] retval = new String[]{dialog.getDirectory(), dialog.getFile()};
		dialog.dispose();
		return retval;
	}

	private static void addMetaActionKey(Keymap keymap, Action a, int keycode) {
		keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(keycode, InputEvent.META_MASK), a);
	}

	private static void initAppleKeys(JTextArea area) {
		Keymap keymap = area.getKeymap();
		addMetaActionKey(keymap, new CutAction(), KeyEvent.VK_X);
		addMetaActionKey(keymap, new CopyAction(), KeyEvent.VK_C);
		addMetaActionKey(keymap, new PasteAction(), KeyEvent.VK_V);
		addMetaActionKey(keymap, new SelectAllAction(), KeyEvent.VK_A);
		area.setKeymap(keymap);
	}

	public void init() {
		JPanel root = new JPanel();
		root.setLayout(new BorderLayout());
		this.getContentPane().add(root, "Center");
		this.console = new JConsole(20, 60);
		JScrollPane scroll = new JScrollPane(this.console,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		root.add(scroll, "Center");
		this.console.setFont(new Font("Courier New", Font.PLAIN, 13));
		this.console.setMargin(new Insets(0, 5, 0, 0));
		this.console.setWrapStyleWord(true);
		this.console.setLineWrap(true);
		initAppleKeys(this.console);
		this.buttons = new JPanel();
		root.add(this.buttons, "South");
		this.buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.bl = new LogoButtonListener(this.console);
		filename = new JTextField("test.txt", 12);
		this.buttons.add(filename);
		JButton choose = new JButton("...");
		this.buttons.add(choose);
		choose.addActionListener(event -> {
			String[] files = LogoConsole.getFileName(MainClass.logoconsole, "logo file");
			LogoConsole.filename.setText(files[1]);
			LogoConsole.this.console.lc.file_field_path = files[0];
		});
		this.addButton("asm", "asm");
		this.addButton("download", "download");
		this.pack();
		this.setVisible(true);
		this.addWindowListener(new ToplevelWindowListener());
		this.console.addKeyListener(new TEKeyListener());
		this.console.lc = MainClass.lc;
		this.console.lc.stdout = new PrintWriter(new TEStream(this.console), true);
		this.console.lc.file_field = filename;
		this.console.lc.status_field = status;
		this.console.requestFocus();
	}

	private void addButton(String text, String name) {
		JButton button = new JButton(text);
		button.setName(name);
		button.addActionListener(this.bl);
		this.buttons.add(button);
	}
}
