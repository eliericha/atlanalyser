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
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.post2pre.Post2Pre4ATL;
import fr.tpt.atlanalyser.tests.generic.GenericPost2PreTest;

@RunWith(Parameterized.class)
public class TestForPhDPost2Pre extends GenericPost2PreTest {

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException {

        // URL resource = Thread.currentThread().getContextClassLoader()
        // .getResource("OldAGTExp");
        // System.out.println(resource.toString());
        // File f = new File(resource.getPath());
        // System.out.println(f.toString());
        // System.out.println(f.isDirectory());
        // System.exit(0);

        Options options = new Options();
        options.addOption(OptionBuilder.hasArg().withArgName("N")
                .withDescription("Number of parallel jobs").create("j"));
        options.addOption(OptionBuilder.withDescription("Display help").create(
                "h"));

        CommandLineParser parser = new BasicParser();
        int jobs = 1;
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                new HelpFormatter().printHelp(
                        TestForPhDPost2Pre.class.getSimpleName(), options);
                System.exit(0);
            }

            if (cmd.hasOption("j")) {
                jobs = Integer.parseInt(cmd.getOptionValue("j"));
            }
        } catch (Exception e) {
            System.out.println("Incorrect command line arguments");
            new HelpFormatter().printHelp(
                    TestForPhDPost2Pre.class.getSimpleName(), options);
            System.exit(1);
        }

        // new TestForPhDPost2Pre(models().get(1)[0], jobs).testPost2Pre();
    }

    private static final String BASEDIR = "examples/ForPhDPost2Pre";

    public TestForPhDPost2Pre(File inputModel) {
        super(BASEDIR, inputModel, 2);
    }

    @Parameters(name = "{index}: {0}")
    public static List<File[]> models() {
        return enumeratePosts(BASEDIR);
    }

    @Override
    public void testPost2Pre() throws IOException {
        executePost2Pre(this.inputPost, 1);
    }

}
