/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.oia.smart.rest;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import org.ihtsdo.cc.termstore.PersistentStoreI;
import org.ihtsdo.tk.rest.client.RestClient;

/**
 *
 * @author kec
 */
@Provider
public class TerminologyRestClientProvider extends SingletonTypeInjectableProvider<Context, PersistentStoreI> {
       //~--- constructors --------------------------------------------------------

   public TerminologyRestClientProvider() {
      super(PersistentStoreI.class, RestClient.getRestClient());
   }

   //~--- methods -------------------------------------------------------------

   @PreDestroy
   public void close() throws Exception {
      // noting to do...
   }

}
