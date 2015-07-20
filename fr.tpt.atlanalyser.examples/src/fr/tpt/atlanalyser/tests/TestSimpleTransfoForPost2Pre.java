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

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import fr.tpt.atlanalyser.tests.generic.GenericTranslateTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSimpleTransfoForPost2Pre extends GenericTranslateTest {

    public TestSimpleTransfoForPost2Pre() {
        super("examples/SimpleTransfoForPost2Pre");
    }

}
