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
package fr.tpt.atlanalyser.tests.generic;

import java.io.IOException;

import org.junit.Test;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.examples.ExampleRunner;

public abstract class GenericTranslateTest extends ExampleRunner {
    
    public GenericTranslateTest(String baseDir) {
        super(baseDir);
    }

    @Test
    public void testTransformAndCompile() throws IOException,
            ATLAnalyserException {
        compileAndTransform();
    }

}
