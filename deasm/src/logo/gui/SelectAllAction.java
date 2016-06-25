package logo.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

class SelectAllAction extends AbstractAction implements Action {
	public void actionPerformed(ActionEvent event) {
		JTextArea area = (JTextArea) event.getSource();
		area.selectAll();
	}
}
