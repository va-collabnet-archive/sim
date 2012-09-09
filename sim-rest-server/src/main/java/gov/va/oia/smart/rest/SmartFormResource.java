
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
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
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
import org.ihtsdo.cc.termstore.PersistentStoreI;
import org.ihtsdo.tk.Ts;
import org.w3c.dom.Document;

/**
 *
 * @author kec
 */
@Path("/smartform")
public class SmartFormResource {
   private static final String SMART_FORMS_CREATE_TEMPLATE_ID = "SmartFormCreate1";
   private static final String LOCAL_HOST_WS_ADDRESS = "http://localhost:8088/cds-wsclient/cds-service/";
   private static String WS_ADDRESS                     =
       //     "http://vahdrdvwls02.aac.va.gov:7241/cds-wsclient/cds-service";
      "http://localhost:8088/cds-wsclient/cds-service/";
   static {
       if (System.getProperty("SmartFormResource.WS_ADDRESS") == null) {
           System.setProperty("SmartFormResource.WS_ADDRESS", LOCAL_HOST_WS_ADDRESS);
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
   public Response putLegoDocument(String str, @PathParam("filename") String filename) throws IOException, ClassNotFoundException, Exception {
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
         try {

            // Populate the template XML string with the create template identifier,
            // the smart forms document id, a unique patient identifier (for testing),
            // and a valid assigning facility.
            HdrDocument hdrDocument = new HdrDocument(doc);
            
            String testString = "<clinicaldata:ClinicalData xsi:schemaLocation=\"Clinicaldata SmartFormCreate1.xsd\" xmlns:clinicaldata=\"Clinicaldata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><templateId>SmartFormCreate1</templateId><clientName>PNCS</clientName><clientRequestInitiationTime>2012-07-25T13:26:32-06:00</clientRequestInitiationTime><patient><smartFormDocuments><smartFormDocument><documentNativeId>1</documentNativeId><documentUUID>8a496169-56ad-40eb-9b08-fab32759e44a</documentUUID><documentStartDate><literal>20120704162517-0600</literal></documentStartDate><documentEndDate><literal>20120705162517-0600</literal></documentEndDate><patient><identifier><identity>8f753050-6e79-4ec2-a1de-93da00950e97</identity><assigningFacility>555</assigningFacility><assigningAuthority>USVHA</assigningAuthority></identifier><name><prefix>PATIENT_PREFIX</prefix><given>PATIENT_GIVEN</given><middle>PATIENT_MIDDLE</middle><family>PATIENT_FAMILY</family><suffix>PATIENT_SUFFIX</suffix><title>PATIENT_TITLE</title></name></patient><provider><identifier><identity>ed2e69bf-b74b-4044-bf05-4df7f3d9faa3</identity><assigningFacility>200M</assigningFacility><assigningAuthority>USVHA</assigningAuthority></identifier><name><prefix>PROVIDER_PREFIX</prefix><given>PROVIDER_GIVEN</given><middle>PROVIDER_MIDDLE</middle><family>PROVIDER_FAMILY</family><suffix>PROVIDER_SUFFIX</suffix><title>PROVIDER_TITLE</title></name></provider><facility><identity>2292e5a7-08bf-4ed1-8708-fe4fe3c3d97b</identity><name>DUMMY_FACILITY</name><assigningAuthority>USVHA</assigningAuthority></facility><assertions><assertion><assertionUUID>54c95f25-8c5d-496e-ac0c-a0553aaf4922</assertionUUID><seqInDoc>0</seqInDoc><assertionStartDate><literal>20120704162517-0600</literal></assertionStartDate><assertionEndDate><literal>20120706162517-0600</literal></assertionEndDate><sectionENID>2</sectionENID><discernibleENID>3</discernibleENID><qualifierENID>4</qualifierENID><valueENID>5</valueENID></assertion></assertions></smartFormDocument></smartFormDocuments></patient></clinicaldata:ClinicalData>";
            String test2String= "<clinicaldata:ClinicalData xsi:schemaLocation=\"Clinicaldata SmartFormCreate1.xsd\" xmlns:clinicaldata=\"Clinicaldata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><templateId>SmartFormCreate1</templateId><clientName>PNCS</clientName><clientRequestInitiationTime>2012-09-03T19:33:37-07:00</clientRequestInitiationTime><patient><smartFormDocuments><smartFormDocument><documentNativeId>1</documentNativeId><documentUUID>fe9a260a-d87d-517c-b973-4e5f099a2e7c</documentUUID><documentStartDate><literal>20120703000000-0700</literal></documentStartDate><documentEndDate><literal>20120703000000-0700</literal></documentEndDate><patient><identifier><identity>c9f2ab97-4099-5db8-97d8-6d747c7463db</identity><assigningFacility>555</assigningFacility><assigningAuthority>USVHA</assigningAuthority></identifier><name><prefix></prefix><given>PATIENT_GIVEN</given><middle>PATIENT_MIDDLE</middle><family>PATIENT_FAMILY</family><suffix>PATIENT_SUFFIX</suffix><title>PATIENT_TITLE</title></name></patient><provider><identifier><identity>15486763-c187-59de-bbaf-090b01acad26</identity><assigningFacility>200M</assigningFacility><assigningAuthority>USVHA</assigningAuthority></identifier><name><prefix>PROVIDER_PREFIX</prefix><given>PROVIDER_GIVEN</given><middle>PROVIDER_MIDDLE</middle><family>PROVIDER_FAMILY</family><suffix>PROVIDER_SUFFIX</suffix><title>PROVIDER_TITLE</title></name></provider><facility><identity>3b9435d3-d822-46d7-80e1-bde37d822037</identity><name>DUMMY_FACILITY</name><assigningAuthority>USVHA</assigningAuthority></facility><assertions><assertion><assertionUUID>7c0afcc0-61de-580b-b99c-f7c88add15d7</assertionUUID><seqInDoc>15</seqInDoc><assertionStartDate><literal>20120703000000-0700</literal></assertionStartDate><assertionEndDate><literal>20120703000000-0700</literal></assertionEndDate><sectionENID>28</sectionENID><discernibleENID>73</discernibleENID><qualifierENID>22</qualifierENID><valueENID>3</valueENID></assertion><assertion><assertionUUID>58e83b1e-c311-5b8d-8373-51c5c24bf406</assertionUUID><seqInDoc>21</seqInDoc><assertionStartDate><literal>20120703000000-0700</literal></assertionStartDate><assertionEndDate><literal>20120703000000-0700</literal></assertionEndDate><sectionENID>28</sectionENID><discernibleENID>75</discernibleENID><qualifierENID>22</qualifierENID><valueENID>3</valueENID></assertion></assertions></smartFormDocument></smartFormDocuments></patient></clinicaldata:ClinicalData>";
            // Create the CDS service proxy object that abstracts the remoting mechanism.
            ClinicalDataServiceSynchronousInterfacePortTypeProxy proxy =
               new ClinicalDataServiceSynchronousInterfacePortTypeProxy(System.getProperty("SmartFormResource.WS_ADDRESS"));

            // This makes the remote call to CDS. The parameters are as follows.
            // 1) The create template XML document.
            // 2) The craete template identifier. For CDS 3.3 the only supported Smart Forms create template
            // is 'SmartFormCreate1'.
            // 3) The request identifier. A unique identifier that can be used to track the request.
            String stringToSend = hdrDocument.toString();
            System.out.println("Sending document: \n" + stringToSend);
            String response = proxy.createClinicalData(stringToSend,
                                 SMART_FORMS_CREATE_TEMPLATE_ID, UUID.randomUUID().toString());

            System.out.println("CDS Create Response: " + response);
         } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();

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
