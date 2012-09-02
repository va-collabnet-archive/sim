
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.ohi.sim.client.event.handlers;

import gov.va.ohi.sim.client.App;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author kec
 */
public class UploadLegoEventHandler implements EventHandler<ActionEvent> {
   Node ownerNode;

   //~--- constructors --------------------------------------------------------

   public UploadLegoEventHandler(Node ownerNode) {
      this.ownerNode = ownerNode;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void handle(ActionEvent t) {
      DirectoryChooser chooser = new DirectoryChooser();

      chooser.setTitle("LEGO Folder");

      File defaultDirectory = new File(System.getProperty("user.home"));

      chooser.setInitialDirectory(defaultDirectory);

      File selectedDirectory = chooser.showDialog(App.stage);

      if (selectedDirectory != null) {
         System.out.println("Selected file: " + selectedDirectory.getAbsolutePath());
      } else {
         System.out.println("No selected file. ");
      }
   }
}
