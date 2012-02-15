
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.sim.impl.expression.node;

//~--- non-JDK imports --------------------------------------------------------
import gov.va.sim.act.expression.node.ConceptNodeBI;

import org.ihtsdo.tk.api.concept.ConceptVersionBI;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.UUID;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author kec
 */
public class ConceptNode extends ExpressionNode<ConceptVersionBI> implements ConceptNodeBI {

    @Override
    public UUID getConceptUuid() {
        return value.getPrimUuid();
    }

    @Override
    public String getFullySpecifiedDesc() throws IOException {
        return value.getFsnDescsActive().iterator().next().getText();
    }

    @Override
    public String getPreferredDesc() throws IOException {
        return value.getPrefDescsActive().iterator().next().getText();
    }

    @Override
    public void addTag(StringBuilder sb) {
        sb.append("concept");
    }

    @Override
    public void addAttributes(StringBuilder sb, boolean verbose) throws IOException {
        if (verbose) {
            sb.append(" fsn=\"");
            sb.append(StringEscapeUtils.escapeXml(getFullySpecifiedDesc()));
            sb.append("\" uuid=\"");
        } else {
            sb.append(" uuid=\"");
        }
        sb.append(getConceptUuid().toString());
        sb.append("\"");
    }

    @Override
    public void appendForHash(StringBuilder sb) throws IOException {
        sb.append(getConceptUuid().toString());
    }
}
