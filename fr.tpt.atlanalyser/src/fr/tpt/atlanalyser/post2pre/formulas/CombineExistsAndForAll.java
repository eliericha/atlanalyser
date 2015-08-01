package fr.tpt.atlanalyser.post2pre.formulas;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.util.EGraphIsomorphyChecker;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.utils.CopierWithResolveErrorNotification;
import fr.tpt.atlanalyser.utils.HenshinUtils;
import fr.tpt.atlanalyser.utils.Morphism;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class CombineExistsAndForAll extends FullSimplifier {

    @Override
    protected LimitedSimplifier newSimplifier(int n) {
        return new CombineExistsAndForAll();
    }

    @Override
    public Formula caseAnd(And and) {
        Formula simplAnd = super.caseAnd(and);

        if (simplAnd instanceof And) {
            and = (And) simplAnd;

            List<Formula> junctionedFormulas = NGCUtils
                    .collectJunctionedFormulas(and);

            for (Formula f1 : junctionedFormulas) {
                for (Formula f2 : junctionedFormulas) {
                    if (f1 != f2) {
                        if (NGCUtils.isForAll(f2)) {

                            NestedCondition nc2 = (NestedCondition) ((Not) f2)
                                    .getChild();

                            List<NestedCondition> ncsInF1;
                            if (f1 instanceof BinaryFormula) {
                                ncsInF1 = NGCUtils
                                        .collectJunctionedFormulas(
                                                (BinaryFormula) f1)
                                        .stream()
                                        .filter(f -> f instanceof NestedCondition)
                                        .map(f -> (NestedCondition) f)
                                        .collect(Collectors.toList());
                            } else if (f1 instanceof NestedCondition) {
                                ncsInF1 = Lists
                                        .newArrayList((NestedCondition) f1);
                            } else {
                                ncsInF1 = Collections.emptyList();
                            }

                            for (NestedCondition nc1 : ncsInF1) {

                                assert nc1 != null;

                                Graph conc1 = nc1.getConclusion();
                                Graph conc2 = nc2.getConclusion();

                                HenshinEGraph eg1 = new HenshinEGraph(conc1);
                                HenshinEGraph eg2 = new HenshinEGraph(conc2);

                                MappingList mappings1 = nc1.getMappings();
                                MappingList mappings2 = nc2.getMappings();

                                Map<EObject, EObject> partialMatch = Maps
                                        .newHashMap();
                                for (Mapping m : mappings1) {
                                    Node origin = m.getOrigin();
                                    Node imageIn2 = mappings2.getImage(origin,
                                            conc2);
                                    if (imageIn2 != null) {
                                        EObject obj1 = eg1.getNode2ObjectMap()
                                                .get(m.getImage());
                                        EObject obj2 = eg2.getNode2ObjectMap()
                                                .get(imageIn2);
                                        partialMatch.put(obj1, obj2);
                                    }
                                }

                                EGraphIsomorphyChecker checker = new EGraphIsomorphyChecker(
                                        eg1, null);
                                Map<EObject, EObject> isomorphMap = checker
                                        .getIsomorphism(eg2, partialMatch);

                                HenshinUtils.dispose(eg1);
                                HenshinUtils.dispose(eg2);

                                if (isomorphMap != null) {
                                    Morphism isomorphism = new Morphism(eg1,
                                            eg2, isomorphMap);
                                    Formula subF2 = ((Not) conc2.getFormula())
                                            .getChild();

                                    if (subF2 != null) {
                                        Formula subF2Copy = CopierWithResolveErrorNotification
                                                .copy(subF2);
                                        List<NestedCondition> nestedConditions = NGCUtils
                                                .getNestedConditions(subF2Copy);
                                        for (NestedCondition nc : nestedConditions) {
                                            for (Mapping m : nc.getMappings()) {
                                                Node image = isomorphism
                                                        .getOrigin(m
                                                                .getOrigin());
                                                m.setOrigin(image);
                                            }
                                        }

                                        conc1.setFormula(NGCUtils
                                                .createConjunction(
                                                        conc1.getFormula(),
                                                        subF2Copy));

                                        Formula subF1 = conc1.getFormula();

                                        if (subF1 instanceof And
                                                && subF2Copy instanceof Or
                                                && ((Or) subF2Copy).getLeft() instanceof Not) {
                                            // We have subF1 = a AND (b OR c).
                                            // Develop it into (a AND b) OR (a
                                            // AND
                                            // c) and simplify it.
                                            Formula a = ((And) subF1).getLeft();
                                            Formula b = ((Or) ((And) subF1)
                                                    .getRight()).getLeft();
                                            Formula c = ((Or) ((And) subF1)
                                                    .getRight()).getRight();

                                            Formula res = NGCUtils
                                                    .createDisjunction(
                                                            NGCUtils.createConjunction(
                                                                    a, b),
                                                            NGCUtils.createConjunction(
                                                                    CopierWithResolveErrorNotification
                                                                            .copy(a),
                                                                    c));

                                            conc1.setFormula(res);

                                            res = new SimplifyIsomorphicFormulas()
                                                    .transform(res);

                                            conc1.setFormula(res);
                                        }

                                        // if (junctionedFormulas.size() == 2) {
                                        // return f1;
                                        // }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return simplAnd;
    }
}
