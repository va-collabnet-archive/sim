
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ohi.sim.fx.taxonomy;

//~--- non-JDK imports --------------------------------------------------------
import javafx.application.Platform;

import javafx.concurrent.Task;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventType;

import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;

import org.ihtsdo.concurrency.FutureHelper;
import org.ihtsdo.fxmodel.FxTaxonomyReferenceWithConcept;
import org.ihtsdo.fxmodel.concept.component.relationship.FxRelationshipChronicle;
import org.ihtsdo.fxmodel.concept.component.relationship.FxRelationshipVersion;
import org.ihtsdo.helper.thread.NamedThreadFactory;
import org.ihtsdo.tk.api.ContradictionException;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kec
 */
public class SimTreeItem extends TreeItem<FxTaxonomyReferenceWithConcept> implements Comparable<SimTreeItem> {

    private static ThreadGroup simTreeItemThreadGroup = new ThreadGroup("SimTreeItem child fetcher pool");
    private static ExecutorService childFetcherPool;
    protected static ExecutorService conceptFetcherPool;

    //~--- static initializers -------------------------------------------------
    static {
        resetExecutorPool();
    }
    //~--- fields --------------------------------------------------------------
    private int multiParentDepth = 0;
    private boolean secondaryParentOpened = false;
    private boolean multiParent = false;
    private List<SimTreeItem> extraParents = new ArrayList();
    private boolean defined = false;
    private ProgressIndicator pi;

    //~--- constructors --------------------------------------------------------
    public SimTreeItem(FxTaxonomyReferenceWithConcept t) {
        this(t, (Node) null);
    }

    public SimTreeItem(FxTaxonomyReferenceWithConcept t, Node node) {
        super(t, node);
    }

    //~--- methods -------------------------------------------------------------
    public void addChildren() {
        if (getValue().getConcept() != null) {
            ProgressIndicator p2 = new ProgressIndicator();

            p2.setSkin(new TaxonomyProgressIndicatorSkin(p2));
            p2.setPrefSize(16, 16);
            p2.setProgress(-1);
            setProgressIndicator(p2);

            ArrayList<SimTreeItem> childrenToProcess = new ArrayList<>();

            for (FxRelationshipChronicle r : getValue().conceptProperty().get().getDestinationRelationships()) {
                for (FxRelationshipVersion rv : r.getVersions()) {
                    try {
                        FxTaxonomyReferenceWithConcept fxtrc = new FxTaxonomyReferenceWithConcept(rv);
                        SimTreeItem childItem = new SimTreeItem(fxtrc);

                        childrenToProcess.add(childItem);
                    } catch (IOException | ContradictionException ex) {
                        Logger.getLogger(SimTreeItem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            Collections.sort(childrenToProcess);
            getChildren().addAll(childrenToProcess);

            FetchConceptsTask fetchChildrenTask = new FetchConceptsTask(childrenToProcess);

            p2.progressProperty().bind(fetchChildrenTask.progressProperty());
            FutureHelper.addFuture(childFetcherPool.submit(fetchChildrenTask));
        }
    }

    public void addChildrenConceptsAndGrandchildrenItems(ProgressIndicator p1) {
        ArrayList<SimTreeItem> grandChildrenToProcess = new ArrayList<>();

        for (TreeItem<FxTaxonomyReferenceWithConcept> child : getChildren()) {
            if (child.getChildren().isEmpty() && (child.getValue().getConcept() != null)) {
                if (child.getValue().getConcept().getDestinationRelationships().isEmpty()) {
                    FxTaxonomyReferenceWithConcept value = child.getValue();
                    child.setValue(null);
                    SimTreeItem noChildItem = (SimTreeItem) child;
                    noChildItem.computeGraphic();
                    noChildItem.setValue(value);
                } else {
                    ArrayList<SimTreeItem> grandChildrenToAdd = new ArrayList<>();

                    for (FxRelationshipChronicle r :
                            child.getValue().conceptProperty().get().getDestinationRelationships()) {
                        for (FxRelationshipVersion rv : r.getVersions()) {
                            try {
                                FxTaxonomyReferenceWithConcept fxtrc = new FxTaxonomyReferenceWithConcept(rv);
                                SimTreeItem grandChildItem = new SimTreeItem(fxtrc);

                                grandChildrenToProcess.add(grandChildItem);
                                grandChildrenToAdd.add(grandChildItem);
                            } catch (IOException | ContradictionException ex) {
                                Logger.getLogger(SimTreeItem.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    Collections.sort(grandChildrenToAdd);
                    child.getChildren().addAll(grandChildrenToAdd);
                }
            } else if (child.getValue().getConcept() == null) {
                grandChildrenToProcess.add((SimTreeItem) child);
            }
        }

        FetchConceptsTask fetchChildrenTask = new FetchConceptsTask(grandChildrenToProcess);

        p1.progressProperty().bind(fetchChildrenTask.progressProperty());
        FutureHelper.addFuture(childFetcherPool.submit(fetchChildrenTask));
    }

    @Override
    public <E extends Event> void addEventHandler(EventType<E> et, EventHandler<E> eh) {
        super.addEventHandler(et, eh);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain edc) {
        return super.buildEventDispatchChain(edc);
    }

    @Override
    public int compareTo(SimTreeItem o) {
        return this.getValue().getRelationshipVersion().getOriginReference().getText().compareTo(
                o.getValue().getRelationshipVersion().getOriginReference().getText());
    }

    public Node computeGraphic() {
        FxTaxonomyReferenceWithConcept ref = getValue();
        if (ref != null && ref.getRelationshipVersion() == null) {
            return SimTreeIcons.ROOT.getImageView();
        } else if (ref != null && ref.getConcept() != null && ref.getConcept().getOriginRelationships().isEmpty()) {
            return SimTreeIcons.ROOT.getImageView();
        } else if (isDefined() && (isMultiParent() || multiParentDepth > 0)) {
            
            if (isSecondaryParentOpened()) {
                return SimTreeIcons.DEFINED_MULTI_PARENT_OPEN.getImageView();
            } else {
                return SimTreeIcons.DEFINED_MULTI_PARENT_CLOSED.getImageView();
            }
        } else if (!isDefined() && (isMultiParent() || multiParentDepth > 0)) {
            if (isSecondaryParentOpened()) {
                return SimTreeIcons.PRIMITIVE_MULTI_PARENT_OPEN.getImageView();
            } else {
                return SimTreeIcons.PRIMITIVE_MULTI_PARENT_CLOSED.getImageView();
            }
        } else if (isDefined() && !isMultiParent()) {
            return SimTreeIcons.DEFINED_SINGLE_PARENT.getImageView();
        }
        return SimTreeIcons.PRIMITIVE_SINGLE_PARENT.getImageView();
    }
 
    @Override
    public TreeItem<FxTaxonomyReferenceWithConcept> nextSibling() {
        return super.nextSibling();
    }

    @Override
    public TreeItem<FxTaxonomyReferenceWithConcept> nextSibling(TreeItem<FxTaxonomyReferenceWithConcept> ti) {
        return super.nextSibling(ti);
    }

    @Override
    public TreeItem<FxTaxonomyReferenceWithConcept> previousSibling() {
        return super.previousSibling();
    }

    @Override
    public TreeItem<FxTaxonomyReferenceWithConcept> previousSibling(
            TreeItem<FxTaxonomyReferenceWithConcept> ti) {
        return super.previousSibling(ti);
    }

    @Override
    public <E extends Event> void removeEventHandler(EventType<E> et, EventHandler<E> eh) {
        super.removeEventHandler(et, eh);
    }

    public void removeGrandchildren() {
        for (TreeItem<FxTaxonomyReferenceWithConcept> child : getChildren()) {
            child.getChildren().clear();
        }
    }

    public static void resetExecutorPool() {
        childFetcherPool = Executors.newFixedThreadPool(Math.min(6,
                Runtime.getRuntime().availableProcessors() + 1), new NamedThreadFactory(simTreeItemThreadGroup,
                "SimTreeItem child fetcher"));
        conceptFetcherPool = Executors.newFixedThreadPool(Math.min(6,
                Runtime.getRuntime().availableProcessors() + 1), new NamedThreadFactory(simTreeItemThreadGroup,
                "SimTreeItem concept fetcher"));
    }

    @Override
    public String toString() {
        if (getValue().getRelationshipVersion() != null) {
            if (multiParentDepth > 0) {
                return getValue().getRelationshipVersion().getDestinationReference().getText();
            } else {
                return getValue().getRelationshipVersion().getOriginReference().getText();
            }
        }

        if (getValue().conceptProperty().get() != null) {
            return getValue().conceptProperty().get().getConceptReference().getText();
        }

        return "root";
    }

    //~--- get methods ---------------------------------------------------------
    public List<SimTreeItem> getExtraParents() {
        return extraParents;
    }

    public int getMultiParentDepth() {
        return multiParentDepth;
    }

    public ProgressIndicator getProgressIndicator() {
        return pi;
    }

    public boolean isDefined() {
        return defined;
    }

    @Override
    public boolean isLeaf() {
        if (multiParentDepth > 0) {
            return true;
        }

        return super.isLeaf();
    }

    public boolean isMultiParent() {
        return multiParent;
    }

    public boolean isSecondaryParentOpened() {
        return secondaryParentOpened;
    }

    //~--- set methods ---------------------------------------------------------
    public void setDefined(boolean defined) {
        this.defined = defined;
    }

    public void setMultiParent(boolean multiParent) {
        this.multiParent = multiParent;
    }

    public void setMultiParentDepth(int multiParentDepth) {
        this.multiParentDepth = multiParentDepth;
    }

    public void setProgressIndicator(ProgressIndicator pi) {
        this.pi = pi;
    }

    public void setSecondaryParentOpened(boolean secondaryParentOpened) {
        this.secondaryParentOpened = secondaryParentOpened;
    }

    //~--- inner classes -------------------------------------------------------
    public class FetchConceptsTask extends Task<Boolean> {

        List<SimTreeItem> children;

        //~--- constructors -----------------------------------------------------
        public FetchConceptsTask(List<SimTreeItem> children) {
            this.children = children;
        }

        //~--- methods ----------------------------------------------------------
        @Override
        protected Boolean call() throws Exception {
            int size = children.size() - 1;
            int processedCount = 0;
            List<Future> futureList = new ArrayList<>();

            for (SimTreeItem childItem : children) {
                if (childItem.getValue().conceptProperty().get() == null) {
                    GetSimTreeItemConcept getter = new GetSimTreeItemConcept(childItem);

                    futureList.add(conceptFetcherPool.submit(getter));
                } else {
                    updateProgress(Math.min(processedCount++, size), size);
                }
            }

            updateProgress(Math.min(processedCount, size), size);

            for (Future future : futureList) {
                try {
                    future.get();
                    updateProgress(Math.min(processedCount++, size), size);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(FutureHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            updateProgress(1, 1);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    FxTaxonomyReferenceWithConcept item = SimTreeItem.this.getValue();

                    SimTreeItem.this.setValue(null);
                    setProgressIndicator(null);
                    SimTreeItem.this.setValue(item);
                }
            });

            return true;
        }
    }
}
