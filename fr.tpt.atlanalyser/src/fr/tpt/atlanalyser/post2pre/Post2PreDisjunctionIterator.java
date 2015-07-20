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

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.javatuples.Pair;

import fr.tpt.atlanalyser.overlapping.GraphOverlapper;
import fr.tpt.atlanalyser.overlapping.GraphOverlapper.GraphOverlapIterator;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;

public abstract class Post2PreDisjunctionIterator extends
        SolutionIterator<Formula> {

    public static Post2PreDisjunctionIterator createIterator(Morphism rule,
            Formula post) {
        return null;
    }

    protected Unit    program;
    Iterator<Formula> disjunctionedPosts;
    protected Formula post;

    public Post2PreDisjunctionIterator(Formula post, Unit program) {
        this.program = program;
        this.post = post;
        if (post instanceof Or) {
            Or or = (Or) post;
            this.disjunctionedPosts = NGCUtils.collectJunctionedFormulas(or)
                    .iterator();
        } else {
            this.disjunctionedPosts = Arrays.asList(post).iterator();
        }
    }

    protected void computeNext() {
        nextSolution = null;
        while (nextSolution == null && disjunctionedPosts.hasNext()) {
            Formula post = disjunctionedPosts.next();
            computeNextForUnit();
        }
    }

    protected abstract void computeNextForUnit();

    private static class Post2RightACIterator extends SolutionIterator<Formula> {

        private Morphism                    a;
        private Formula                     nestedFormula;
        private GraphOverlapIterator          overlaps;
        private Graph                       newHost;
        private Post2PreDisjunctionIterator subFormulaIterator;
        private NestedCondition             currentNewCdt;

        public Post2RightACIterator(NestedCondition post, Morphism anchor,
                Engine hengine) {
            a = new Morphism(post);
            this.newHost = anchor.getTarget();
            nestedFormula = post.getConclusion().getFormula();
            overlaps = new GraphOverlapper.GraphOverlapIterator(newHost,
                    a.getTarget(), Pair.with(anchor, a), false, true, false,
                    hengine);
        }

        @Override
        protected void computeNext() {
            if (currentNewCdt != null && subFormulaIterator != null
                    && subFormulaIterator.hasNext()) {
                Formula nextSubFormula = subFormulaIterator.next();
                NestedCondition nextCdt = EcoreUtil.copy(currentNewCdt);
                nextCdt.getConclusion().setFormula(nextSubFormula);
                nextSolution = nextCdt;
                return;
            }

            if (overlaps.hasNext()) {
                Pair<Morphism, Morphism> nextOverlap = overlaps.next();
                Morphism newCdtMorphism = nextOverlap.getValue0();
                currentNewCdt = NGCUtils.createNestedCondition(newCdtMorphism);

                if (nestedFormula != null) {
                    subFormulaIterator = Post2PreDisjunctionIterator
                            .createIterator(nextOverlap.getValue1(),
                                    nestedFormula);

                    if (subFormulaIterator.hasNext()) {
                        Formula nextSubFormula = subFormulaIterator.next();
                        NestedCondition nextCdt = EcoreUtil.copy(currentNewCdt);
                        nextCdt.getConclusion().setFormula(nextSubFormula);
                        nextSolution = nextCdt;
                    } else {
                        subFormulaIterator = null;
                        nextSolution = currentNewCdt;
                    }

                    return;
                }
            }
        }
    }

    private static class Post2PreRule extends Post2PreDisjunctionIterator {

        private Morphism             ruleMorphism;
        private Formula              post;
        private Post2RightACIterator rightACIterator;

        public Post2PreRule(Rule rule, Formula post) {
            super(post, rule);
            this.ruleMorphism = new Morphism(rule);
            this.post = post;
            this.rightACIterator = new Post2RightACIterator(
                    (NestedCondition) post, null, null);
        }

        @Override
        protected void computeNextForUnit() {
            // TODO Auto-generated method stub

        }

    }

}
