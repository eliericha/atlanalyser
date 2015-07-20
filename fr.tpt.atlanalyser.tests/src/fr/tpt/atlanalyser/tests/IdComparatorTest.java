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
package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.tpt.atlanalyser.post2pre.IWithId;
import fr.tpt.atlanalyser.post2pre.IdComparator;

public class IdComparatorTest {

    class Obj implements IWithId, Comparable<Obj> {

        private int[] id;

        public Obj(int... id) {
            this.id = id;
        }

        @Override
        public int[] getId() {
            return id;
        }

        @Override
        public int compareTo(Obj o) {
            return new IdComparator().compare(this, o);
        }

    }

    @Test
    public void test() {
        IdComparator comp = new IdComparator();
        assertTrue(comp.compare(new Obj(1), new Obj(2)) < 0);
        assertTrue(comp.compare(new Obj(1, 1), new Obj(2)) < 0);
        assertTrue(comp.compare(new Obj(1), new Obj(2, 1)) < 0);
        assertTrue(comp.compare(new Obj(1, 1), new Obj(1, 2)) < 0);
        assertTrue(comp.compare(new Obj(1, 1), new Obj(1, 1)) == 0);
    }

}
