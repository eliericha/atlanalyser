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

/**
 * This visitor simplifies formulas using standard logic properties, including
 * formulas nested in NestedConditions.
 * 
 * @author richa
 *
 */
public class FullSimplifier extends LimitedSimplifier {

    protected static final int INFINITE_NESTING = Integer.MAX_VALUE;

    public FullSimplifier() {
        super(INFINITE_NESTING);
    }

    @Override
    protected LimitedSimplifier newSimplifier(int n) {
        return new FullSimplifier();
    }

}