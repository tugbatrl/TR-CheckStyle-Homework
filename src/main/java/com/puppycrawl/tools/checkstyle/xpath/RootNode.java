///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.xpath;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.XpathUtil;
import com.puppycrawl.tools.checkstyle.xpath.iterators.DescendantIterator;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.GenericTreeInfo;
import net.sf.saxon.om.NamespaceUri;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tree.iter.ArrayIterator;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.tree.iter.SingleNodeIterator;
import net.sf.saxon.type.Type;

/**
 * Represents DetailAST's root node of Xpath-tree.
 */
// Anti-Pattern Refactor: SpeculativeGenerality removed with "Collapse Hierarchy".
// AbstractRootNode had only one subclass (RootNode), so its members were moved
// into RootNode and RootNode now extends AbstractNode directly.
public class RootNode extends AbstractNode {

    // Anti-Pattern Refactor: constant moved from AbstractRootNode.
    /** Name of the root element. */
    private static final String ROOT_NAME = "ROOT";

    // Anti-Pattern Refactor: constant moved from AbstractRootNode.
    /** Constant for optimization. */
    private static final AbstractNode[] EMPTY_ABSTRACT_NODE_ARRAY = new AbstractNode[0];

    /** The ast node. */
    private final DetailAST detailAst;

    /**
     * Creates a new {@code RootNode} instance.
     *
     * @param detailAst reference to {@code DetailAST}
     */
    public RootNode(DetailAST detailAst) {
        // Anti-Pattern Refactor: super(...) call moved from AbstractRootNode constructor.
        super(new GenericTreeInfo(Configuration.newConfiguration()));
        this.detailAst = detailAst;
    }

    /**
     * Iterates siblings of the current node and
     * recursively creates new Xpath-nodes.
     *
     * @return children list
     */
    @Override
    protected List<AbstractNode> createChildren() {
        return XpathUtil.createChildren(this, this, detailAst);
    }

    /**
     * Determine whether the node has any children.
     *
     * @return {@code true} is the node has any children.
     */
    @Override
    public boolean hasChildNodes() {
        return detailAst != null;
    }

    /**
     * Returns line number.
     *
     * @return line number
     */
    @Override
    public int getLineNumber() {
        return detailAst.getLineNo();
    }

    /**
     * Returns column number.
     *
     * @return column number
     */
    @Override
    public int getColumnNumber() {
        return detailAst.getColumnNo();
    }

    /**
     * Getter method for token type. Returns the actual type of the underlying
     * AST root, which is either {@code COMPILATION_UNIT} for an ordinary
     * compilation unit or {@code COMPACT_COMPILATION_UNIT} for a JEP 512
     * compact source file.
     *
     * @return token type of the underlying AST root
     */
    @Override
    public int getTokenType() {
        return detailAst.getType();
    }

    /**
     * Returns underlying node.
     *
     * @return underlying node
     */
    @Override
    public DetailAST getUnderlyingNode() {
        return detailAst;
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Compares current object with specified for order.
     * Throws {@code UnsupportedOperationException} because functionality not required here.
     *
     * @param nodeInfo another {@code NodeInfo} object
     * @return number representing order of current object to specified one
     */
    @Override
    public int compareOrder(NodeInfo nodeInfo) {
        throw throwUnsupportedOperationException();
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Returns attribute value. Throws {@code UnsupportedOperationException} because root node
     * has no attributes.
     *
     * @param namespace namespace
     * @param localPart actual name of the attribute
     * @return attribute value
     */
    @Override
    public String getAttributeValue(NamespaceUri namespace, String localPart) {
        throw throwUnsupportedOperationException();
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Returns local part.
     *
     * @return local part
     */
    @Override
    public String getLocalPart() {
        return ROOT_NAME;
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Returns type of the node.
     *
     * @return node kind
     */
    @Override
    public int getNodeKind() {
        return Type.DOCUMENT;
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Returns parent.
     *
     * @return parent
     */
    @Override
    public NodeInfo getParent() {
        return null;
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Returns root of the tree.
     *
     * @return root of the tree
     */
    @Override
    public NodeInfo getRoot() {
        return this;
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Determines axis iteration algorithm. Throws {@code UnsupportedOperationException} in case,
     * when there is no axis iterator for given axisNumber.
     *
     * @param axisNumber element from {@code AxisInfo}
     * @return {@code AxisIterator} object
     */
    @Override
    public AxisIterator iterateAxis(int axisNumber) {
        return switch (axisNumber) {
            case AxisInfo.ANCESTOR,
                 AxisInfo.PARENT,
                 AxisInfo.FOLLOWING,
                 AxisInfo.FOLLOWING_SIBLING,
                 AxisInfo.PRECEDING,
                 AxisInfo.PRECEDING_SIBLING -> EmptyIterator.ofNodes();

            case AxisInfo.ANCESTOR_OR_SELF,
                 AxisInfo.SELF -> SingleNodeIterator.makeIterator(this);

            case AxisInfo.CHILD -> {
                if (hasChildNodes()) {
                    yield new ArrayIterator.OfNodes<>(
                            getChildren().toArray(EMPTY_ABSTRACT_NODE_ARRAY));
                }
                yield EmptyIterator.ofNodes();
            }

            case AxisInfo.DESCENDANT -> {
                if (hasChildNodes()) {
                    yield new DescendantIterator(this, DescendantIterator.StartWith.CHILDREN);
                }
                yield EmptyIterator.ofNodes();
            }

            case AxisInfo.DESCENDANT_OR_SELF ->
                new DescendantIterator(this, DescendantIterator.StartWith.CURRENT_NODE);

            default -> throw throwUnsupportedOperationException();
        };
    }

    // Anti-Pattern Refactor: method moved from AbstractRootNode.
    /**
     * Getter method for node depth.
     *
     * @return always {@code 0}
     */
    @Override
    public int getDepth() {
        return 0;
    }

    // Anti-Pattern Refactor: helper method moved from AbstractRootNode.
    /**
     * Returns UnsupportedOperationException exception.
     *
     * @return UnsupportedOperationException exception
     */
    private static UnsupportedOperationException throwUnsupportedOperationException() {
        return new UnsupportedOperationException("Operation is not supported");
    }

}