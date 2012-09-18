/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import gov.va.demo.nb.sim.jpa.Documents;

/**
 *
 * @author kec
 */
public class HdrRetrieval {
    String hdrRetrievalString;
    
    public HdrRetrieval(Documents doc) {
        
      StringBuilder    sb             = new StringBuilder();
      //J-
sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
sb.append("<filter:filter vhimVersion=\"Vhim_4_00\"");
sb.append("    xsi:schemaLocation=\"Filter Smart_Form_Single_Patient_All_Data_Filter.xsd\" xmlns:filter=\"Filter\"");
sb.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
sb.append("    <filterId>SMART_FORM_SINGLE_PATIENT_ALL_DATA_FILTER</filterId>");
sb.append("    <clientName>PNCS</clientName>");
sb.append("    <clientRequestInitiationTime>2001-12-17T09:30:47Z</clientRequestInitiationTime>");
sb.append("    <patients>");
sb.append("        <resolvedIdentifiers>");
sb.append("            <assigningAuthority>USVHA</assigningAuthority>");
sb.append("            <assigningFacility>552</assigningFacility>");
sb.append("            <identity>").append(doc.getPatientnid().getPInternalEntryNumber()).append("</identity>");
sb.append("        </resolvedIdentifiers>");
sb.append("    </patients>");
sb.append("    <entryPointFilter queryName=\"ID_1\">");
sb.append("        <domainEntryPoint>SmartFormDocument</domainEntryPoint>");
sb.append("        <startDate>2012-07-01</startDate>");
sb.append("        <endDate>2013-12-31</endDate>");
sb.append("        <queryTimeoutSeconds>60</queryTimeoutSeconds>");
sb.append("        <otherQueryParameters>");
sb.append("            <documentNativeId>").append(doc.getDnid()).append("</documentNativeId>");
sb.append("        </otherQueryParameters>");
sb.append("    </entryPointFilter>");
sb.append("</filter:filter>");
        //J+
hdrRetrievalString = sb.toString();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
