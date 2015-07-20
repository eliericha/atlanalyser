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
package fr.tpt.atlanalyser.post2pre.ncvalidators;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;

public class ExcludeEReferenceNCValidator implements NestedConditionValidator {

    private EReference excludedERef;

    public ExcludeEReferenceNCValidator(EReference excludedERef) {
        this.excludedERef = excludedERef;
    }

    @Override
    public boolean isValid(NestedCondition nc) {
        Graph conclusion = nc.getConclusion();
        return conclusion.getEdges(excludedERef).isEmpty();
    }
    
    @Override
    public String toString() {
        return "ExcludeEReference: " + excludedERef.getName();
    }

}