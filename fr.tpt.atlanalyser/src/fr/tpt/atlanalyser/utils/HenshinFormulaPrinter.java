package fr.tpt.atlanalyser.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.Xor;
import org.eclipse.emf.henshin.model.impl.BinaryFormulaImpl;
import org.eclipse.emf.henshin.model.impl.GraphImpl;
import org.eclipse.emf.henshin.model.impl.NestedConditionImpl;
import org.eclipse.emf.henshin.model.impl.NotImpl;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

public class HenshinFormulaPrinter extends HenshinSwitch<String> {

    private int indent = 0;

    public static String print(Formula formula) {
        return new HenshinFormulaPrinter().doSwitch(formula);
    }

    private String getIndent() {
        String res = "";
        for (int i = 0; i < indent; i++) {
            res += " ";
        }
        return res;
    }

    @Override
    public String defaultCase(EObject object) {
        return object.toString();
    }

    @Override
    public String caseAnd(And object) {
        Formula left = ((BinaryFormulaImpl) object).basicGetLeft();
        Formula right = ((BinaryFormulaImpl) object).basicGetRight();
        return this.doSwitch(left) + "\n" + getIndent() + "&& "
                + this.doSwitch(right);
    }

    @Override
    public String caseOr(Or object) {
        Formula left = ((BinaryFormulaImpl) object).basicGetLeft();
        Formula right = ((BinaryFormulaImpl) object).basicGetRight();
        return this.doSwitch(left) + "\n" + getIndent() + "|| "
                + this.doSwitch(right);
    }

    @Override
    public String caseXor(Xor object) {
        Formula left = ((BinaryFormulaImpl) object).basicGetLeft();
        Formula right = ((BinaryFormulaImpl) object).basicGetRight();
        return this.doSwitch(left) + " XOR " + this.doSwitch(right);
    }

    @Override
    public String caseNot(Not object) {
        String res = getIndent() + "!( ";
        this.indent += 3;
        Formula child = ((NotImpl) object).basicGetChild();
        res += this.doSwitch(child).trim() + ")";
        this.indent -= 3;
        return res;
    }

    @Override
    public String caseNestedCondition(NestedCondition object) {
        Graph conclusion = ((NestedConditionImpl) object).basicGetConclusion();

        if (conclusion.eIsProxy()) {
            return "proxy: " + EcoreUtil.getURI(conclusion);
        }

        List<GraphElement> els = new ArrayList<GraphElement>(
                conclusion.getNodes());
        els.addAll(conclusion.getEdges());

        if ("TRUE".equals(conclusion.getName())) {
            return "TRUE";
        }

        String res = "∃( ";

        if (conclusion.getName() != null) {
            res += conclusion.getName();
        }

        // res += " nest[" + new CountNestingLevels().doSwitch(object) + "]";

        this.indent += 3;
        for (GraphElement graphElement : els) {
            res += "\n" + getIndent() + graphElement;
        }
        this.indent -= 3;

        Formula formula = ((GraphImpl) conclusion).basicGetFormula();
        if (formula != null) {
            this.indent += 3;
            res += ",\n" + this.doSwitch(formula);
            this.indent -= 3;
        }

        res += ")";

        return res;
    }

    @Override
    public String doSwitch(EObject theEObject) {
        if (theEObject.eIsProxy()) {
            return "proxy: " + EcoreUtil.getURI(theEObject);
        } else {
            return super.doSwitch(theEObject);
        }
    }
}
