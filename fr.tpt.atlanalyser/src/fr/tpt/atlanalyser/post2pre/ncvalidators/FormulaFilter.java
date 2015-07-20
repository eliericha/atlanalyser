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
package fr.tpt.atlanalyser.post2pre.ncvalidators;

import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;

import fr.tpt.atlanalyser.post2pre.formulas.SimplifyingTransformer;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class FormulaFilter extends SimplifyingTransformer {

    private NestedConditionValidator validator;

    public FormulaFilter(NestedConditionValidator validator) {
        this.validator = validator;
    }

    @Override
    public Formula caseNestedCondition(NestedCondition nc) {
        if (validator.isValid(nc)) {
            Graph conclusion = nc.getConclusion();
            Formula formula = conclusion.getFormula();
            if (formula != null) {
                formula = this.transform(formula);
                conclusion.setFormula(formula);
                if (NGCUtils.isFalse(formula)) {
                    return formula;
                }
            }
            return nc;
        } else {
            return NGCUtils.createFalse();
        }
    }

}
