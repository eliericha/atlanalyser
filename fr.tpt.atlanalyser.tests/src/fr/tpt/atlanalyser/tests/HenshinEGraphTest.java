package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.*;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.interpreter.util.HenshinEGraph;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Node;
import org.junit.Test;

public class HenshinEGraphTest {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Test
    public void test() {
        Graph g = HF.createGraph("G");
        Node p = HF.createNode(g, EcorePackage.Literals.EPACKAGE, "p");
        Node c1 = HF.createNode(g, EcorePackage.Literals.ECLASS, "c1");
        Node c2 = HF.createNode(g, EcorePackage.Literals.ECLASS, "c2");
        Node c3 = HF.createNode(g, EcorePackage.Literals.ECLASS, "c3");

        Edge e0 = HF.createEdge(p, c3,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        e0.setIndex("0");

        Edge e1 = HF.createEdge(p, c2,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        e1.setIndex("1");

        Edge e2 = HF.createEdge(p, c1,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        e2.setIndex("3");

        HenshinEGraph henshinEGraph = new HenshinEGraph(g);

        EPackage ePkg = (EPackage) henshinEGraph.getNode2ObjectMap().get(p);
        EList<EClassifier> eClassifiers = ePkg.getEClassifiers();

        Map<EObject, Node> object2NodeMap = henshinEGraph.getObject2NodeMap();

        assertEquals(c3, object2NodeMap.get(eClassifiers.get(0)));
        assertEquals(c2, object2NodeMap.get(eClassifiers.get(1)));
        assertEquals(c1, object2NodeMap.get(eClassifiers.get(2)));
    }
}
