
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import gov.va.demo.nb.sim.jpa.Documents;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author kec
 */
public class HdrRetrieval {
   String hdrRetrievalString;

   //~--- constructors --------------------------------------------------------

   public HdrRetrieval(Documents doc) {
      StringBuilder sb = new StringBuilder();
         DateTimeFormatter dtf = DateTimeFormat.forPattern(HdrDocument.DATE_FORMAT_PATTERN);
   
      //J-
sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
sb.append("<filter:filter vhimVersion=\"Vhim_4_00\"");
sb.append("xsi:schemaLocation=\"Filter Smart_Form_Single_Patient_All_Data_Filter.xsd\" xmlns:filter=\"Filter\"");
sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
sb.append("<filterId>SMART_FORM_SINGLE_PATIENT_ALL_DATA_FILTER</filterId>");
sb.append("<clientName>PNCS</clientName>");
sb.append("<clientRequestInitiationTime>").append(dtf.print(System.currentTimeMillis())).append("</clientRequestInitiationTime>");
sb.append("<patients>");
sb.append("<resolvedIdentifiers>");
sb.append("<assigningAuthority>USVHA</assigningAuthority>");
sb.append("<assigningFacility>552</assigningFacility>");
sb.append("<identity>").append(doc.getPatientnid().getPInternalEntryNumber()).append("</identity>");
sb.append("</resolvedIdentifiers>");
sb.append("</patients>");
sb.append("<entryPointFilter queryName=\"ID_1\">");
sb.append("<domainEntryPoint>SmartFormDocument</domainEntryPoint>");
sb.append("<startDate>2012-07-01</startDate>");
sb.append("<endDate>2013-12-31</endDate>");
sb.append("<queryTimeoutSeconds>60</queryTimeoutSeconds>");
sb.append("<otherQueryParameters>");
sb.append("<documentNativeId>").append(doc.getDnid()).append("</documentNativeId>");
sb.append("</otherQueryParameters>");
sb.append("</entryPointFilter>");
sb.append("</filter:filter>");
        //J+
      hdrRetrievalString = sb.toString();
   }

   //~--- methods -------------------------------------------------------------

   private static void parse(InputStream stream)
           throws SAXException, ParserConfigurationException, IOException {
      SAXParserFactory.newInstance().newSAXParser().parse(stream, new DefaultHandler());
   }

   @Override
   public String toString() {
      return super.toString();
   }

   public boolean validate() {
      boolean  valid     = true;
        try {
            parse(new ByteArrayInputStream(hdrRetrievalString.getBytes()));
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            Logger.getLogger(HdrRetrieval.class.getName()).log(Level.SEVERE, null, ex);
        }
      
      
      String[] encodings = { "UTF-8", "UTF-16", "ISO-8859-1" };

      for (String actual : encodings) {
         for (String declared : encodings) {
            if (!actual.equals(declared)) {
               valid = false;

               String xml     = "<?xml version='1.0' encoding='" + declared + "'?><x/>";
               byte[] encoded = xml.getBytes(Charset.forName(actual));

               try {
                  parse(new ByteArrayInputStream(encoded));
                  System.out.println("HIDDEN ERROR! actual:" + actual + " " + xml);
               } catch (SAXException | ParserConfigurationException | IOException e) {
                  System.out.println(e.getMessage() + " actual:" + actual + " xml:" + xml);
               }
            }
         }
      }

      return valid;
   }
}
