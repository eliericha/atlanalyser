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

import java.util.Comparator;

public class IdComparator implements Comparator<IWithId> {
    @Override
    public int compare(IWithId o1, IWithId o2) {
        int[] id1 = o1.getId();
        int[] id2 = o2.getId();
        for (int i = 0; i < Math.min(id1.length, id2.length); i++) {
            if (id1[i] != id2[i]) {
                return id1[i] - id2[i];
            }
        }
        return id2.length - id1.length;
    }
}