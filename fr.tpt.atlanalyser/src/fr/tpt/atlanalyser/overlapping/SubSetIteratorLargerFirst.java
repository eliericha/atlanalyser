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

public class SubSetIteratorLargerFirst<T> extends SubSetIterator<T> {

    public SubSetIteratorLargerFirst(Set<T> set, Set<T> forcedSubSet,
            boolean excludeEmptySubSet) {
        this.excludeEmptySubSet = excludeEmptySubSet;
        this.forcedSubSet = forcedSubSet != null ? forcedSubSet : Collections
                .emptySet();

        Set<T> actualSet = Sets.difference(set, this.forcedSubSet);

        Iterator<T> iterator = actualSet.iterator();
        this.head = iterator.hasNext() ? iterator.next() : null;
        this.tail = Sets.newLinkedHashSet();
        while (iterator.hasNext()) {
            this.tail.add(iterator.next());
        }
        tailSubSets = this.head != null ? new SubSetIteratorLargerFirst<T>(
                tail, this.forcedSubSet, false) : null;
    }

    @Override
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
                currentTailSubSet = (Set<T>) tailSubSets.next();
                nextSubSet = Sets.newLinkedHashSet(currentTailSubSet);
                nextSubSet.addAll(forcedSubSet);
                nextSubSet.add(head);
                return;
            } else {
                // no more subgraphs
                return;
            }
        }

        if (currentTailSubSet != null) {
            Set<T> newSubSet = Sets.newLinkedHashSet(currentTailSubSet);
            newSubSet.addAll(forcedSubSet);
            currentTailSubSet = null;
            nextSubSet = newSubSet;
            if (excludeEmptySubSet && nextSubSet.isEmpty()) {
                nextSubSet = null;
            } else {
                return;
            }
        }
    }

}
