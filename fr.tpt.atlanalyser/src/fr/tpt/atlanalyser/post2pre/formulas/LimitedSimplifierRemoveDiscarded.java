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
package fr.tpt.atlanalyser.post2pre.formulas;

import java.io.IOException;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.henshin.model.Formula;

import fr.tpt.atlanalyser.ATLAnalyserRuntimeException;
import fr.tpt.atlanalyser.post2pre.resources.ConcurrentResourceSet;
import fr.tpt.atlanalyser.utils.Utils;

public class LimitedSimplifierRemoveDiscarded extends LimitedSimplifier {

    private Resource preserveRes;

    public LimitedSimplifierRemoveDiscarded(int maxNestedLevels,
            Resource preserveRes) {
        super(maxNestedLevels);
        this.preserveRes = preserveRes;
    }

    @Override
    protected LimitedSimplifier newSimplifier(int n) {
        return new LimitedSimplifierRemoveDiscarded(n, preserveRes);
    }

    @Override
    protected void onDiscardBeforeTransform(Formula f) {
        new AbstractFormulaVisitor() {
            @Override
            public Formula transform(Formula formula) {
                if (formula != null) {
                    Internal eDirectResource = ((InternalEObject) formula)
                            .eDirectResource();
                    if (eDirectResource != null) {
                        synchronized (eDirectResource) {
                            Utils.replaceWithDummy(formula);
                            if (eDirectResource.getContents().size() == 0
                                    || Utils.allDummy(eDirectResource
                                            .getContents())) {
                                ConcurrentResourceSet resourceSet = (ConcurrentResourceSet) eDirectResource
                                        .getResourceSet();
                                resourceSet.lockWrite();
                                try {
                                    eDirectResource.delete(null);
                                } catch (IOException e) {
                                    throw new ATLAnalyserRuntimeException(e);
                                } finally {
                                    resourceSet.unlockWrite();
                                }
                            }
                        }
                    }
                    return super.transform(formula);
                } else {
                    return null;
                }
            }
        }.transform(f);
    }

}
