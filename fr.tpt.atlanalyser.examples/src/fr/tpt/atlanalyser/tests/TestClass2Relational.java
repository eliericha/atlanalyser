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

public class TestClass2Relational extends GenericTranslateTest {

    public TestClass2Relational() {
        super("examples/Class2Relational");
    }

    public static void main(String[] args) throws IOException,
            ATLAnalyserException {
        (new TestClass2Relational()).testTransformAndCompile();
    }

}
