// Modified from com.sun.javafx.scene.control.skin.TreeCellSkin...
/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package gov.va.ohi.sim.fx.arena;

import com.sun.javafx.scene.control.skin.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import com.sun.javafx.css.StyleableDoubleProperty;
import com.sun.javafx.css.StyleableProperty;
import com.sun.javafx.css.converters.SizeConverter;
import com.sun.javafx.scene.control.behavior.TreeCellBehavior;

public class ConceptViewCellSkin extends CellSkinBase<TreeCell<?>, TreeCellBehavior> {

    /**
     * The amount of space to multiply by the treeItem.level to get the left
     * margin for this tree cell. This is settable from CSS
     */
    private DoubleProperty indent = null;
    public final void setIndent(double value) { indentProperty().set(value); }
    public final double getIndent() { return indent == null ? 10.0 : indent.get(); }
    public final DoubleProperty indentProperty() { 
        if (indent == null) {
            indent = new StyleableDoubleProperty(10.0) {

                @Override
                public Object getBean() {
                    return ConceptViewCellSkin.this;
                }

                @Override
                public String getName() {
                    return "indent";
                }

                @Override
                public StyleableProperty getStyleableProperty() {
                    return StyleableProperties.INDENT;
                }
                
            };
        }
        return indent; 
    }

    public ConceptViewCellSkin(TreeCell<?> control) {
        super(control, new TreeCellBehavior(control));
        
        updateDisclosureNode();
        
        registerChangeListener(control.treeItemProperty(), "TREE_ITEM");
    }
    
    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ( "TREE_ITEM".equals(p)) {
            updateDisclosureNode();
        }
    }
    
    private void updateDisclosureNode() {
        if (getSkinnable().isEmpty()) {
            return;
        }

        Node disclosureNode = getSkinnable().getDisclosureNode();
        if (disclosureNode == null) {
            return;
        }
        
        TreeItem treeItem = getSkinnable().getTreeItem();
        
        boolean disclosureVisible = treeItem != null && ! treeItem.isLeaf();
        disclosureNode.setVisible(disclosureVisible);
            
        if (! disclosureVisible) {
            getChildren().remove(disclosureNode);
        } else if (disclosureNode.getParent() == null) {
            getChildren().add(disclosureNode);
            disclosureNode.toFront();
        } else {
            disclosureNode.toBack();
        }
    }

    @Override public void impl_processCSS(boolean reapply) {
        // This is needed now that TreeCell is Labeled - otherwise RT-15450 occurs
        updateDisclosureNode();
        
        super.impl_processCSS(reapply);
    }
    
    @Override protected void layoutChildren() {
        TreeItem treeItem = getSkinnable().getTreeItem();
        if (treeItem == null) {
            return;
        }
        
        TreeView tree = getSkinnable().getTreeView();
        if (tree == null) {
            return;
        }
        
        // figure out the content area that is to be filled
        double x = getInsets().getLeft();
        double y = getInsets().getTop();
        double w = getWidth() - (getInsets().getLeft() + getInsets().getRight());
        double h = getHeight() - (getInsets().getTop() + getInsets().getBottom());

        Node disclosureNode = getSkinnable().getDisclosureNode();
        
        int level = TreeView.getNodeLevel(getSkinnable().getTreeItem());
        if (! tree.isShowRoot()) {
            level--;
        }
        double leftMargin = getIndent() * level;

        x += leftMargin;

        // position the disclosure node so that it is at the proper indent
        boolean disclosureVisible = disclosureNode != null && treeItem != null && ! treeItem.isLeaf();

        final double defaultDisclosureWidth = 0;   // RT-19656: default width of default disclosure node
        double disclosureWidth = defaultDisclosureWidth;

        if (disclosureVisible && disclosureNode != null) {
            disclosureWidth = disclosureNode.prefWidth(-1);

            double ph = disclosureNode.prefHeight(-1);

            disclosureNode.resize(disclosureWidth, ph);
            positionInArea(disclosureNode, x, y,
                    disclosureWidth, h, /*baseline ignored*/0,
                    HPos.CENTER, VPos.CENTER);
        }

        // determine starting point of the graphic or cell node, and the
        // remaining width available to them
        final int padding = treeItem.getGraphic() == null ? 0 : 3;
        x += disclosureWidth + padding;
        w -= (leftMargin + disclosureWidth + padding);

        layoutLabelInArea(x, y, w, h);
    }

    @Override protected double computePrefHeight(double width) {
        double pref = super.computePrefHeight(width);
        Node d = getSkinnable().getDisclosureNode();
        return (d == null) ? pref : Math.max(d.prefHeight(-1), pref);
    }
    
    @Override protected double computePrefWidth(double height) {
        double labelWidth = super.computePrefWidth(height);

        double pw = getInsets().getLeft() + getInsets().getRight();

        TreeView tree = getSkinnable().getTreeView();
        if (tree == null) {
            return pw;
        }
        
        TreeItem treeItem = getSkinnable().getTreeItem();
        if (treeItem == null) {
            return pw;
        }
        
        pw = labelWidth;

        // determine the amount of indentation
        int level = TreeView.getNodeLevel(treeItem);
        if (! tree.isShowRoot()) {
            level--;
        }
        pw += getIndent() * level;

        // include the disclosure node width
        Node disclosureNode = getSkinnable().getDisclosureNode();
        final double defaultDisclosureWidth = 0;
        pw += Math.max(defaultDisclosureWidth, disclosureNode.prefWidth(-1));

        return pw;
    }

    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    /** @treatAsPrivate */
    private static class StyleableProperties {
        private static final StyleableProperty<ConceptViewCellSkin,Number> INDENT = 
            new StyleableProperty<ConceptViewCellSkin,Number>("-fx-indent",
                SizeConverter.getInstance(), 10.0) {

            @Override
            public boolean isSettable(ConceptViewCellSkin n) {
                return n.indent == null || !n.indent.isBound();
            }

            @Override
            public WritableValue<Number> getWritableValue(ConceptViewCellSkin n) {
                return n.indentProperty();
            }
        };
        
        private static final List<StyleableProperty> STYLEABLES;
        static {
            final List<StyleableProperty> styleables =
                new ArrayList<>(CellSkinBase.impl_CSS_STYLEABLES());
            Collections.addAll(styleables,
                INDENT
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
    
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public static List<StyleableProperty> impl_CSS_STYLEABLES() {
        return ConceptViewCellSkin.StyleableProperties.STYLEABLES;
    }

    /**
     * RT-19263
     * @treatAsPrivate implementation detail
     * @deprecated This is an experimental API that is not intended for general use and is subject to change in future versions
     */
    @Deprecated
    @Override
    public List<StyleableProperty> impl_getStyleableProperties() {
        return impl_CSS_STYLEABLES();
    }

}
