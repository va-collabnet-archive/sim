
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
import javafx.stage.FileChooser;
import org.ihtsdo.tk.rest.RestClient;

/**
 *
 * @author kec
 */
public class UploadSmartEncounterEventHandler implements EventHandler<ActionEvent> {
   Node ownerNode;

   //~--- constructors --------------------------------------------------------

   public UploadSmartEncounterEventHandler(Node ownerNode) {
      this.ownerNode = ownerNode;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void handle(ActionEvent t) {
      FileChooser chooser = new FileChooser();

      chooser.setInitialDirectory(new File(System.getProperty("user.home")));
      chooser.setTitle("Select SMART Encounter...");

      File selectedFile = chooser.showOpenDialog(App.stage);

      if (selectedFile != null) {
         System.out.println("Selected directory: " + selectedFile.getAbsolutePath());
         
      } else {
         System.out.println("No selected directory. ");
      }
   }
}
