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

import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import fr.tpt.atlanalyser.examples.ExampleRunner;

public abstract class GenericExecTest extends ExampleRunner {

    protected File inputModel;

    public GenericExecTest(String baseDir, File inputModel) {
        super(baseDir);
        this.inputModel = inputModel;
    }

    public static Collection<File[]> enumerateModels(String baseDir) {
        File inputModelsDir = new File(baseDir, "InputModels");
        List<File[]> result = new ArrayList<File[]>();
        if (inputModelsDir.isDirectory()) {
            File[] inputModels = inputModelsDir.listFiles();
            for (File file : inputModels) {
                result.add(new File[] { file });
            }
        }
        return result;
    }

    @Test
    public void test() throws IOException {
        File atlOutputFile = new File(atlOutputDir,
                FilenameUtils.getBaseName(inputModel.getPath()) + "_atl.xmi");
        File henshinOutputFile = new File(henshinOutputDir,
                FilenameUtils.getBaseName(inputModel.getPath())
                        + "_henshin.xmi");
        deleteIfExists(atlOutputFile);
        deleteIfExists(henshinOutputFile);

        Stopwatch timer = Stopwatch.createStarted();
        Resource atlOutput = applyAtlTransformation(inputModel.getPath(),
                atlOutputFile.getPath());
        System.out.println("ATL executed in " + timer.stop());

        timer = Stopwatch.createStarted();
        Resource henshinOutput = applyHenshinTransformation(
                inputModel.getPath(), henshinOutputFile.getPath());
        System.out.println("AGT executed in " + timer.stop());

        compareModels(atlOutput, henshinOutput);
    }
}
