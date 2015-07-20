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

import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;

import fr.tpt.atlanalyser.utils.NGCUtils;

/**
 * This visitor simplifies formulas using standard logic properties, including
 * formulas nested in NestedConditions.
 * 
 * @author richa
 *
 */
public class FullSimplifier extends SimplifyingTransformer {

    @Override
    public Formula caseNestedCondition(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();
        Formula formula = conclusion.getFormula();
        if (formula != null) {
            formula = this.transform(formula);
            if (NGCUtils.isFalse(formula)) {
                return formula;
            } else if (NGCUtils.isTrue(formula)) {
                conclusion.setFormula(null);
            } else {
                conclusion.setFormula(formula);
            }
        }

        // formula might have become null after the above, so getFormula()
        // again
        if (conclusion.getFormula() == null) {
            // Check with NestedCondition::isTrue which checks if the
            // condition morphism is surjective (in which case it's always
            // true).
            if (nc.isTrue()) {
                return NGCUtils.createTrue();
            }
        }

        return nc;
    }

}