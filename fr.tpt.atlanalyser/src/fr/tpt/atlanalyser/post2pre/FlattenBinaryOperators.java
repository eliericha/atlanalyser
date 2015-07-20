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
package fr.tpt.atlanalyser.post2pre;

import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Or;

import fr.tpt.atlanalyser.post2pre.formulas.FormulaTransformer;
import fr.tpt.atlanalyser.utils.GraphCopier;
import fr.tpt.atlanalyser.utils.Morphism;

public class FlattenBinaryOperators extends FormulaTransformer {

    private static class MoveToNewHost extends FormulaTransformer {

        private Morphism copyMorphism;

        public MoveToNewHost(Morphism copyMorphism) {
            this.copyMorphism = copyMorphism;
        }

        @Override
        public Formula caseNestedCondition(NestedCondition nc) {
            MappingList mappings = nc.getMappings();
            for (Mapping mapping : mappings) {
                mapping.setOrigin(copyMorphism.getImage(mapping.getOrigin()));
            }
            return nc;
        }
    }

    @Override
    public Formula caseNestedCondition(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();
        Formula formula = conclusion.getFormula();

        if (formula != null && formula instanceof Or) {
            Or or = (Or) formula;
            Formula left = or.getLeft();
            Formula right = or.getRight();

            Morphism concToLeftCopy = GraphCopier.copy(conclusion);
            NestedCondition leftNC = HF.createNestedCondition();
            Graph leftConc = concToLeftCopy.getTarget();
            leftNC.setConclusion(leftConc);
            leftConc.setFormula(new MoveToNewHost(concToLeftCopy)
                    .transform(left));
            or.setLeft(this.transform(leftNC));

            Morphism concToRightCopy = GraphCopier.copy(conclusion);
            NestedCondition rightNC = HF.createNestedCondition();
            Graph rightConc = concToRightCopy.getTarget();
            rightNC.setConclusion(rightConc);
            rightConc.setFormula(new MoveToNewHost(concToRightCopy)
                    .transform(right));
            or.setRight(this.transform(rightNC));

            return or;
        }

        return nc;
    }

}
