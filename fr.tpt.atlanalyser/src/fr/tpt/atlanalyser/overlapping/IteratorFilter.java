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
package fr.tpt.atlanalyser.overlapping;

import java.util.Iterator;

public abstract class IteratorFilter<T> implements Iterator<T> {

    private Iterator<T> subIterator;
    private boolean     computedNext;
    private T           next;

    public IteratorFilter(Iterator<T> subIterator) {
        this.subIterator = subIterator;
    }

    protected static <T> Iterator<T> iterator(Iterator<T> subIterator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        if (!computedNext) {
            computeNext();
            computedNext = true;
        }

        return next != null;
    }

    private void computeNext() {
        next = null;
        while (next == null && subIterator.hasNext()) {
            T nextCandidate = subIterator.next();
            if (isValid(nextCandidate)) {
                next = nextCandidate;
            }
        }
    }

    protected abstract boolean isValid(T nextCandidate);

    @Override
    public T next() {
        if (hasNext()) {
            computedNext = false;
        }
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
