package fr.tpt.atlanalyser.atl2agt;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;

import fr.tpt.atlanalyser.atl.OCL.BooleanExp;
import fr.tpt.atlanalyser.atl.OCL.IntegerExp;
import fr.tpt.atlanalyser.atl.OCL.IteratorExp;
import fr.tpt.atlanalyser.atl.OCL.OCLFactory;
import fr.tpt.atlanalyser.atl.OCL.OclExpression;
import fr.tpt.atlanalyser.atl.OCL.OclType;
import fr.tpt.atlanalyser.atl.OCL.StringExp;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;
import fr.tpt.atlanalyser.atl.OCL.util.OCLSwitch;

public class OCLTyper extends OCLSwitch<OclType> {

    private ATL2Henshin             context;
    private static final OCLFactory OF = OCLFactory.eINSTANCE;

    public OCLTyper(ATL2Henshin context) {
        this.context = context;
    }

    public OclType getType(OclExpression exp) {
        if (exp.getType() == null) {
            // If the type has not yet been determined, compute it.
            exp.setType(doSwitch(exp));
        }
        assert exp.getType() != null;
        return exp.getType();
    }

    @Override
    public OclType caseBooleanExp(BooleanExp object) {
        OclType type = OF.createOclType();
        type.setName("Boolean");
        return type;
    }

    @Override
    public OclType caseIntegerExp(IntegerExp object) {
        OclType type = OF.createOclType();
        type.setName("Integer");
        return type;
    }

    @Override
    public OclType caseStringExp(StringExp object) {
        OclType type = OF.createOclType();
        type.setName("String");
        return type;
    }

    @Override
    public OclType caseVariableExp(VariableExp object) {
        OclType type = object.getReferredVariable().getType();
        String typeName = type.getName();
        EClass eClass = context.resolveOclType(typeName);
        // TODO handle variables where the type is a collection
        return EcoreUtil.copy(type);
    }

    @Override
    public OclType caseIteratorExp(IteratorExp object) {
        String iteratorName = object.getName();
        OclExpression source = object.getSource();
        OclExpression body = object.getBody();
        switch (iteratorName) {
        case "exists":
        case "forAll":
            OclType type = OF.createOclType();
            type.setName("Boolean");
            return type;

        case "select":
            return EcoreUtil.copy(getType(source));

        case "collect":
            return EcoreUtil.copy(getType(body));

        default:
            return null;
        }
    }
}
