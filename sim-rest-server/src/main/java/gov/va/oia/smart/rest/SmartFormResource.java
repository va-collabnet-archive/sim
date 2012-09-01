/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.ihtsdo.cc.termstore.PersistentStoreI;

/**
 *
 * @author kec
 */
@Path("/smartform")
public class SmartFormResource {

    @Context
    PersistentStoreI ts;
    @Context
    UriInfo uriInfo;

    @Path("")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response putSmartDocument(String str)
            throws IOException, ClassNotFoundException, Exception {
        ProcessPncsDoc processor = new ProcessPncsDoc();
        processor.process(str);

        return Response.created(uriInfo.getAbsolutePath()).build();
    }
    @Path("test")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response putSmartDocumentTest(String str)
            throws IOException, ClassNotFoundException, Exception {
        long time = System.currentTimeMillis();
        System.out.println("Got smart doc @: " + time + " (" +new Date(time).toGMTString() + ")");
        File testDir = new File("test");
        testDir.mkdirs();
        File outputFile = new File(testDir, "Test-" + time + ".xml");
        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.append(str);
        }
        return Response.created(uriInfo.getAbsolutePath()).build();
    }
}
