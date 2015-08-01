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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

public class HenshinContainmentVisitor<T> extends HenshinSwitch<T> {
    @Override
    public T doSwitch(EObject eObject) {
        // First the object
        T result = super.doSwitch(eObject);

        // Then its contents
        final EList<EObject> eContents = eObject.eContents();
        for (EObject contObj : eContents) {
            this.doSwitch(contObj);
        }

        return result;
    }
}