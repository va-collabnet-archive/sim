
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import gov.va.demo.nb.sim.jpa.Assertions;
import gov.va.demo.nb.sim.jpa.Documents;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author kec
 */
public class HdrDocument {
   private static final String CLIENT_NAME = "SIM Test Server";

   /**
    * Do we really need two different date formats?
    */
   private static final String DATE_FORMAT_PATTERN  = "yyyy-MM-dd'T'HH:mm:ssZ";
   private static final String DATE_FORMAT_PATTERN2 = "yyyyMMddHHmmssZ";

   //~--- fields --------------------------------------------------------------

   String hdrDocString;

   //~--- constructors --------------------------------------------------------

   public HdrDocument(Documents doc) {
      SimpleDateFormat dateFormatter  = new SimpleDateFormat(DATE_FORMAT_PATTERN);
      SimpleDateFormat dateFormatter2 = new SimpleDateFormat(DATE_FORMAT_PATTERN2);
      Date             docStart       = doc.getInid().getIstart();
      Date             docEnd         = doc.getInid().getIend();
      StringBuilder    sb             = new StringBuilder();

      //J-
sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
sb.append("<clinicaldata:ClinicalData xsi:schemaLocation=\"Clinicaldata SmartFormCreate1.xsd\"\n");
sb.append("  xmlns:clinicaldata=\"Clinicaldata\" \n"
        + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
sb.append("  <templateId>SmartFormCreate1</templateId>\n");
sb.append("  <clientName>"+CLIENT_NAME+"</clientName>\n");
sb.append("  <clientRequestInitiationTime>").append(dateFormatter.format(new Date())).append("</clientRequestInitiationTime>\n");
sb.append("    <patient>\n");
sb.append("      <smartFormDocuments>\n");
sb.append("        <smartFormDocument>\n");
sb.append("          <documentNativeId>").append(doc.getDnid()).append("</documentNativeId>\n");
sb.append("          <documentUUID>").append(doc.getDuuid()).append("</documentUUID>\n");
sb.append("          <documentStartDate>\n");
sb.append("            <literal>").append(dateFormatter2.format(doc.getInid().getIstart())).append("</literal>\n");
sb.append("          </documentStartDate>\n");
sb.append("          <documentEndDate>\n");
sb.append("            <literal>").append(dateFormatter2.format(doc.getInid().getIend())).append("</literal>\n");
sb.append("          </documentEndDate>\n");
sb.append("          <patient>\n");
sb.append("            <identifier>\n");
sb.append("              <identity>").append(doc.getPatientnid().getIdentity()).append("</identity>\n");
sb.append("              <assigningFacility>").append(doc.getPatientnid().getIdAssigningFacility()).append("</assigningFacility>\n");
sb.append("              <assigningAuthority>").append(doc.getPatientnid().getIdAssigningAuthority()).append("</assigningAuthority>\n");
sb.append("            </identifier>\n");
sb.append("            <name>\n");
sb.append("              <prefix>").append(doc.getPatientnid().getPrefix()).append("</prefix>\n");
sb.append("              <given>").append(doc.getPatientnid().getGivenName()).append("</given>\n");
sb.append("              <middle>").append(doc.getPatientnid().getMiddleName()).append("</middle>\n");
sb.append("              <family>").append(doc.getPatientnid().getFamilyName()).append("</family>\n");
sb.append("              <suffix>").append(doc.getPatientnid().getSuffix()).append("</suffix>\n");
sb.append("              <title>").append(doc.getPatientnid().getTitle()).append("</title>\n");
sb.append("            </name>\n");
sb.append("          </patient>\n");
sb.append("          <provider>\n");
sb.append("            <identifier>\n");
sb.append("              <identity>").append(doc.getProvidernid().getIdentity()).append("</identity>\n");
sb.append("              <assigningFacility>").append(doc.getProvidernid().getIdAssigningFacility()).append("</assigningFacility>\n");
sb.append("              <assigningAuthority>").append(doc.getProvidernid().getIdAssigningAuthority()).append("</assigningAuthority>\n");
sb.append("            </identifier>\n");
sb.append("            <name>\n");
sb.append("              <prefix>").append(doc.getProvidernid().getPrefix()).append("</prefix>\n");
sb.append("              <given>").append(doc.getProvidernid().getGivenName()).append("</given>\n");
sb.append("              <middle>").append(doc.getProvidernid().getMiddleName()).append("</middle>\n");
sb.append("              <family>").append(doc.getProvidernid().getFamilyName()).append("</family>\n");
sb.append("              <suffix>").append(doc.getProvidernid().getSuffix()).append("</suffix>\n");
sb.append("              <title>").append(doc.getProvidernid().getTitle()).append("</title>\n");
sb.append("            </name>\n");
sb.append("          </provider>\n");
sb.append("          <facility>\n");
sb.append("            <identity>").append(doc.getInstitutionNumber()).append("</identity>\n");
sb.append("            <name>").append(doc.getInstitutionName()).append("</name>\n");
sb.append("            <assigningAuthority>USVHA</assigningAuthority>\n");
sb.append("          </facility>\n");
sb.append("          <assertions>\n");
//
for (Assertions assertion: doc.getAssertionsCollection()) {
sb.append("            <assertion>\n");
sb.append("               <assertionUUID>").append(assertion.getAuuid()).append("</assertionUUID>\n");
sb.append("               <seqInDoc>").append(assertion.getSeqInDoc()).append("</seqInDoc>\n");
sb.append("               <assertionUUID>").append(assertion.getAuuid()).append("</assertionUUID>\n");
sb.append("                 <assertionStartDate>\n");
sb.append("                   <literal>").append(dateFormatter2.format(assertion.getInid().getIstart())).append("</literal>\n");
sb.append("                 </assertionStartDate>\n");
sb.append("                 <assertionEndDate>\n");
sb.append("                   <literal>").append(dateFormatter2.format(assertion.getInid().getIend())).append("</literal>\n");
sb.append("                 </assertionEndDate>\n");
sb.append("               <sectionENID>").append(assertion.getSectionEnid().getEnid()).append("</sectionENID>\n");
sb.append("               <discernibleENID>").append(assertion.getDiscernableEnid().getEnid()).append("</discernibleENID>\n");
sb.append("               <qualifierENID>").append(assertion.getQualifierEnid().getEnid()).append("</qualifierENID>\n");
sb.append("            </assertion>\n");
}
//
sb.append("          </assertions>\n");
sb.append("        </smartFormDocument>\n");
sb.append("      </smartFormDocuments>\n");
sb.append("    </patient>\n");
sb.append("</clinicaldata:ClinicalData>\n");
        //J+

      hdrDocString = sb.toString().replace(">null<", "><");
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public String toString() {
      return hdrDocString;
   }
}
