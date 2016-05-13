package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Before;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.EClassUtils;
import fr.tpt.atlanalyser.utils.EPackageUtils;

public class UtilsTest {

    EPackage pkg;
    EPackage program;

    @Before
    public void setUp() throws IOException {
        XMIResourceImpl res = new XMIResourceImpl(URI.createFileURI("in.ecore"));
        res.load(Collections.emptyMap());
        pkg = (EPackage) res.getContents().get(0);

        ResourceSet rs = new HenshinResourceSet();
        program = EPackageUtils.loadDynamicEcore(rs, "Program.ecore").get(0);
    }

    @Test
    public void test_subclasses() {
        EClass rootEClass = (EClass) pkg.getEClassifier("RootIn");
        Set<EClass> allSubclasses = EClassUtils.getAllSubclasses(rootEClass);
        assertEquals(2, allSubclasses.size());

        EClass eClass = EPackageUtils.findEClassRecursively(program,
                "Structure");
        allSubclasses = EClassUtils.getAllSubclasses(eClass);
        assertTrue(allSubclasses.size() > 0);
    }

    @Test
    public void test_allcontainingrefs() {
        EClass eClass = EPackageUtils.findEClassRecursively(program,
                "Structure");
        Set<EReference> containingRefs = EClassUtils
                .getAllPossibleContainingReferences(eClass);

        assertTrue(containingRefs.size() > 0);
    }
}
