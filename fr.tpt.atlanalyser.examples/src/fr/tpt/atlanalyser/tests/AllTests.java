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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestSimpleCMG.class,
    TestSimpleCMGExec.class,
    TestSimpleCMG1.class,
    TestSimpleCMG1Exec.class,
    TestFamilies2Persons.class,
    TestFamilies2PersonsExec.class,
    TestClass2Relational.class,
    TestClass2RelationalExec.class,
    TestER2REL.class,
    TestER2RELExec.class,
    TestNGC.class,
    TestNGCExec.class,
    TestNonInjectiveInPatterns.class,
    TestNonInjectiveInPatternsExec.class
})
public class AllTests {

}
