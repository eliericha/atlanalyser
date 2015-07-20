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
package fr.tpt.atlanalyser.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.UnaryFormula;
import org.eclipse.emf.henshin.model.impl.NestedConditionImpl;
import org.eclipse.emf.henshin.model.impl.NotImpl;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.post2pre.formulas.FullSimplifier;

public class NGCUtils {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    public static class NGCOneShotSimplifier extends HenshinSwitch<Formula> {

        private boolean didSomething = false;

        public Formula simplify(Formula formula) {
            didSomething = false;
            return this.doSwitch(formula);
        }

        public boolean didSomething() {
            return didSomething;
        }

        @Override
        public Formula doSwitch(EObject theEObject) {
            if (!didSomething) {
                return super.doSwitch(theEObject);
            } else {
                return (Formula) theEObject;
            }
        }

        @Override
        public Formula defaultCase(EObject object) {
            return (Formula) object;
        }

        @Override
        public Formula caseNot(Not not) {
            Formula child = not.getChild();

            if (child instanceof Or) {
                Or or = (Or) child;
                List<Formula> disjunctedFormulas = collectJunctionedFormulas(or);

                boolean foundCounterExample = false;
                for (Formula formula : disjunctedFormulas) {
                    if (formula instanceof NestedCondition) {
                        NestedCondition nc = (NestedCondition) formula;
                        if (nc.getConclusion().getFormula() instanceof Not) {
                            // all good, keep checking
                        } else {
                            foundCounterExample = true;
                            break;
                        }
                    } else {
                        foundCounterExample = true;
                    }
                }

                if (foundCounterExample) {
                    // Can't simplify. Handle the child and return.
                    not.setChild(this.doSwitch(child));
                    return not;
                } else {
                    // Transform into an And of negations
                    didSomething = true;
                    List<Not> negations = negate(disjunctedFormulas);
                    Formula and = createConjunction(negations);
                    return and;
                }

            } else if (child instanceof Not) {
                didSomething = true;
                Formula newChild = this.doSwitch(((Not) child).getChild());
                return newChild;
            }

            not.setChild(this.doSwitch(child));
            return not;
        }

        @Override
        public Formula caseAnd(And and) {
            Formula left = and.getLeft();
            Formula right = and.getRight();
            Formula subFalse = left.isFalse() ? left : right.isFalse() ? right
                    : null;

            Formula subTrue = left.isTrue() ? left : right.isTrue() ? right
                    : null;

            if (subFalse != null) {
                didSomething = true;
                return createFalse();
            } else if (subTrue != null) {
                Formula other = left == subTrue ? right : left;
                didSomething = true;
                return this.doSwitch(other);
            } else {
                and.setLeft(this.doSwitch(left));
                and.setRight(this.doSwitch(right));
                return and;
            }
        }

        @Override
        public Formula caseOr(Or or) {
            Formula subFalse = or.getLeft().isFalse() ? or.getLeft() : or
                    .getRight().isFalse() ? or.getRight() : null;

            Formula subTrue = or.getLeft().isTrue() ? or.getLeft() : or
                    .getRight().isTrue() ? or.getRight() : null;

            if (subTrue != null) {
                didSomething = true;
                return createTrue();
            } else if (subFalse != null) {
                Formula other = or.getLeft() == subFalse ? or.getRight() : or
                        .getLeft();
                didSomething = true;
                return this.doSwitch(other);
            } else {
                or.setLeft(this.doSwitch(or.getLeft()));
                or.setRight(this.doSwitch(or.getRight()));
                return or;
            }
        }

        @Override
        public Formula caseNestedCondition(NestedCondition ngc) {
            Graph conclusion = ngc.getConclusion();
            Formula nestedFormula = conclusion.getFormula();
            if (nestedFormula != null) {
                if (nestedFormula.isTrue()) {
                    didSomething = true;
                    conclusion.setFormula(null);
                } else if (nestedFormula.isFalse()) {
                    didSomething = true;
                    return createFalse();
                } else {
                    // Simplify the nested formula
                    Formula simplifiedNested = this.doSwitch(nestedFormula);
                    conclusion.setFormula(simplifiedNested);
                }
            }
            return ngc;
        }
    }

    public static Formula simplify(Formula ngc) {
        // NGCOneShotSimplifier simplifier = new NGCOneShotSimplifier();
        // do {
        // ngc = simplifier.simplify(ngc);
        // } while (simplifier.didSomething());
        ngc = new FullSimplifier().transform(ngc);
        return ngc;
    }

    public static Formula createTrue() {
        NestedCondition tTrue = HF.createNestedCondition();
        tTrue.setConclusion(HF.createGraph("TRUE"));
        HenshinUtils.annotate(tTrue, "TRUE", null);
        return tTrue;
    }

    public static boolean isTrue(Formula f) {
        if (f instanceof NestedConditionImpl) {
            NestedConditionImpl nc = (NestedConditionImpl) f;
            // Graph conclusion = nc.getConclusion();
            // The following is to avoid resolving a Graph which has been dumped
            // to a file
            Graph conclusion = (Graph) nc.basicGetConclusion();
            if (conclusion != null) {
                String name = conclusion.getName();
                return name != null && name.contains("TRUE");
            } else {
                return false;
            }
        }
        return false;
    }

    public static Formula createFalse() {
        Not fFalse = HF.createNot();
        fFalse.setChild(createTrue());
        HenshinUtils.annotate(fFalse, "FALSE", null);
        return fFalse;
    }

    public static boolean isFalse(Formula f) {
        if (f instanceof Not) {
            Not not = (Not) f;
            Formula child = ((NotImpl) not).basicGetChild();
            if (child != null) {
                return isTrue(child);
            } else {
                return false;
            }
        }
        return false;
    }

    public static NestedCondition createNestedCondition(Morphism cdtMorphism) {
        NestedCondition newCdt = HenshinFactory.eINSTANCE
                .createNestedCondition();
        newCdt.setConclusion(cdtMorphism.getTarget());
        for (Mapping m : cdtMorphism) {
            newCdt.getMappings().add(m.getOrigin(), m.getImage());
        }
        return newCdt;
    }

    public static Not negate(Formula in) {
        Not not = HF.createNot();
        not.setChild(in);
        return not;
    }

    public static List<Not> negate(List<? extends Formula> formulas) {
        return Lists.newArrayList(Lists.transform(formulas,
                new Function<Formula, Not>() {
                    @Override
                    public Not apply(Formula in) {
                        Not not = negate(in);
                        return not;
                    }
                }));
    }

    public static Formula createConjunction(List<? extends Formula> formulas) {
        formulas = Lists.newArrayList(Collections2.filter(formulas,
                Predicates.notNull()));
        Collections.reverse(formulas);
        Formula current = formulas.get(0);
        List<? extends Formula> toDisjunt = formulas
                .subList(1, formulas.size());
        for (Formula f : toDisjunt) {
            And and = HF.createAnd();
            and.setLeft(f);
            and.setRight(current);
            current = and;
        }
        return current;
    }

    public static Formula createDisjunction(List<? extends Formula> formulas) {
        Collections.reverse(formulas);
        Formula current = formulas.get(0);
        List<? extends Formula> toDisjunt = formulas
                .subList(1, formulas.size());
        for (Formula f : toDisjunt) {
            Or or = HF.createOr();
            or.setLeft(f);
            or.setRight(current);
            current = or;
        }
        return current;
    }

    public static Formula createConjunction(Formula... formulas) {
        return createConjunction(Arrays.asList(formulas));
    }

    public static Formula createDisjunction(Formula... formulas) {
        return createDisjunction(Arrays.asList(formulas));
    }

    public static List<Formula> collectJunctionedFormulas(final BinaryFormula op) {
        HenshinSwitch<List<Formula>> collector = new HenshinSwitch<List<Formula>>() {
            private ArrayList<Formula> result = new ArrayList<Formula>();

            @Override
            public List<Formula> defaultCase(EObject object) {
                return result;
            }

            @Override
            public List<Formula> caseBinaryFormula(BinaryFormula subOp) {
                Formula left = subOp.getLeft();
                Formula right = subOp.getRight();
                if (left.eClass() == op.eClass()) {
                    this.doSwitch(left);
                } else {
                    result.add(left);
                }

                if (right.eClass() == op.eClass()) {
                    this.doSwitch(right);
                } else {
                    result.add(right);
                }

                return result;
            }

        };

        return collector.doSwitch(op);
    }

    public static void appendToAC(Graph hostGraph, Formula ac) {
        Formula formula = hostGraph.getFormula();
        if (formula != null) {
            ac = createConjunction(ac, formula);
        }
        hostGraph.setFormula(ac);
    }

    public static Graph getHostGraph(Formula post) {
        EObject container = post.eContainer();
        while (container != null) {
            if (container instanceof Graph) {
                return (Graph) container;
            }
            container = container.eContainer();
        }
        return null;
    }

    /**
     * Does not navigate into NestedConditions
     * 
     * @param f
     * @return
     */
    public static List<NestedCondition> getNestedConditions(Formula f) {
        final List<NestedCondition> result = Lists.newArrayList();
        if (f != null) {
            new HenshinSwitch<Object>() {
                public Object caseNestedCondition(NestedCondition object) {
                    result.add(object);
                    return object;
                }

                public Object caseBinaryFormula(BinaryFormula object) {
                    this.doSwitch(object.getLeft());
                    this.doSwitch(object.getRight());
                    return object;
                }

                public Object caseUnaryFormula(UnaryFormula object) {
                    this.doSwitch(object.getChild());
                    return object;
                }

            }.doSwitch(f);
        }

        return result;
    }

    /**
     * Navigates into NestedConditions.
     * 
     * @param f
     * @return
     */
    public static List<NestedCondition> getAllNestedConditions(Formula f) {
        final List<NestedCondition> result = Lists.newArrayList();
        if (f != null) {
            new HenshinSwitch<Object>() {
                public Object caseNestedCondition(NestedCondition object) {
                    result.add(object);
                    Graph conclusion = ((NestedConditionImpl) object)
                            .basicGetConclusion();
                    // if it's a proxy, do not navigate it
                    if (!conclusion.eIsProxy()) {
                        Formula formula = conclusion.getFormula();
                        if (formula != null) {
                            this.doSwitch(formula);
                        }
                    }
                    return object;
                }

                public Object caseBinaryFormula(BinaryFormula object) {
                    this.doSwitch(object.getLeft());
                    this.doSwitch(object.getRight());
                    return object;
                }

                public Object caseUnaryFormula(UnaryFormula object) {
                    this.doSwitch(object.getChild());
                    return object;
                }

            }.doSwitch(f);
        }

        return result;
    }

    public static int getNestedLevels(Graph host) {
        int n = 0;
        Formula formula = host.getFormula();
        if (formula != null) {
            n = new HenshinSwitch<Integer>() {
                int n = 0;

                @Override
                public Integer caseNestedCondition(NestedCondition object) {
                    return n = Math.max(n,
                            1 + getNestedLevels(((NestedConditionImpl) object)
                                    .basicGetConclusion()));
                }

                @Override
                public Integer caseBinaryFormula(BinaryFormula object) {
                    this.doSwitch(object.getLeft());
                    this.doSwitch(object.getRight());
                    return n;
                }

                @Override
                public Integer caseUnaryFormula(UnaryFormula object) {
                    this.doSwitch(object.getChild());
                    return n;
                }
            }.doSwitch(formula);
        }
        return n;
    }

    /**
     * Check that conditions nested in 'nc' correctly reference the conclusion
     * of 'nc' in their mappings.
     * 
     * @param nc
     * @throws ATLAnalyserRuntimeException if an inconsistency is found.
     */
    public static void checkConsistency(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();
        Formula formula = conclusion.getFormula();
        if (formula != null) {
            List<NestedCondition> nestedConditions2 = getNestedConditions(formula);
            for (NestedCondition nestedCondition2 : nestedConditions2) {
                MappingList mappings = nestedCondition2.getMappings();
                for (Mapping m : mappings) {
                    if (m.getOrigin().eContainer() != conclusion) {
                        throw new ATLAnalyserRuntimeException();
                    }
                }
            }
        }
    }
}
