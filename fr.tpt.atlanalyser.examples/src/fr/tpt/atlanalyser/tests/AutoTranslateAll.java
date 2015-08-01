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
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.examples.ExampleRunner;
import fr.tpt.atlanalyser.tests.generic.GenericTranslateTest;

@RunWith(Parameterized.class)
public class AutoTranslateAll extends GenericTranslateTest {

    private static final String BASEDIR = "examples";

    public AutoTranslateAll(File transfoBasedir) {
        super(transfoBasedir.toString());
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<File[]> transformations() {
        return enumerateTransfos(new File(BASEDIR));
    }

    // @RunWith(Parameterized.class)
    // public static class ExecTest extends GenericExecTest {
    //
    // public ExecTest(File inputModel) {
    // super(transfoBasedir.toString(), inputModel);
    // }
    //
    // @Parameters(name = "{index}: {0}")
    // public static Collection<File[]> models() {
    // return enumerateModels(transfoBasedir.toString());
    // }
    // }

}
