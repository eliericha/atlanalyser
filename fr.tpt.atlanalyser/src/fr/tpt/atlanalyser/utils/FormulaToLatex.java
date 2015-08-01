package fr.tpt.atlanalyser.utils;

import java.io.File;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class FormulaToLatex {

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            System.exit(-1);
        }

        String inputHenshin = args[0];
        HenshinResourceSet resSet = new HenshinResourceSet();
        Resource resource = resSet.getResource(inputHenshin);

        Module module = (Module) resource.getContents().get(0);

        String latex = HenshinFormulaToLatex.toLatex(module);

        System.out.println(latex);
    }

    private static void printUsage() {
        System.out.printf("Usage: %s <input.henshin>",
                FormulaToLatex.class.getSimpleName());
    }

}
