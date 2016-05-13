package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.HenshinFormulaToLatex;

public class HenshinFormulaToLatexTest {

    @Test
    public void testToLatex() {
        HenshinResourceSet resSet = new HenshinResourceSet();
        Resource resource = resSet.getResource("pre.henshin");
        Module module = (Module) resource.getContents().get(0);

        Rule rule = (Rule) module.getUnits().get(0);
        Formula pre = rule.getLhs().getFormula();

        String latex = HenshinFormulaToLatex.toLatex(pre);

        System.out.println(latex);
    }

}
