
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import gov.va.demo.nb.sim.jpa.Assertions;
import gov.va.demo.nb.sim.jpa.Documents;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author kec
 */
public class HdrDocument {
   private static final String CLIENT_NAME = "PNCS";

   /**
    * Do we really need two different date formats?
    */
   private static final String DATE_FORMAT_PATTERN  = "yyyy-MM-dd'T'HH:mm:ssZZ";
   private static final String DATE_FORMAT_PATTERN2 = "yyyyMMddHHmmssZ";

   //~--- fields --------------------------------------------------------------

   String hdrDocString;

   //~--- constructors --------------------------------------------------------

   public HdrDocument(Documents doc) {
      //SimpleDateFormat dateFormatter  = new SimpleDateFormat(DATE_FORMAT_PATTERN);
      SimpleDateFormat dateFormatter2 = new SimpleDateFormat(DATE_FORMAT_PATTERN2);
      DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN);
      Date             docStart       = doc.getInid().getIstart();
      Date             docEnd         = doc.getInid().getIend();
      StringBuilder    sb             = new StringBuilder();

      //J-
//sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
sb.append("<clinicaldata:ClinicalData xsi:schemaLocation=\"Clinicaldata SmartFormCreate1.xsd\"");
sb.append("  xmlns:clinicaldata=\"Clinicaldata\" "
        + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
sb.append("<templateId>SmartFormCreate1</templateId>");
sb.append("<clientName>"+CLIENT_NAME+"</clientName>");
sb.append("<clientRequestInitiationTime>").append(dtf.print(System.currentTimeMillis())).append("</clientRequestInitiationTime>");
sb.append("<patient>");
sb.append("<smartFormDocuments>");
sb.append("<smartFormDocument>");
sb.append("<documentNativeId>").append(doc.getDnid()).append("</documentNativeId>");
sb.append("<documentUUID>").append(doc.getDuuid()).append("</documentUUID>");
sb.append("<documentStartDate>");
sb.append("<literal>").append(dateFormatter2.format(doc.getInid().getIstart())).append("</literal>");
sb.append("</documentStartDate>");
sb.append("<documentEndDate>");
sb.append("<literal>").append(dateFormatter2.format(doc.getInid().getIend())).append("</literal>");
sb.append("</documentEndDate>");
sb.append("<patient>");
sb.append("<identifier>");
sb.append("<identity>").append(doc.getPatientnid().getpInternalEntryNumber()).append("</identity>");
sb.append("<assigningFacility>").append(doc.getPatientnid().getIdAssigningFacility()).append("</assigningFacility>");
sb.append("<assigningAuthority>").append(doc.getPatientnid().getIdAssigningAuthority()).append("</assigningAuthority>");
sb.append("</identifier>");
sb.append("<name>");
sb.append("<prefix>").append(doc.getPatientnid().getPrefix()).append("</prefix>");
sb.append("<given>").append(doc.getPatientnid().getGivenName()).append("</given>");
sb.append("<middle>").append(doc.getPatientnid().getMiddleName()).append("</middle>");
sb.append("<family>").append(doc.getPatientnid().getFamilyName()).append("</family>");
sb.append("<suffix>").append(doc.getPatientnid().getSuffix()).append("</suffix>");
sb.append("<title>").append(doc.getPatientnid().getTitle()).append("</title>");
sb.append("</name>");
sb.append("</patient>");
sb.append("<provider>");
sb.append("<identifier>");
sb.append("<identity>").append(doc.getProvidernid().getPuuid()).append("</identity>");
sb.append("<assigningFacility>").append(doc.getProvidernid().getIdAssigningFacility()).append("</assigningFacility>");
sb.append("<assigningAuthority>").append(doc.getProvidernid().getIdAssigningAuthority()).append("</assigningAuthority>");
sb.append("</identifier>");
sb.append("<name>");
sb.append("<prefix>").append(doc.getProvidernid().getPrefix()).append("</prefix>");
sb.append("<given>").append(doc.getProvidernid().getGivenName()).append("</given>");
sb.append("<middle>").append(doc.getProvidernid().getMiddleName()).append("</middle>");
sb.append("<family>").append(doc.getProvidernid().getFamilyName()).append("</family>");
sb.append("<suffix>").append(doc.getProvidernid().getSuffix()).append("</suffix>");
sb.append("<title>").append(doc.getProvidernid().getTitle()).append("</title>");
sb.append("</name>");
sb.append("</provider>");
sb.append("<facility>");
//sb.append("<identity>").append(doc.getInstitutionNumber()).append("</identity>");
sb.append("<identity>").append(UUID.randomUUID()).append("</identity>");
sb.append("<name>").append(doc.getInstitutionName()).append("</name>");
sb.append("<assigningAuthority>USVHA</assigningAuthority>");
sb.append("</facility>");
sb.append("<assertions>");
//
for (Assertions assertion: doc.getAssertionsCollection()) {
sb.append("<assertion>");
sb.append("<assertionUUID>").append(assertion.getAuuid()).append("</assertionUUID>");
sb.append("<seqInDoc>").append(assertion.getSeqInDoc()).append("</seqInDoc>");
sb.append("<assertionStartDate>");
sb.append("<literal>").append(dateFormatter2.format(assertion.getInid().getIstart())).append("</literal>");
sb.append("</assertionStartDate>");
sb.append("<assertionEndDate>");
sb.append("<literal>").append(dateFormatter2.format(assertion.getInid().getIend())).append("</literal>");
sb.append("</assertionEndDate>");
sb.append("<sectionENID>").append(assertion.getSectionEnid().getEnid()).append("</sectionENID>");
sb.append("<discernibleENID>").append(assertion.getDiscernableEnid().getEnid()).append("</discernibleENID>");
sb.append("<qualifierENID>").append(assertion.getQualifierEnid().getEnid()).append("</qualifierENID>");
sb.append("<valueENID>").append(assertion.getValueEnid().getEnid()).append("</valueENID>");
sb.append("</assertion>");
}
//
sb.append("</assertions>");
sb.append("</smartFormDocument>");
sb.append("</smartFormDocuments>");
sb.append("</patient>");
sb.append("</clinicaldata:ClinicalData>");
        //J+

      hdrDocString = sb.toString().replace(">null<", "><");
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public String toString() {
      return hdrDocString;
   }
}
