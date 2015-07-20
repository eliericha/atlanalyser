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
import java.util.Set;

public class SubSetGenerator<T> implements Iterable<Set<T>> {

    private Set<T>  set;
    private Set<T>  forcedSubSet;
    private boolean excludeEmptySubSet;

    public SubSetGenerator(Set<T> set, Set<T> forcedSubSet,
            boolean excludeEmptySubSet) {
        this.set = set;
        this.forcedSubSet = forcedSubSet;
        this.excludeEmptySubSet = excludeEmptySubSet;
    }

    @Override
    public Iterator<Set<T>> iterator() {
        return new SubSetIteratorLargerFirst<T>(set, forcedSubSet,
                excludeEmptySubSet);
    }

}