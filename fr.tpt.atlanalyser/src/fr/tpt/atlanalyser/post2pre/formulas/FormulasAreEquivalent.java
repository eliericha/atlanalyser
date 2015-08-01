package fr.tpt.atlanalyser.post2pre.formulas;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.util.EGraphIsomorphyChecker;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.UnaryFormula;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.utils.HenshinUtils;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class FormulasAreEquivalent extends HenshinSwitch<Boolean> {

    private Morphism anchor;

    public FormulasAreEquivalent(Formula f1) {
        this.f1 = f1;
    }

    public FormulasAreEquivalent(Formula f1, Morphism anchor) {
        this.f1 = f1;
        this.anchor = anchor;
        if (anchor != null) {
            assert anchor.getSource() == NGCUtils.getHostGraph(f1);
        }
    }

    private Formula f1;

    @Override
    public Boolean caseBinaryFormula(BinaryFormula object) {
        if (f1.eClass() == object.eClass()) {
            Formula left1 = ((BinaryFormula) f1).getLeft();
            Formula right1 = ((BinaryFormula) f1).getRight();
            return new FormulasAreEquivalent(left1, anchor).doSwitch(object
                    .getLeft())
                    && new FormulasAreEquivalent(right1, anchor)
                            .doSwitch(object.getRight());
        }
        return false;
    }

    @Override
    public Boolean caseUnaryFormula(UnaryFormula object) {
        if (f1.eClass() == object.eClass()) {
            Formula child1 = ((UnaryFormula) f1).getChild();
            return new FormulasAreEquivalent(child1, anchor).doSwitch(object
                    .getChild());
        }
        return false;
    }

    @Override
    public Boolean caseNestedCondition(NestedCondition nc2) {
        if (f1.eClass() == nc2.eClass()) {
            NestedCondition nc1 = (NestedCondition) f1;

            Graph conc1 = nc1.getConclusion();
            Formula formula1 = conc1.getFormula();
            Graph conc2 = nc2.getConclusion();
            Formula formula2 = conc2.getFormula();

            if (formula1 != null ^ formula2 != null) {
                return false;
            }

            HenshinEGraph eg1 = new HenshinEGraph(conc1);
            HenshinEGraph eg2 = new HenshinEGraph(conc2);
            EGraphIsomorphyChecker isomorphyChecker = new EGraphIsomorphyChecker(
                    eg1, null);

            Map<EObject, EObject> partialMatch = null;
            Morphism cdt1 = null;
            Morphism cdt2 = null;
            if (anchor != null) {
                // initialize morphisms
                assert anchor.getSource() == nc1.getHost();
                assert anchor.getTarget() == nc2.getHost();
                cdt1 = new Morphism(nc1);
                cdt2 = new Morphism(nc2);

                // initialize partial match
                partialMatch = Maps.newHashMap();
                for (Mapping m : cdt2) {
                    Node origin = m.getOrigin();
                    Node imageIn1 = cdt1.getImage(anchor.getOrigin(origin));
                    if (imageIn1 != null) {
                        EObject obj1 = eg1.getNode2ObjectMap().get(imageIn1);
                        EObject obj2 = eg2.getNode2ObjectMap()
                                .get(m.getImage());
                        partialMatch.put(obj1, obj2);
                    }
                }
            }

            Map<EObject, EObject> isomorphMap = isomorphyChecker
                    .getIsomorphism(eg2, partialMatch);

            HenshinUtils.dispose(eg1);
            HenshinUtils.dispose(eg2);

            if (isomorphMap != null) {
                Morphism isomorphism = new Morphism(eg1, eg2, isomorphMap);

                if (anchor != null) {
                    Morphism compose1 = cdt1.compose(isomorphism);
                    Morphism compose2 = anchor.compose(cdt2);

                    if (!compose1.equals(compose2)) {
                        return false;
                    }
                }

                if (formula1 != null && formula2 != null) {
                    return new FormulasAreEquivalent(formula1, isomorphism)
                            .doSwitch(formula2);
                } else if (formula1 != null ^ formula2 != null) {
                    return false;
                } else {
                    // both formulas are null
                    return true;
                }

            } else {
                // no isomorphism
                return false;
            }
        }
        return false;
    }

}
