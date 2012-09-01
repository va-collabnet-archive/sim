package gov.va.ohi.sim.client;

import java.util.UUID;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ihtsdo.fxmodel.concept.FxConcept;
import org.ihtsdo.fxmodel.fetchpolicy.RefexPolicy;
import org.ihtsdo.fxmodel.fetchpolicy.RelationshipPolicy;
import org.ihtsdo.fxmodel.fetchpolicy.VersionPolicy;
import org.ihtsdo.tk.binding.Taxonomies;
import org.ihtsdo.tk.rest.RestClient;

/**
 * Hello world!
 *
 */
public class App extends Application {
   public static FxConcept fxc;
   public static Stage     stage;

   //~--- methods -------------------------------------------------------------

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) throws Exception {
      App.stage = stage;
      System.out.println("javafx.version: " + System.getProperty("javafx.version"));
      System.out.println("javafx.runtime.version: " + System.getProperty("javafx.runtime.version"));
      System.out.println("java.runtime.version: " + System.getProperty("java.runtime.version"));
      RestClient.setup(RestClient.defaultLocalHostSvr);
      fxc = RestClient.getRestClient().getFxConcept(Taxonomies.SNOMED.getUuids()[0],
              UUID.fromString("d0a05080-b5de-11e1-afa6-0800200c9a66"));
      fxc = RestClient.getRestClient().getFxConcept(Taxonomies.SNOMED.getUuids()[0],
              UUID.fromString("d0a05080-b5de-11e1-afa6-0800200c9a66"), VersionPolicy.ACTIVE_VERSIONS,
              RefexPolicy.REFEX_MEMBERS,
              RelationshipPolicy.ORIGINATING_AND_DESTINATION_TAXONOMY_RELATIONSHIPS);

      Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));

      stage.setScene(new Scene(root));
      stage.show();
   }
}
