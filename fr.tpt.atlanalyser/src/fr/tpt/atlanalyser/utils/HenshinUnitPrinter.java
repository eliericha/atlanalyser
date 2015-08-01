package fr.tpt.atlanalyser.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.LoopUnit;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.SequentialUnit;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

import com.google.common.base.Joiner;

public class HenshinUnitPrinter {

    private static class UnitPrinter extends HenshinSwitch<String> {

        @Override
        public String defaultCase(EObject object) {
            return object.eClass().getName() + " is not supported in printer";
        }

        @Override
        public String caseRule(Rule object) {
            return object.getName();
        }

        @Override
        public String caseLoopUnit(LoopUnit object) {
            return doSwitch(object.getSubUnit()) + "!";
        }

        @Override
        public String caseSequentialUnit(SequentialUnit object) {
            List<String> subUnits = object.getSubUnits().stream()
                    .map(p -> doSwitch(p)).collect(Collectors.toList());
            return "(" + Joiner.on("; ").join(subUnits) + ")";
        }
    }

    public static String print(Unit unit) {
        return new UnitPrinter().doSwitch(unit);
    }

}
