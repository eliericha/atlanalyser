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
package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.javatuples.Pair;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.GraphCopier;
import fr.tpt.atlanalyser.utils.Morphism;

public class MorphismTest {

    private static final EReference     EPACKAGE_ESUBPACKAGES = EcorePackage.Literals.EPACKAGE__ESUBPACKAGES;
    private static final EReference     EPACKAGE_ECLASSIFIERS = EcorePackage.Literals.EPACKAGE__ECLASSIFIERS;
    private static final EClass         ECLASS                = EcorePackage.Literals.ECLASS;
    private static final EClass         EPACKAGE              = EcorePackage.Literals.EPACKAGE;
    private static final HenshinFactory HF                    = HenshinFactory.eINSTANCE;
    private Graph                       g1;
    private Graph                       g2;
    private static Adapter              failOnModified;

    static {
        failOnModified = new Adapter() {

            @Override
            public void setTarget(Notifier newTarget) {
                // TODO Auto-generated method stub

            }

            @Override
            public void notifyChanged(Notification notification) {
                fail("Object " + notification.getNotifier()
                        + " must not be modified");
            }

            @Override
            public boolean isAdapterForType(Object type) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Notifier getTarget() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public MorphismTest() {
        g1 = HF.createGraph("G1");
        g2 = HF.createGraph("G2");

        HF.createNode(g1, EPACKAGE, "p1");
        HF.createNode(g1, EPACKAGE, "p2");
        HF.createNode(g1, EPACKAGE, "p3");

        HF.createNode(g2, EPACKAGE, "p1");
        HF.createNode(g2, EPACKAGE, "p2");
        HF.createNode(g2, EPACKAGE, "p3");
    }

    @Test
    public void testIsInjective() {
        Morphism m = new Morphism(g1, g2);

        m.add(g1.getNode("p1"), g2.getNode("p1"));
        m.add(g1.getNode("p2"), g2.getNode("p2"));

        assertTrue(m.isInjective());

        m.add(g1.getNode("p3"), g2.getNode("p2"));

        assertFalse(m.isInjective());
    }

    @Test
    public void testIsSurjective() {
        Morphism m = new Morphism(g1, g2);

        m.add(g1.getNode("p1"), g2.getNode("p1"));
        m.add(g1.getNode("p2"), g2.getNode("p2"));

        assertFalse(m.isSurjective());

        m.add(g1.getNode("p2"), g2.getNode("p3"));

        assertTrue(m.isSurjective());
    }

    @Test
    public void testCompose() {
        Morphism m1 = new Morphism(g1, g2);

        m1.add(g1.getNode("p1"), g2.getNode("p1"));
        m1.add(g1.getNode("p2"), g2.getNode("p2"));

        Graph g3 = HF.createGraph("G3");

        HF.createNode(g3, EPACKAGE, "p1");
        HF.createNode(g3, EPACKAGE, "p2");
        HF.createNode(g3, EPACKAGE, "p3");

        Morphism m2 = new Morphism(g2, g3);

        m2.add(g2.getNode("p1"), g3.getNode("p3"));
        m2.add(g2.getNode("p2"), g3.getNode("p3"));

        Morphism m3 = m1.compose(m2);

        assertEquals(g1, m3.getSource());
        assertEquals(g3, m3.getTarget());
        assertEquals(2, m3.size());
        assertEquals(g3.getNode("p3"), m3.getImage(g1.getNode("p1")));
        assertEquals(g3.getNode("p3"), m3.getImage(g1.getNode("p2")));
        assertNull(m3.getImage(g1.getNode("p3")));
        assertNull(m3.getOrigin(g3.getNode("p1")));
    }

    @Test
    public void testEdgeMapping() {
        Edge e1 = HF.createEdge(g1.getNode("p1"), g1.getNode("p2"),
                EPACKAGE_ESUBPACKAGES);
        Edge e2 = HF.createEdge(g1.getNode("p2"), g1.getNode("p3"),
                EPACKAGE_ESUBPACKAGES);
        Edge e3 = HF.createEdge(g2.getNode("p1"), g2.getNode("p2"),
                EPACKAGE_ESUBPACKAGES);
        Edge e4 = HF.createEdge(g2.getNode("p2"), g2.getNode("p3"),
                EPACKAGE_ESUBPACKAGES);

        Morphism m1 = new Morphism(g1, g2);
        m1.add(e1, e3);

        assertEquals(e3, m1.getImage(e1));
        assertEquals(e1, m1.getOrigin(e3));
        assertNull(m1.getImage(e2));
        assertNull(m1.getOrigin(e4));

        try {
            m1.add(e2, e3);
            fail("Excepted exception was not thrown");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testPushoutComplement() {
        Graph A = HF.createGraph("A");
        HF.createNode(A, EPACKAGE, "p");
        HF.createNode(A, ECLASS, "c");

        Morphism a = GraphCopier.copy(A);
        Graph B = a.getTarget();
        B.setName("B");
        HF.createNode(B, ECLASS, "c1");
        HF.createEdge(B.getNode("p"), B.getNode("c"), EPACKAGE_ECLASSIFIERS);

        Morphism b = GraphCopier.copy(B);
        Graph D = b.getTarget();
        D.setName("D");
        HF.createNode(D, EPACKAGE, "p1");
        HF.createEdge(D.getNode("p"), D.getNode("p1"), EPACKAGE_ESUBPACKAGES);

        A.eAdapters().add(failOnModified);
        B.eAdapters().add(failOnModified);
        D.eAdapters().add(failOnModified);

        Pair<Morphism, Morphism> pushoutComplement = Morphism
                .getPushoutComplement(a, b);

        assertNotNull(pushoutComplement);
    }

    @Test
    public void testPushoutComplementNoComplement() {
        Graph A = HF.createGraph("A");
        HF.createNode(A, EPACKAGE, "p");
        HF.createNode(A, ECLASS, "c");

        Morphism a = GraphCopier.copy(A);
        Graph B = a.getTarget();
        B.setName("B");
        HF.createNode(B, ECLASS, "c1");
        HF.createEdge(B.getNode("p"), B.getNode("c"), EPACKAGE_ECLASSIFIERS);

        Morphism b = GraphCopier.copy(B);
        Graph D = b.getTarget();
        D.setName("D");
        HF.createNode(D, EPACKAGE, "p1");
        HF.createEdge(D.getNode("p"), D.getNode("p1"), EPACKAGE_ESUBPACKAGES);
        HF.createEdge(D.getNode("p1"), D.getNode("c1"), EPACKAGE_ECLASSIFIERS);

        A.eAdapters().add(failOnModified);
        B.eAdapters().add(failOnModified);
        D.eAdapters().add(failOnModified);

        Pair<Morphism, Morphism> pushoutComplement = Morphism
                .getPushoutComplement(a, b);

        assertNull(pushoutComplement);

    }
    
    @Test
    public void testMorphismNotifier() {
        Graph g1 = HF.createGraph();
        Graph g2 = HF.createGraph();
        Morphism m = new Morphism(g1, g2);
        
        assertEquals(m, g1.eAdapters().get(0));
        assertEquals(m, g2.eAdapters().get(0));
        
        m.dispose();
        
        assertEquals(0, g1.eAdapters().size());
        assertEquals(0, g2.eAdapters().size());
    }

}
