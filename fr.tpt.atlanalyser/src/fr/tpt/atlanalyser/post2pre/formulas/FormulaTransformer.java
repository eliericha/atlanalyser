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

import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.Xor;

public abstract class FormulaTransformer extends AbstractFormulaVisitor {

    protected static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    public FormulaTransformer() {
        super();
    }

    @Override
    public Formula caseAnd(And and) {
        And newAnd = HF.createAnd();
        Formula left = this.transform(and.getLeft());
        Formula right = this.transform(and.getRight());
        newAnd.setLeft(left);
        newAnd.setRight(right);
        return newAnd;
    }

    @Override
    public Formula caseOr(Or or) {
        Or newOr = HF.createOr();
        Formula left = this.transform(or.getLeft());
        Formula right = this.transform(or.getRight());
        newOr.setLeft(left);
        newOr.setRight(right);
        return newOr;
    }

    @Override
    public Formula caseXor(Xor xor) {
        Xor newXor = HF.createXor();
        Formula left = this.transform(xor.getLeft());
        Formula right = this.transform(xor.getRight());
        newXor.setLeft(left);
        newXor.setRight(right);
        return newXor;
    }

    @Override
    public Formula caseNot(Not not) {
        Not newNot = HF.createNot();
        newNot.setChild(this.transform(not.getChild()));
        return newNot;
    }

    @Override
    public abstract Formula caseNestedCondition(NestedCondition nc);

}