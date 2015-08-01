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
import java.util.List;
import java.util.stream.Collectors;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.tpt.atlanalyser.tests.generic.GenericPost2PreTest;

@RunWith(Parameterized.class)
public class Post2PrePerfTests extends GenericPost2PreTest {

    private static final int    JOBS    = 1;
    private static final String BASEDIR = "examples/ForPhDPost2Pre";

    public Post2PrePerfTests(File inputModel) {
        super(BASEDIR, inputModel, JOBS);
    }

    @Parameters(name = "{index}: {0}")
    public static List<File[]> models() {
        List<File[]> result = enumeratePosts(BASEDIR).stream()
                .filter(f -> !"Post11.henshin".equals(f[0].getName()))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public void testPost2Pre() throws IOException {
        int maxNumRuleIterations = 2;
        executePost2Pre(this.inputPost, maxNumRuleIterations);
    }

}
