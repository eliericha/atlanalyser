/*******************************************************************************
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Elie Richa - initial implementation
 *******************************************************************************/
package fr.tpt.atlanalyser.post2pre.formulas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;

import fr.tpt.atlanalyser.utils.NGCUtils;

public abstract class CopyingSimplifyingTransformer extends
        AbstractFormulaVisitor {

    private static final HenshinFactory HF     = HenshinFactory.eINSTANCE;
    private static final Logger         LOGGER = LogManager
                                                       .getLogger(CopyingSimplifyingTransformer.class
                                                               .getSimpleName());

    protected void onDiscardBeforeTransform(Formula f) {
    }

    @Override
    public Formula caseAnd(And and) {
        Formula left = this.transform(and.getLeft());
        if (NGCUtils.isFalse(left)) {
            // false AND <> = false
            LOGGER.trace("false AND <> = false");
            onDiscardBeforeTransform(and.getRight());
            onDiscardBeforeTransform(and);
            return left;
        }
        Formula right = this.transform(and.getRight());
        if (NGCUtils.isFalse(right)) {
            // <> AND false = false
            LOGGER.trace("<> AND false = false");
            onDiscardBeforeTransform(left);
            onDiscardBeforeTransform(and);
            return right;
        }

        if (NGCUtils.isTrue(left)) {
            // true AND right = right
            LOGGER.trace("true AND right = right");
            onDiscardBeforeTransform(left);
            onDiscardBeforeTransform(and);
            return right;
        }

        if (NGCUtils.isTrue(right)) {
            // left AND true = left
            LOGGER.trace("left AND true = left");
            onDiscardBeforeTransform(right);
            onDiscardBeforeTransform(and);
            return left;
        }
        And newAnd = HF.createAnd();
        newAnd.setLeft(left);
        newAnd.setRight(right);
        return newAnd;
    }

    @Override
    public Formula caseOr(Or or) {
        Formula left = or.getLeft();
        left = this.transform(left);
        if (NGCUtils.isTrue(left)) {
            LOGGER.trace("true OR <> = true");
            onDiscardBeforeTransform(or.getRight());
            onDiscardBeforeTransform(or);
            return left;
        }
        Formula right = this.transform(or.getRight());
        if (NGCUtils.isTrue(right)) {
            LOGGER.trace("<> OR true = true");
            onDiscardBeforeTransform(left);
            onDiscardBeforeTransform(or);
            return right;
        }

        if (NGCUtils.isFalse(left)) {
            LOGGER.trace("false OR right = right");
            onDiscardBeforeTransform(left);
            onDiscardBeforeTransform(or);
            return right;
        }
        if (NGCUtils.isFalse(right)) {
            LOGGER.trace("left OR false = left");
            onDiscardBeforeTransform(right);
            onDiscardBeforeTransform(or);
            return left;
        }

        Or newOr = HF.createOr();
        newOr.setLeft(left);
        newOr.setRight(right);
        return newOr;
    }

    @Override
    public Formula caseNot(Not not) {
        Formula child = this.transform(not.getChild());
        if (child instanceof Not) {
            Not childNot = (Not) child;
            // not (not (f)) = f
            LOGGER.trace("not not f = f");
            Formula child2 = childNot.getChild();
            onDiscardBeforeTransform(not);
            return child2;
        }
        Not newNot = HF.createNot();
        newNot.setChild(child);
        return newNot;
    }

}