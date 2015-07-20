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
package fr.tpt.atlanalyser.post2pre;

import java.util.Iterator;

public abstract class SolutionIterator<T> implements Iterator<T> {

    protected T     nextSolution;
    private boolean computedNext;

    @Override
    public boolean hasNext() {
        if (!computedNext) {
            computeNext();
            computedNext = true;
        }
        return nextSolution != null;
    }

    protected abstract void computeNext();

    @Override
    public T next() {
        if (hasNext()) {
            computedNext = false;
        }
        return nextSolution;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}