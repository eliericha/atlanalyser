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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

public class CopierPreservingReferences {

    private static class InternalCopier extends EcoreUtil.Copier {

        private static final long serialVersionUID = 7138113537916788570L;

        public InternalCopier() {
            super(true, true);
        }

        protected void copyReference(EReference eReference, EObject eObject,
                EObject copyEObject) {
            if (eObject.eIsSet(eReference)) {
                if (eReference.isMany()) {
                    @SuppressWarnings("unchecked")
                    InternalEList<EObject> source = (InternalEList<EObject>) eObject
                            .eGet(eReference);
                    @SuppressWarnings("unchecked")
                    InternalEList<EObject> target = (InternalEList<EObject>) copyEObject
                            .eGet(getTarget(eReference));
                    if (source.isEmpty()) {
                        target.clear();
                    } else {
                        boolean isBidirectional = eReference.getEOpposite() != null;
                        int index = 0;
                        for (Iterator<EObject> k = resolveProxies ? source
                                .iterator() : source.basicIterator(); k
                                .hasNext();) {
                            EObject referencedEObject = k.next();
                            EObject copyReferencedEObject = get(referencedEObject);
                            if (copyReferencedEObject == null) {
                                if (useOriginalReferences) {
                                    target.addUnique(index, referencedEObject);
                                    ++index;
                                }
                            } else {
                                if (isBidirectional) {
                                    int position = target
                                            .indexOf(copyReferencedEObject);
                                    if (position == -1) {
                                        target.addUnique(index,
                                                copyReferencedEObject);
                                    } else if (index != position) {
                                        target.move(index,
                                                copyReferencedEObject);
                                    }
                                } else {
                                    target.addUnique(index,
                                            copyReferencedEObject);
                                }
                                ++index;
                            }
                        }
                    }
                } else {
                    Object referencedEObject = eObject.eGet(eReference,
                            resolveProxies);
                    if (referencedEObject == null) {
                        copyEObject.eSet(getTarget(eReference), null);
                    } else {
                        Object copyReferencedEObject = get(referencedEObject);
                        if (copyReferencedEObject == null) {
                            if (useOriginalReferences) {
                                copyEObject.eSet(getTarget(eReference),
                                        referencedEObject);
                            }
                        } else {
                            copyEObject.eSet(getTarget(eReference),
                                    copyReferencedEObject);
                        }
                    }
                }
            }
        }
    }

    public static <T extends EObject> T copy(T eObject) {
        EcoreUtil.Copier copier = new InternalCopier();
        @SuppressWarnings("unchecked")
        T copy = (T) copier.copy(eObject);
        copier.copyReferences();
        return copy;
    }

    public static <T> Collection<T> copyAllWithRefs(
            Collection<? extends T> eObjects) {
        EcoreUtil.Copier copier = new InternalCopier();
        Collection<T> result = copier.copyAll(eObjects);
        copier.copyReferences();
        return result;
    }

}
