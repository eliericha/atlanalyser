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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;

public class CopierWithResolveErrorNotification {

    @SuppressWarnings("serial")
    public static class InternalCopier extends EcoreUtil.Copier {

        public EObject copy(EObject eObject) {
            eObject = super.copy(eObject);
            if (eObject.eIsProxy()) {
                throw new ATLAnalyserRuntimeException(
                        "Copying non resolved object: "
                                + EcoreUtil.getURI(eObject));
            }
            return eObject;
        }

    }

    public static <T extends EObject> T copy(T obj) {
        InternalCopier copier = new InternalCopier();
        @SuppressWarnings("unchecked")
        T result = (T) copier.copy(obj);
        copier.copyReferences();
        return result;
    }
}