
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import gov.va.demo.dom.PncsLegoMapGenerator;
import gov.va.demo.nb.sim.jpa.Documents;
import gov.va.med.cds.soap.client.ClinicalDataServiceSynchronousInterfacePortTypeProxy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axis.AxisFault;
import org.ihtsdo.cc.termstore.PersistentStoreI;
import org.ihtsdo.tk.Ts;
import org.w3c.dom.Document;

/**
 *
 * @author kec
 */
@Path("/smartform")
public class SmartFormResource {
   private static final String LOCAL_HOST_WS_ADDRESS          =
      "http://localhost:8088/cds-wsclient/cds-service/";
   private static final String SMART_FORMS_CREATE_TEMPLATE_ID = "SmartFormCreate1";
   private static String       WS_ADDRESS                     =
      "http://vahdrdvwls02.aac.va.gov:7241/cds-wsclient/cds-service";

   private static final Logger logger                         =
      Logger.getLogger(SmartFormResource.class.getName());

   //~--- static initializers -------------------------------------------------

   static {
      if (System.getProperty("SmartFormResource.WS_ADDRESS") == null) {
         System.setProperty("SmartFormResource.WS_ADDRESS", WS_ADDRESS);
         
      }
   }

   //~--- fields --------------------------------------------------------------

   @Context
   PersistentStoreI ts;
   @Context
   UriInfo          uriInfo;

   //~--- methods -------------------------------------------------------------

   @Path("lego/{filename}")
   @PUT
   @Consumes(MediaType.TEXT_PLAIN)
   public Response putLegoDocument(String str, @PathParam("filename") String filename)
           throws IOException, ClassNotFoundException, Exception {
      DocumentBuilderFactory factory      = DocumentBuilderFactory.newInstance();
      DocumentBuilder        builder      = factory.newDocumentBuilder();
      Document               mapDoc       = builder.parse(new ByteArrayInputStream(str.getBytes()));
      PncsLegoMapGenerator   mapGenerator = new PncsLegoMapGenerator(filename);

      mapGenerator.addToMap(mapDoc, Ts.getGlobalSnapshot());

      return Response.created(uriInfo.getAbsolutePath()).build();
   }

   @Path("hdr")
   @PUT
   @Consumes(MediaType.TEXT_PLAIN)
   public Response putSmartDocumentHdr(String str) throws IOException, ClassNotFoundException, Exception {
      ProcessPncsDoc processor = new ProcessPncsDoc();
      Documents      doc       = processor.process(str);

      if (doc != null) {
            String                                               address =
               System.getProperty("SmartFormResource.WS_ADDRESS");
         try {

            // Populate the template XML string with the create template identifier,
            // the smart forms document id, a unique patient identifier (for testing),
            // and a valid assigning facility.
            HdrDocument hdrDocument = new HdrDocument(doc);

            // Create the CDS service proxy object that abstracts the remoting mechanism.
            ClinicalDataServiceSynchronousInterfacePortTypeProxy proxy   =
               new ClinicalDataServiceSynchronousInterfacePortTypeProxy(address);

            // This makes the remote call to CDS. The parameters are as follows.
            // 1) The create template XML document.
            // 2) The craete template identifier. For CDS 3.3 the only supported Smart Forms create template
            // is 'SmartFormCreate1'.
            // 3) The request identifier. A unique identifier that can be used to track the request.
            String stringToSend = hdrDocument.toString();

            logger.log(Level.INFO, "Sending document: \n{0} \n\n to address: {1}",
                       new Object[] { stringToSend,
                                      address });

            String response = proxy.createClinicalData(stringToSend, SMART_FORMS_CREATE_TEMPLATE_ID,
                                 UUID.randomUUID().toString());

            logger.log(Level.INFO, "CDS Create Response: {0}", response);
            if (response.contains("fatalErrors")) {
                return Response.notModified("Error sending to HDR: " + address + " Fault:\n"+ response).build();
            }
         } catch (AxisFault e) {
             logger.log(Level.SEVERE, "Error sending to HDR: " + address + " Fault: "+ e.getFaultString(), e);
             
             return Response.notModified("Error sending to HDR: " + address + " Fault: "+ e.getFaultString()).build();
         } catch (Exception e) {
             logger.log(Level.SEVERE, "Error sending to HDR", e);

            return Response.notAcceptable(new ArrayList<Variant>()).build();
         }
      }

      return Response.created(uriInfo.getAbsolutePath()).build();
   }

   @Path("sim")
   @PUT
   @Consumes(MediaType.TEXT_PLAIN)
   public Response putSmartDocumentSim(String str) throws IOException, ClassNotFoundException, Exception {
      ProcessPncsDoc processor = new ProcessPncsDoc();
      Documents      doc       = processor.process(str);

      if (doc != null) {
         HdrDocument hdrDocument = new HdrDocument(doc);

         System.out.println(hdrDocument.toString());
      }

      return Response.created(uriInfo.getAbsolutePath()).build();
   }

   @Path("test")
   @PUT
   @Consumes(MediaType.TEXT_PLAIN)
   public Response putSmartDocumentTest(String str) throws IOException, ClassNotFoundException, Exception {
      long time = System.currentTimeMillis();

      System.out.println("Got smart doc @: " + time + " (" + new Date(time).toGMTString() + ")");

      File testDir = new File("test");

      testDir.mkdirs();

      File outputFile = new File(testDir, "Test-" + time + ".xml");

      try (FileWriter fw = new FileWriter(outputFile)) {
         fw.append(str);
      }

      return Response.created(uriInfo.getAbsolutePath()).build();
   }
}
