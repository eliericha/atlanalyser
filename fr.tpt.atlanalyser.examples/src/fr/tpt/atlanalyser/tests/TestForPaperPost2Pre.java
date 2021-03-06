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
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.tpt.atlanalyser.tests.generic.GenericPost2PreTest;

@RunWith(Parameterized.class)
public class TestForPaperPost2Pre extends GenericPost2PreTest {

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
                        TestForPaperPost2Pre.class.getSimpleName(),
                        options);
                System.exit(0);
            }

            if (cmd.hasOption("j")) {
                jobs = Integer.parseInt(cmd.getOptionValue("j"));
            }
        } catch (Exception e) {
            System.out.println("Incorrect command line arguments");
            new HelpFormatter().printHelp(
                    TestForPaperPost2Pre.class.getSimpleName(), options);
            System.exit(1);
        }

        new TestForPaperPost2Pre(models().get(1)[0], jobs).testPost2Pre();
    }

    private static final String BASEDIR = "examples/ForPaper";

    public TestForPaperPost2Pre(File inputModel, int jobs) {
        super(BASEDIR, inputModel, jobs);
    }

    public TestForPaperPost2Pre(File inputModel) {
        this(inputModel, 2);
    }

    @Parameters(name = "{index}: {0}")
    public static List<File[]> models() {
        return enumeratePosts(BASEDIR);
    }

    @Override
    public void testPost2Pre() throws IOException {
        executePost2Pre(inputPost, 1);
    }

}
