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
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.Xor;

public abstract class AbstractFormulaVisitor {

    public AbstractFormulaVisitor() {
        super();
    }

    public Formula caseNestedCondition(NestedCondition nc) {
        Formula formula = nc.getConclusion().getFormula();
        if (formula != null) {
            this.transform(formula);
        }
        return nc;
    }

    public Formula caseNot(Not not) {
        this.transform(not.getChild());
        return not;
    }

    private Formula caseBinaryFormula(BinaryFormula binF) {
        this.transform(binF.getLeft());
        this.transform(binF.getRight());
        return binF;
    }

    public Formula caseXor(Xor xor) {
        return caseBinaryFormula(xor);
    }

    public Formula caseOr(Or or) {
        return caseBinaryFormula(or);
    }

    public Formula caseAnd(And and) {
        return caseBinaryFormula(and);
    }

    public Formula transform(Formula formula) {
        int classifierID = formula.eClass().getClassifierID();
        Formula result = null;
        switch (classifierID) {
        case HenshinPackage.AND:
            result = caseAnd((And) formula);
            break;
        case HenshinPackage.OR:
            result = caseOr((Or) formula);
            break;
        case HenshinPackage.XOR:
            result = caseXor((Xor) formula);
            break;
        case HenshinPackage.NOT:
            result = caseNot((Not) formula);
            break;
        case HenshinPackage.NESTED_CONDITION:
            result = caseNestedCondition((NestedCondition) formula);
            break;
        default:
            result = caseDefault(formula);
            break;
        }
        return result;
    }

    public Formula caseDefault(Formula formula) {
        throw new UnsupportedOperationException("Not implemented for "
                + formula);
    }

}