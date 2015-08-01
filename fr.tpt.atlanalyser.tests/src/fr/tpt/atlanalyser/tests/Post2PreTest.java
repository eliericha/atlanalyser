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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.HenshinPackage;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResource;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import fr.tpt.atlanalyser.post2pre.Post2Pre;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class Post2PreTest {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testPostToRightAC() throws IOException {
        ResourceSet resSet = new HenshinResourceSet();
        Resource resource = resSet.createResource(URI
                .createFileURI("plop.henshin"));
        Module module = HF.createModule();
        resource.getContents().add(module);

        Rule rule = HF.createRule("CreateEClass");
        module.getUnits().add(rule);
        Node p = HF.createNode(rule.getLhs(), EcorePackage.Literals.EPACKAGE,
                "p");
        p.setAction(new Action(Action.Type.PRESERVE));
        Node c = HF
                .createNode(rule.getRhs(), EcorePackage.Literals.ECLASS, "c");
        HF.createEdge(rule.getMappings().getImage(p, rule.getRhs()), c,
                EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Rule postRule = HF.createRule("Post");
        module.getUnits().add(postRule);
        Graph emptyHostGraph = HF.createGraph();
        postRule.setLhs(emptyHostGraph);
        NestedCondition post1 = emptyHostGraph.createPAC("Postcondition");
        Graph conc = post1.getConclusion();
        Node p1 = HF.createNode(conc, EcorePackage.Literals.EPACKAGE, "p1");
        Node c1 = HF.createNode(conc, EcorePackage.Literals.ECLASS, "c1");
        Node c2 = HF.createNode(conc, EcorePackage.Literals.ECLASS, "c2");
        HF.createEdge(p1, c1, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);
        HF.createEdge(p1, c2, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS);

        Formula leftAC = new Post2Pre().postToLeftAC(post1, rule, null, null);
        rule.getLhs().setFormula(leftAC);

        Formula pre = Post2Pre.leftACToExistsConstraint(rule, leftAC);

        Rule preRule = HF.createRule("Pre");
        module.getUnits().add(preRule);
        preRule.setLhs((Graph) pre.eContainer());

        HenshinResource res = new HenshinResource(
                URI.createFileURI("out.henshin"));
        res.getContents().add(module);
        Map<Object, Object> options = Maps.newHashMap();
        // options.put(HenshinResource.OPTION_PROCESS_DANGLING_HREF,
        // HenshinResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
        res.save(options);
    }

    @Test
    public void testTrueFalseNCs() {
        assertTrue(NGCUtils.createTrue().isTrue());
        assertTrue(NGCUtils.createFalse().isFalse());
    }

    @Test
    public void testPost2PreFile() throws IOException {
        HenshinPackage.eINSTANCE.getNode();
        HenshinResource res = new HenshinResource(
                URI.createFileURI("in.henshin"));
        res.load(Collections.emptyMap());
        Module root = (Module) res.getContents().get(0);

        Formula post = ((Rule) root.getUnit("Post")).getLhs().getFormula();
        Rule rule = (Rule) root.getUnit("Rule");

        Formula pre = new Post2Pre().post2Pre(post, rule);

        Rule preRule = HF.createRule("Pre");
        Graph preLhs = HF.createGraph();
        preLhs.setFormula(pre);
        preRule.setLhs(preLhs);

        root.getUnits().add(preRule);

        Rule ruleCopy = EcoreUtil.copy(preRule);
        ruleCopy.setName("Pre_Simplified");
        Formula simplifiedLeftAC = NGCUtils.simplify(ruleCopy.getLhs()
                .getFormula());
        ruleCopy.getLhs().setFormula(simplifiedLeftAC);
        root.getUnits().add(ruleCopy);

        res.setURI(URI.createFileURI("out.henshin"));
        res.save(Collections.emptyMap());
    }

    @Test
    public void testPost2PreUnitsFile() throws IOException {
        HenshinPackage.eINSTANCE.getNode();
        HenshinResource res = new HenshinResource(
                URI.createFileURI("in.henshin"));
        res.load(Collections.emptyMap());
        Module root = (Module) res.getContents().get(0);

        Formula post = ((Rule) root.getUnit("Post")).getLhs().getFormula();

        Unit unit = root.getUnit("Main");

        Formula pre = new Post2Pre().post2Pre(post, unit, 3, false);

        Rule preRule = HF.createRule("Pre");
        Graph preLhs = HF.createGraph();
        preLhs.setFormula(pre);
        preRule.setLhs(preLhs);

        root.getUnits().add(preRule);

        res.setURI(URI.createFileURI("out.henshin"));
        res.save(Collections.emptyMap());
    }

    @Test
    public void testPost2PreUnitsWithSimplFile() throws IOException {
        HenshinPackage.eINSTANCE.getNode();
        HenshinResource res = new HenshinResource(
                URI.createFileURI("in.henshin"));
        res.load(Collections.emptyMap());
        Module root = (Module) res.getContents().get(0);

        Formula post = ((Rule) root.getUnit("Post")).getLhs().getFormula();

        Unit unit = root.getUnit("Main");

        Formula pre = new Post2Pre().post2Pre(post, unit, 3, false);

        Rule preRule = HF.createRule("Pre");
        Graph preLhs = HF.createGraph();
        preLhs.setFormula(pre);
        preRule.setLhs(preLhs);

        root.getUnits().add(preRule);

        res.setURI(URI.createFileURI("out.henshin"));
        res.save(Collections.emptyMap());
    }

}
