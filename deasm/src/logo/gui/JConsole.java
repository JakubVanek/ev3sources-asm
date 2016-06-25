package logo.gui;

import logo.LContext;
import logo.LogoCommandRunner;

import javax.swing.*;

public class JConsole extends JTextArea {
	public LContext lc;
	public String file;
	public String dir;
	/*
	String prefix = "";
	String suffix = "";
	*/

	public JConsole(int rows, int columns) {
		super(rows, columns);
	}

	private static int findStartOfLine(String str, int from) {
		int index = str.lastIndexOf('\n', from - 1);
		return index < 0 ? 0 : index + 1;
	}

	private static int findEndOfLine(String str, int from) {
		int index = str.indexOf('\n', from);
		return index < 0 ? str.length() : index;
	}

	public void handlecr() {
		String text = this.getText();
		int sol = JConsole.findStartOfLine(text, this.getCaretPosition());
		int eol = JConsole.findEndOfLine(text, sol);
		if (eol == text.length()) {
			this.append("\n");
		}

		this.setCaretPosition(eol + 1);
		/*
		if (!this.prefix.equals("")) {
			this.runSilent(this.prefix + text.substring(sol, eol) + this.suffix);
		} else {
		*/
		this.runLine(text.substring(sol, eol));
		//}
	}

	public void runLine(String line) {
		(new Thread(new LogoCommandRunner(line, this.lc))).start();
	}

	public void runSilent(String line) {
		(new Thread(new LogoCommandRunner(line, this.lc, true))).start();
	}
}
