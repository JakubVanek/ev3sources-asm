package logo.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class ToplevelWindowListener extends WindowAdapter {
   public void windowClosing(WindowEvent event) {
      System.exit(0);
   }
}
