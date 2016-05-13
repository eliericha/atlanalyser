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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.examples.ExampleRunner;
import fr.tpt.atlanalyser.tests.generic.GenericExecTest;
import fr.tpt.atlanalyser.tests.generic.GenericTranslateTest;

@RunWith(Parameterized.class)
public class AutoExecAll extends GenericExecTest {

    private static final String BASEDIR = "examples";

    public AutoExecAll(File transfo, File inputModel) {
        super(transfo.toString(), inputModel);
    }

    @Parameters(name = "{index}: {1}")
    public static Collection<File[]> models() {
        File transfosDir = new File(BASEDIR);
        File[] transfos = transfosDir.listFiles();
        List<File[]> result = new ArrayList<File[]>();
        for (File t : transfos) {
            Collection<File[]> inputModels = enumerateModels(t.toString());
            for (File[] inputModel : inputModels) {
                result.add(new File[] { t, inputModel[0] });
            }
        }
        return result;
    }

    @Test
    public void test() {

    }

}
