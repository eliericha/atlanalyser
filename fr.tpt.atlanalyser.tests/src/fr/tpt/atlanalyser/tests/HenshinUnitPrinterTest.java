package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.*;

import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.LoopUnit;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.SequentialUnit;
import org.junit.Test;

import fr.tpt.atlanalyser.utils.HenshinUnitPrinter;

public class HenshinUnitPrinterTest {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Test
    public void testPrint() {
        Rule r1 = HF.createRule("R1");
        Rule r2 = HF.createRule("R2");

        LoopUnit r1Loop = HF.createLoopUnit();
        r1Loop.setSubUnit(r1);
        LoopUnit r2Loop = HF.createLoopUnit();
        r2Loop.setSubUnit(r2);

        SequentialUnit main = HF.createSequentialUnit();
        main.getSubUnits().add(r1Loop);
        main.getSubUnits().add(r2Loop);

        System.out.println(HenshinUnitPrinter.print(main));
    }

}
