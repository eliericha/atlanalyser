package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.*;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Test;

import fr.tpt.atlanalyser.post2pre.formulas.FormulasAreEquivalent;

public class FormulasAreEquivalentTest {

    private static final EcorePackage   EP = EcorePackage.eINSTANCE;
    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Test
    public void testSimpleConditions() {
        NestedCondition nc1 = HF.createNestedCondition();
        Graph g1 = HF.createGraph();
        nc1.setConclusion(g1);
        HF.createNode(g1, EP.getEPackage(), "p");
        HF.createNode(g1, EP.getEClass(), "c");
        HF.createEdge(g1.getNode("p"), g1.getNode("c"),
                EP.getEPackage_EClassifiers());

        NestedCondition nc2 = EcoreUtil.copy(nc1);

        assertTrue(new FormulasAreEquivalent(nc1).doSwitch(nc2));

        nc2.getConclusion().getEdges().clear();

        assertFalse(new FormulasAreEquivalent(nc1).doSwitch(nc2));
    }

    @Test
    public void testNestedConditions() {
        HenshinResourceSet resSet = new HenshinResourceSet();
        Resource resource = resSet.getResource("pre.henshin");
        Module module = (Module) resource.getContents().get(0);

        Rule rule = (Rule) module.getUnits().get(0);
        Formula pre = rule.getLhs().getFormula();

        Formula preCopy = EcoreUtil.copy(pre);

        assertTrue(new FormulasAreEquivalent(pre).doSwitch(preCopy));
    }

}
