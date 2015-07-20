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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public class EObjectUtils {

    public static List<EObject> findEClassInstanceInObj(EObject obj,
            EClass eClass) {
        List<EObject> result = new ArrayList<EObject>();
        for (TreeIterator<EObject> iterator = obj.eAllContents(); iterator
                .hasNext();) {
            EObject containedObj = (EObject) iterator.next();

            if (eClass.isSuperTypeOf(containedObj.eClass())) {
                result.add(containedObj);
            }
        }
        return result;
    }

}
