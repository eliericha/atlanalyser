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

import java.util.List;

import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;

import fr.tpt.atlanalyser.utils.NGCUtils;
import fr.tpt.atlanalyser.utils.Utils;

/**
 * This visitor simplifies formulas using standard logic properties, but does
 * not visit formulas nested in NestedConditions.
 * 
 * @author richa
 *
 */
public class LimitedSimplifier extends SimplifyingTransformer {

    private int navigateNested;

    public LimitedSimplifier() {
        this(0);
    }

    public LimitedSimplifier(int maxNestedLevels) {
        navigateNested = maxNestedLevels;
    }

    protected LimitedSimplifier newSimplifier(int n) {
        return new LimitedSimplifier(n);
    }

    @Override
    public Formula caseNestedCondition(NestedCondition nc) {
        if (!NGCUtils.isTrue(nc)) {
            Graph conclusion = nc.getConclusion();
            Formula formula = conclusion.getFormula();
            if (formula != null) {
                if (navigateNested > 0) {
                    // check nested conditions validity
                    NGCUtils.checkConsistency(nc);

                    formula = newSimplifier(navigateNested - 1).transform(
                            formula);
                }
                if (NGCUtils.isFalse(formula)) {
                    Utils.removeFromContainer(formula);
                    onDiscardBeforeTransform(nc);
                    return formula;
                } else if (NGCUtils.isTrue(formula)) {
                    onDiscardBeforeTransform(formula);
                    conclusion.setFormula(null);
                } else {
                    conclusion.setFormula(formula);
                }
            }

            // formula might have become null after the above, so getFormula()
            // again
            formula = conclusion.getFormula();
            if (formula == null) {
                // Check with NestedCondition::isTrue which checks if the
                // condition morphism is surjective (in which case the condition
                // is always true).
                if (nc.isTrue()) {
                    onDiscardBeforeTransform(nc);
                    return NGCUtils.createTrue();
                }
            } else {
                // Check if condition is surjective, in which case it is
                // useless.
                MappingList mappings = nc.getMappings();
                if (mappings.isOnto(nc.getConclusion())) {
                    List<NestedCondition> nestedConditions = NGCUtils
                            .getNestedConditions(formula);
                    for (NestedCondition subNc : nestedConditions) {
                        MappingList subMappings = subNc.getMappings();
                        for (Mapping m : subMappings) {
                            Node parentOrigin = mappings.getOrigin(m
                                    .getOrigin());
                            assert parentOrigin != null;
                            m.setOrigin(parentOrigin);
                        }
                    }
                    return formula;
                }
            }
        }

        return nc;
    }

}