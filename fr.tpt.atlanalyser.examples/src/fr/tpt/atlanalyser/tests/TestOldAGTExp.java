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

import java.io.IOException;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.tests.generic.GenericTranslateTest;

public class TestOldAGTExp extends GenericTranslateTest {

    public static void main(String args[]) throws IOException,
            ATLAnalyserException {
        new TestOldAGTExp().testATL2AGT();
    }

    public TestOldAGTExp() {
        super("examples/OldAGTExp");
    }

}
