/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ohi.sim.fx.concept;

import java.util.UUID;
import javafx.concurrent.Task;
import org.ihtsdo.fxmodel.concept.FxConcept;
import org.ihtsdo.fxmodel.fetchpolicy.RefexPolicy;
import org.ihtsdo.fxmodel.fetchpolicy.RelationshipPolicy;
import org.ihtsdo.fxmodel.fetchpolicy.VersionPolicy;
import org.ihtsdo.tk.rest.client.RestClient;

/**
 *
 * @author kec
 */
public class GetConceptTask extends Task<FxConcept> {

    private UUID conceptUuid;
    private VersionPolicy versionPolicy = VersionPolicy.ALL_VERSIONS;
    private RefexPolicy refexPolicy = RefexPolicy.REFEX_MEMBERS;
    private RelationshipPolicy relationshipPolicy = RelationshipPolicy.ORIGINATING_AND_DESTINATION_RELATIONSHIPS;

    public GetConceptTask(UUID conceptUuid) {
        this.conceptUuid = conceptUuid;
    }

    public GetConceptTask(UUID conceptUuid,
            VersionPolicy versionPolicy,
            RefexPolicy refexPolicy,
            RelationshipPolicy relationshipPolicy) {
        this.conceptUuid = conceptUuid;
    }

    @Override
    public FxConcept call() throws Exception {
        return RestClient.getRestClient().getFxConcept(
                conceptUuid,
                UUID.fromString("d0a05080-b5de-11e1-afa6-0800200c9a66"), versionPolicy,
                refexPolicy,
                relationshipPolicy);
    }
}
