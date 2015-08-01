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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import fr.tpt.atlanalyser.ATLAnalyserException;
import fr.tpt.atlanalyser.examples.ExampleRunner;

public abstract class GenericTranslateTest extends ExampleRunner {

    public GenericTranslateTest(String baseDir) {
        super(baseDir);
    }

    @Test
    public void testATL2AGT() throws IOException, ATLAnalyserException {
        compileAndTransform();
    }

    public static Collection<File[]> enumerateTransfos(File transfosDir) {
        File[] transfos = transfosDir.listFiles();
        List<File[]> result = new ArrayList<File[]>();
        for (File file : transfos) {
            result.add(new File[] { file });
        }
        return result;
    }

}
