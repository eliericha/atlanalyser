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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.InterpreterFactory;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.util.InterpreterUtil;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;

import fr.tpt.atlanalyser.utils.NGCUtils;

public class NGCSimplificationTest {

    private static final InterpreterFactory IF = InterpreterFactory.INSTANCE;
    private static final HenshinFactory     HF = HenshinFactory.eINSTANCE;
    static private Module                   module;

    @Before
    public void setUp() throws Exception {
        // init Henshin package
        HenshinPackage.eINSTANCE.getNode();
        HenshinResource res = new HenshinResource(
                URI.createFileURI("ngc_simpl.henshin"));
        res.load(Collections.emptyMap());
        module = (Module) res.getContents().get(0);
    }

    @Ignore
    @Test
    public void testAndFalse() {
        Graph g = HF.createGraph();
        And and = HF.createAnd();
        g.setFormula(and);
        and.setLeft(createFalse());
        NestedCondition otherCond = HF.createNestedCondition();
        and.setRight(otherCond);
        Graph otherCondConc = HF.createGraph("notTrue");
        otherCond.setConclusion(otherCondConc);
        HF.createNode(otherCondConc, EcorePackage.Literals.EPACKAGE, null);

        EGraph eGraph = IF.createEGraph();
        eGraph.add(g);

        Rule andFalse = (Rule) module.getUnit("andFalse");

        Engine engine = IF.createEngine();
        List<Match> matches = InterpreterUtil.findAllMatches(engine, andFalse,
                eGraph, null);

        System.out.println(Joiner.on("\n").join(matches));
    }

    private NestedCondition createUndeterminedCond() {
        NestedCondition otherCond = HF.createNestedCondition();
        Graph otherCondConc = HF.createGraph("someCond");
        otherCond.setConclusion(otherCondConc);
        HF.createNode(otherCondConc, EcorePackage.Literals.EPACKAGE, null);
        assertFalse(otherCond.isTrue() || otherCond.isFalse());
        return otherCond;
    }

    @Test
    public void testAndFalse1() {
        And and = HF.createAnd();
        and.setLeft(createFalse());
        NestedCondition otherCond = createUndeterminedCond();
        and.setRight(otherCond);

        Formula simplified = NGCUtils.simplify(and);

        assertTrue(simplified.isFalse());
    }

    private Formula createFalse() {
        return NGCUtils.createFalse();
    }

    @Test
    public void testAndTrue() {
        And and = HF.createAnd();
        and.setLeft(createTrue());
        NestedCondition otherCond = createUndeterminedCond();
        and.setRight(otherCond);

        Formula simplified = NGCUtils.simplify(and);

        assertEquals(otherCond, simplified);
    }

    private Formula createTrue() {
        return NGCUtils.createTrue();
    }

    @Test
    public void testOrTrue() {
        Or or = HF.createOr();
        or.setLeft(createTrue());
        NestedCondition otherCond = createUndeterminedCond();
        or.setRight(otherCond);

        Formula simplified = NGCUtils.simplify(or);

        assertTrue(simplified.isTrue());
    }

    @Test
    public void testOrFalse() {
        Or or = HF.createOr();
        or.setLeft(createFalse());
        NestedCondition otherCond = createUndeterminedCond();
        or.setRight(otherCond);

        Formula simplified = NGCUtils.simplify(or);

        assertEquals(otherCond, simplified);
    }

    @Test
    public void testNotNot() {
        Not evenNots = HF.createNot();
        Not parentNot = evenNots;
        for (int i = 0; i < 5; i++) {
            Not newNot = HF.createNot();
            parentNot.setChild(newNot);
            parentNot = newNot;
        }
        parentNot.setChild(createTrue());

        Not oddNots = HF.createNot();
        oddNots.setChild(EcoreUtil.copy(evenNots));

        Formula evenSimpl = NGCUtils.simplify(evenNots);
        Formula oddSimpl = NGCUtils.simplify(oddNots);

        assertTrue(evenSimpl.isTrue());
        assertTrue(evenSimpl instanceof NestedCondition);
        assertTrue(oddSimpl.isFalse());
        assertTrue(oddSimpl instanceof Not);
        assertTrue(((Not) oddSimpl).getChild() instanceof NestedCondition);
    }

    @Test
    public void testNestedNotAndOr() {
        And and = HF.createAnd();
        and.setLeft(createTrue());
        Not not = HF.createNot();
        and.setRight(not);
        Or or = HF.createOr();
        not.setChild(or);
        or.setLeft(createFalse());
        Not not1 = HF.createNot();
        or.setRight(not1);
        Formula undCond = createUndeterminedCond();
        not1.setChild(undCond);

        Formula simplified = NGCUtils.simplify(and);

        assertEquals(undCond, simplified);
    }

}
