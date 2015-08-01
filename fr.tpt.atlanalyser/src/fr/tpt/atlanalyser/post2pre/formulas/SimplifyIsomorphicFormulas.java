package fr.tpt.atlanalyser.post2pre.formulas;

import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;

import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class SimplifyIsomorphicFormulas extends FullSimplifier {

    @Override
    protected LimitedSimplifier newSimplifier(int n) {
        return new SimplifyIsomorphicFormulas();
    }

    @Override
    public Formula caseAnd(And and) {
        return caseBinaryFormula(and);
    }

    @Override
    public Formula caseOr(Or or) {
        return caseBinaryFormula(or);
    }

    private Formula caseBinaryFormula(BinaryFormula binF) {
        Formula simplBinF = null;
        if (binF instanceof And) {
            And and = (And) binF;
            simplBinF = super.caseAnd(and);
        } else if (binF instanceof Or) {
            Or or = (Or) binF;
            simplBinF = super.caseOr(or);
        } else {
            simplBinF = super.transform(binF);
        }

        if (binF != simplBinF) {
            EcoreUtil.replace(binF, simplBinF);
        }

        if (simplBinF instanceof BinaryFormula) {
            binF = (BinaryFormula) simplBinF;

            List<Formula> junctedFormulas = NGCUtils
                    .collectJunctionedFormulas(binF);

            for (Formula f1 : junctedFormulas) {
                for (Formula f2 : junctedFormulas) {
                    if (f1 != f2) {
                        NestedCondition nc1 = null;
                        NestedCondition nc2 = null;
                        if (f1 instanceof NestedCondition
                                && f2 instanceof Not
                                && ((Not) f2).getChild() instanceof NestedCondition) {
                            nc1 = (NestedCondition) f1;
                            nc2 = (NestedCondition) ((Not) f2).getChild();
                        } else if (f1 instanceof NestedCondition
                                && f2 instanceof NestedCondition) {
                            nc1 = (NestedCondition) f1;
                            nc2 = (NestedCondition) f2;
                        }

                        if (nc1 != null && nc2 != null) {
                            Graph host = nc1.getHost();
                            Morphism anchor = new Morphism(host, host);
                            for (Node n : host.getNodes()) {
                                anchor.add(n, n);
                            }

                            Boolean areEquivalent = new FormulasAreEquivalent(
                                    nc1, anchor).doSwitch(nc2);

                            if (areEquivalent) {
                                onDiscardBeforeTransform(binF);
                                if (f2 instanceof Not) {
                                    if (binF instanceof And) {
                                        return NGCUtils.createFalse();
                                    } else if (binF instanceof Or) {
                                        return NGCUtils.createTrue();
                                    }
                                } else {
                                    junctedFormulas.remove(f2);
                                    if (binF instanceof And) {
                                        return NGCUtils
                                                .createConjunction(junctedFormulas);
                                    } else if (binF instanceof Or) {
                                        return NGCUtils
                                                .createDisjunction(junctedFormulas);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return simplBinF;
    }
}
