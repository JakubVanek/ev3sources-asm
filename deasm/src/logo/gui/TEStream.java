package logo.gui;

import javax.swing.*;
import java.io.OutputStream;

class TEStream extends OutputStream {
	private final JTextArea textArea;
	private StringBuilder buffer = new StringBuilder();

	TEStream(JTextArea area) {
		this.textArea = area;
	}

	public void write(int ch) {
		if (ch == '\n') {
			buffer.append('\n');
		} else {
			if (ch == '\r') {
				return;
			}
			buffer.append((char) ch);
		}

	}

	public void flush() {
		int caret = textArea.getCaretPosition();
		textArea.insert(buffer.toString(), caret);
		textArea.setCaretPosition(caret + buffer.length());
		buffer.setLength(0);
	}
}
