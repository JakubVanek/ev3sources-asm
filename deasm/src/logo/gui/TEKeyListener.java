package logo.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class TEKeyListener extends KeyAdapter {
	public void keyPressed(KeyEvent event) {
		JConsole console = (JConsole) event.getComponent();
		char ch = event.getKeyChar();
		int code = event.getKeyCode();
		if (ch == '\n' && code == KeyEvent.VK_ENTER) {
			console.handlecr();
			event.consume();
		} else if (ch == 1) {
			console.selectAll();
			event.consume();
		} else if (ch == 18) {
			console.runLine("reload startup");
			event.consume();
		} else if (ch == 27) {
			console.lc.stop_requested = true;
			event.consume();
		}
	}
}
