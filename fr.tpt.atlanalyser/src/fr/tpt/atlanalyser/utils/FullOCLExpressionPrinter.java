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
package fr.tpt.atlanalyser.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import fr.tpt.atlanalyser.atl.OCL.OclModelElement;
import fr.tpt.atlanalyser.atl.OCL.VariableExp;
import fr.tpt.atlanalyser.atl.OCL.util.OCLExpressionSimplePrinter;
import fr.tpt.atlanalyser.atl2agt.ATL2Henshin;

public class FullOCLExpressionPrinter extends OCLExpressionSimplePrinter {

    private final ATL2Henshin   context;
    private Map<String, String> variableSubstitutionMap = new HashMap<String, String>();

    public FullOCLExpressionPrinter(ATL2Henshin atl2Henshin) {
        context = atl2Henshin;
    }

    public FullOCLExpressionPrinter(ATL2Henshin atl2Henshin,
            Map<String, String> varSubstMap) {
        context = atl2Henshin;
        this.variableSubstitutionMap.putAll(varSubstMap);
    }

    @Override
    public String caseVariableExp(VariableExp object) {
        final String varName = object.getReferredVariable().getVarName();
        final String substVarName = variableSubstitutionMap.get(varName);
        return substVarName == null ? varName : substVarName;
    }

    @Override
    public String caseOclModelElement(OclModelElement object) {
        final String metaclassName = object.getName();
        final EClass eClass = context.resolveOclType(metaclassName);

        final String fullMetaclassName = fullMetaclassName(eClass);

        return fullMetaclassName;
    }

    protected String fullMetaclassName(final EClass eClass) {
        String res = eClass.getName();
        EPackage ePkg = eClass.getEPackage();

        while (ePkg != null) {
            res = ePkg.getName() + "::" + res;
            ePkg = ePkg.getESuperPackage();
        }

        return res;
    }
}