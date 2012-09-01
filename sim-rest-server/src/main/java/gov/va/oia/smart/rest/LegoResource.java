/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import gov.va.demo.dom.PncsLegoMapGenerator;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.ihtsdo.cc.termstore.PersistentStoreI;
import org.ihtsdo.tk.Ts;
import org.w3c.dom.Document;

/**
 *
 * @author kec
 */
@Path("/lego")
public class LegoResource {

    @Context
    PersistentStoreI ts;
    @Context
    UriInfo uriInfo;

    @Path("")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response putLegoDocument(String str)
            throws IOException, ClassNotFoundException, Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document mapDoc = builder.parse(new ByteArrayInputStream(str.getBytes()));
        PncsLegoMapGenerator mapGenerator = new PncsLegoMapGenerator();

        mapGenerator.addToMap(mapDoc, Ts.getGlobalSnapshot());

        return Response.created(uriInfo.getAbsolutePath()).build();
    }
}
