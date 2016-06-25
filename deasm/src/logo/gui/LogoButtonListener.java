package logo.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LogoButtonListener implements ActionListener {
   private JConsole cc;

   LogoButtonListener(JConsole var1) {
      this.cc = var1;
   }

   public void actionPerformed(ActionEvent event) {
      String line = ((JButton)event.getSource()).getName();
      this.cc.runSilent(line);
   }
}
