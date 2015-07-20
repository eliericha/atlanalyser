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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class SubSetIterator<T> implements Iterator<Set<T>> {

    protected Set<T>            nextSubSet;
    protected boolean           computedNextSubSet;
    protected T                 head;
    protected Set<T>            tail;
    protected SubSetIterator<T> tailSubSets;
    protected Set<T>            currentTailSubSet;
    protected Set<T>            forcedSubSet;
    protected boolean           excludeEmptySubSet;

    @SuppressWarnings("unchecked")
    public SubSetIterator(Set<T> set, Set<T> forcedSubSet,
            boolean excludeEmptySubSet) {
        this.excludeEmptySubSet = excludeEmptySubSet;
        this.forcedSubSet = forcedSubSet != null ? forcedSubSet
                : Collections.EMPTY_SET;

        Set<T> actualSet = Sets.difference(set, this.forcedSubSet);

        Iterator<T> iterator = actualSet.iterator();
        this.head = iterator.hasNext() ? iterator.next() : null;
        this.tail = Sets.newLinkedHashSet();
        while (iterator.hasNext()) {
            this.tail.add(iterator.next());
        }
        tailSubSets = this.head != null ? new SubSetIterator<T>(tail,
                this.forcedSubSet, false) : null;
    }
    
    protected SubSetIterator() {
    }

    @Override
    public boolean hasNext() {
        if (!computedNextSubSet) {
            computeNextSubSet();
            computedNextSubSet = true;
        }
        return nextSubSet != null;
    }

    protected void computeNextSubSet() {
        nextSubSet = null;

        if (tail == null) {
            // no more subgraphs
            return;
        }

        if (head == null) {
            nextSubSet = Sets.newLinkedHashSet(forcedSubSet);
            tail = null;
            return;
        }

        if (currentTailSubSet == null) {
            if (tailSubSets.hasNext()) {
                Set<T> tailSubSet = (Set<T>) tailSubSets.next();
                nextSubSet = Sets.newLinkedHashSet(tailSubSet);
                nextSubSet.addAll(forcedSubSet);
                currentTailSubSet = tailSubSet;
                if (excludeEmptySubSet && nextSubSet.isEmpty()) {
                    nextSubSet = null;
                } else {
                    return;
                }
            } else {
                // no more subgraphs
                return;
            }
        }

        if (currentTailSubSet != null) {
            Set<T> newSubSet = Sets.newLinkedHashSet();
            newSubSet.add(head);
            newSubSet.addAll(currentTailSubSet);
            newSubSet.addAll(forcedSubSet);
            currentTailSubSet = null;
            nextSubSet = newSubSet;
        }
    }

    @Override
    public Set<T> next() {
        if (hasNext()) {
            computedNextSubSet = false;
        }
        return nextSubSet;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}