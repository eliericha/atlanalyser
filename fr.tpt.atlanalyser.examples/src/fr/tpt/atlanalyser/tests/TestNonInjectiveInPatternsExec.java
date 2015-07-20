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

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.tpt.atlanalyser.tests.generic.GenericExecTest;

@RunWith(Parameterized.class)
public class TestNonInjectiveInPatternsExec extends GenericExecTest {

    private static final String BASEDIR = "examples/NonInjectiveInPatterns";

    public TestNonInjectiveInPatternsExec(File inputModel) {
        super(BASEDIR, inputModel);
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<File[]> models() {
        return enumerateModels(BASEDIR);
    }
    
}
