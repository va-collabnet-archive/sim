
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.dom;

//~--- non-JDK imports --------------------------------------------------------
import gov.va.demo.expression.ExpressionManager;
import gov.va.demo.nb.sim.jpa.Expressions;
import gov.va.demo.nb.sim.jpa.JpaManager;
import gov.va.demo.nb.sim.jpa.PncsLegoMap;
import gov.va.sim.impl.expression.Expression;

import org.ihtsdo.tk.api.TerminologySnapshotDI;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

//~--- JDK imports ------------------------------------------------------------

import java.beans.PropertyVetoException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 *
 * @author kec
 */
public class PncsLegoMapGenerator {

    private EntityManager em;
    private int pncsId;
    private String pncsValue;

    //~--- methods -------------------------------------------------------------
    public void addToMap(Document assertionDoc, TerminologySnapshotDI terminologySnapshot)
            throws IOException, PropertyVetoException, NoSuchAlgorithmException {
        em = JpaManager.getEntityManager();

        int whatToShow = NodeFilter.SHOW_ELEMENT;
        NodeFilter filter = new ExpressionRootNodeFilter();
        boolean entityReferenceExpansion = false;
        TreeWalker walker = ((DocumentTraversal) assertionDoc).createTreeWalker(assertionDoc, whatToShow,
                filter, entityReferenceExpansion);
        Node domNode = walker.nextNode();
        PncsLegoMap map = null;
        EntityTransaction entr = null;

        while (domNode != null) {
            if (domNode.getNodeName().equals("pncs")) {
                if (domNode.getAttributes().getNamedItem("value") != null) {
                    pncsValue = domNode.getAttributes().getNamedItem("value").getNodeValue();
                } else {
                    pncsValue = "null";
                }

                pncsId = Integer.parseInt(domNode.getAttributes().getNamedItem("id").getNodeValue());
            } else if (domNode.getNodeName().equals("discernable")) {
                entr = em.getTransaction();

                if (entr.isActive()) {
                    entr.rollback();
                }

                entr.begin();
                map = new PncsLegoMap();
                map.setPncsId(pncsId);
                map.setPncsValue(pncsValue);

                try {
                    Expressions dbExpression = processExpression(assertionDoc, domNode, whatToShow,
                            entityReferenceExpansion, terminologySnapshot);

                    map.setDiscernableEnid(dbExpression);
                } catch (Exception ex) {
                    Logger.getLogger(DomExpressionAdapter.class.getName()).log(Level.SEVERE, null, ex);
                    map = null;
                }
            } else if (domNode.getNodeName().equals("qualifier")) {
                if (map != null) {
                    Expressions dbExpression = processExpression(assertionDoc, domNode, whatToShow,
                            entityReferenceExpansion, terminologySnapshot);

                    map.setQualifierEnid(dbExpression);
                }
            } else if (domNode.getNodeName().equals("value")) {
                if (map != null) {
                    Expressions dbExpression = processExpression(assertionDoc, domNode, whatToShow,
                            entityReferenceExpansion, terminologySnapshot);

                    map.setValueEnid(dbExpression);

                    Query countPncsIdPncsValueQuery = em.createNamedQuery("PncsLegoMap.countPncsIdPncsValue");

                    countPncsIdPncsValueQuery.setParameter("pncsValue", map.getPncsValue());
                    countPncsIdPncsValueQuery.setParameter("pncsId", map.getPncsId());

                    List obs = countPncsIdPncsValueQuery.getResultList();
                    long count = (Long) obs.get(0);

                    if (count == 0) {
                        em.persist(map);
                        entr.commit();
                        System.out.println("wrote map: " + map);
                    } else {
                        entr.rollback();
                        System.out.println("  dup map: " + map);
                    }

                    map = null;
                } else {
                    entr.rollback();
                }
            }

            domNode = walker.nextNode();
        }
    }

    private Expressions processExpression(Document assertionDoc, Node domNode, int whatToShow,
            boolean entityReferenceExpansion, TerminologySnapshotDI terminologySnapshot)
            throws DOMException, IOException, PropertyVetoException, UnsupportedEncodingException,
            NoSuchAlgorithmException {
        for (int i = 0; i < domNode.getChildNodes().getLength(); i++) {
            Node child = domNode.getChildNodes().item(i);

            if (child.getNodeName().equals("concept")) {
                TreeWalker expressionWalker = ((DocumentTraversal) assertionDoc).createTreeWalker(child,
                        whatToShow, null, entityReferenceExpansion);
                Expression exp = DomExpressionAdapter.convertToExpression(expressionWalker, terminologySnapshot);
                Query countEuuidQuery = em.createNamedQuery("Expressions.findByEuuid");

                countEuuidQuery.setParameter("euuid", exp.getUuid().toString());

                List obs = countEuuidQuery.getResultList();

                if (obs.isEmpty()) {

                    EntityTransaction entr = em.getTransaction();
                    if (!entr.isActive()) {
                        entr.begin();
                    }
                    Expressions expressionObj = new Expressions();
                    expressionObj.setCnid(ExpressionManager.getCnid(exp));
                    expressionObj.setEuuid(exp.getUuid().toString());
                    expressionObj.setExpression(exp.getXml());
                    em.persist(expressionObj);
                    return expressionObj;

                } else {
                    Expressions dbExpression = (Expressions) obs.get(0);

                    return dbExpression;
                }
            }
        }

        throw new IOException("No expression found: " + domNode);
    }

    //~--- inner classes -------------------------------------------------------
    public static class ExpressionRootNodeFilter implements NodeFilter {

        @Override
        public short acceptNode(Node n) {
            if (n.getNodeName().equals("pncs")) {
                return NodeFilter.FILTER_ACCEPT;
            }

            if (n.getNodeName().equals("discernable")) {
                return NodeFilter.FILTER_ACCEPT;
            }

            if (n.getNodeName().equals("qualifier")) {
                return NodeFilter.FILTER_ACCEPT;
            }

            if (n.getNodeName().equals("value")) {
                return NodeFilter.FILTER_ACCEPT;
            }

            if (n.getNodeName().equals("concept")) {
                return NodeFilter.FILTER_REJECT;
            }

            return NodeFilter.FILTER_SKIP;
        }
    }
}
