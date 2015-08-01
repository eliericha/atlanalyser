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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Test;

import fr.tpt.atlanalyser.examples.ExampleRunner;

public abstract class GenericPost2PreTest extends ExampleRunner {

    protected File inputPost;

    public GenericPost2PreTest(String baseDir, File inputPost, int jobs) {
        super(baseDir, jobs);
        this.inputPost = inputPost;
    }

    public GenericPost2PreTest(String baseDir, File inputPost) {
        this(baseDir, inputPost, 2);
    }

    protected static List<File[]> enumeratePosts(String basedir) {
        List<File[]> result = new ArrayList<File[]>();
        File inputModelsDir = new File(basedir, "Postconditions");
        if (inputModelsDir.isDirectory()) {
            File[] inputModels = inputModelsDir
                    .listFiles((FilenameFilter) new SuffixFileFilter(".henshin"));
            for (File file : inputModels) {
                result.add(new File[] { file });
            }
        }
        return result;
    }

    @Test
    public void testPost2Pre() throws IOException {
        executePost2Pre(inputPost);
    }

}
