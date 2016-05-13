package fr.tpt.atlanalyser.atl2agt;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.ocl.ecore.OCLExpression;

import fr.tpt.atlanalyser.atl.OCL.BooleanExp;
import fr.tpt.atlanalyser.atl.OCL.NavigationOrAttributeCallExp;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;
import fr.tpt.atlanalyser.atl.OCL.OclType;
import fr.tpt.atlanalyser.atl.OCL.PropertyCallExp;
import fr.tpt.atlanalyser.atl.OCL.VariableDeclaration;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;
import fr.tpt.atlanalyser.atl.OCL.util.OCLSwitch;
import fr.tpt.atlanalyser.utils.NGCUtils;

public class FormalOCL2NGC {

    private static final HenshinFactory HF         = HenshinFactory.eINSTANCE;
    private static Integer              varCounter = 1;
    private Graph                       host;
    private BoolExpSwitch               boolExpVisitor;
    private ATL2Henshin                 atl2Henshin;

    public FormalOCL2NGC(ATL2Henshin atl2Henshin, Graph host) {
        this.atl2Henshin = atl2Henshin;
        this.host = host;
        this.boolExpVisitor = new BoolExpSwitch();
    }

    private static String newVarName() {
        return "v_" + (varCounter++).toString();
    }

    private class BoolExpSwitch extends OCLSwitch<Formula> {

        @Override
        public Formula caseBooleanExp(BooleanExp object) {
            if (object.isBooleanSymbol()) {
                // return true
                return NGCUtils.createTrue();
            } else {
                // return false
                return NGCUtils.createFalse();
            }
        }

    }

    private Formula tr_E(OCLExpression exp) {
        return boolExpVisitor.doSwitch(exp);
    }

    private class NavExpSwitch extends OCLSwitch<Formula> {

        private Node result;

        public NavExpSwitch(Node result) {
            this.result = result;
        }

        @Override
        public Formula caseVariableExp(VariableExp object) {
            NestedCondition res = HF.createNestedCondition();
            VariableDeclaration referredVariable = object.getReferredVariable();
            String varName = referredVariable.getVarName();
            OclType type = referredVariable.getType();
            EClass eClass = atl2Henshin.resolveOclType(type);
            Node node = HF.createNode(res.getConclusion(), eClass, varName
                    + "=" + result.getName());
            res.getMappings().add(this.result, node);

            return res;
        }

        @Override
        public Formula caseNavigationOrAttributeCallExp(
                NavigationOrAttributeCallExp object) {
            OclExpression source = object.getSource();
            String property = object.getName();

            return super.caseNavigationOrAttributeCallExp(object);
        }
    }

    private Formula tr_N(OCLExpression exp, Node result) {
        return new NavExpSwitch(result).doSwitch(exp);
    }

    private class SetExpSwitch extends OCLSwitch<Formula> {

        private Node result;

        public SetExpSwitch(Node result) {
            this.result = result;
        }
    }

    private Formula tr_S(OCLExpression exp, Node result) {
        return new SetExpSwitch(result).doSwitch(exp);
    }

}
